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

import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.IPredicate;
import com.hi3project.broccoli.bsdl.api.meta.INaturalLanguage;
import com.hi3project.broccoli.bsdl.api.IOntology;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.meta.IOntologyLanguage;
import com.hi3project.broccoli.bsdl.api.meta.IVersionNumber;
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Implementation of IOntology that contains: Concepts, Instances and Imports
 *
 *
 * 
 */
public class Ontology extends SemanticAxiom implements IOntology
{

    private Collection<IImport> imports = new ArrayList<IImport>();
    private List<Concept> concepts = new ArrayList<Concept>();
    private List<Instance> instances = new ArrayList<Instance>();
    
    

    public Ontology() throws SemanticModelException
    {
        super();
    }
    
    public Ontology(ISemanticIdentifier semanticIdentifier) throws SemanticModelException
    {
        super();
        this.setSemanticIdentifier(semanticIdentifier);
    }
    
    
    @Override
    public ISemanticIdentifier identifier()
    {
        return this.getSemanticIdentifier();
    }
    
    
    public Collection<Concept> getConcepts(SemanticIdentifier sa)
    {
        Collection<Concept> conceptsCol = new ArrayList<Concept>();
        for (Concept concept : concepts)
        {
            if (concept.getSemanticIdentifier().equals(sa))
            {
                conceptsCol.add(concept);
            }
            // TODO: if concept subsumes sa...
        }
        return conceptsCol;
    }

    public Collection<Concept> concepts()
    {
        return concepts;
    }
    
    public Collection<Concept> getDistinctConcepts()
    {
        Collection<Concept> distinctConcepts = new ArrayList<Concept>();
        for (Concept concept : this.concepts())
        {
            if (!isContained(concept, distinctConcepts))
            {
                distinctConcepts.add(concept);
            }
        }
        return distinctConcepts;
    }

    public void addAxiom(SemanticAxiom axiom)
    {
        if (axiom instanceof Concept)
        {
            addConcept((Concept) axiom);
        }
        if (axiom instanceof Instance)
        {
            addInstance((Instance) axiom);
        }
    }

    public void addConcept(Concept concept)
    {
        if (!concepts.contains(concept))
        {
            concepts.add(concept);
        }
    }

    public Collection<Instance> getInstancesByConceptURI(SemanticIdentifier sa) throws SemanticModelException
    {
        Collection<Concept> conceptsToSearch = getConcepts(sa);
        Collection<Instance> instancesCol = new ArrayList<Instance>();
        for (Instance instance : instances)
        {
            for (Concept conceptToSearch : conceptsToSearch)
            {
                if (instance.getConcept().semanticAxiom().equals(conceptToSearch))
                {
                    instancesCol.add(instance);
                }
            }
        }
        return instancesCol;
    }

    public Collection<Instance> instances()
    {
        return instances;
    }
    
    public Collection<Instance> getDistinctInstances()
    {
        Collection<Instance> distinctInstances = new ArrayList<Instance>();
        for (Instance instance : this.instances())
        {
            if (!isContained(instance, distinctInstances))
            {
                distinctInstances.add(instance);
            }
        }
        return distinctInstances;
    }

    public void addInstance(Instance instance)
    {
        if (!instances.contains(instance))
        {
            instances.add(instance);
        }
    }

    public Collection<IImport> imports()
    {
        return imports;
    }

    public void addImport(IImport imp)
    {
        if (!imports.contains(imp))
        {
            imports.add(imp);
        }
    }

    @Override
    public SemanticAxiom joinWith(SemanticAxiom other) throws SemanticModelException
    {
        if (!(other instanceof Ontology))
        {
            throw new SemanticModelException("Not an ontology: " + other.toString(), null);
        }
        if (other.getSemanticIdentifier().equals(getSemanticIdentifier()))
        {
            Ontology otherOntology = (Ontology) other;
            for (IImport imp : otherOntology.imports())
            {
                addImport(imp);
            }
            for (Concept concept : otherOntology.concepts())
            {
                addConcept(concept);
            }
            for (Instance instance : otherOntology.instances())
            {
                addInstance(instance);
            }
        }
        return this;
    }

    @Override
    public Collection<IAxiom> axioms()
    {
        Collection<IAxiom> axioms = new ArrayList<IAxiom>();
        axioms.addAll(getDistinctConcepts());
        axioms.addAll(getDistinctInstances());
        return axioms;
    }

    @Override
    public IVersionNumber version() throws SemanticModelException
    {
        return getVersionNumber();
    }

    @Override
    public IOntologyLanguage ontologyLanguage() throws SemanticModelException
    {
        return getOntologyLanguage();
    }

    @Override
    public INaturalLanguage naturalLanguage() throws SemanticModelException
    {
        return getNaturalLanguage();
    }

    @Override
    public Collection properties() throws SemanticModelException
    {
        Collection<IPredicate> properties = new ArrayList<IPredicate>();
        for (Concept concept : concepts())
        {
            properties.add(new Property().setSubject(this).setObject(concept));
        }
        for (Instance instance : instances())
        {
            properties.add(new PropertyValue().setSubject(this).setObject(instance));
        }
        return properties;
    }
    
    
    private static boolean isContained(SemanticAxiom axiomContained, Collection axioms)
    {
        if (null == axiomContained) return false;
        for (Object axiom : axioms)
        {
            if ( null != axiom
                    && null != ((SemanticAxiom)axiom).getSemanticIdentifier()
                    && null != axiomContained.getSemanticIdentifier()
                    && ((SemanticAxiom)axiom).getSemanticIdentifier().equals(axiomContained.getSemanticIdentifier()) )
            {
                return true;
            }
        }
        return false;
    }
    
}
