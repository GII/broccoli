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

package com.hi3project.broccoli.bsdm.impl;

import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGrounding;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.IServiceProfile;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.BSDLJavaServiceImplementation;
import com.hi3project.broccoli.bsdm.impl.profile.ServiceProfile;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGroundingFactory;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGroundingFactory;
import com.hi3project.broccoli.bsdm.api.serializing.IMessageSerializer;
import com.hi3project.broccoli.bsdm.impl.grounding.AsyncMessageServiceGrounding;
import com.hi3project.broccoli.io.BSDFLogger;
import java.util.ArrayList;
import java.util.Collection;


/**
 *
 * 
 */
public class ServiceDescription extends ConceptBehaviourImplementation implements IServiceDescription
{

    private ServiceProfile profile = null;
    private Collection<IServiceGrounding> groundings = new ArrayList<IServiceGrounding>();
    private Collection<IServiceImplementation> implementations = new ArrayList<IServiceImplementation>();
    IBSDLRegistry bsdlRegistry = null;

    public ServiceDescription(
            IMessageSerializer messageSerializer,
            IServiceGroundingFactory groundingFactory,
            IFunctionalityGroundingFactory funcGroundingFactory,
            IBSDLRegistry bsdlRegistry,
            Instance instance) throws ModelException
    {
        this.bsdlRegistry = bsdlRegistry;
        setInstance(instance);
        profile = new ServiceProfile(getSinglePropertyAsInstance("profile"), this);
        for (IObject iobject : getProperties("grounding"))
        {
            if (iobject instanceof Instance)
            {
                Instance groundingInstance = (Instance) iobject;
                
                IServiceGrounding serviceGrounding = 
                    groundingFactory.instanceFor(
                        funcGroundingFactory,
                        groundingInstance.getConcept().semanticAxiom().getSemanticIdentifier().toString(),
                        groundingInstance, 
                        this,
                        messageSerializer);
                
                if (null != serviceGrounding) groundings.add(serviceGrounding);
            }
        }
        for (IObject iobject : getProperties("implementation"))
        {
            if (iobject instanceof Instance)
            {
                implementations.add(new BSDLJavaServiceImplementation((Instance) iobject, this));
            }
        }
    }

    @Override
    public IBSDLRegistry getBSDLRegistry()
    {
        return bsdlRegistry;
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return sconceptIdentifier();
    }

    public static SemanticIdentifier sconceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm#serviceDescription");
    }

    @Override
    public Collection<IServiceGrounding> getGroundings()
    {
        return groundings;
    }

    @Override
    public IServiceProfile getProfile()
    {
        return profile;
    }

    @Override
    public String name()
    {
        if (null != getProfile())
        {
            return getProfile().name();
        }
        return null;
    }

    @Override
    public Collection<IResult> executeSingleResultFunctionalitySyncronously(String advertisedFunctionalityName, Collection<IAnnotatedValue> values) throws ModelException
    {
        BSDFLogger.getLogger().info("Executes functionality: " + advertisedFunctionalityName);
        IServiceFunctionality functionality = this.getFunctionality(advertisedFunctionalityName);
        return functionality == null ? null : functionality.executeWithValuesSyncronously(values);
    }

    @Override
    public IServiceFunctionality getFunctionality(String functionalityName) throws ModelException
    {
        if (null != getProfile())
        {
            if (null != getProfile().advertisedFunctionalities())
            {
                for (IServiceFunctionality advertisedFunctionality : getProfile().advertisedFunctionalities())
                {
                    if (functionalityName.equals(advertisedFunctionality.name()))
                    {
                        return advertisedFunctionality;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Collection<IServiceImplementation> getImplementations()
    {
        return this.implementations;
    }
    
    @Override
    public void disposeImplementations()
    {
        implementations = new ArrayList<IServiceImplementation>();
    }

    @Override
    public ISemanticIdentifier getIdentifier()
    {
        try
        {
            if (getInstance() instanceof Instance)
            {
                return ((Instance) getInstance()).getSemanticIdentifier();
            }
            return null;
        } catch (SemanticModelException ex)
        {
            return null;
        }
    }
                

    @Override
    public void executeMultipleResultFunctionalityAsyncronously(String advertisedFunctionalityName, Collection<IAnnotatedValue> values, IAsyncMessageClient client) throws ModelException
    {
        BSDFLogger.getLogger().info("Executes functionality: " + advertisedFunctionalityName);
        IServiceFunctionality functionality = this.getFunctionality(advertisedFunctionalityName);
        if (null != functionality) functionality.executeWithValues(values, client);
    }

    @Override
    public void executeSingleResultFunctionalityAsyncronously(String advertisedFunctionalityName, Collection<IAnnotatedValue> values, IAsyncMessageClient client) throws ModelException
    {
        this.executeMultipleResultFunctionalityAsyncronously(advertisedFunctionalityName, values, client);
    }

    @Override
    public void subscribeTo(String advertisedSubscriptionName, IAsyncMessageClient client) throws ModelException
    {
        BSDFLogger.getLogger().info("Launches subscription: " + advertisedSubscriptionName);
        IServiceFunctionality functionality = this.getFunctionality(advertisedSubscriptionName);
        if (null != functionality) functionality.subscribeTo(client);
    }
    
    
    @Override
    public ServiceDescription applyConnectorURL(String connectorURL) throws ModelException
    {
        
        Collection<IServiceGrounding> modifiedGroundings = new ArrayList<IServiceGrounding>();
        
        for (IServiceGrounding grounding : this.getGroundings())
        {
            
           if (grounding instanceof AsyncMessageServiceGrounding) 
           {
               AsyncMessageServiceGrounding amsGrounding = (AsyncMessageServiceGrounding) grounding;
               modifiedGroundings.add(amsGrounding.setConnectorURLforNewGrounding(connectorURL));
               
           } else
           {
               modifiedGroundings.add(grounding);
           }
           
        }
        
        this.groundings = modifiedGroundings;
        
        return this;
        
    }

    @Override
    public void activateGroundings() throws ModelException
    {
        for (IServiceGrounding serviceGrounding : this.getGroundings())
        {
            serviceGrounding.activate();
        }
    }
    
    
}
