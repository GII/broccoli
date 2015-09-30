/*******************************************************************************
 *
 * Copyright (C) 2015 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright (C) 2015 Alejandro Paz <alejandropl@lagostelle.com>
 *
 * This file is part of Broccoli.
 *
 * Broccoli is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Broccoli is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Broccoli. If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl;

import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdf.impl.implementation.OntologyToJavaReference;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.InstanceArrayList;
import com.hi3project.broccoli.bsdl.impl.InstanceLiteral;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdf.impl.implementation.AJavaServiceImplementation;
import com.hi3project.broccoli.bsdm.api.implementation.FunctionalityExecutionVO;
import com.hi3project.broccoli.bsdf.impl.parsing.JenaBeanConverter;
import com.hi3project.broccoli.bsdl.impl.AbstractInstanceArrayList;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.impl.functionality.OutputValue;
import com.hi3project.broccoli.bsdm.impl.functionality.ParameterValue;
import com.hi3project.broccoli.io.BSDFLogger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Subgrounding implementation for Java-to-OWL using Jenabeans and pure BSDL
 *
 * 
 */
public class BSDLJavaServiceFunctionalityBSDLRealization extends AbstractBSDLJavaFunctionalityImplementation
{

    JenaBeanConverter jenaBeanConverter = null;

    Object functionalityImplementation = null;

    public BSDLJavaServiceFunctionalityBSDLRealization(Instance instance) throws SemanticModelException
    {
        super(instance);
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/grounding#functionalityGroundingJena");
    }

    private JenaBeanConverter getConverter() throws SemanticModelException
    {
        if (null == jenaBeanConverter)
        {
            IBSDLRegistry bsdlRegistry = ((IServiceDescription) this.getServiceImplementation().getServiceDescription()).getBSDLRegistry();
            jenaBeanConverter = new JenaBeanConverter(bsdlRegistry);
            for (OntologyToJavaReference ontToJava : ontologyToJavaReferences())
            {
                jenaBeanConverter.addOntologyToJavaReference(ontToJava);
            }
        }

        return jenaBeanConverter;
    }

    @Override
    public Collection<IResult> executeWithValues(Collection<IAnnotatedValue> values, FunctionalityExecutionVO executionVO) throws ModelException
    {
        final String errorMsg = "Exception executing from class: " + className() + " and method: " + methodName();
        try
        {
            Class clas = Class.forName(className());
            Method method = JenaBeanConverter.extractClassMethodByName(clas, methodName());

            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            
            int firstElement = 0;
            boolean sameSize = 
                    
                    (parameterTypes.length == values.size()
                        || ( (parameterTypes.length == values.size() + 1) 
                            && (parameterTypes[firstElement].getCanonicalName().equals(FunctionalityExecutionVO.class.getCanonicalName()) )));
            
            if (parameterTypes.length > 0 &&
                    parameterTypes[0].getCanonicalName().equals(FunctionalityExecutionVO.class.getCanonicalName()))
            {
                args[firstElement++] = executionVO;
            }
            
            for (int i = firstElement; i < parameterTypes.length; i++)
            {
                Class<?> parameterClass = parameterTypes[i];
                if (sameSize)
                {
                    args[i] = createValue((IAnnotatedValue) values.toArray()[i - firstElement], getConverter());
                } else
                {
                    Object arg = getValueOfType(parameterClass, values);
                    if (null != arg)
                    {
                        args[i] = arg;
                    }
                }
            }
            Object result = method.invoke(loadFunctionalityImplementationObject(), args);
            Collection<IResult> outputs = new ArrayList<IResult>();
            if (null != result)
            {
                IOutputValue output = createOutput(result, getAdvertisedFunctionality());
                if (output instanceof OutputValue)
                {
                    ((OutputValue) output).setValue(result);
                }
                outputs.add(output);
            }
            return outputs;

        } catch (ClassNotFoundException ex)
        {
            BSDFLogger.getLogger().error("Class not found in executeWithValues: " + values, ex);
            throw new ServiceExecutionException(errorMsg, ex);
        } catch (IllegalArgumentException ex)
        {
            throw new ServiceExecutionException(errorMsg, ex);
        } catch (InvocationTargetException ex)
        {
            throw new ServiceExecutionException(errorMsg, ex);
        } catch (IllegalAccessException ex)
        {
            throw new ServiceExecutionException(errorMsg, ex);
        }
    }
    
    @Override
    public Object getFunctionalityImplementationObject()
    {
        return this.functionalityImplementation;
    }

