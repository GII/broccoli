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

package com.hi3project.broccoli.bsdf.impl.owl;

import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLIOException;
import com.hi3project.broccoli.io.BSDFLogger;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.mindswap.owl.OWLKnowledgeBase;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

/**
 * <p>
 * Abstraction for OWL related operations, based on OWLAPI. Reads OWLSAPI
 * services also
 *<p>
 * It performs the operations on the ontologies of a OWLOntologyManager, keeping
 * them merged in a single OWLOntology
 *<p>
 * Includes methods to translates from any valid OWL2 syntax, to RDF/XML
 *<p>
 * Methods loadAsServiceIntoKB and loadIntoKB load OWLSAPI services
 *
 *
 * 
 */
public class OWLContainer
{

    final static String ioErrorMsg = "Input/output error with OWL ontology";
    private OWLOntologyManager owlOntologyManager = null;
    private OWLOntologyFormat owlOntologyFormat = null;
    private OWLDataFactory owlDataFactory = null;
    private OWLOntology mergedOntology = null;
    private OWLReasoner reasonerForMergedOntology = null;

    public OWLContainer(OWLOntologyFormat owlOntologyFormat)
    {
        BSDFLogger.getLogger().info("Instances an OWLContainer of: " + owlOntologyFormat.toString());
        owlOntologyManager = OWLManager.createOWLOntologyManager();
        owlDataFactory = owlOntologyManager.getOWLDataFactory();
        this.owlOntologyFormat = owlOntologyFormat;
    }

    public OWLContainer()
    {
        this(new RDFXMLOntologyFormat());
    }

    /**
     * *********************************************************************************************************************
     */
    public boolean contains(IRI iri)
    {
        return mergedOntology.containsClassInSignature(iri)
                || mergedOntology.containsIndividualInSignature(iri);
    }

    public boolean contains(URI uri)
    {
        return contains(IRI.create(uri));
    }

    public boolean contains(String uri)
    {
        return contains(IRI.create(uri));
    }

    public boolean containsClassFor(IRI iri)
    {
        return mergedOntology.containsClassInSignature(iri);
    }

    public boolean containsClassFor(URI uri)
    {
        return containsClassFor(IRI.create(uri));
    }

    public boolean containsClassFor(String uri)
    {
        return containsClassFor(IRI.create(uri));
    }

    public boolean containsInstanceFor(IRI iri)
    {
        return mergedOntology.containsIndividualInSignature(iri);
    }

    public boolean containsInstanceFor(URI uri)
    {
        return containsInstanceFor(IRI.create(uri));
    }

    public boolean containsInstanceFor(String uri)
    {
        return containsInstanceFor(IRI.create(uri));
    }

    public OWLClass getClass(URI classURI)
    {
        return owlDataFactory.getOWLClass(IRI.create(classURI));
    }

    public OWLClass getClass(String classURI) throws URISyntaxException
    {
        return getClass(new URI(classURI));
    }

    public Set<OWLClass> getSuperClassesOf(OWLClass owlClass)
    {
        Set<OWLClass> superClasses = new HashSet<OWLClass>();
        Iterator<OWLClassExpression> iterator = owlClass.getSuperClasses(mergedOntology).iterator();
        while (iterator.hasNext())
        {
            OWLClassExpression next = iterator.next();
            if (!next.isAnonymous())
            {
                superClasses.add(next.asOWLClass());
            }
        }
        return superClasses;
    }

    public Set<OWLObjectProperty> getObjectPropertiesOfClass(OWLClass owlClass)
    {
        Set<OWLClass> classes = allSuperClassesOf(owlClass);
        classes.add(owlClass);

        Set<OWLObjectProperty> objectProperties = new HashSet<OWLObjectProperty>();
        Set<OWLObjectProperty> allObjectProperties = mergedOntology.getObjectPropertiesInSignature();
        Iterator<OWLObjectProperty> IteratorAllObjectProperties = allObjectProperties.iterator();
        while (IteratorAllObjectProperties.hasNext())
        {
            OWLObjectProperty objectProp = IteratorAllObjectProperties.next();
            Iterator<OWLClassExpression> iteratorDomains = objectProp.getDomains(mergedOntology).iterator();
            while (iteratorDomains.hasNext())
            {
                OWLClassExpression domainOWLClass = iteratorDomains.next();
                if (!domainOWLClass.isAnonymous() && classes.contains(domainOWLClass.asOWLClass()))
                {
                    objectProperties.add(objectProp);
                }
            }
        }

        return objectProperties;
    }

