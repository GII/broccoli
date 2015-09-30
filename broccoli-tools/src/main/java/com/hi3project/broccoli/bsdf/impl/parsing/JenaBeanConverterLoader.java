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
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdf.impl.implementation.OntologyToJavaReference;
import com.hi3project.broccoli.bsdl.api.parsing.IBSDLLoader;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.io.BSDFLogger;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A loader to help clients to load "OWLToJavaReferences"
 *
 * 
 */
public class JenaBeanConverterLoader
{

    private static final String ontologyToJavaReferenceConcept = "http://hi3project.com/broccoli/bsdm/implementation#ontologyToJavaReference";

    private IBSDLLoader bsdlLoader;
    private IBSDLRegistry bsdlRegistry;

    public JenaBeanConverterLoader(IBSDLRegistry bsdlRegistry, IBSDLLoader bsdlLoader)
    {
        this.bsdlRegistry = bsdlRegistry;
        this.bsdlLoader = bsdlLoader;
    }

    public void setBSDLRegistry(IBSDLRegistry bsdlRegistry)
    {
        bsdlLoader.setBSDLRegistry(bsdlRegistry);
    }

    public Collection<OntologyToJavaReference> readOntologyToJavaReferencesFrom(Collection<ISemanticLocator> locators) throws ModelException
    {
        Collection<Instance> readInstances = bsdlLoader.readInstancesFromLocationOfConcept(locators, new SemanticIdentifier(ontologyToJavaReferenceConcept));
        Collection<OntologyToJavaReference> references = new ArrayList<OntologyToJavaReference>();
        for (Instance instance : readInstances)
        {
            references.add(new OntologyToJavaReference(instance));
        }
        return references;
    }

    public Collection<OntologyToJavaReference> readOntologyToJavaReferencesFrom(ISemanticLocator locator) throws ModelException
    {
        Collection<ISemanticLocator> locators = new ArrayList<ISemanticLocator>();
        locators.add(locator);
        return readOntologyToJavaReferencesFrom(locators);
    }

    public JenaBeanConverter createJenaBeanConverter(Collection<ISemanticLocator> locators) throws ModelException
    {
        JenaBeanConverter jenabeanConverter = new JenaBeanConverter(bsdlRegistry);
        if (null != locators)
        {
            for (ISemanticLocator locator : locators)
            {
                for (OntologyToJavaReference owlToJavaReference : readOntologyToJavaReferencesFrom(locator))
                {
                    jenabeanConverter.addOntologyToJavaReference(owlToJavaReference);
                }
            }
        }
        return jenabeanConverter;
    }

    public JenaBeanConverter createJenaBeanConverter(ISemanticLocator locator) throws ModelException
    {
        BSDFLogger.getLogger().info("Creates a JenaBeanConverter for: " + locator.toString());
        Collection<ISemanticLocator> locators = new ArrayList<ISemanticLocator>();
        locators.add(locator);
        return createJenaBeanConverter(locators);
    }
}
