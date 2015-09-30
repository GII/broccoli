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

package com.hi3project.broccoli.bsdf.impl.owls.grounding;


import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IEffect;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityResultMessage;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import com.hi3project.broccoli.bsdf.impl.owls.profile.functionality.OWLSAtomicService;
import com.hi3project.broccoli.bsdf.impl.owls.serviceBuilder.DynamicAtomicJavaGroundingFactory;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGrounding;
import java.io.Writer;
import java.util.Collection;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.service.Service;



/**
 *  Implementation of IOWLSDynamicGrounding. A "sub-grounding" for OWL-S atomic services that uses
 * a DynamicAtomicJavaGroundingFactory to perform the actual work
 * 
 * 
 */
public class DynamicJavaGrounding implements IOWLSDynamicGrounding {
    
    private OWLSAtomicService serviceFunctionality = null;
    private DynamicAtomicJavaGroundingFactory dynamicAtomicJavaGroundingFactory;
    
    public DynamicJavaGrounding(DynamicAtomicJavaGroundingFactory dynamicAtomicJavaGroundingFactory) {
       this.dynamicAtomicJavaGroundingFactory = dynamicAtomicJavaGroundingFactory;
    }
    
    @Override
    public Grounding addToService(Service service) throws ModelException {
         if (null != service.getGrounding()) {
            service.removeGrounding(null);
        }        
        return dynamicAtomicJavaGroundingFactory.generateOWLGrounding(service);
    }
   
    @Override
    public IServiceFunctionality getAdvertisedFunctionality() {
        return serviceFunctionality;
    }
    
    public OWLSAtomicService getAdvertisedFunctionalityAsOWLSAtomicService()  {
        if (getAdvertisedFunctionality() instanceof OWLSAtomicService) {
            return (OWLSAtomicService) getAdvertisedFunctionality();
        }
        return null;
    }
            
    
    @Override
    public void setAdvertisedFunctionality(IServiceFunctionality advertisedFunctionality) {
        if (advertisedFunctionality instanceof OWLSAtomicService) {
            serviceFunctionality = (OWLSAtomicService) advertisedFunctionality;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<IResult> executeWithValuesSyncronously(Collection<IAnnotatedValue> values) throws ModelException {
        return getAdvertisedFunctionalityAsOWLSAtomicService().executeWithValuesSyncronously(values);
    }
  
    public IAnnotatedValue addAnnotatedValue(Object value) throws ModelException {
        return getAdvertisedFunctionalityAsOWLSAtomicService().addAnnotatedValue(value);
    }

   
    public IAnnotatedValue addAnnotatedValue(Collection value) throws ModelException {
        return getAdvertisedFunctionalityAsOWLSAtomicService().addAnnotatedValue(value);
    }
   
    public IEffect addEffect(String inputName, Object concept) throws ModelException {
        return getAdvertisedFunctionalityAsOWLSAtomicService().addEffect(inputName, concept);
    }
    
    
    public void writeTo(Writer writer) throws ModelException {
        getAdvertisedFunctionalityAsOWLSAtomicService().writeTo(writer);
    }

    @Override
    public IOutputValue createOutput(Object result, IServiceFunctionality functionality) throws SemanticModelException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String name()
    {
        return this.serviceFunctionality.name();
    }

    @Override
    public void registerResult(String requesterID, String conversationID, FunctionalityResultMessage resultMsg) throws ServiceExecutionException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void executeWithValues(Collection<IAnnotatedValue> values, IAsyncMessageClient client) throws ModelException
    {
        Collection<IResult> syncronousResults = this.executeWithValuesSyncronously(values);
        FunctionalityResultMessage resultMessage = new FunctionalityResultMessage(syncronousResults, client.getName());
        client.receiveMessage(resultMessage);
    }

    @Override
    public IServiceImplementation getServiceImplementation()
    {
        throw new UnsupportedOperationException("Not supported yet.");
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
