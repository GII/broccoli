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

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.impl.parsing.ReferenceToSemanticAxiom;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * An Instance has always a Concept that defines it.
 *<p>
 * The properties are in the form of PropertyValue, that point to literals or
 * other Instances.
 *<p>
 * Optionally, the Instance knows its associated imports
 * <p>
 * 
 */
public class Instance extends SemanticAxiom
{

    private Collection<PropertyValue> properties = new ArrayList<PropertyValue>();
    private ReferenceToSemanticAxiom<Concept> concept;
    private Collection<IImport> imports;

    public Instance() throws SemanticModelException
    {
        super();
        this.imports = new ArrayList<IImport>();
    }

    public Instance(IBSDLRegistry bsdlRegistry, SemanticIdentifier id) throws SemanticModelException
    {
        this();
        this.imports = new ArrayList<IImport>();
        setConcept(bsdlRegistry, id);
    }

    public Collection<IImport> imports()
    {
        return imports;
    }

    @Override
    public Collection properties() throws SemanticModelException
    {
        return getPropertiesAsPropertyValue();
    }

    public Collection<PropertyValue> getPropertiesAsPropertyValue() throws SemanticModelException
    {
        return properties;
    }

    public PropertyValue addProperty(PropertyValue propertyValue) throws SemanticModelException
    {
        properties().add(propertyValue);
        propertyValue.setSubject(this);
        return propertyValue;
    }

    public Collection<PropertyValue> getProperty(String name) throws SemanticModelException
    {
        Collection<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
        for (PropertyValue property : getPropertiesAsPropertyValue())
        {
            if (property.getName().equals(name))
            {
                propertyValues.add((PropertyValue) property);
            }
        }
        return propertyValues;
    }

    public PropertyValue containsProperty(PropertyValue otherPropertyValue) throws SemanticModelException
    {
        for (PropertyValue propertyValue : getPropertiesAsPropertyValue())
        {
            if (propertyValue.equals(otherPropertyValue))
            {
                return propertyValue;
            }
        }
        return null;
    }

    public PropertyValue containsPropertyWithObject(ISemanticIdentifier otherPropertyValueSemanticIdentifier) throws SemanticModelException
    {
        if (null == otherPropertyValueSemanticIdentifier)
        {
            return null;
        }
        for (PropertyValue propertyValue : getPropertiesAsPropertyValue())
        {
            if (propertyValue.getObject() instanceof SemanticAxiom)
            {
                if (otherPropertyValueSemanticIdentifier.equals(
                        ((SemanticAxiom) propertyValue.getObject()).getSemanticIdentifier()))
                {
                    return propertyValue;
                }
            }
        }
        return null;
    }

    public ReferenceToSemanticAxiom<Concept> getConcept()
    {
        return concept;
    }

    public final void setConcept(IBSDLRegistry bsdlRegistry, SemanticIdentifier id) throws SemanticModelException
    {
        this.concept = new ReferenceToSemanticAxiom<Concept>(bsdlRegistry, id, Concept.class);
    }

    public void setConcept(Concept concept) throws SemanticModelException
    {
        this.concept = new ReferenceToSemanticAxiom<Concept>(concept);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj) 
        {
            return false;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (!(obj instanceof Instance))
        {
            return false;
        }
        try
        {
            Instance other = (Instance) obj;
            for (PropertyValue otherPropertyValue : other.getPropertiesAsPropertyValue())
            {
                if (null == containsProperty(otherPropertyValue))
                {
                    return false;
                }
            }
            return true;
        } catch (SemanticModelException ex)
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + (this.properties != null ? this.properties.hashCode() : 0);
        hash = 67 * hash + (this.concept != null ? this.concept.hashCode() : 0);
        return hash;
    }

    /**
     * <p>
     * Precondition: only intented for Instances with the same
     * ISemanticIdentifier.
     *<p>
     * The "other" Instance may have properties not referenced by this: those
     * properties are added.
     *
     * <p>
     * When the "other" Instance has a property that this one also has, but the
     * "other" Instance property is different, joinWith proceeds recursively.
     *
     */
    @Override
    public SemanticAxiom joinWith(SemanticAxiom other) throws SemanticModelException
    {
        if (!(other instanceof Instance))
        {
            throw new SemanticModelException("Not an instance: " + other.toString(), null);
        }
        Collection<PropertyValue> propertiesToAdd = new ArrayList<PropertyValue>();
        for (PropertyValue otherPropertyValue : ((Instance) other).getPropertiesAsPropertyValue())
        {
            if (null != otherPropertyValue.getObject() && otherPropertyValue.getObject() instanceof SemanticAxiom)
            {
                PropertyValue propertyValue = containsPropertyWithObject(((SemanticAxiom) otherPropertyValue.getObject()).getSemanticIdentifier());
                if (null == propertyValue)
                {
                    if (null == containsProperty(otherPropertyValue))
                    {
                        propertiesToAdd.add(otherPropertyValue);
                    }
                } else
                {
                    if (null != otherPropertyValue.getObject()
                            && otherPropertyValue.getObject() instanceof SemanticAxiom
                            && null != propertyValue.getObject()
                            && propertyValue.getObject() instanceof SemanticAxiom)
                    {
                        SemanticAxiom joinWith = ((SemanticAxiom) propertyValue.getObject()).joinWith((SemanticAxiom) otherPropertyValue.getObject());
                        propertyValue.setObject(joinWith);
                    }
                }
            }
        }
        for (PropertyValue propertyToAdd : propertiesToAdd)
        {
            addProperty(propertyToAdd);
        }
        return this;
    }
}
