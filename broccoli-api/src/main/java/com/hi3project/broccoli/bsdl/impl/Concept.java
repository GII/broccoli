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

package com.hi3project.broccoli.bsdl.impl;

import com.hi3project.broccoli.bsdl.impl.meta.MetaPropertyOntologyLanguage;
import com.hi3project.broccoli.bsdl.impl.meta.MetaPropertyNaturalLanguage;
import com.hi3project.broccoli.bsdl.impl.meta.MetaPropertyVersion;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.impl.parsing.ReferenceToSemanticAxiom;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * A concept in BSDL may represent a frame-like definition that contains
 * properties and rules, or may act as the subject of an OWL-style axiom
 *<p>
 * It knows the ReferenceToSemanticAxiom that point to its superconcepts.
 *<p>
 * The properties of a Concept are of the Property type, intented to point to
 * other Concepts.
 *
 *
 * 
 */
public class Concept extends SemanticAxiom
{

    private Collection<ReferenceToSemanticAxiom<Concept>> superconcepts;
    private Collection<Property> properties;

    public Concept() throws SemanticModelException
    {
        super();
        this.superconcepts = new ArrayList<ReferenceToSemanticAxiom<Concept>>();
        this.properties = new ArrayList<Property>();
    }

    public Collection<ReferenceToSemanticAxiom<Concept>> superconcepts()
    {
        return superconcepts;
    }

    @Override
    public Collection<Property> properties()
    {
        return properties;
    }

    public Collection<Property> allProperties()
    {
        Collection<Property> allProperties = new ArrayList<Property>();
        allProperties.addAll(properties());
        for (ReferenceToSemanticAxiom<Concept> superconcept : superconcepts())
        {
            try
            {
                allProperties.addAll(superconcept.semanticAxiom().properties());
            } catch (SemanticModelException ex)
            {
                return allProperties;
            }
        }
        return allProperties;
    }

    public void addProperty(Property property) throws SemanticModelException
    {
        properties().add(property);
        property.setSubject(this);
    }

    public Property getProperty(String name) throws SemanticModelException
    {
        for (Property property : properties)
        {
            if (property.getName().equalsIgnoreCase(name))
            {
                return property;
            }
        }
        for (ReferenceToSemanticAxiom<Concept> refToConcept : superconcepts)
        {
            if (refToConcept.semanticAxiom() instanceof Concept)
            {
                Property foundProperty = ((Concept) refToConcept.semanticAxiom()).getProperty(name);
                if (null != foundProperty)
                {
                    return foundProperty;
                }
            }
        }
        return null;
    }

    /**
     * In no circunstances may one concept join with another
     *
     * @param semanticAxiom
     * @return
     * @throws SemanticModelException
     */
    @Override
    public SemanticAxiom joinWith(SemanticAxiom semanticAxiom) throws SemanticModelException
    {
        return this;
    }

    @Override
    public MetaPropertyNaturalLanguage getNaturalLanguage() throws SemanticModelException
    {
        return this.getOntology().semanticAxiom().getNaturalLanguage();
    }

    @Override
    public MetaPropertyOntologyLanguage getOntologyLanguage() throws SemanticModelException
    {
        return this.getOntology().semanticAxiom().getOntologyLanguage();
    }

    @Override
    public MetaPropertyVersion getVersionNumber() throws SemanticModelException
    {
        return this.getOntology().semanticAxiom().getVersionNumber();
    }   
    
    
}
