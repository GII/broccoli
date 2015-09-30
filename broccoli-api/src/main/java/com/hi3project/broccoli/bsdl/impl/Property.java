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
import com.hi3project.broccoli.bsdl.impl.parsing.Multiplicity;

/**
 *  A Property has: 
 * <ul>
 * <li>a Concept for subject</li>
 * <li>an IObject</li>
 * <li>a name</li>
 * <li>a multiplicity</li>
 * </ul>
 * 
 */
public class Property implements IPredicate {

    private Concept subjectConcept;
    private IObject object;
    private String name;
    private Multiplicity multiplicity;

    public Property() {
        multiplicity = new Multiplicity();
    }

    public Property(String name) {
        this();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public ISubject getSubject() {
        return subjectConcept;
    }

    @Override
    public IObject getObject() {
        return object;
    }

    @Override
    public IPredicate setSubject(ISubject subject) throws SemanticModelException {
        if (!(subject instanceof Concept)) {
            throw new SemanticModelException("", null);
        }
        this.subjectConcept = (Concept) subject;
        return this;
    }

    @Override
    public IPredicate setObject(IObject object) throws SemanticModelException {
        this.object = object;
        return this;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Property other = (Property) obj;
        return other.getObject().equals(this.getObject())
                && other.getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.subjectConcept != null && this.subjectConcept.getSemanticIdentifier() != null ? this.subjectConcept.getSemanticIdentifier().hashCode() : 0);
        hash = 83 * hash + (this.object != null ? this.object.hashCode() : 0);
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 83 * hash + (this.multiplicity != null ? this.multiplicity.hashCode() : 0);
        return hash;
    }
}
