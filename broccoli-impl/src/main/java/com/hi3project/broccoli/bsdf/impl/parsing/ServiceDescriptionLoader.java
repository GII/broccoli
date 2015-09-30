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

package com.hi3project.broccoli.bsdf.impl.parsing;

import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.parsing.IParameterConverter;
import com.hi3project.broccoli.bsdm.api.parsing.IServiceDescriptionLoader;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IRequestedFunctionality;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdf.impl.implementation.OntologyToJavaReference;
import com.hi3project.broccoli.bsdf.impl.serializing.JSONMessageSerializer;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentParser;
import com.hi3project.broccoli.bsdl.api.parsing.PreParsingRule;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.DocumentPreParser;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGroundingFactory;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGroundingFactory;
import com.hi3project.broccoli.bsdm.api.serializing.IMessageSerializer;
import com.hi3project.broccoli.bsdm.impl.ServiceDescription;
import com.hi3project.broccoli.bsdm.impl.parsing.BSDLBSDMLoader;
import com.hi3project.broccoli.bsdm.impl.profile.functionality.RequestedFunctionality;
import com.hi3project.broccoli.io.BSDFLogger;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * <b>Description:</b></p>
 * <p>
 * Implementation of IServiceDescriptionLoader. So it can read service and
 * requested funcitonality descriptors to load them into a BSDLRegistry.</p>
 * <p>
 * Depends on a BSDLBSDMLoader and uses an JenaBeanConverter as
 * IParameterConverter for the BSDLRegistry.
 * </p>
 *
 * 
 */
public class ServiceDescriptionLoader implements IServiceDescriptionLoader
{

    protected static final String requestedFunctionalityConcept = "http://hi3project.com/broccoli/bsdm/profile#requestedFunctionality";
    protected static final String clientOntologyToJavaReferenceConcept = "http://hi3project.com/broccoli/bsdm/implementation#ontologyToJavaReference";
    protected static final String serviceDescriptionConcept = "http://hi3project.com/broccoli/bsdm#serviceDescription";

    private BSDLBSDMLoader bsdlLoader;
    private IBSDLRegistry bsdlRegistry;
    private JenaBeanConverter jenabeanConverter;
    private IServiceGroundingFactory groundingFactory;
    private IFunctionalityGroundingFactory funcGroundingFactory;
    private IMessageSerializer messageSerializer;

    public ServiceDescriptionLoader(
            IServiceGroundingFactory groundingFactory,
            IFunctionalityGroundingFactory funcGroundingFactory,
            IBSDLRegistry bsdlRegistry,
            IMessageSerializer messageSerializer,
            Collection<PreParsingRule> preParsingRules)
    {
        BSDFLogger.getLogger().info("Instances a ServiceDescriptionLoader");
        this.bsdlRegistry = bsdlRegistry;
        this.bsdlLoader = new BSDLBSDMLoader(bsdlRegistry, preParsingRules);
        this.jenabeanConverter = new JenaBeanConverter(bsdlRegistry);
        this.groundingFactory = groundingFactory;
        this.funcGroundingFactory = funcGroundingFactory;
        this.messageSerializer = messageSerializer;
    }
    
    public ServiceDescriptionLoader(
            IServiceGroundingFactory groundingFactory,
            IFunctionalityGroundingFactory funcGroundingFactory,
            IBSDLRegistry bsdlRegistry,
            IMessageSerializer messageSerializer)
    {
        this(groundingFactory, funcGroundingFactory, bsdlRegistry, messageSerializer, new ArrayList<PreParsingRule>());
    }

    public ServiceDescriptionLoader(
            IServiceGroundingFactory groundingFactory,
            IFunctionalityGroundingFactory funcGroundingFactory,
            IMessageSerializer messageSerializer) throws ModelException
    {
        this(groundingFactory, funcGroundingFactory, new BSDLRegistry(new BSDLBSDMLoader()), messageSerializer, new ArrayList<PreParsingRule>());
    }

    @Override
    public void setBSDLRegistry(IBSDLRegistry bsdlRegistry)
    {
        if (bsdlRegistry instanceof BSDLRegistry)
        {
            this.bsdlRegistry = (BSDLRegistry) bsdlRegistry;
            bsdlLoader.setBSDLRegistry((BSDLRegistry) bsdlRegistry);
        }
    }

