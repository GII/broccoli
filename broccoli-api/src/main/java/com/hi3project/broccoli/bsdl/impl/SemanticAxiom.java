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

import com.hi3project.broccoli.conf.ProjProperties;
import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.meta.MetaPropertyNaturalLanguage;
import com.hi3project.broccoli.bsdl.impl.meta.MetaPropertyOntologyLanguage;
import com.hi3project.broccoli.bsdl.impl.meta.MetaPropertyVersion;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.impl.parsing.ReferenceToSemanticAxiom;

/**
 * <p>
 * An axiom that may act as Subject and Object. And knows:
 *
 * <ul> 
 * <li>its ISemanticIdentifier (must have)</li>
 * <li>optionally, its ontology (recomended)</li>
 * <li>when no ontology is present (or overwriting it): ontology language,
 * natural language, and version number</li>
 * </ul>
 * <p>
 * Any SemanticAxiom implementation must implement the joinWith(anotherAxiom)
 * method, that is intended as a "merge" operation for different instances
 * axioms with the same ISemanticIdentifier
 *
 *
 * 
 */
public abstract class SemanticAxiom implements ISubject, IObject
{

    private ISemanticIdentifier semanticIdentifier;

    private MetaPropertyOntologyLanguage ontologyLanguage = null;
    private MetaPropertyNaturalLanguage naturalLanguage = null;
    private MetaPropertyVersion versionNumber = null;

    private ReferenceToSemanticAxiom<Ontology> ontology = null;

    public SemanticAxiom() throws SemanticModelException
    {
        ontologyLanguage = new MetaPropertyOntologyLanguage(ProjProperties.BSDL);
        naturalLanguage = new MetaPropertyNaturalLanguage();
        versionNumber = new MetaPropertyVersion();
    }

    public ReferenceToSemanticAxiom<Ontology> getOntology()
    {
        return ontology;
    }

    public void setOntology(ReferenceToSemanticAxiom<Ontology> ontology) throws SemanticModelException
    {
        this.ontology = ontology;
        if (null != ontology) this.ontology.semanticAxiom().addAxiom(this);
    }

    public void setOntology(IBSDLRegistry bsdlRegistry, ISemanticIdentifier ontologyIdentifier) throws SemanticModelException
    {
        this.setOntology(new ReferenceToSemanticAxiom<Ontology>(bsdlRegistry, ontologyIdentifier, Ontology.class));
    }

    public MetaPropertyNaturalLanguage getNaturalLanguage() throws SemanticModelException
    {
        return naturalLanguage;
    }

    public void setNaturalLanguage(MetaPropertyNaturalLanguage naturalLanguage)
    {
        this.naturalLanguage = naturalLanguage;
    }

    public MetaPropertyOntologyLanguage getOntologyLanguage() throws SemanticModelException
    {
        return ontologyLanguage;
    }

    public void setOntologyLanguage(MetaPropertyOntologyLanguage ontologyLanguage)
    {
        this.ontologyLanguage = ontologyLanguage;
    }

    public MetaPropertyVersion getVersionNumber() throws SemanticModelException
    {
        return versionNumber;
    }

    public void setVersionNumber(MetaPropertyVersion versionNumber)
    {
        this.versionNumber = versionNumber;
    }

    public ISemanticIdentifier getSemanticIdentifier()
    {
        return semanticIdentifier;
    }

    public void setSemanticIdentifier(ISemanticIdentifier semanticIdentifier)
    {
        this.semanticIdentifier = semanticIdentifier;
    }

    public SemanticAxiom semanticAxiom() throws SemanticModelException
    {
        return this;
    }

    public abstract SemanticAxiom joinWith(SemanticAxiom semanticAxiom) throws SemanticModelException;

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
        final SemanticAxiom other = (SemanticAxiom) obj;
        if (null != getSemanticIdentifier())
        {
            return (null != other.getSemanticIdentifier()
                    && other.getSemanticIdentifier().equals(getSemanticIdentifier()));
        } else
        {
            return null == other.getSemanticIdentifier();
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 47 * hash + (this.semanticIdentifier != null ? this.semanticIdentifier.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return "SemanticAxiom{" + "semanticIdentifier=" + semanticIdentifier + '}';
    }

}
