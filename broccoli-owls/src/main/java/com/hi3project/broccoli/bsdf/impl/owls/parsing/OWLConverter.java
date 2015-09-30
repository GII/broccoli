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

package com.hi3project.broccoli.bsdf.impl.owls.parsing;

import com.hi3project.broccoli.bsdf.impl.owl.OWLContainer;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLIOException;
import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLConverter;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.Concept;
import com.hi3project.broccoli.bsdl.impl.Property;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.parsing.ReferenceToSemanticAxiom;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.io.BSDFLogger;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Implementation of IBSDLConverter for OWL ontologies, based on OWLAPI. Depends
 * on BSDLRegistry and OWLContainer </p><p>
 * Currently able to build a Concept from a given OWLClass identifier </p>
 *
 * 
 */
public class OWLConverter implements IBSDLConverter
{

    private static final String BSDLLiteral = "http://hi3project.com/broccoli/bsdl#literal";
    public static final String ONTOLOGY_ID = "owl";
    private IBSDLRegistry registry = null;
    private OWLContainer owlContainer = null;

    public OWLConverter(IBSDLRegistry registry, OWLContainer owlContainer)
    {
        this.registry = registry;
        this.owlContainer = owlContainer;
    }

    public OWLConverter(IBSDLRegistry registry)
    {
        this(registry, new OWLContainer(new RDFXMLOntologyFormat()));
    }

    public OWLConverter(IBSDLRegistry registry, Collection<ISemanticLocator> ontologyLocators) throws OWLIOException
    {
        this(registry);
        if (null != ontologyLocators && ontologyLocators.size() > 0)
        {
            owlContainer.loadOntologyIfNotLoaded(ontologyLocators.iterator().next().getURI());
        }
        owlContainer.createMergedOntology();
    }

    @Override
    public ISemanticIdentifier addOntologyFrom(ISemanticLocator ontologyLocator) throws OWLIOException
    {
        OWLOntology loadedOntology;
        try
        {
            loadedOntology = owlContainer.loadOntologyIfNotLoaded(ontologyLocator.getURI());
        } catch (OWLIOException ex)
        {
            return null;
        }
        owlContainer.createMergedOntology();
        if (null != loadedOntology.getOntologyID().getOntologyIRI())
        {
            return new SemanticIdentifier(loadedOntology.getOntologyID().getOntologyIRI().toURI());
        } else
        {
            return null;
        }
    }

    @Override
    public boolean knows(String key)
    {
        return key.contains("owl");
    }

    public Concept classToConcept(OWLClass owlClass) throws SemanticModelException
    {
        IAxiom axiom = registry.axiomFor(new SemanticIdentifier(owlClass.getIRI().toURI()));
        if (null != axiom)
        {
            if (axiom instanceof Concept)
            {
                return (Concept) axiom;
            }
            return null;
        }

        return createConceptFor(owlClass);
    }

    @Override
    public IBSDLConverter newInstance()
    {
        return new OWLConverter(registry, owlContainer);
    }

    @Override
    public boolean contains(ISemanticIdentifier identifier)
    {
        return owlContainer.contains(identifier.getURI());
    }

    @Override
    public IAxiom createAxiomFor(ISemanticIdentifier identifier) throws SemanticModelException
    {
        if (owlContainer.containsClassFor(identifier.getURI()))
        {
            return createConceptFor(identifier.getURI());
        }
        return null;
    }

    private Concept createConceptFor(URI classURI) throws SemanticModelException
    {
        return createConceptFor(owlContainer.getClass(classURI));
    }

    private Concept createConceptFor(OWLClass owlClass) throws SemanticModelException
    {
        Concept concept = new Concept();

        // identifier
        concept.setSemanticIdentifier(new SemanticIdentifier(owlClass.getIRI().toURI()));

        // superconcepts        
        Iterator<OWLClass> iteratorSuperClasses = owlContainer.getSuperClassesOf(owlClass).iterator();
        while (iteratorSuperClasses.hasNext())
        {
            OWLClass superClass = iteratorSuperClasses.next();
            concept.superconcepts().add(
                    new ReferenceToSemanticAxiom<Concept>(
                            registry,
                            new SemanticIdentifier(superClass.getIRI().toURI()),
                            Concept.class
                    ));
        }

        // properties (DataProperty)
        Iterator<OWLDataProperty> iteratorDataProperty = owlContainer.getDataPropertiesOfClass(owlClass).iterator();
        while (iteratorDataProperty.hasNext())
        {
            Property propertyToAdd = new Property();
            concept.addProperty(propertyToAdd);
            propertyToAdd.setSubject(concept);
            propertyToAdd.setName(new SemanticIdentifier(iteratorDataProperty.next().getIRI().toURI()).getName());
            propertyToAdd.setObject(
                    new ReferenceToSemanticAxiom<Concept>(
                            registry,
                            new SemanticIdentifier(BSDLLiteral),
                            Concept.class
                    ));
        }

        // properties (ObjectProperty)
        Iterator<OWLObjectProperty> iteratorObjectProperty = owlContainer.getObjectPropertiesOfClass(owlClass).iterator();
        while (iteratorObjectProperty.hasNext())
        {
            Property propertyToAdd = new Property();
            concept.addProperty(propertyToAdd);
            propertyToAdd.setSubject(concept);
            OWLObjectProperty owlObjectProperty = iteratorObjectProperty.next();
            propertyToAdd.setName(new SemanticIdentifier(owlObjectProperty.getIRI().toURI()).getName());
            propertyToAdd.setObject(
                    new ReferenceToSemanticAxiom<Concept>(
                            registry,
                            new SemanticIdentifier(owlContainer.getObjectPropertyDomain(owlObjectProperty).iterator().next().getIRI().toURI()),
                            Concept.class
                    ));
        }

        BSDFLogger.getLogger().info("Builds the concept: " + concept.toString() + " for the OWLClass: " + owlClass.toString());
        
        return concept;
    }
}
