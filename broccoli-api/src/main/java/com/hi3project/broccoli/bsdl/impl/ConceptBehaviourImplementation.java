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
import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>
 * Wrapper for Instance-like ISubjects that expands the Concept functionallity.
 * With several utility methods to access its attributes and properties, that
 * may be of different kinds
 *<p>
 * Any implementation of this must have an ISemanticIdentifier that indicates
 * the Concept this ConceptBehaviourImplementation works for
 *<p>
 * This is the base for the implementations of BSDO concepts
 *
 *
 * 
 */
public abstract class ConceptBehaviourImplementation
{

    protected ISubject instance;


    public ISubject getInstance() throws SemanticModelException
    {
        return instance;
    }

    public void setInstance(ISubject instance) throws SemanticModelException
    {
        this.instance = instance;
    }

    public Collection<IObject> getProperties(String name) throws SemanticModelException
    {
        Collection<IObject> properties = new ArrayList<IObject>();
        if (null != getInstance() && getInstance() instanceof Instance)
        {
            Iterator<PropertyValue> iterator = ((Instance) getInstance()).getProperty(name).iterator();
            while (iterator.hasNext())
            {
                properties.add(iterator.next().getObject());
            }
        }
        return properties;
    }

    public IObject getSingleProperty(String name) throws SemanticModelException
    {
        Collection<IObject> properties = getProperties(name);
        if (null != properties && properties.size() > 0)
        {
            return properties.iterator().next();
        }
        return null;
    }

    public Instance getSinglePropertyAsInstance(String name) throws SemanticModelException
    {
        IObject iobject = getSingleProperty(name);
        if (null != iobject && iobject instanceof Instance)
        {
            return (Instance) iobject;
        }
        return null;
    }

    public InstanceLiteral getSinglePropertyAsInstanceLiteral(String name) throws SemanticModelException
    {
        IObject iobject = getSingleProperty(name);
        if (null != iobject && iobject instanceof InstanceLiteral)
        {
            return (InstanceLiteral) iobject;
        }
        return null;
    }

    public InstanceURI getSinglePropertyAsInstanceURI(String name) throws SemanticModelException
    {
        IObject iobject = getSingleProperty(name);
        if (null != iobject && iobject instanceof InstanceURI)
        {
            return (InstanceURI) iobject;
        }
        return null;
    }

    public String getSinglePropertyAsInstanceLiteralValue(String name) throws SemanticModelException
    {
        InstanceLiteral instanceLiteral = getSinglePropertyAsInstanceLiteral(name);
        if (null != instanceLiteral)
        {
            return instanceLiteral.getValue();
        }
        return null;
    }

    public SemanticIdentifier getSinglePropertyAsInstanceURIValue(String name) throws SemanticModelException
    {
        InstanceURI instanceURI = getSinglePropertyAsInstanceURI(name);
        if (null != instanceURI)
        {
            return instanceURI.getValue();
        }
        return null;
    }

    public abstract SemanticIdentifier conceptIdentifier() throws SemanticModelException;

}
