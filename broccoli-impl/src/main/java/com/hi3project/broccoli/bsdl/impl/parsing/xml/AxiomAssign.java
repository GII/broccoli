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

package com.hi3project.broccoli.bsdl.impl.parsing.xml;

import com.hi3project.broccoli.bsdl.api.IAxiomProperty;
import com.hi3project.broccoli.bsdl.api.ISyntaxElement;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentParser;
import com.hi3project.broccoli.bsdl.impl.Concept;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.InstanceLiteral;
import com.hi3project.broccoli.bsdl.impl.InstanceURI;
import com.hi3project.broccoli.bsdl.impl.Ontology;
import com.hi3project.broccoli.bsdl.impl.Property;
import com.hi3project.broccoli.bsdl.impl.PropertyValue;
import com.hi3project.broccoli.bsdl.impl.SemanticAxiom;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.meta.MetaPropertyNaturalLanguage;
import com.hi3project.broccoli.bsdl.impl.meta.MetaPropertyOntologyLanguage;
import com.hi3project.broccoli.bsdl.impl.meta.MetaPropertyVersion;
import com.hi3project.broccoli.bsdl.impl.parsing.Import;
import com.hi3project.broccoli.bsdl.impl.parsing.InstanceReference;
import com.hi3project.broccoli.bsdl.impl.parsing.Multiplicity;
import com.hi3project.broccoli.bsdl.impl.parsing.ReferenceToSemanticAxiom;
import com.hi3project.broccoli.bsdl.impl.parsing.SimpleProperty;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;

