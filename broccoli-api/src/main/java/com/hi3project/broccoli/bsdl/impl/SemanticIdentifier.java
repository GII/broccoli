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

import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISyntaxElement;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * 
 */
public class SemanticIdentifier implements ISemanticIdentifier, ISyntaxElement
{

    private URI uri = null;

    public SemanticIdentifier(URI uri)
    {
        this.uri = uri;
    }

    public SemanticIdentifier(String uri) throws SemanticModelException
    {
        try
        {
            this.uri = new URI(uri);
        } catch (URISyntaxException e)
        {
            throw new SemanticModelException("Not a valid URI: " + uri, e);
        }
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
        final SemanticIdentifier other = (SemanticIdentifier) obj;
        if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        return hash;
    }

    @Override
    public String toString()
    {
        return uri.toString();
    }

    @Override
    public URI getURI()
    {
        return uri;
    }

    @Override
    public String getAuthorityName()
    {
        String[] namespaceParts = getURI().toString().split("#");
        return namespaceParts[0];

    }

    @Override
    public String getName()
    {
        String[] namespaceParts = getURI().toString().split("#");
        return namespaceParts.length > 0 ? namespaceParts[1] : null;
    }

    @Override
    public String getLastName()
    {
        return this.getName();
    }

}
