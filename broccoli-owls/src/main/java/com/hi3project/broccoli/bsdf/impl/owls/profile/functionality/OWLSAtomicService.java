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

package com.hi3project.broccoli.bsdf.impl.owls.profile.functionality;

import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IFunctionalityCategory;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInputValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IEffect;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IExecutableWithInputsFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutput;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdf.api.discovery.IMatchmaker;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGrounding;
import com.hi3project.broccoli.bsdm.api.implementation.IFunctionalityImplementation;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.nonFunctionalProperties.INonFunctionalProperty;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityResultMessage;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.NotYetImplementedException;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLTranslationException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import com.hi3project.broccoli.bsdf.impl.owls.grounding.IOWLSDynamicGrounding;
import com.hi3project.broccoli.bsdf.impl.owl.OWLValueObject;
import com.hi3project.broccoli.bsdm.api.implementation.FunctionalityExecutionVO;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;
import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLModel;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.execution.DefaultProcessMonitor;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

/**
 * <p>
 Implementation of IServiceFunctionality for OWL-S using OWLS API.
 Currently it implements the "addValue" and "execute" methods that should be
 in a proper grounding implementation. </p><p>
 * It lacks a proper implementation for methods that belong to a
 * IServiceDescription, IProfile and IGrounding implementation because OWL-S
 * services are functionalities </p><p>
 * It uses a IOWLSDynamicGrounding object as a grounding provider. </p><p>
 * Any instance should be created from OWLSServiceBuilder </p>
 *
 * 
 */
public class OWLSAtomicService implements IServiceFunctionality, IExecutableWithInputsFunctionality, IServiceImplementation, IFunctionalityImplementation
{

    private Service service = null;
    private IOWLSDynamicGrounding grounding = null;
    private Map<IInput, IInputValue> inputs = null;
    private Collection<IOutput> outputs = null;
    private IServiceDescription serviceDescription = null;
    private IMatchmaker matchmaker = null;
    private IFunctionalityGrounding functionalityGrounding;
    

    public OWLSAtomicService(Service service)
    {
        setService(service);
    }

    public final void setService(Service service)
    {
        this.service = service;
    }

    public Service getService()
    {
        return service;
    }

    public synchronized void setGrounding(IOWLSDynamicGrounding grounding) throws ModelException
    {
        this.grounding = grounding;
        grounding.addToService(service);
    }

    public synchronized IOWLSDynamicGrounding getGrounding()
    {
        return grounding;
    }

    public boolean hasGrounding()
    {
        return !getService().getGroundings().isEmpty();
    }

    public AtomicProcess getAtomicProcess()
    {
        return service.getProcess().castTo(AtomicProcess.class);
    }

    public boolean hasProcess()
    {
        return (null != getService().getProcess());
    }

    public OWLModel getModel()
    {
        return service.getOntology();
    }

    public IMatchmaker getMatchmaker()
    {
        return matchmaker;
    }

    public void setMatchmaker(IMatchmaker matchmaker)
    {
        this.matchmaker = matchmaker;
    }

    /**
     * *************************************************************************************************************************
     */
    /**
     * Executes the handled OWL-S service, given a list of inputs as IParameter
     *
     * @param inputs
     * @return
     * @throws ModelException
     */
    @Override
    public synchronized Collection<IResult> executeWithInputs(Collection<IInputValue> inputs) throws ModelException
    {
        if (!hasGrounding())
        {
            return null;
        }
        ValueMap<Output, OWLValue> returnedOutputs;
        try
        {
            returnedOutputs = executionEngine().execute(
                    service.getProcess(),
                    fromParameterInputs(service.getProcess(), inputs),
                    service.getKB());
        } catch (ExecutionException ex)
        {
            throw new ServiceExecutionException("Problem executing service with inputs: " + inputs.toString(), ex);
        }

        return valuesToParameters(service.getProcess(), returnedOutputs);
    }

    @Override
    public Collection<IResult> executeWithValuesSyncronously(Collection<IAnnotatedValue> values) throws ModelException
    {
        return executeWithInputs(annotatedValuesToInputValues(values));
    }
    
    
    public void writeTo(Writer writer) throws ModelException
    {
        Grounding tempGrounding = service.getGrounding();
        service.removeGrounding(tempGrounding);
        service.getKB().write(writer, service.getURI());
        service.addGrounding(tempGrounding);
    }

