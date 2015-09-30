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

import com.hi3project.broccoli.bsdf.impl.owls.exceptions.DynamicGroundingBuildingException;
import com.hi3project.broccoli.bsdf.impl.owls.profile.functionality.OWLSAtomicService;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IExecutableWithInputsFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IEffect;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInputValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.implementation.FunctionalityExecutionVO;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAdvertisedFunctionality;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import com.hi3project.broccoli.owls.grounding.OWLSFromBSDMServiceBuilder;
import java.io.Writer;
import java.util.Collection;

/**
 * A Java Subgrounding for BSDL grounding, using OWLSAtomicService from
 * BSDL-OWLS implementation
 *
 * 
 */
public class BSDLJavaServiceFunctionalityOWLSRealization extends AbstractBSDLJavaFunctionalityImplementation implements IExecutableWithInputsFunctionality
{

    private OWLSAtomicService funcOWLS = null;

    public BSDLJavaServiceFunctionalityOWLSRealization(Instance instance) throws SemanticModelException
    {
        super(instance);
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/grounding#functionalityGroundingJena");
    }

    @Override
    public synchronized Collection<IResult> executeWithInputs(Collection<IInputValue> inputs) throws ModelException
    {
        return getOWLSGrounding().executeWithInputs(inputs);
    }

    public synchronized Collection<IResult> executeWithValuesSyncronously(Collection<IAnnotatedValue> values) throws ModelException
    {
        return getOWLSGrounding().executeWithValuesSyncronously(values);
    }

    @Override
    public synchronized IInputValue addInputValue(String inputName, Object value) throws ModelException
    {
        return getOWLSGrounding().addInputValue(inputName, value);
    }

    @Override
    public synchronized IInputValue addInputValue(String inputName, Collection value) throws ModelException
    {
        return getOWLSGrounding().addInputValue(inputName, value);
    }

    @Override
    public synchronized IInputValue addInputValue(IInput input, Object value) throws ModelException
    {
        return getOWLSGrounding().addInputValue(input, value);
    }

    @Override
    public synchronized IInputValue addInputValue(IInput input, Collection value) throws ModelException
    {
        return getOWLSGrounding().addInputValue(input, value);
    }

    public synchronized IAnnotatedValue addAnnotatedValue(Object value) throws ModelException
    {
        return getOWLSGrounding().addAnnotatedValue(value);
    }

    public synchronized IAnnotatedValue addAnnotatedValue(Collection value) throws ModelException
    {
        return getOWLSGrounding().addAnnotatedValue(value);
    }

    public synchronized IEffect addEffect(String inputName, Object concept) throws ModelException
    {
        return getOWLSGrounding().addEffect(inputName, concept);
    }

    public void writeTo(Writer writer) throws ModelException
    {
        getOWLSGrounding().writeTo(writer);
    }

    public synchronized OWLSAtomicService getOWLSGrounding() throws ModelException
    {
        if (null == funcOWLS)
        {

            try
            {
                if (getAdvertisedFunctionality() instanceof IAdvertisedFunctionality)
                {
                    funcOWLS = new OWLSFromBSDMServiceBuilder(
                            ((IServiceDescription) 
                                    getAdvertisedFunctionality().
                                            getServiceDescription()).
                                                getBSDLRegistry().
                                                    getLocatorsRegistry()).
                                buildOWLSServiceFromBSDOService((IAdvertisedFunctionality)getAdvertisedFunctionality());
                }

            } catch (ClassNotFoundException ex)
            {
                throw new DynamicGroundingBuildingException("Class not found", ex);
            }
        }
        return funcOWLS;
    }

    @Override
    public Collection<IResult> executeWithValues(Collection<IAnnotatedValue> values, FunctionalityExecutionVO executionVO) throws ModelException
    {
        return this.executeWithValuesSyncronously(values);
    }

    @Override
    public IOutputValue createOutput(Object result, IServiceFunctionality functionality) throws SemanticModelException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object loadFunctionalityImplementationObject() throws ServiceExecutionException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getFunctionalityImplementationObject()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString()
    {
        return "BSDLJavaServiceFunctionalityOWLSRealization{" + "funcOWLS=" + funcOWLS
                + super.toString()
                + '}';
    }

}
