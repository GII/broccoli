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

import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.implementation.IFunctionalityImplementation;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdf.impl.implementation.FunctionalityImplementationFactory;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.implementation.FunctionalityExecutionVO;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import com.hi3project.broccoli.io.BSDFLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * <b>Description:</b></p>
 *
 *
 * <p>
 * <b>Creation date:</b>
 * 18-06-2014 </p>
 *
 * <p>
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 18-06-2014 - Initial release</li>
 * </ul>
 *
 *
 * 
 * @version 1
 */
public class BSDLJavaServiceImplementation extends ConceptBehaviourImplementation implements IServiceImplementation
{

    private Map<String, IFunctionalityImplementation> functionalityImplementations;
    private String implementationType = null;
    private IServiceDescription serviceDescription;

    public BSDLJavaServiceImplementation(Instance instance, IServiceDescription serviceDescription) throws SemanticModelException
    {
        setInstance(instance);

        this.setServiceDescription(serviceDescription);

        this.implementationType = getSinglePropertyAsInstanceLiteralValue("implementationType");

        this.functionalityImplementations = new HashMap<String, IFunctionalityImplementation>();
        for (IObject iobject : getProperties("functionalityImplementation"))
        {
            if (iobject instanceof Instance)
            {
                AbstractBSDLJavaFunctionalityImplementation functionalityImplementation
                        = FunctionalityImplementationFactory.getSingleton().instanceFor(implementationType, (Instance) iobject);
                if (null != functionalityImplementation)
                {
                    functionalityImplementation.setServiceImplementation(this);
                    functionalityImplementation.setAdvertisedFunctionality(
                        getFunctionality(
                                functionalityImplementation.getSinglePropertyAsInstance("advertisedFunctionality").getSemanticIdentifier(), 
                                serviceDescription.getProfile().advertisedFunctionalities()));                    
                    this.functionalityImplementations.put(functionalityImplementation.name(), functionalityImplementation);
                }
            }
        }
    }

    @Override
    public Collection<IResult> executeWithValues(
            String functionalityName,
            Collection<IAnnotatedValue> values,
            FunctionalityExecutionVO executionVO) throws ModelException
    {
        IFunctionalityImplementation functionalityImplementation = this.getFunctionalityImplementation(functionalityName);
        if (null != functionalityImplementation)
        {
            BSDFLogger.getLogger().info("Executes a functionality: " + functionalityName);
            return functionalityImplementation.executeWithValues(values, executionVO);
        } else
        {
            BSDFLogger.getLogger().info("Cannot execute this functionality: "
                    + functionalityName 
                    + " because it doesn t exist in this implementation: "
                    + this.toString());
            return new ArrayList<IResult>();
        }
    }

    @Override
    public Collection<IFunctionalityImplementation> getFunctionalityImplementations()
    {
        return this.functionalityImplementations.values();
    }

    @Override
    public IFunctionalityImplementation getFunctionalityImplementation(String name)
    {
        return this.functionalityImplementations.get(name);
    }

    @Override
    public IServiceDescription getServiceDescription()
    {
        return this.serviceDescription;
    }

    @Override
    public final void setServiceDescription(IServiceDescription serviceDescription)
    {
        this.serviceDescription = serviceDescription;
    }
    
    @Override
    public Object getImplementationOf(Class clas) throws ServiceExecutionException
    {
        for (IFunctionalityImplementation funcImpl : this.getFunctionalityImplementations())
        {
            if (null != funcImpl.getFunctionalityImplementationObject()
                    && funcImpl.getFunctionalityImplementationObject().getClass().equals(clas))
            {
                return funcImpl.getFunctionalityImplementationObject();
            }
        }
        return null;
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm#serviceImplementation");
    }

    @Override
    public String toString()
    {
        return "BSDLJavaServiceImplementation{" + "implementationType=" + implementationType + ", serviceDescription=" + serviceDescription + '}';
    }
            

    public static IServiceFunctionality getFunctionality(ISemanticIdentifier functionalityIdentifier, Collection<IServiceFunctionality> functionalities) throws SemanticModelException
    {
        for (IServiceFunctionality func : functionalities)
        {
            if (((ConceptBehaviourImplementation) func).getInstance() instanceof Instance)
            {
                Instance funcInstance = (Instance) ((ConceptBehaviourImplementation) func).getInstance();
                if (null != funcInstance.getSemanticIdentifier() && funcInstance.getSemanticIdentifier().equals(functionalityIdentifier))
                {
                    return func;
                }
            }
        }
        return null;
    }
    
}
