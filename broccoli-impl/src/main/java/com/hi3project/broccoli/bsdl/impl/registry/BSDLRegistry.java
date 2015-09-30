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

package com.hi3project.broccoli.bsdl.impl.registry;

import com.hi3project.broccoli.conf.ProjProperties;
import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.IOntology;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.parsing.IBSDLLoader;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentLoader;
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLConverter;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.api.registry.ILocatorsRegistry;
import com.hi3project.broccoli.bsdl.impl.Ontology;
import com.hi3project.broccoli.bsdl.impl.SemanticAxiom;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.io.BSDFLogger;
import java.util.*;

/**
 * <p>
 * Complete implementation of IBSDLRegistry. Depends on SemanticAxiom
 * implementation </p><p>
 * Depends on SemanticAxiom implementation </p><p>
 * Uses a HashMap of ISemanticIdentifier -- SemanticAxiom as cache of IAxiom
 *
 *
 * 
 */
public class BSDLRegistry implements IBSDLRegistry
{

    private Map<ISemanticIdentifier, SemanticAxiom> axiomsMap = new HashMap<ISemanticIdentifier, SemanticAxiom>();
    private Map<ISemanticIdentifier, Integer> axiomsCounterMap = new HashMap<ISemanticIdentifier, Integer>();
    private Collection<IBSDLConverter> converters = new ArrayList<IBSDLConverter>();
    private Set<ISemanticIdentifier> ontologies = new HashSet<ISemanticIdentifier>();
    private LocatorsRegistry locatorsRegistry = new LocatorsRegistry();
    private IDocumentLoader bsdlDocumentLoader;

    public BSDLRegistry(IBSDLLoader bsdlLoader) throws ModelException
    {
        bsdlLoader.setBSDLRegistry(this);
        bsdlDocumentLoader = bsdlLoader.createBSDMBSDLDocumentLoader();
    }

    @Override
    public synchronized Collection<ISemanticIdentifier> addOntology(ISemanticLocator locator, ISemanticIdentifier identifier, String ontologyLangID) throws ModelException
    {
        if (isLocatorFromBSD(locator))
        {
            bsdlDocumentLoader.readFrom(locator);
            BSDFLogger.getLogger().info(
                    "Adds a BSD ontology from: " + locator.toString(), identifier);
        } else
        {
            locatorsRegistry.addLocatorFor(locator, identifier);
            for (IBSDLConverter converter : converters())
            {
                if (converter.knows(ontologyLangID))
                {
                    converter.addOntologyFrom(locator);
                    BSDFLogger.getLogger().info(
                            "Adds a non BSD ontology (" + ontologyLangID + ") from: " + locator.toString(), identifier);
                }
            }
        }

        return Arrays.asList(identifier);
    }

    @Override
    public synchronized Collection<ISemanticIdentifier> addOntology(ISemanticLocator locator) throws ModelException
    {

        if (isLocatorFromBSD(locator))
        {
            Collection<IAxiom> readAxioms = bsdlDocumentLoader.readFrom(locator);
            return getOntologiesFrom(readAxioms);
        } else
        {
            ArrayList<ISemanticIdentifier> loadedOntologies = new ArrayList<ISemanticIdentifier>();
            for (IBSDLConverter converter : converters())
            {
                ISemanticIdentifier identifierForAddedOntology = converter.addOntologyFrom(locator);
                if (null != identifierForAddedOntology)
                {
                    BSDFLogger.getLogger().info(
                            "Adds a non BSD ontology from: " + locator.toString(), identifierForAddedOntology);
                    locatorsRegistry.addLocatorFor(locator, identifierForAddedOntology);
                    loadedOntologies.add(identifierForAddedOntology);
                }
            }
            return loadedOntologies;
        }
    }

    public static Collection<ISemanticIdentifier> getOntologiesFrom(Collection<IAxiom> readAxioms)
    {
        ArrayList<ISemanticIdentifier> loadedOntologies = new ArrayList<ISemanticIdentifier>();
        for (IAxiom axiom : readAxioms)
        {
            if (axiom instanceof IOntology)
            {
                BSDFLogger.getLogger().info(
                        "Adds a BSD ontology: " + axiom.toString(), axiom);
                loadedOntologies.add(((IOntology) axiom).identifier());
            }
        }
        return loadedOntologies;
    }

