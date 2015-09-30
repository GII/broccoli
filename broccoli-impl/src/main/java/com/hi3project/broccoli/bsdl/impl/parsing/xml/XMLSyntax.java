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

import javax.xml.namespace.QName;

/**
 *  Vocabulary for BSDL XML syntax, as it is defined on the XML Schema
 * 
 * 
 */
public class XMLSyntax {
    
    public static String ONTOLOGY() {
        return "ontology";
    }
    
    public static String ONTOLOGY_LANGUAGE() {
        return "ontologyLanguage";
    }
    
    public static String NATURAL_LANGUAGE() {
        return "naturalLanguage";
    }
    
    public static String VERSION_NUMBER() {
        return "versionNumber";
    }
    
    public static String IMPORT() {
        return "import";
    }
    
    public static String IMPORT_PREFIX() {
        return "prefix";
    }
    
    public static String IMPORT_URI() {
        return "to";
    }
    
    public static String IMPORT_DIALECT() {
        return "dialect";
    }
    
    public static String CONCEPT() {
        return "concept";
    }
    
    public static String INSTANCE() {
        return "instance";
    }
    
    public static String SUPERCONCEPT() {
        return "subconceptOf";
    }
    
    public static String URI() {
        return "URI";
    }
    
    public static String CONCEPT_OF_INSTANCE_URI() {
        return "of";
    }
    
    public static String PROPERTY() {
        return "property";
    }
    
    public static String PROPERTY_NAME() {
        return "name";
    }        
    
    public static String MULTIPLICITY() {
        return "multiplicity";
    }
    
    public static String MULTIPLICITY_MIN() {
        return "min";
    }
    
    public static String MULTIPLICITY_MAX() {
        return "max";
    }
    
    public static String PROPERTY_VALUE() {
        return "with";
    }
    
    public static String PROPERTY_VALUE_NAME() {
        return "property";
    }        
    
    public static String PROPERTY_VALUE_LITERAL() {
        return "value";
    }
    
    public static String PROPERTY_VALUE_URI() {
        return "valueURI";
    } 
    
    public static String PROPERTY_VALUE_OF_INSTANCE_URI() {
        return "ofURI";
    }
    
    public static QName qnameURI() {
        return new QName(XMLSyntax.URI());
    }
}