    public IEffect addEffect(String inputName, Object concept) throws ModelException
    {
        OWLSEffect effect = new OWLSEffect(getModel(), inputName, concept);
        if (!addEffect(effect))
        {
            return null;
        }
        return effect;
    }

    private synchronized boolean addEffect(IEffect effect) throws ModelException
    {
        if (effect instanceof OWLSEffect)
        {
            Result result = service.getProcess().createResult(((OWLSEffect) effect).getOWLValueObject().uri());
            service.getProcess().addResult(result);
            effects().add(effect);
            return true;
        }
        return false;
    }

    @Override
    public synchronized Collection<IEffect> effects() throws ModelException
    {
        Collection<IEffect> effects = new ArrayList<IEffect>();
        OWLIndividualList<Result> results = service.getProcess().getResults();
        Iterator<Result> iter = results.iterator();
        while (iter.hasNext())
        {
            Result result = iter.next();
            effects.add(new OWLSEffect(service.getProcess().getOntology(), result));
        }
        return effects;
    }
    /**
     * *************************************************************************************************************************
     */
    private static ProcessExecutionEngine executionEngine = null;

    private static ProcessExecutionEngine executionEngine()
    {
        if (null == executionEngine)
        {
            executionEngine = OWLSFactory.createExecutionEngine();
            executionEngine.addMonitor(new DefaultProcessMonitor());
        }
        return executionEngine;
    }

    /**
     * Translates a list of IParameter to a collection of Input,OWLValue
     *
     * @param process
     * @param parameters
     * @return
     */
    private static ValueMap<Input, OWLValue> fromParameterInputs(Process process, Collection<IInputValue> parameters) throws ModelException
    {
        if (parameters.isEmpty())
        {
            return null;
        }
        ValueMap<Input, OWLValue> valueMap = new ValueMap<Input, OWLValue>();
        for (IInputValue parameter : parameters)
        {
            valueMap.setValue(
                    process.getInput(parameter.getInput().name()),
                    process.getKB().createDataValue(parameter.object()));
        }
        return valueMap;
    }