    @Override
    public synchronized void removeOntology(ISemanticIdentifier ontologyIdentifier) throws SemanticModelException
    {
        IOntology ontology = null;
        ISemanticIdentifier identifier = null;
        Iterator<ISemanticIdentifier> iterator = this.ontologies.iterator();
        while (iterator.hasNext() && null == ontology)
        {
            identifier = iterator.next();
            if (identifier.equals(ontologyIdentifier))
            {
                ontology = ontologyOf(ontologyIdentifier);
                for (IAxiom axiom : ontology.axioms())
                {
                    this.deleteAxiom(axiom);
                }
            }
        }
        if (null != ontology)
        {
            BSDFLogger.getLogger().info("Removes: " + ontologyIdentifier.toString());

            if (this.deleteAxiom(axiomFor(ontologyIdentifier)))
            {
                this.ontologies.remove(ontologyIdentifier);
            }

        }
    }

    @Override
    public synchronized IAxiom addAxiom(IAxiom axiom) throws SemanticModelException
    {
        if (axiom instanceof ISemanticIdentifier)
        {
            addOnlySemanticIdentifier((ISemanticIdentifier) axiom);
            return axiom;
        }
        if (axiom instanceof SemanticAxiom)
        {
            SemanticAxiom semanticAxiom = (SemanticAxiom) axiom;
            semanticAxiom = addAnnotatedObject(semanticAxiom);
            if (axiom instanceof Ontology)
            {
                if (!ontologies.contains(semanticAxiom.getSemanticIdentifier()))
                {
                    ontologies.add(semanticAxiom.getSemanticIdentifier());
                }
            } else
            {
                if (null != semanticAxiom.getOntology() && null != semanticAxiom.getOntology().semanticAxiom())
                {
                    semanticAxiom.getOntology().semanticAxiom().addAxiom(semanticAxiom);
                }
            }
            return semanticAxiom;
        }
        return null;
    }

    @Override
    public synchronized boolean deleteAxiom(ISemanticIdentifier identifier)
    {
        if (!axiomsMap.containsKey(identifier))
        {
            return false;
        }
        Integer counter = axiomsCounterMap.get(identifier);
        if (1 >= counter)
        {
            axiomsMap.remove(identifier);
            axiomsCounterMap.remove(identifier);
            return true;
        } else
        {
            axiomsCounterMap.put(identifier, counter - 1);
            return false;
        }
    }

    private boolean deleteAxiom(IAxiom axiom)
    {
        if (axiom instanceof SemanticAxiom)
        {
            SemanticAxiom sAxiom = (SemanticAxiom) axiom;
            return this.deleteAxiom(sAxiom.getSemanticIdentifier());
        }
        return false;
    }

    @Override
    public IAxiom axiomFor(ISemanticIdentifier semanticAnnotation) throws SemanticModelException
    {
        IAxiom axiom = annotatedObjectFor(semanticAnnotation);
        if (null != axiom)
        {
            return axiom;
        }
        for (IBSDLConverter converter : converters())
        {
            if (null != (axiom = converter.createAxiomFor(semanticAnnotation)))
            {
                return axiom;
            }
        }
        return null;
    }

    @Override
    public synchronized void clean()
    {
        axiomsMap = new HashMap<ISemanticIdentifier, SemanticAxiom>();
        axiomsCounterMap = new HashMap<ISemanticIdentifier, Integer>();
        converters = new ArrayList<IBSDLConverter>();
        ontologies = new HashSet<ISemanticIdentifier>();
    }

    @Override
    public synchronized Collection<IAxiom> addAxioms(Collection<IAxiom> axioms) throws SemanticModelException
    {
        Collection<IAxiom> returnedAxioms = new ArrayList<IAxiom>();
        for (IAxiom axiom : axioms)
        {
            returnedAxioms.add(addAxiom(axiom));
        }
        return returnedAxioms;
    }