    public Set<OWLObjectProperty> getObjectPropertiesOfClass(URI classURI)
    {
        return getObjectPropertiesOfClass(getClass(classURI));
    }

    public Set<OWLObjectProperty> getObjectPropertiesOfClass(String classURI) throws URISyntaxException
    {
        return getObjectPropertiesOfClass(getClass(classURI));
    }

    public Set<OWLClass> getObjectPropertyDomain(OWLObjectProperty objectProperty)
    {
        Set<OWLClass> objectPropertieDomains = new HashSet<OWLClass>();
        Set<OWLClassExpression> domains = objectProperty.getDomains(mergedOntology);
        for (OWLClassExpression owlClassExpresion : domains)
        {
            objectPropertieDomains.add(owlClassExpresion.asOWLClass());
        }
        return objectPropertieDomains;
    }

    public Set<OWLDataProperty> getDataPropertiesOfClass(OWLClass owlClass)
    {
        Set<OWLClass> classes = allSuperClassesOf(owlClass);
        classes.add(owlClass);

        Set<OWLDataProperty> dataProperties = new HashSet<OWLDataProperty>();
        Set<OWLDataProperty> allDataProperties = mergedOntology.getDataPropertiesInSignature();
        Iterator<OWLDataProperty> IteratorAllDataProperties = allDataProperties.iterator();
        while (IteratorAllDataProperties.hasNext())
        {
            OWLDataProperty dataProp = IteratorAllDataProperties.next();
            Iterator<OWLClassExpression> iteratorDomains = dataProp.getDomains(mergedOntology).iterator();
            while (iteratorDomains.hasNext())
            {
                OWLClass domainOWLClass = iteratorDomains.next().asOWLClass();
                if (classes.contains(domainOWLClass))
                {
                    dataProperties.add(dataProp);
                }
            }
        }

        return dataProperties;
    }

    public Set<OWLDataProperty> getDataPropertiesOfClass(URI classURI)
    {
        return getDataPropertiesOfClass(getClass(classURI));
    }

    public Set<OWLDataProperty> getDataPropertiesOfClass(String classURI) throws URISyntaxException
    {
        return getDataPropertiesOfClass(getClass(classURI));
    }

    public OWLNamedIndividual getIndividual(URI individualURI)
    {
        return owlDataFactory.getOWLNamedIndividual(IRI.create(individualURI));
    }

    public OWLNamedIndividual getIndividual(String individualURI) throws URISyntaxException
    {
        return getIndividual(new URI(individualURI));
    }

    /**
     * *********************************************************************************************************************
     */
    public Set<OWLClass> allSuperClassesOf(OWLClass owlClass)
    {
        Set<OWLClass> superOWLClasses = new HashSet<OWLClass>();
        if (null != reasonerForMergedOntology)
        {
            Iterator<Node<OWLClass>> iteratorNodeOWLClass = reasonerForMergedOntology.getSuperClasses(owlClass, false).iterator();
            while (iteratorNodeOWLClass.hasNext())
            {
                Iterator<OWLClass> iteratorOWLClass = iteratorNodeOWLClass.next().iterator();
                while (iteratorOWLClass.hasNext())
                {
                    superOWLClasses.add(iteratorOWLClass.next());
                }
            }
        }
        return superOWLClasses;
    }

    /**
     * *********************************************************************************************************************
     */
    public OWLOntology loadOntologyFromFile(File file) throws OWLIOException
    {
        try
        {
            return owlOntologyManager.loadOntologyFromOntologyDocument(file);
        } catch (OWLOntologyCreationException ex)
        {
            throw new OWLIOException(ioErrorMsg, ex);
        }
    }

    public OWLOntology loadOntologyIfNotLoaded(URI uri) throws OWLIOException
    {
        // if the ontology is not already managed, we try to load it
        OWLOntology owlOntology = owlOntologyManager.getOntology(IRI.create(uri));
        if (null == owlOntology)
        {
            owlOntology = loadOntologyFromURI(uri);
        }
        return owlOntology;
    }

    public void unloadOntologies()
    {
        Set<OWLOntology> ontologies = owlOntologyManager.getOntologies();
        for (OWLOntology ontology : ontologies)
        {
            owlOntologyManager.removeOntology(ontology);
        }
    }

    public void saveFullOntologyToStream(OutputStream outputStream) throws OWLIOException
    {
        try
        {
            owlOntologyManager.saveOntology(createMergedOntology(), prepareFormat(), outputStream);
        } catch (OWLOntologyStorageException ex)
        {
            throw new OWLIOException(ioErrorMsg, ex);
        }
    }