    @Override
    public synchronized Object loadFunctionalityImplementationObject() throws ServiceExecutionException
    {
        if (null == this.functionalityImplementation)
        {
            try
            {
                Class clas = Class.forName(className());
                // check whether there is already an object of this class loaded (for another func. impl. maybe)
                Object existingImpl = this.getServiceImplementation().getImplementationOf(clas);
                if (null != existingImpl)
                {
                    this.functionalityImplementation = existingImpl;
                } else
                {
                    this.functionalityImplementation = clas.newInstance();
                    if (this.functionalityImplementation instanceof AJavaServiceImplementation)
                    {
                        // for implementations that are instances of AJavaServiceImplementation, 
                        //the configurations properties are passed
                        AJavaServiceImplementation javaServiceImplementation = (AJavaServiceImplementation) this.functionalityImplementation;
                        javaServiceImplementation.setConfigurationProperties(this.configurationProperties);
                        javaServiceImplementation.initService();
                    }
                }
                
                BSDFLogger.getLogger().info("Loads functionality implementation: " + this.functionalityImplementation.toString());
                
                registerFunctionalityImplementation(this.functionalityImplementation);

            } catch (Exception ex)
            {
                throw new ServiceExecutionException("", ex);
            }
        }

        return this.functionalityImplementation;
    }

    private synchronized void registerFunctionalityImplementation(Object functionalityImplementationObject)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {

        if (null != functionalityImplementationObject
                && functionalityImplementationObject instanceof AJavaServiceImplementation)
        {
            AJavaServiceImplementation serviceImplementation = (AJavaServiceImplementation) functionalityImplementationObject;
            serviceImplementation.addFunctionalityGrounding(this);
        }

    }

    private Object getValueOfType(Class<?> clas, Collection<IAnnotatedValue> values) throws ModelException
    {
        for (IAnnotatedValue annotatedValue : values)
        {
            String javaClassName = getConverter().identifierToJavaClassName(annotatedValue.getSemanticAnnotation());
            if (clas.getName().equals(javaClassName) || clas.isAssignableFrom(List.class))
            {
                return createValue(annotatedValue, getConverter());
            }
        }
        return null;
    }

    public static Object createValue(IAnnotatedValue annotatedValue, JenaBeanConverter jenaBeanConverter) throws ModelException
    {
        if (annotatedValue instanceof ParameterValue && ((ParameterValue) annotatedValue).value() instanceof Instance)
        {
            return jenaBeanConverter.createObjectFor((Instance) ((ParameterValue) annotatedValue).value());
        }
        if (annotatedValue instanceof ParameterValue && ((ParameterValue) annotatedValue).value() instanceof InstanceLiteral)
        {
            return ((InstanceLiteral) ((ParameterValue) annotatedValue).value()).getValue();
        }
        if (annotatedValue instanceof ParameterValue && ((ParameterValue) annotatedValue).value() instanceof InstanceArrayList)
        {
            Iterator<Instance> iterator = ((InstanceArrayList) ((ParameterValue) annotatedValue).value()).values().iterator();
            Collection returnValues = new ArrayList();
            while (iterator.hasNext())
            {
                returnValues.add(jenaBeanConverter.createObjectFor(iterator.next()));
            }
            return returnValues;
        }
        return null;
    }

    public static OutputValue createOutput(Object result, JenaBeanConverter jenaBeanConverter, IServiceFunctionality functionality) throws SemanticModelException
    {
        if (result instanceof List)
        {
            AbstractInstanceArrayList resultInstance = jenaBeanConverter.createInstanceFor((List) result);
            return new OutputValue(resultInstance, functionality.outputs());
        } else
        {
            if (result instanceof String)
            {
                InstanceLiteral resultInstance = new InstanceLiteral((String) result);
                return new OutputValue(resultInstance, functionality.outputs());
            } else
            {
                Instance resultInstance = jenaBeanConverter.createInstanceFor(result);
                return new OutputValue(resultInstance, functionality.outputs());
            }
        }
    }

    @Override
    public IOutputValue createOutput(Object result, IServiceFunctionality functionality) throws SemanticModelException
    {
        return createOutput(result, getConverter(), functionality);
    }

    public IAnnotatedValue addAnnotatedValue(Object value) throws ModelException
    {
        return getConverter().createAnnotatedValue(value);
    }

    public IAnnotatedValue addAnnotatedValue(Collection value) throws ModelException
    {
        return getConverter().createAnnotatedValue(value);
    }

    @Override
    public String toString()
    {
        return "BSDLJavaServiceFunctionalityBSDLRealization{" + "functionalityImplementation=" + functionalityImplementation 
                + super.toString()
                + '}';
    }        

}
