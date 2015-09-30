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

package com.hi3project.broccoli.owls;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGrounding;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.IServiceProfile;
import com.hi3project.broccoli.bsdm.api.profile.IServiceType;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdm.api.profile.nonFunctionalProperties.INonFunctionalProperty;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.io.BSDFLogger;
import com.hi3project.broccoli.bsdf.impl.owls.profile.functionality.OWLSAtomicService;
import com.hi3project.broccoli.bsdf.impl.parsing.JenaBeanConverter;
import com.hi3project.broccoli.owls.grounding.OWLSFunctionalityGroundingJenaGrounding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * 
 */
public class OWLSServiceDescription implements IServiceDescription, IServiceProfile
{

    private OWLSAtomicService owlsAtomicService = null;
    private OWLSFunctionalityGroundingJenaGrounding functionalityGrounding = null;

    public OWLSServiceDescription(OWLSAtomicService owlsAtomicService,
            OWLSFunctionalityGroundingJenaGrounding functionalityGrounding, IBSDLRegistry bsdlRegistry)
    {
        this.owlsAtomicService = owlsAtomicService;
        owlsAtomicService.setServiceDescription(this);
        this.functionalityGrounding = functionalityGrounding;
    }

    @Override
    public Collection<IServiceGrounding> getGroundings()
    {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public IServiceProfile getProfile()
    {
        return this;
    }

    @Override
    public String name()
    {
        if (null != owlsAtomicService)
        {
            return owlsAtomicService.name();
        }
        return null;
    }

    @Override
    public ISemanticIdentifier getIdentifier()
    {
        if (null != owlsAtomicService && null != owlsAtomicService.getService().getURI())
        {
            return new SemanticIdentifier(owlsAtomicService.getService().getURI());
        }
        return null;
    }

    @Override
    public Collection<IResult> executeSingleResultFunctionalitySyncronously(String advertisedFunctionalityName, Collection<IAnnotatedValue> values) throws ModelException
    {
        if (null != owlsAtomicService && null != owlsAtomicService.getService().getURI()
                && null != functionalityGrounding)
        {
            BSDFLogger.getLogger().info("Executes functionality: " + advertisedFunctionalityName);
            return functionalityGrounding.executeWithValuesSyncronously(values);
        }
        BSDFLogger.getLogger().info("Cannot execute functionality: " + advertisedFunctionalityName);
        return null;
    }

    @Override
    public Collection<IServiceFunctionality> advertisedFunctionalities()
    {
        ArrayList functionalities = new ArrayList();
        functionalities.add(this.owlsAtomicService);
        return functionalities;
    }

    @Override
    public Collection<INonFunctionalProperty> nonFuntionalProperties()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IServiceType getServiceType()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IServiceFunctionality getFunctionality(String functionalityName) throws ModelException
    {
        return this.owlsAtomicService;
    }

    @Override
    public Collection<IServiceImplementation> getImplementations()
    {
        return new ArrayList<IServiceImplementation>(Arrays.asList(this.owlsAtomicService));
    }

    @Override
    public void executeMultipleResultFunctionalityAsyncronously(String advertisedFunctionalityName, Collection<IAnnotatedValue> values, IAsyncMessageClient client) throws ModelException
    {
        if (null != owlsAtomicService && null != owlsAtomicService.getService().getURI()
                && null != functionalityGrounding)
        {
            BSDFLogger.getLogger().info("Executes functionality: " + advertisedFunctionalityName);
            functionalityGrounding.executeWithValues(values, client);
            return;
        }
        BSDFLogger.getLogger().info("Cannot execute functionality: " + advertisedFunctionalityName);
    }

    @Override
    public IBSDLRegistry getBSDLRegistry()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void executeSingleResultFunctionalityAsyncronously(String advertisedFunctionalityName, Collection<IAnnotatedValue> values, IAsyncMessageClient client) throws ModelException
    {
        this.executeMultipleResultFunctionalityAsyncronously(advertisedFunctionalityName, values, client);
    }

    @Override
    public void subscribeTo(String advertisedSubscriptionName, IAsyncMessageClient client) throws ModelException
    {
        this.executeMultipleResultFunctionalityAsyncronously(advertisedSubscriptionName, new ArrayList(), client);
    }

    @Override
    public void disposeImplementations()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IServiceDescription applyConnectorURL(String connectorURL)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void activateGroundings() throws ModelException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
