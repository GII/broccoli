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

import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.IPredicate;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.impl.parsing.ReferenceToSemanticAxiom;

/**
 * A PropertyValue has:
 * <ul>
 * <li>an Instance for subject</li>
 * <li>an IObject</li>
 * <li>a name</li>
 * <li>a multiplicity</li>
 * </ul>
 *
 * 
 */
public class PropertyValue implements IPredicate
{

    private Instance subjectInstance;
    private IObject object;
    private String name;
    private ReferenceToSemanticAxiom<Ontology> ontology = null;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ReferenceToSemanticAxiom<Ontology> getOntology()
    {
        return ontology;
    }

    public void setOntology(ReferenceToSemanticAxiom<Ontology> ontology)
    {
        this.ontology = ontology;
    }

    @Override
    public ISubject getSubject()
    {
        return subjectInstance;
    }

    @Override
    public IObject getObject()
    {
        return object;
    }

    @Override
    public IPredicate setSubject(ISubject subject) throws SemanticModelException
    {
        if (!(subject instanceof Instance))
        {
            throw new SemanticModelException("", null);
        }
        this.subjectInstance = (Instance) subject;
        return this;
    }

    @Override
    public IPredicate setObject(IObject object) throws SemanticModelException
    {
        this.object = object;
        return this;
    }

    /**
     * When the "object" (as in subject-predicate-object) is an Instances, it
     * knows how to set its Concept (because that Concept is the object of the
     * Concept Property that holds the same name of this PropertyValue)
     *
     * @return
     * @throws SemanticModelException
     */
    public Concept setConceptForObjectInstance() throws SemanticModelException
    {
        Concept foundConcept = null;
        if (null != getObject() && getObject() instanceof Instance && null != subjectInstance)
        {
            SemanticAxiom semanticAxiom = subjectInstance.getConcept().semanticAxiom();
            if (semanticAxiom instanceof Concept)
            {
                Property foundProperty = ((Concept) semanticAxiom).getProperty(name);
                if (null != foundProperty && foundProperty.getObject() instanceof SemanticAxiom)
                {
                    SemanticAxiom axiomForPropertyObject = ((SemanticAxiom) foundProperty.getObject()).semanticAxiom();
                    if (axiomForPropertyObject instanceof Concept)
                    {
                        foundConcept = (Concept) axiomForPropertyObject;
                    }
                }
            }
            Instance objectInstance = (Instance) getObject();
            objectInstance.setConcept(foundConcept);
        }
        return foundConcept;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final PropertyValue other = (PropertyValue) obj;
        return other.getObject().equals(this.getObject())
                && other.getName().equals(this.getName());
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + (this.subjectInstance != null && this.subjectInstance.getSemanticIdentifier() != null ? this.subjectInstance.getSemanticIdentifier().hashCode() : 0);
        hash = 23 * hash + (this.object != null ? this.object.hashCode() : 0);
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