    @Override
    public Collection<IAxiom> axiomsFor(String partialIdentifier)
    {
        Collection<IAxiom> axioms = new ArrayList<IAxiom>();
        final Iterator<Map.Entry<ISemanticIdentifier, SemanticAxiom>> iterator = axiomsMap.entrySet().iterator();
        IAxiom axiom;
        while (iterator.hasNext())
        {
            final Map.Entry<ISemanticIdentifier, SemanticAxiom> entry = iterator.next();
            if (entry.getKey().getName().contains(partialIdentifier) && null != (axiom = entry.getValue()))
            {
                axioms.add(axiom);
            }
        }
        return axioms;
    }

    @Override
    public void registerConverter(IBSDLConverter converter)
    {
        converters.add(converter);
    }

    @Override
    public synchronized void registerImportsForConverter(Collection<IImport> imports) throws ModelException
    {
        for (IImport imp : imports)
        {
            for (ISemanticLocator locator : locatorsRegistry.getLocatorsFor(imp.getPrefixed()))
            {
                for (IBSDLConverter converter : this.converters())
                {
                    if (converter.knows(imp.getPrefix()))
                    {
                        converter.addOntologyFrom(locator);
                    }
                }
            }
        }
    }

    @Override
    public Collection<IBSDLConverter> converters()
    {
        return converters;
    }

    @Override
    public Collection<IOntology> ontologies() throws SemanticModelException
    {
        Collection<IOntology> ontologiesC = new ArrayList<IOntology>();
        for (ISemanticIdentifier semanticIdentifier : ontologies)
        {
            IOntology ontology = ontologyOf(semanticIdentifier);
            if (null != ontology)
            {
                ontologiesC.add(ontology);
            }
        }
        return ontologiesC;
    }

    private IOntology ontologyOf(ISemanticIdentifier identifier) throws SemanticModelException
    {
        IAxiom ontology = axiomFor(identifier);
        if (null != ontology && ontology instanceof IOntology)
        {
            return (IOntology) ontology;
        }
        return null;
    }

    @Override
    public SemanticAxiom annotatedObjectFor(ISemanticIdentifier identifier)
    {
        return axiomsMap.get(identifier);
    }

    private synchronized SemanticAxiom addAnnotatedObject(SemanticAxiom aObject) throws SemanticModelException
    {
        SemanticAxiom getObject = axiomsMap.get(aObject.getSemanticIdentifier());
        if (null == getObject)
        {
            axiomsMap.put(aObject.getSemanticIdentifier(), aObject);
            axiomsCounterMap.put(aObject.getSemanticIdentifier(), 1);
            return aObject;
        } else
        {
            if (!getObject.equals(aObject))
            {
                axiomsMap.put(aObject.getSemanticIdentifier(), getObject.joinWith(aObject));
            }
            axiomsCounterMap.put(aObject.getSemanticIdentifier(), axiomsCounterMap.get(aObject.getSemanticIdentifier()) + 1);
            return getObject;
        }
    }

    private synchronized SemanticAxiom addOnlySemanticIdentifier(ISemanticIdentifier identifier)
    {
        SemanticAxiom axiom = annotatedObjectFor(identifier);
        if (null == axiom)
        {
            axiomsMap.put(identifier, null);
            axiomsCounterMap.put(identifier, 1);
        } else
        {
            axiomsCounterMap.put(identifier, axiomsCounterMap.get(identifier) + 1);
        }
        return axiom;
    }

    @Override
    public ILocatorsRegistry getLocatorsRegistry()
    {
        return this.locatorsRegistry;
    }

    public static boolean isLocatorFromBSD(ISemanticLocator semanticLocator)
    {
        return isLocatorFromBSD(semanticLocator.getURI().getPath());
    }

    public static boolean isLocatorFromBSD(String locator)
    {
        String[] split = locator.split("\\.");
        if (split.length <= 1)
        {
            return false;
        }
        String extension = split[split.length - 1];
        return extension.equalsIgnoreCase(ProjProperties.BSDL_FILE_EXTENSION)
                || extension.equalsIgnoreCase(ProjProperties.BSDM_FILE_EXTENSION)
                || extension.equalsIgnoreCase("xml");
    }
}
