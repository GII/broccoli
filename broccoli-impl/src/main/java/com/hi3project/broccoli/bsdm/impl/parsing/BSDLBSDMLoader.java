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

package com.hi3project.broccoli.bsdm.impl.parsing;

import com.hi3project.broccoli.bsdl.impl.parsing.xml.BSDLXMLDOMParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.XMLSyntax;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.BSDLXMLStAXParser;
import com.hi3project.broccoli.Mark;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.stax.ConceptPropertyParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.stax.OntologyParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.stax.PropertyMultiplicityParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.stax.AttributeSimpleParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.stax.AttributeURIParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.stax.ConceptParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.stax.ImportParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.stax.InstancePropertyParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.stax.InstanceParser;
import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.parsing.IBSDLLoader;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentParser;
import com.hi3project.broccoli.bsdl.api.parsing.PreParsingRule;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticLocator;
import com.hi3project.broccoli.bsdl.impl.parsing.BSDLDocumentLoader;
import com.hi3project.broccoli.bsdl.impl.parsing.InstanceIntegrityChecker;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.DocumentPreParser;
import com.hi3project.broccoli.io.BSDFLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * Helper class that can load a BSDO service, given its document descriptor
 * locations. It uses an IBSDLRegistry to register there the read axioms
 *<p>
 * It knows how to build a BSDLDocumentLoader able to read any BSDL XML document
 *<p>
 * It can register the full current BSDO specification so it can read any BSDO
 * service
 *
 *
 * 
 */
public final class BSDLBSDMLoader implements IBSDLLoader
{        

    private static final String broccoliDir = 
            System.getProperty("user.dir") + File.separator + ".."
            + File.separator + "broccoli-api" + File.separator + "src"
            + File.separator + "main" + File.separator + "resources"
            + File.separator + "com" + File.separator + "hi3project" 
            + File.separator + "broccoli" + File.separator;
    
    private static final String broccoliJarDir = 
            BSDLDocumentLoader.JAR_RESOURCE_PREFIX + Mark.class.getName() + BSDLDocumentLoader.JAR_RESOURCE_SEPARATOR;

    public static final String ontologyTestXMLDir = broccoliDir + "ontology" + File.separator;
    
    public static final String ontologyXMLDir = broccoliJarDir + "ontology" + File.separator;

    private BSDLRegistry bsdlRegistry = null;
    
    private Collection<PreParsingRule> preParsingRules = new ArrayList<PreParsingRule>();

    
    public BSDLBSDMLoader(IBSDLRegistry bsdlRegistry)
    {
        setBSDLRegistry(bsdlRegistry);
    }
    
    
    public BSDLBSDMLoader() {}
           
    public BSDLBSDMLoader(IBSDLRegistry bsdlRegistry, Collection<PreParsingRule> preParsingRules)
    {
        this(bsdlRegistry);
        this.preParsingRules = preParsingRules;
    }
    
    public final void BSDLLoader(BSDLRegistry bsdlRegistry)
    {
        this.bsdlRegistry = bsdlRegistry;
    }

    @Override
    public void setBSDLRegistry(IBSDLRegistry bsdlRegistry)
    {
        if (bsdlRegistry instanceof BSDLRegistry)
        {
            this.bsdlRegistry = (BSDLRegistry)bsdlRegistry;
        }
    }

    @Override
    public Collection<Instance> readInstancesFromLocationOfConcept(Collection<ISemanticLocator> locators, ISemanticIdentifier conceptIdentifier) throws ModelException
    {
        BSDFLogger.getLogger().info("Reads and parses descriptors to load instances of: " + conceptIdentifier.toString());
        BSDLDocumentLoader documentLoader = createBSDMBSDLDocumentLoader();
        Collection<IAxiom> readAxioms = documentLoader.readFrom(locators);        
        return this.filterInstancesByConcept(readAxioms, conceptIdentifier);
    }
    
    @Override
    public Collection<Instance> readInstancesFromStringOfConcept(Collection<String> contents, ISemanticIdentifier conceptIdentifier) throws ModelException
    {
        BSDFLogger.getLogger().info("Reads and parses descriptors to load instances of: " + conceptIdentifier.toString());
        BSDLDocumentLoader documentLoader = createBSDMBSDLDocumentLoader();
        Collection<IAxiom> readAxioms = new ArrayList<IAxiom>();
        for (String content : contents)
        {
            readAxioms.addAll(documentLoader.readFrom(content));
        }
        return this.filterInstancesByConcept(readAxioms, conceptIdentifier);
    }
    
    private Collection<Instance> filterInstancesByConcept(Collection<IAxiom> readAxioms, ISemanticIdentifier conceptIdentifier) throws ModelException
    {
        Collection<Instance> readInstances = new ArrayList<Instance>();
        for (IAxiom axiom : readAxioms)
        {
            if (axiom instanceof Instance)
            {
                if (((Instance) axiom).getConcept().semanticAxiom().getSemanticIdentifier().equals(conceptIdentifier))
                {
                    readInstances.add((Instance) axiom);
                }
            }
        }
        return readInstances;
    }

