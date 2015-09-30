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

package com.hi3project.broccoli.bsdm.impl.grounding;

import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.BSDLJavaServiceImplementation;
import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.asyncronous.IChannelProducer;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGrounding;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGrounding;
import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdm.impl.asyncronous.ChannelManager;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGroundingFactory;
import com.hi3project.broccoli.bsdm.api.serializing.IMessageSerializer;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceGroundingException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 *  <b>Description:</b></p>
 * service grounding abstract implementation that handles a set of 
 * functionality groundings and defers the creation of messages to subclasses.
 *
 *
 * <p>
 *  Colaborations:
 *
 * <ul>
 * <li>a channelProducersHelper that is shared among its functionality groundings.</li>
 * </ul>
 * 
 * <p>
 *  Responsabilities:
 *
 * <ul>
 * <li>knows how to access a specific functionality grounding.</li>
 * <li>holds reference to an IChannelProducer that will be used to send result 
 * and notification messages.</li>
 * </ul>
 * 
 * <p>
 * 
 *
 *
 * 
 */
public abstract class AbstractServiceGrounding extends ConceptBehaviourImplementation implements IServiceGrounding
{

    private Collection<IFunctionalityGrounding> functionalityGroundings = new ArrayList<IFunctionalityGrounding>();
    private String ontologyLanguage = null;
    private ChannelManager channelProducersHelper = null;    
    private ISemanticIdentifier identifier = null;
    protected IServiceDescription service = null;
    protected IMessageSerializer messageSerializer;
    
    public AbstractServiceGrounding(
            IFunctionalityGroundingFactory funcGroundingFactory,
            IMessageSerializer messageSerializer,
            Instance instance,
            IServiceDescription serviceDescription) throws ModelException
    {
        setInstance(instance);
        this.identifier = instance.getSemanticIdentifier();
        this.service = serviceDescription;
        this.messageSerializer = messageSerializer;
        ontologyLanguage = getSinglePropertyAsInstanceLiteralValue("ontologyLanguage");
        this.channelProducersHelper = new ChannelManager();
        this.channelProducersHelper.startProcessing();
        for (IObject iobject : getProperties("functionalityGrounding"))
        {
            if (iobject instanceof Instance)
            {
                
                Instance functionalityGroundingInstance = (Instance) iobject;
                
                IFunctionalityGrounding functionalityGrounding = 
                        funcGroundingFactory.instanceFor(
                                functionalityGroundingInstance.getConcept().semanticAxiom().getSemanticIdentifier().toString(),
                                functionalityGroundingInstance, 
                                this, 
                                messageSerializer,
                                channelProducersHelper);
                
                if (null != functionalityGrounding && functionalityGrounding instanceof AbstractFunctionalityGrounding)
                {
                    functionalityGroundings.add(functionalityGrounding);
                    functionalityGrounding.setAdvertisedFunctionality(
                            BSDLJavaServiceImplementation.getFunctionality(((AbstractFunctionalityGrounding)functionalityGrounding).getSinglePropertyAsInstance("advertisedFunctionality").getSemanticIdentifier(), service.getProfile().advertisedFunctionalities()));
                }
            }
        }
    }
    
    @Override
    public void addFunctionalityGrounding(IFunctionalityGrounding functionalityGrounding)
    {
        this.functionalityGroundings.add(functionalityGrounding);
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm#serviceGrounding");
    }
    
    public ISemanticIdentifier instanceIdentifier()
    {
        return this.identifier;
    }

    @Override
    public Collection<IFunctionalityGrounding> functionalityGroundings()
    {
        return functionalityGroundings;
    }

    @Override
    public final String ontologyLanguage()
    {
        return ontologyLanguage;
    }
    
    public IServiceDescription getServiceDescription()
    {
        return this.service;
    }
    

    public IFunctionalityGrounding getFunctionalityGrounding(SemanticIdentifier functionalityGroundingIdentifier) throws SemanticModelException
    {
        for (IFunctionalityGrounding funcGrounding : functionalityGroundings())
        {
            if (((AbstractFunctionalityGrounding) funcGrounding).getInstance() instanceof Instance)
            {
                Instance funcGroundingInstance = (Instance) ((AbstractFunctionalityGrounding) funcGrounding).getInstance();
                if (null != funcGroundingInstance.getSemanticIdentifier() && funcGroundingInstance.getSemanticIdentifier().equals(functionalityGroundingIdentifier))
                {
                    return funcGrounding;
                }
            }
        }
        return null;
    }
    
    public AbstractFunctionalityGrounding getFunctionalityGrounding(String name) throws SemanticModelException
    {
        for (IFunctionalityGrounding funcGrounding : functionalityGroundings())
        {
            if (funcGrounding.name().equalsIgnoreCase(name))
            {
                return (AbstractFunctionalityGrounding)funcGrounding;
            }
        }
        return null;
    }
    
    abstract public IChannelProducer getControlChannelProducer() throws ServiceGroundingException;

    @Override
    public String toString()
    {
        return "AbstractServiceGrounding{" + "ontologyLanguage=" + ontologyLanguage + ", identifier=" + identifier + ", service=" + service + '}';
    }

}
