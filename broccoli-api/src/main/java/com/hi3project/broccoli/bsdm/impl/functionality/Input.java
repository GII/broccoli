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

package com.hi3project.broccoli.bsdm.impl.functionality;

import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInput;

/**
 *
 * 
 */
public class Input extends ConceptBehaviourImplementation implements IInput
{

    private String name = null;
    private SemanticIdentifier semanticAnnotation = null;

    public Input(Instance instance) throws SemanticModelException
    {
        setInstance(instance);
        name = getSinglePropertyAsInstanceLiteralValue("name");
        semanticAnnotation = getSinglePropertyAsInstanceURIValue("type");
    }

    public Input(String name, String identifier) throws SemanticModelException
    {
        this.name = name;
        this.semanticAnnotation = new SemanticIdentifier(identifier);
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/profile#input");
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public ISemanticIdentifier getSemanticAnnotation()
    {
        return semanticAnnotation;
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
        final Input other = (Input) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
        {
            return false;
        }
        return this.semanticAnnotation == other.semanticAnnotation || (this.semanticAnnotation != null && this.semanticAnnotation.equals(other.semanticAnnotation));
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 71 * hash + (this.semanticAnnotation != null ? this.semanticAnnotation.hashCode() : 0);
        return hash;
    }

}
