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

import com.hi3project.broccoli.bsdf.impl.implementation.OntologyToJavaReference;
import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.AbstractBSDLJavaFunctionalityImplementation;
import com.hi3project.broccoli.bsdf.impl.owl.KBWithReasonerBuilder;
import com.hi3project.broccoli.bsdf.impl.owl.OWLContainer;
import com.hi3project.broccoli.bsdf.impl.owl.ObjectOWLSTranslator;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.DynamicGroundingBuildingException;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLTranslationException;
import com.hi3project.broccoli.bsdf.impl.owls.profile.functionality.OWLSAtomicService;
import com.hi3project.broccoli.bsdf.impl.owls.serviceBuilder.AbstractDynamicGroundingFactory;
import com.hi3project.broccoli.bsdf.impl.owls.serviceBuilder.AbstractOWLSServiceBuilder;
import com.hi3project.broccoli.bsdf.impl.owls.serviceBuilder.DynamicAtomicJavaGroundingFactory;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdl.api.registry.ILocatorsRegistry;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutput;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAdvertisedFunctionality;
import com.hi3project.broccoli.io.BSDFLogger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;

/**
 * Implementation of AbstractOWLSServiceBuilder that can build an
 * OWLSAtomicService from a given BSDL AdvertisedFunctionality
 *
 * 
 */
public class OWLSFromBSDMServiceBuilder extends AbstractOWLSServiceBuilder
{

    ILocatorsRegistry locatorsRegistry = null;

    public OWLSFromBSDMServiceBuilder(ILocatorsRegistry locatorsRegistry)
    {
        this(null, locatorsRegistry);
    }

    public OWLSFromBSDMServiceBuilder(AbstractDynamicGroundingFactory dynamicGroundingFactory,
            ILocatorsRegistry locatorsRegistry)
    {
        super(dynamicGroundingFactory);
        this.locatorsRegistry = locatorsRegistry;
    }

    public OWLSAtomicService buildOWLSServiceFromBSDOService(IAdvertisedFunctionality functionality) throws ModelException, ClassNotFoundException
    {
        if (null == functionality.getFunctionalityImplementation())
        {
            throw new DynamicGroundingBuildingException("Functionality without grounding data cannot be used to build a dynamic OWLS grounding", null);
        }
        if (functionality.getFunctionalityImplementation() instanceof AbstractBSDLJavaFunctionalityImplementation)
        {
            this.groundingFactory = new DynamicAtomicJavaGroundingFactory(
                    Class.forName(((AbstractBSDLJavaFunctionalityImplementation) functionality.getFunctionalityImplementation()).className()),
                    ((AbstractBSDLJavaFunctionalityImplementation) functionality.getFunctionalityImplementation()).methodName());
        } else
        {
            throw new DynamicGroundingBuildingException("Functionality grounding not valid for a OWLS Java dynamic grounding: "
                    + functionality.getFunctionalityImplementation().toString());
        }

        OWLContainer owlSyntaxTranslator = new OWLContainer();
        OWLKnowledgeBase kb = KBWithReasonerBuilder.newKB();
        Service serviceOWLAPI = kb.createService(functionality.getIdentifier().getURI());

        // functionality name
        serviceOWLAPI.setName(functionality.name());

        // imports
        for (IImport imp : functionality.getImports())
        {
            Collection<ISemanticLocator> locatorsForImport = locatorsRegistry.getLocatorsFor(imp.getPrefixed());
            if (null != locatorsForImport && locatorsForImport.size() > 0)
            {
                owlSyntaxTranslator.loadIntoKB(kb, locatorsForImport.iterator().next().getURI());
            }
        }

        try
        {
            // add profile
            Profile profileOWLAPI = kb.createProfile(composeURIwithPostfix(functionality.getIdentifier().getURI(), "profile"));

            // add inputs to profile
            for (IInput input : functionality.inputs())
            {
                Input createInput = kb.createInput(input.getSemanticAnnotation().getURI());
                createInput.setParamType(input.getSemanticAnnotation().getURI());
                profileOWLAPI.addInput(createInput);
            }

            // add outputs to profile
            for (IOutput output : functionality.outputs())
            {
                Output createOutput = kb.createOutput(output.getSemanticAnnotation().getURI());
                createOutput.setParamType(output.getSemanticAnnotation().getURI());
                profileOWLAPI.addOutput(createOutput);
            }

            // add profile to service
            serviceOWLAPI.addProfile(profileOWLAPI);
            profileOWLAPI.setService(serviceOWLAPI);
        } catch (URISyntaxException ex)
        {
            throw new OWLTranslationException("Error composing URI for profile for: " + functionality.getIdentifier().toString(), ex);
        }

        // mappings for "OWL to Java"
        for (OntologyToJavaReference owlToJava : ((AbstractBSDLJavaFunctionalityImplementation) functionality.getFunctionalityImplementation()).ontologyToJavaReferences())
        {
            ObjectOWLSTranslator.registerNamespace(owlToJava.ontologyURI().getURI().toString(), owlToJava.javaNamespace());
        }

        OWLSAtomicService service = new OWLSAtomicService(serviceOWLAPI);
        
        BSDFLogger.getLogger().info("Returns an OWLSAtomicService for: " + functionality.name());
        
        return completeOWLSServiceIfNeeded(service);
    }

    private static URI composeURIwithPostfix(URI uri, String postfix) throws URISyntaxException
    {
        return new URI(uri.toString() + "/" + postfix);
    }
}