    /**
     * Translates a collection of Output,OWLvalue to a list of IParameter
     *
     * @param process
     * @param values
     * @return
     * @throws NotYetImplementedException
     * @throws OWLTranslationException
     */
    private synchronized Collection<IResult> valuesToParameters(Process process, ValueMap<Output, OWLValue> values) throws NotYetImplementedException, OWLTranslationException
    {
        if (values.isEmpty())
        {
            return null;
        }
        Collection<IResult> parameters = new ArrayList<IResult>();
        Iterator<Entry<Output, OWLValue>> valuesIterator = values.iterator();
        while (valuesIterator.hasNext())
        {
            Entry<Output, OWLValue> valuesEntry = valuesIterator.next();
            final OWLSParameter parameter
                    = new OWLSParameter(
                            process.getOntology(),
                            valuesEntry.getKey().getName(),
                            valuesEntry.getValue());
            parameters.add(new IOutputValue()
            {
                @Override
                public IOutput getOutput()
                {
                    try
                    {
                        return output(parameter.name());
                    } catch (ModelException ex)
                    {
                        return null;
                    }
                }

                @Override
                public Object object() throws ModelException
                {
                    return parameter.object();
                }

                @Override
                public ISemanticIdentifier getSemanticAnnotation()
                {
                    return parameter.getSemanticAnnotation();
                }

                @Override
                public ISubject value() throws ModelException
                {
                    if (null != functionalityGrounding)
                    {
                        return functionalityGrounding.createOutput(parameter.object(), OWLSAtomicService.this).value();
                    }
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
        }
        return parameters;
    }

    /**
     * *************************************************************************************************************************
     */
    private synchronized Collection<IInputValue> annotatedValuesToInputValues(Collection<IAnnotatedValue> values) throws ModelException
    {
        restartInputsMap();
        Collection<IInputValue> inputValues = new HashSet<IInputValue>();
        for (IAnnotatedValue annotatedValue : values)
        {
            IInput input = input(annotatedValue);
            if (null == input)
            {
                throw new ServiceExecutionException("Cannot find a suitable input for value: " + annotatedValue, null);
            }
            inputValues.add(addInputValue(input, annotatedValue));
        }
        return inputValues;
    }
 
    public IAnnotatedValue addAnnotatedValue(Object value) throws ModelException
    {
        return OWLValueObject.buildFromObject(getModel(), value);
    }

    public IAnnotatedValue addAnnotatedValue(Collection value) throws ModelException
    {
        return OWLValueObject.buildFromCollection(getModel(), value);
    }

    /**
     * *************************************************************************************************************************
     */
    
    
    @Override
    public IInputValue addInputValue(final String inputName, final Object value) throws ModelException
    {
        return addInputValue(input(inputName), value);
    }

    @Override
    public IInputValue addInputValue(final String inputName, final Collection value) throws ModelException
    {
        return addInputValue(input(inputName), value);
    }

    @Override
    public IInputValue addInputValue(final IInput input, Object value) throws ModelException
    {
        final OWLSParameter owlsParameter = new OWLSParameter(getModel(), input.name(), value);
        return addInputValue(input, owlsParameter);
    }

    @Override
    public IInputValue addInputValue(final IInput input, Collection value) throws ModelException
    {
        final OWLSParameter owlsParameter = new OWLSParameter(getModel(), input.name(), value);
        return addInputValue(input, owlsParameter);
    }

    private IInputValue addInputValue(final IInput input, IAnnotatedValue annotatedValue) throws ModelException
    {
        if (annotatedValue instanceof OWLValueObject)
        {
            final OWLSParameter owlsParameter = new OWLSParameter(getModel(), input.name(), (OWLValueObject) annotatedValue);
            return addInputValue(input, owlsParameter);
        } else
        {
            return addInputValue(input, annotatedValue.object());
        }
    }

    private synchronized IInputValue addInputValue(final IInput input, final OWLSParameter owlsParameter) throws ModelException
    {
        IInputValue inputValue = inputValueFrom(input, owlsParameter);
        inputsMap().put(input, inputValue);
        return inputValue;
    }

    @Override
    public Collection<IInput> inputs() throws ModelException
    {
        return inputsMap().keySet();
    }

    public IInput input(String inputName) throws ModelException
    {
        for (IInput input : inputs())
        {
            if (input.name().equals(inputName))
            {
                return input;
            }
        }
        return null;
    }

    public IInput input(IAnnotatedValue annotatedValue) throws ModelException
    {
        for (IInput input : inputs())
        {
            if (null == getMatchmaker())
            {
                if (null == inputsMap().get(input) && input.getSemanticAnnotation().getURI().equals(annotatedValue.getSemanticAnnotation().getURI()))
                {
                    return input;
                }
            } else
            {
                if (getMatchmaker().same(input.getSemanticAnnotation(), annotatedValue.getSemanticAnnotation())
                        || -1 < getMatchmaker().subsumes(input.getSemanticAnnotation(), annotatedValue.getSemanticAnnotation())
                        || -1 < getMatchmaker().subsumes(annotatedValue.getSemanticAnnotation(), input.getSemanticAnnotation()))
                {
                    return input;
                }
            }
        }
        return null;
    }

    private IInputValue inputValueFrom(final IInput input, final OWLSParameter owlsParameter)
    {
        return new IInputValue()
        {
            @Override
            public IInput getInput()
            {
                return input;
            }

            @Override
            public Object object() throws ModelException
            {
                return owlsParameter.object();
            }

            @Override
            public ISemanticIdentifier getSemanticAnnotation()
            {
                return owlsParameter.getSemanticAnnotation();
            }

            @Override
            public ISubject value() throws ModelException
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    private Map<IInput, IInputValue> inputsMap() throws ModelException
    {
        if (null == inputs)
        {
            return restartInputsMap();
        }
        return inputs;
    }

    private synchronized Map<IInput, IInputValue> restartInputsMap() throws ModelException
    {
        inputs = new HashMap<IInput, IInputValue>();
        Iterator<Input> processInputs = service.getProcess().getInputs().iterator();
        while (processInputs.hasNext())
        {
            inputs.put((new OWLSInput(processInputs.next())), null);
        }
        return inputs;
    }

    /**
     * *************************************************************************************************************************
     */
    
    
    @Override
    public synchronized Collection<IOutput> outputs()
    {
        if (null == outputs)
        {
            outputs = new ArrayList<IOutput>();
            final Iterator<Output> processInputs = service.getProcess().getOutputs().iterator();
            while (processInputs.hasNext())
            {
                outputs.add(new IOutput()
                {
                    private Output owlOutput = processInputs.next();

                    @Override
                    public String name()
                    {
                        return owlOutput.getName();
                    }

                    @Override
                    public ISemanticIdentifier getSemanticAnnotation()
                    {
                        //return new SemanticIdentifier(owlOutput.getType().getURI());
                        return new SemanticIdentifier(owlOutput.getParamType().getURI());
                    }
                });
            }
        }

        return outputs;
    }

    public IOutput output(String outputName) throws ModelException
    {
        for (IOutput output : outputs())
        {
            if (output.name().equals(outputName))
            {
                return output;
            }
        }
        return null;
    }

    /**
     * *************************************************************************************************************************
     */
    
    
    @Override
    public IFunctionalityCategory category()
    {
        return null;
    }

    @Override
    public Collection<INonFunctionalProperty> nonFunctionalProperties()
    {
        return new ArrayList<INonFunctionalProperty>();
    }

    @Override
    public synchronized void setFunctionalityGrounding(IFunctionalityGrounding functionalityGrounding) throws ModelException
    {
        if (functionalityGrounding instanceof IOWLSDynamicGrounding)
        {
            setGrounding((IOWLSDynamicGrounding) functionalityGrounding);
        } else
        {
            this.functionalityGrounding = functionalityGrounding;
        }
    }

    @Override
    public synchronized Collection<IFunctionalityGrounding> getFunctionalityGroundings()
    {
        Collection<IFunctionalityGrounding> groundings = new ArrayList<IFunctionalityGrounding>();
        if (null != getGrounding())
        {
            groundings.add(getGrounding());
        }
        return groundings;
    }

    @Override
    public String name()
    {
        return this.service.getLocalName();
    }

    @Override
    public IServiceDescription getServiceDescription()
    {
        return serviceDescription;
    }

    public void setServiceDescription(IServiceDescription serviceDescription)
    {
        this.serviceDescription = serviceDescription;
    }

    @Override
    public void executeWithValues(Collection<IAnnotatedValue> values, IAsyncMessageClient client) throws ModelException
    {
        Collection<IResult> syncronousResults = this.executeWithValuesSyncronously(values);
        FunctionalityResultMessage resultMessage = new FunctionalityResultMessage(syncronousResults, client.getName());
        client.receiveMessage(resultMessage);
    } 

    @Override
    public Collection<IResult> executeWithValues(
            String functionalityName,
            Collection<IAnnotatedValue> values,
            FunctionalityExecutionVO executionVO) throws ModelException
    {
        if (! this.functionalityGrounding.getAdvertisedFunctionality().name().equalsIgnoreCase(functionalityName))
            throw new ServiceExecutionException("The invoked functionality [" + functionalityName + "] is not managed here");
        return this.executeWithValuesSyncronously(values);
    }

    @Override
    public Collection<IFunctionalityImplementation> getFunctionalityImplementations()
    {
        Collection<IFunctionalityImplementation> funcImpls = new ArrayList<IFunctionalityImplementation>();
        funcImpls.add(this);
        return funcImpls;
    }

    @Override
    public IFunctionalityImplementation getFunctionalityImplementation(String name)
    {
        return this;
    }

    @Override
    public IServiceImplementation getServiceImplementation()
    {
        return this;
    }

    @Override
    public Collection<IResult> executeWithValues(Collection<IAnnotatedValue> values, FunctionalityExecutionVO executionVO) throws ModelException
    {
        return this.executeWithValuesSyncronously(values);
    }      

    @Override
    public void setServiceImplementation(IServiceImplementation serviceImplementation)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IOutputValue createOutput(Object result, IServiceFunctionality functionality) throws SemanticModelException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IServiceFunctionality getAdvertisedFunctionality()
    {
        return this;
    }

    @Override
    public Object getImplementationOf(Class clas)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getFunctionalityImplementationObject()
    {
        return this;
    }

    @Override
    public Object loadFunctionalityImplementationObject() throws ServiceExecutionException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IFunctionalityImplementation getFunctionalityImplementation()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISemanticIdentifier getIdentifier()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void subscribeTo(IAsyncMessageClient client) throws ModelException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
   
}