/**
 * <p>
 *  <b>Description:</b></p>
 *
 *
 * <p><b>Creation date:</b> 
 * 21-11-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 21-11-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class AxiomAssign 
{
    
    private BSDLRegistry bsdlRegistry = null;
    private IDocumentParser documentParser = null;
    
    
    public AxiomAssign(BSDLRegistry bsdlRegistry)
    {
        this.bsdlRegistry = bsdlRegistry;
    }
    
    
    public void setDocumentParser(IDocumentParser documentParser) {
        this.documentParser = documentParser;
    }
    
    public IDocumentParser getDocumentParser() {
        return documentParser;
    }
    
    
    /*
     *  A Concept:
     *      -has always an URI as an attribute
     *      -may have an URI for a superconcept, as an attribute
     *      may have N superconcepts as contained elements. Each superconcept may be:
     *          a concept
     *          or an URI
     *      may have N properties as contained elements   
     */
    public boolean assignParsedElement(Concept parsedConcept, String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException
    {
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.URI()) && syntaxElement instanceof SemanticIdentifier)
        {
            parsedConcept.setSemanticIdentifier((SemanticIdentifier) syntaxElement);
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.ONTOLOGY()))
        {
            if (syntaxElement instanceof SemanticIdentifier)
            {
                parsedConcept.setOntology(bsdlRegistry, (SemanticIdentifier) syntaxElement);
            }
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.SUPERCONCEPT()))
        {
            if (syntaxElement instanceof SemanticIdentifier)
            {
                parsedConcept.superconcepts().add(
                        new ReferenceToSemanticAxiom<Concept>(
                                bsdlRegistry, 
                                (SemanticIdentifier) syntaxElement,
                                Concept.class
                        ));
                return true;
            }
            if (syntaxElement instanceof Concept)
            {
                parsedConcept.superconcepts().add(
                        new ReferenceToSemanticAxiom<Concept>((Concept) syntaxElement));
                return true;
            }
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.PROPERTY()))
        {
            parsedConcept.addProperty(((Property) syntaxElement));
            return true;
        }
        return false;
    }
    
    /*
     * A property: -has a name as attribute - may have an URI as attribute - may
     * contain N concepts as contained alements - may have N properties as
     * contained elements (syntactic sugar for a containted concept) - may have
     * a multiplicity (as contained element)
     */
    public boolean assignParsedElement(Property parsedProperty, String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException {
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.PROPERTY_NAME()) && syntaxElement instanceof IAxiomProperty) {
            parsedProperty.setName(((IAxiomProperty) syntaxElement).getValue().toString());
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.URI()) && syntaxElement instanceof SemanticIdentifier) {
            parsedProperty.setObject(
                    new ReferenceToSemanticAxiom<Concept>(
                            bsdlRegistry, 
                            (SemanticIdentifier) syntaxElement,
                            Concept.class
                    ));
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.MULTIPLICITY()) && syntaxElement instanceof Multiplicity) {
            parsedProperty.setMultiplicity(((Multiplicity) syntaxElement));
        }
        return false;
    }
    
    
    public boolean assignParsedElement(Import parsedImport, String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException {
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.IMPORT_PREFIX()) && syntaxElement instanceof IAxiomProperty) {
            parsedImport.setPrefix(((IAxiomProperty) syntaxElement).getValue().toString());
            if (null != getDocumentParser()) getDocumentParser().addImport(parsedImport);
            return true;
        }        
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.IMPORT_URI()) && syntaxElement instanceof IAxiomProperty) {
            parsedImport.setPrefixed(((IAxiomProperty) syntaxElement).getValue().toString());
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.IMPORT_DIALECT()) && syntaxElement instanceof IAxiomProperty) {
            parsedImport.setDialect(((IAxiomProperty) syntaxElement).getValue().toString());
            return true;
        }
        return false;
    }
    
    
    /*
     *  An Instance:
     *      - has always "URI" as attribute
     *      - has always "of" as atribute pointing to a concept s URI
     *      may have N "with" elements, each one representing a property value
     */
    public boolean assignParsedElement(Instance parsedInstance, String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException
    {
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.URI()) && syntaxElement instanceof SemanticIdentifier)
        {
            parsedInstance.setSemanticIdentifier((SemanticIdentifier) syntaxElement);
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.ONTOLOGY()))
        {
            if (syntaxElement instanceof SemanticIdentifier)
            {
                parsedInstance.setOntology(bsdlRegistry, (SemanticIdentifier) syntaxElement);
            }
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.CONCEPT_OF_INSTANCE_URI()) && syntaxElement instanceof SemanticIdentifier)
        {
            parsedInstance.setConcept(bsdlRegistry, (SemanticIdentifier) syntaxElement);
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.PROPERTY_VALUE()) && syntaxElement instanceof PropertyValue)
        {
            ((PropertyValue) syntaxElement).setOntology(parsedInstance.getOntology());
            parsedInstance.addProperty(((PropertyValue) syntaxElement));
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.IMPORT()) && syntaxElement instanceof Import)
        {
            parsedInstance.imports().add((Import) syntaxElement);
            return true;
        }
        return false;
    }
    
    
    public boolean assignParsedElement(PropertyValue parsedPropertyValue, String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException
    {
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.PROPERTY_VALUE_NAME()) && syntaxElement instanceof IAxiomProperty)
        {
            parsedPropertyValue.setName(((IAxiomProperty) syntaxElement).getValue().toString());
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.PROPERTY_VALUE_LITERAL()) && syntaxElement instanceof IAxiomProperty)
        {
            InstanceLiteral value = new InstanceLiteral(((IAxiomProperty) syntaxElement).getValue().toString());
            // TODO: add to ontology ?
            parsedPropertyValue.setObject(value);
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.PROPERTY_VALUE_URI()) && syntaxElement instanceof SemanticIdentifier)
        {
            InstanceURI value = new InstanceURI((SemanticIdentifier) syntaxElement);
            // TODO: add to ontology ?
            parsedPropertyValue.setObject(value);
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.PROPERTY_VALUE()) && syntaxElement instanceof PropertyValue)
        {
            ((PropertyValue) syntaxElement).setOntology(parsedPropertyValue.getOntology());
            if (null != parsedPropertyValue.getObject())
            {
                if (parsedPropertyValue.getObject() instanceof Instance)
                {
                    ((Instance) parsedPropertyValue.getObject()).addProperty((PropertyValue) syntaxElement);
                }
            } else
            {
                Instance newInstance = new Instance();
                newInstance.addProperty((PropertyValue) syntaxElement);
                parsedPropertyValue.setObject(newInstance);
                newInstance.setOntology(parsedPropertyValue.getOntology());
                parsedPropertyValue.setConceptForObjectInstance();
            }
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.INSTANCE()) && syntaxElement instanceof Instance)
        {
            ((Instance) syntaxElement).setOntology(parsedPropertyValue.getOntology());
            parsedPropertyValue.setObject((Instance) syntaxElement);
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.PROPERTY_VALUE_OF_INSTANCE_URI()) && syntaxElement instanceof SemanticIdentifier)
        {
            parsedPropertyValue.setObject(
                    new InstanceReference(
                            new ReferenceToSemanticAxiom(
                                    bsdlRegistry,
                                    (SemanticIdentifier) syntaxElement,
                                    Instance.class
                            )));
            return true;
        }
        return false;
    }
    
    /*
     *  An Ontology:
     *      -has always an URI as an attribute
     *      -may have an URI as ontologyLanguage
     *      -may have a naturalLanguage referenced as a literal
     *      -may have a versionNumber, as a literal
     *      may have N concepts as contained elements
     *      may have N instances as contained elements
     */
    public boolean assignParsedElement(Ontology parsedOntology, String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException
    {
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.URI()) && syntaxElement instanceof SemanticIdentifier)
        {
            parsedOntology.setSemanticIdentifier((SemanticIdentifier) syntaxElement);
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.ONTOLOGY_LANGUAGE()) && syntaxElement instanceof SemanticIdentifier)
        {
            parsedOntology.setOntologyLanguage(new MetaPropertyOntologyLanguage((SemanticIdentifier) syntaxElement));
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.NATURAL_LANGUAGE()) && syntaxElement instanceof SimpleProperty)
        {
            parsedOntology.setNaturalLanguage(new MetaPropertyNaturalLanguage(((SimpleProperty) syntaxElement).getValue().toString()));
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.VERSION_NUMBER()) && syntaxElement instanceof SimpleProperty)
        {
            parsedOntology.setVersionNumber(new MetaPropertyVersion(((SimpleProperty) syntaxElement).getValue().toString()));
            return true;
        }
        if (syntaxElement instanceof Concept || syntaxElement instanceof Instance)
        {
            ((SemanticAxiom) syntaxElement).setOntology(bsdlRegistry, parsedOntology.getSemanticIdentifier());
            return true;
        }
        return false;
    }
    
    /*
     *  A property:
     *          -has a min: number or "*"
     *          -has a max: number or "*"
     */
    public boolean assignParsedElement(Multiplicity parsedMultiplicity, String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException {
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.MULTIPLICITY_MIN()) && syntaxElement instanceof SimpleProperty) {
            if (((SimpleProperty) syntaxElement).getValue().toString().equalsIgnoreCase("*")) {
                parsedMultiplicity.setMin(null);
            } else {
                parsedMultiplicity.setMin(Integer.parseInt(((SimpleProperty) syntaxElement).getValue().toString()));
            }
            return true;
        }
        if (syntaxElementName.equalsIgnoreCase(XMLSyntax.MULTIPLICITY_MAX()) && syntaxElement instanceof SimpleProperty) {
            if (((SimpleProperty) syntaxElement).getValue().toString().equalsIgnoreCase("*")) {
                parsedMultiplicity.setMax(null);
            } else {
                parsedMultiplicity.setMax(Integer.parseInt(((SimpleProperty) syntaxElement).getValue().toString()));
            }
            return true;
        }
        return false;
    }
    
}
