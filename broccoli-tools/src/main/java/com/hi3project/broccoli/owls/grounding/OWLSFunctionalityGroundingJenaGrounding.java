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

package com.hi3project.broccoli.owls.grounding;


import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.BSDLJavaServiceFunctionalityBSDLRealization;
import com.hi3project.broccoli.bsdf.impl.owls.profile.functionality.OWLSAtomicService;
import com.hi3project.broccoli.bsdf.impl.parsing.JenaBeanConverter;
import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGrounding;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityResultMessage;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGrounding;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * 
 */
public class OWLSFunctionalityGroundingJenaGrounding implements IFunctionalityGrounding
{

    JenaBeanConverter jenaBeanConverter = null;
    private OWLSAtomicService owlsAtomicService;

    public OWLSFunctionalityGroundingJenaGrounding(JenaBeanConverter jenaBeanConverter, OWLSAtomicService owlsAtomicService) throws ModelException
    {
        this.jenaBeanConverter = jenaBeanConverter;
        this.owlsAtomicService = owlsAtomicService;
        owlsAtomicService.setFunctionalityGrounding(this);
    }

    @Override
    public IServiceFunctionality getAdvertisedFunctionality()
    {
        return owlsAtomicService;
    }

    @Override
    public void setAdvertisedFunctionality(IServiceFunctionality advertisedFunctionality) throws ModelException
    {
        if (advertisedFunctionality instanceof OWLSAtomicService)
        {
            this.owlsAtomicService = (OWLSAtomicService) advertisedFunctionality;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<IResult> executeWithValuesSyncronously(Collection<IAnnotatedValue> values) throws ModelException
    {
        // transform values using jenaBeanConverter
        List<IAnnotatedValue> executionValues = new ArrayList();
        for (IAnnotatedValue value : values)
        {
            Object executionValue = BSDLJavaServiceFunctionalityBSDLRealization.createValue(value, jenaBeanConverter);
            executionValues.add(this.owlsAtomicService.addAnnotatedValue(executionValue));
        }
        // call owlsAtomicService execution
        return this.owlsAtomicService.executeWithValuesSyncronously(executionValues);
    }

    
    public void writeTo(Writer writer) throws ModelException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IOutputValue createOutput(Object result, IServiceFunctionality functionality) throws SemanticModelException
    {
        return BSDLJavaServiceFunctionalityBSDLRealization.createOutput(result, jenaBeanConverter, functionality);
    }

    @Override
    public void registerResult(String requesterID, String conversationID, FunctionalityResultMessage resultMsg)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String name()
    {
        return this.owlsAtomicService.getGrounding().name();
    }

    @Override
    public void executeWithValues(Collection<IAnnotatedValue> values, IAsyncMessageClient client) throws ModelException
    {
        Collection<IResult> syncronousResults = this.executeWithValuesSyncronously(values);
        FunctionalityResultMessage resultMessage = 
                new FunctionalityResultMessage(
                        syncronousResults, 
                        this.getAdvertisedFunctionality().name(),
                        client.getName(),
                        null);
        client.receiveMessage(resultMessage);
    }

    @Override
    public IServiceImplementation getServiceImplementation()
    {
        return this.owlsAtomicService;
    }

    @Override
    public void setServiceGrounding(IServiceGrounding serviceGrounding)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void subscribeTo(IAsyncMessageClient client) throws ModelException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerNotification(FunctionalityResultMessage resultMsg) throws ServiceExecutionException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

}