    @Override
    public BSDLDocumentLoader createBSDMBSDLDocumentLoader() throws ModelException
    {
        BSDFLogger.getLogger().info("Instances a BSDLDocumentLoader");
        BSDLDocumentLoader documentLoader = new BSDLDocumentLoader(bsdlRegistry);
        IDocumentParser documentParser = createBSDLDocumentParser();
        if (this.preParsingRules.size() > 0)
        {
            DocumentPreParser preParser = 
                    new DocumentPreParser(
                            this.preParsingRules);
            documentParser.registerPreParser(preParser);
        }
        documentLoader.registerDocumentParser(documentParser);
        documentLoader.registerAxiomsChecker(new InstanceIntegrityChecker());
        documentLoader.readFrom(descriptorLocationsForBSDMCore());
        return documentLoader;
    }

    @Override
    public IDocumentParser createBSDLDocumentParser()
    {
        return this.createBSDLXMLDOMDocumentParser();
    }

    private IDocumentParser createBSDLXMLStAXDocumentParser()
    {
        IDocumentParser aBSDLParser = new BSDLXMLStAXParser();

        aBSDLParser.registerParser(new ConceptParser(bsdlRegistry));
        aBSDLParser.registerParser(new AttributeURIParser(bsdlRegistry));
        aBSDLParser.registerParser(new ConceptPropertyParser(bsdlRegistry));
        aBSDLParser.registerParser(new AttributeURIParser(XMLSyntax.SUPERCONCEPT(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.PROPERTY_NAME(), bsdlRegistry));
        aBSDLParser.registerParser(new PropertyMultiplicityParser(bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.MULTIPLICITY_MAX(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.MULTIPLICITY_MIN(), bsdlRegistry));
        aBSDLParser.registerParser(new ImportParser(bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.IMPORT_PREFIX(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.IMPORT_URI(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.IMPORT_DIALECT(), bsdlRegistry));
        aBSDLParser.registerParser(new InstanceParser(bsdlRegistry));
        aBSDLParser.registerParser(new InstancePropertyParser(bsdlRegistry));
        aBSDLParser.registerParser(new AttributeURIParser(XMLSyntax.CONCEPT_OF_INSTANCE_URI(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.PROPERTY_VALUE_LITERAL(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.PROPERTY_VALUE_NAME(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeURIParser(XMLSyntax.PROPERTY_VALUE_URI(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeURIParser(XMLSyntax.PROPERTY_VALUE_OF_INSTANCE_URI(), bsdlRegistry));

        aBSDLParser.registerParser(new OntologyParser(bsdlRegistry));
        aBSDLParser.registerParser(new AttributeURIParser(XMLSyntax.ONTOLOGY(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeURIParser(XMLSyntax.ONTOLOGY_LANGUAGE(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.NATURAL_LANGUAGE(), bsdlRegistry));
        aBSDLParser.registerParser(new AttributeSimpleParser(XMLSyntax.VERSION_NUMBER(), bsdlRegistry));

        return aBSDLParser;
    }
    
    private IDocumentParser createBSDLXMLDOMDocumentParser()
    {
        IDocumentParser aBSDLParser = new BSDLXMLDOMParser();

        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.ConceptParser(bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeURIParser(bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.ConceptPropertyParser(bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeURIParser(XMLSyntax.SUPERCONCEPT(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.PROPERTY_NAME(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.PropertyMultiplicityParser(bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.MULTIPLICITY_MAX(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.MULTIPLICITY_MIN(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.ImportParser(bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.IMPORT_PREFIX(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.IMPORT_URI(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.IMPORT_DIALECT(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.InstanceParser(bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.InstancePropertyParser(bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeURIParser(XMLSyntax.CONCEPT_OF_INSTANCE_URI(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.PROPERTY_VALUE_LITERAL(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.PROPERTY_VALUE_NAME(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeURIParser(XMLSyntax.PROPERTY_VALUE_URI(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeURIParser(XMLSyntax.PROPERTY_VALUE_OF_INSTANCE_URI(), bsdlRegistry));

        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.OntologyParser(bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeURIParser(XMLSyntax.ONTOLOGY(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeURIParser(XMLSyntax.ONTOLOGY_LANGUAGE(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.NATURAL_LANGUAGE(), bsdlRegistry));
        aBSDLParser.registerParser(new com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.AttributeSimpleParser(XMLSyntax.VERSION_NUMBER(), bsdlRegistry));

        return aBSDLParser;
    }

    public static Collection<ISemanticLocator> descriptorLocationsForBSDMCore() throws ParsingException
    {
        Collection<ISemanticLocator> descriptorLocations = new ArrayList<ISemanticLocator>();

        descriptorLocations.add(new SemanticLocator(ontologyXMLDir + "bsdlOntology.xml"));

        descriptorLocations.add(new SemanticLocator(ontologyXMLDir + "bsdmOntology.xml"));

        descriptorLocations.add(new SemanticLocator(ontologyXMLDir + "securityOntology.xml"));

        return descriptorLocations;
    }

}