    public void saveOntologyToStream(URI ontologyURI, OutputStream outputStream) throws OWLIOException
    {
        try
        {
            owlOntologyManager.saveOntology(owlOntologyManager.getOntology(IRI.create(ontologyURI)), prepareFormat(), outputStream);
        } catch (OWLOntologyStorageException ex)
        {
            throw new OWLIOException(ioErrorMsg, ex);
        }
    }

    public void saveOntologyToStream(String ontologyURI, OutputStream outputStream) throws OWLIOException
    {
        try
        {
            saveOntologyToStream(new URI(ontologyURI), outputStream);
        } catch (URISyntaxException ex)
        {
            throw new OWLIOException(ioErrorMsg, ex);
        }
    }

    public OWLKnowledgeBase loadAsServiceIntoKB(OWLKnowledgeBase kb, URI uri) throws OWLIOException
    {
        OWLOntology owlServiceOntologyLoaded = loadOntologyIfNotLoaded(uri);
        try
        {
            if (isOntologyRDFXML(owlServiceOntologyLoaded))
            {
                kb.readService(uri);
            } else
            {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                saveFullOntologyToStream(outputStream);
                kb.readService(new ByteArrayInputStream(outputStream.toByteArray()), owlServiceOntologyLoaded.getOntologyID().getOntologyIRI().toURI());
            }
        } catch (IOException ex)
        {
            throw new OWLIOException("Exception reading a service", ex);
        }
        return kb;
    }

    public OWLKnowledgeBase loadIntoKB(OWLKnowledgeBase kb, URI uri) throws OWLIOException
    {
        OWLOntology owlServiceOntologyLoaded = loadOntologyIfNotLoaded(uri);
        try
        {
            if (isOntologyRDFXML(owlServiceOntologyLoaded))
            {
                kb.read(uri);
            } else
            {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                saveOntologyToStream(owlServiceOntologyLoaded.getOntologyID().getOntologyIRI().toURI(), outputStream);
                kb.read(new ByteArrayInputStream(outputStream.toByteArray()), owlServiceOntologyLoaded.getOntologyID().getOntologyIRI().toURI());
            }
        } catch (IOException ex)
        {
            throw new OWLIOException("Exception reading a service", ex);
        }
        return kb;
    }

    /**
     * *********************************************************************************************************************
     */
    public OWLOntology createMergedOntology() throws OWLIOException
    {
        OWLOntologyMerger owlOntologyMerger = new OWLOntologyMerger(owlOntologyManager);
        try
        {
            mergedOntology = owlOntologyMerger.createMergedOntology(owlOntologyManager, IRI.create(mergedOntologyURI()));
            reasonerForMergedOntology = new StructuralReasoner(mergedOntology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
            return mergedOntology;
        } catch (OWLOntologyCreationException ex)
        {
            throw new OWLIOException(ioErrorMsg, ex);
        }
    }

    /**
     * *********************************************************************************************************************
     */
    private OWLOntology loadOntologyFromURI(URI uri) throws OWLIOException
    {
        try
        {
            return owlOntologyManager.loadOntologyFromOntologyDocument(IRI.create(uri));
        } catch (OWLOntologyCreationException ex)
        {
            throw new OWLIOException(ioErrorMsg, ex);
        }
    }

    private String mergedOntologyURI()
    {
        Set<OWLOntology> ontologies = owlOntologyManager.getOntologies();
        StringBuffer buf = new StringBuffer();
        for (OWLOntology ontology : ontologies)
        {
            buf.append("oid" + ontology.getOntologyID().hashCode());
        }
        return buf.toString() + "M";
    }

    private OWLOntologyFormat prepareFormat()
    {
        Set<OWLOntology> ontologies = owlOntologyManager.getOntologies();
        for (OWLOntology ontology : ontologies)
        {
            OWLOntologyFormat currentFormat = owlOntologyManager.getOntologyFormat(ontology);
            if (currentFormat.isPrefixOWLOntologyFormat())
            {
                ((PrefixOWLOntologyFormat) owlOntologyFormat).copyPrefixesFrom(currentFormat.asPrefixOWLOntologyFormat());
            }
        }
        return owlOntologyFormat;
    }

    private boolean isOntologyRDFXML(OWLOntology owlOntology)
    {
        return owlOntologyManager.getOntologyFormat(owlOntology).equals(new RDFXMLOntologyFormat());
    }
}
