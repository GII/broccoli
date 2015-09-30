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

package com.hi3project.broccoli.bsdf.impl.implementation;

import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;

/**
 *
 * 
 */
public class OntologyToJavaReference extends ConceptBehaviourImplementation
{

    private SemanticIdentifier ontologyURI = null;
    private String javaNamespace = null;

    public OntologyToJavaReference(Instance instance) throws SemanticModelException
    {
        setInstance(instance);
        this.ontologyURI = getSinglePropertyAsInstanceURIValue("ontologyURI");
        this.javaNamespace = getSinglePropertyAsInstanceLiteralValue("javaNamespace");
    }

    public OntologyToJavaReference(String ontoloryURI, String javaNamespace) throws SemanticModelException
    {
        this.ontologyURI = new SemanticIdentifier(ontoloryURI);
        this.javaNamespace = javaNamespace;
    }

    public SemanticIdentifier ontologyURI()
    {
        return ontologyURI;
    }

    public String javaNamespace()
    {
        return javaNamespace;
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/implementation#ontologyToJavaReference");
    }

}