    @Override
    public IBSDLRegistry getBSDLRegistry()
    {
        return this.bsdlRegistry;
    }

    @Override
    public IServiceDescriptionLoader addOntology(ISemanticLocator locator) throws ModelException
    {
        bsdlRegistry.addOntology(locator);
        return this;
    }

    @Override
    public IParameterConverter getParameterConverter()
    {
        return this.jenabeanConverter;
    }

    @Override
    public Collection<IServiceDescription> readServicesFrom(Collection<ISemanticLocator> locators) throws ModelException
    {
        Collection<Instance> readInstances;
        readInstances = bsdlLoader.readInstancesFromLocationOfConcept(locators, new SemanticIdentifier(serviceDescriptionConcept));
        Collection<IServiceDescription> services = new ArrayList<IServiceDescription>();
        for (Instance instance : readInstances)
        {

            ServiceDescription service = createServiceDescription(instance);
            services.add(service);

        }
        return services;
    }

    @Override
    public Collection<IServiceDescription> readServicesFrom(ISemanticLocator locator) throws ModelException
    {
        Collection<ISemanticLocator> locators = new ArrayList<ISemanticLocator>();
        locators.add(locator);
        return readServicesFrom(locators);
    }

    @Override
    public Collection<IServiceDescription> readServices(Collection<String> serviceDescriptors) throws ModelException
    {
        Collection<Instance> readInstances = bsdlLoader.readInstancesFromStringOfConcept(serviceDescriptors, new SemanticIdentifier(serviceDescriptionConcept));
        Collection<IServiceDescription> services = new ArrayList<IServiceDescription>();
        for (Instance instance : readInstances)
        {
            services.add(new ServiceDescription(messageSerializer, groundingFactory, funcGroundingFactory, bsdlRegistry, instance));
        }
        return services;
    }

    @Override
    public IServiceDescriptionLoader addOntologyReferences(ISemanticLocator locator) throws SemanticModelException, ModelException
    {
        Collection<ISemanticLocator> locators = new ArrayList<ISemanticLocator>();
        locators.add(locator);
        Collection<Instance> readInstances = bsdlLoader.readInstancesFromLocationOfConcept(locators, new SemanticIdentifier(clientOntologyToJavaReferenceConcept));
        for (Instance instance : readInstances)
        {
            this.addOntologyReferences(new OntologyToJavaReference(instance));
        }
        return this;
    }

    public void addOntologyReferences(OntologyToJavaReference ontologyReference)
    {
        jenabeanConverter.addOntologyToJavaReference(ontologyReference);
    }

    @Override
    public Collection<IRequestedFunctionality> readFunctionalitiesFrom(Collection<ISemanticLocator> locators) throws ModelException
    {
        Collection<Instance> readInstances = bsdlLoader.readInstancesFromLocationOfConcept(locators, new SemanticIdentifier(requestedFunctionalityConcept));
        Collection<IRequestedFunctionality> functionalities = new ArrayList<IRequestedFunctionality>();
        for (Instance instance : readInstances)
        {
            functionalities.add(new RequestedFunctionality(bsdlRegistry, instance));
        }
        return functionalities;
    }

    @Override
    public Collection<IRequestedFunctionality> readFunctionalitiesFrom(ISemanticLocator locator) throws ModelException
    {
        Collection<ISemanticLocator> locators = new ArrayList<ISemanticLocator>();
        locators.add(locator);
        return readFunctionalitiesFrom(locators);
    }

    @Override
    public Collection<IRequestedFunctionality> readFunctionalities(Collection<String> contents) throws ModelException
    {
        Collection<Instance> readInstances = bsdlLoader.readInstancesFromStringOfConcept(contents, new SemanticIdentifier(requestedFunctionalityConcept));
        Collection<IRequestedFunctionality> functionalities = new ArrayList<IRequestedFunctionality>();
        for (Instance instance : readInstances)
        {
            functionalities.add(new RequestedFunctionality(bsdlRegistry, instance));
        }
        return functionalities;
    }

    private ServiceDescription createServiceDescription(Instance instance) throws ModelException
    {
        return new ServiceDescription(messageSerializer, groundingFactory, funcGroundingFactory, bsdlRegistry, instance);
    }

}
