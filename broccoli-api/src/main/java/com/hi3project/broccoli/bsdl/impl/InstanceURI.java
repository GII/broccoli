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
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * 
 */
public class InstanceURI extends ConceptBehaviourImplementation implements ISubject, IObject {

    private SemanticIdentifier identifier;

    public InstanceURI(SemanticIdentifier value) {
        this.identifier = value;
    }

    public SemanticIdentifier getValue() {
        return identifier;
    }

    public void setValue(SemanticIdentifier value) {
        this.identifier = value;
    }
    
    @Override
    public ISubject getInstance() {
        return this;
    }

    @Override
    public void setInstance(ISubject instance) throws SemanticModelException {
        if (instance instanceof InstanceURI) {
            super.setInstance(instance);
            setValue(((InstanceURI)instance).getValue());
        }        
        throw new SemanticModelException("Cannot set instance of: " + instance.toString(), null);
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdl#semanticIdentifier");
    }

    @Override
    public Collection properties() {
        return new ArrayList();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InstanceURI other = (InstanceURI) obj;
        return this.identifier == other.identifier || (this.identifier != null && this.identifier.equals(other.identifier));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        return hash;
    }
    
    
}
