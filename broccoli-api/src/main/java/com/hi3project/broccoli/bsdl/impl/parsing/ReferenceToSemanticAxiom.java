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

package com.hi3project.broccoli.bsdl.impl.parsing;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLConverter;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.Ontology;
import com.hi3project.broccoli.bsdl.impl.SemanticAxiom;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * Reference to a SemanticAxiom: it must contain the ISemanticIdentier, and it
 * contains the referenced SemanticAxiom or knows how to obtain it from a
 * BSDLRegistry.
 *
 *
 * 
 * @param <T>
 */
public class ReferenceToSemanticAxiom<T extends SemanticAxiom> extends SemanticAxiom
{

    private T cachedObject = null;
    private Class<T> typeCachedObject = null;
    private IBSDLRegistry bsdlRegistry = null;

    public ReferenceToSemanticAxiom(IBSDLRegistry bsdlRegistry, Class<T> typeCachedObject) throws SemanticModelException
    {
        super();
        this.bsdlRegistry = bsdlRegistry;
        this.typeCachedObject = typeCachedObject;
    }

    public ReferenceToSemanticAxiom(
            IBSDLRegistry bsdlRegistry, 
            ISemanticIdentifier identifier,
            Class<T> typeCachedObject) throws SemanticModelException
    {
        this(bsdlRegistry, typeCachedObject);
        setSemanticIdentifier(identifier);
    }

    public ReferenceToSemanticAxiom(T object) throws SemanticModelException
    {
        super();
        this.cachedObject = object;
        if (null != object && null != object.getSemanticIdentifier())
        {
            setSemanticIdentifier(object.getSemanticIdentifier());
        }
    }

    @Override
    public T semanticAxiom() throws SemanticModelException
    {
        if (null == cachedObject)
        {
            cachedObject = (T) bsdlRegistry.annotatedObjectFor(getSemanticIdentifier());
        }
        if (null == cachedObject && Ontology.class.equals(this.typeCachedObject))
        {
            this.cachedObject = (T) new Ontology(getSemanticIdentifier());
            bsdlRegistry.addAxiom(this.cachedObject);
            return (T) this.cachedObject;
        }
        if (null == cachedObject)
        {
            for (IBSDLConverter converter : bsdlRegistry.converters())
            {
                if (converter.contains(getSemanticIdentifier()))
                {
                    return (T) converter.createAxiomFor(getSemanticIdentifier());
                }
            }
        }
        return cachedObject;
    }

    private void setSemanticAxiom(SemanticAxiom semanticAxiom)
    {
        this.cachedObject = (T) semanticAxiom;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (obj instanceof ReferenceToSemanticAxiom)
        {
            return ((ReferenceToSemanticAxiom) obj).getSemanticIdentifier().equals(this.getSemanticIdentifier());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + (this.getSemanticIdentifier() != null ? this.getSemanticIdentifier().hashCode() : 0);
        hash = 83 * hash + (this.cachedObject != null ? this.cachedObject.hashCode() : 0);
        return hash;
    }

    @Override
    public SemanticAxiom joinWith(SemanticAxiom semanticAxiom) throws SemanticModelException
    {
        if (semanticAxiom instanceof ReferenceToSemanticAxiom
                && semanticAxiom.semanticAxiom().getClass().equals(this.semanticAxiom().getClass()))
        {
            setSemanticAxiom(semanticAxiom().joinWith(semanticAxiom.semanticAxiom()));
        }
        return this;
    }

    @Override
    public Collection properties() throws SemanticModelException
    {
        if (null != semanticAxiom())
        {
            return semanticAxiom().properties();
        }
        return new ArrayList();
    }
}
