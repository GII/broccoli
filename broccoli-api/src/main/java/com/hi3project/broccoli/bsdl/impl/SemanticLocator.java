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

import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import java.net.URI;
import java.net.URISyntaxException;


/**
 *
 * 
 */
public class SemanticLocator implements ISemanticLocator {
    
    private SemanticIdentifier semanticIdentifier = null;
    private URI uri = null;
    
    public SemanticLocator(String path) throws ParsingException {
        try {
            if (path.contains("://"))
            {
                this.uri = new URI(path);
            } else
            {
                this.uri = new URI("file://" + path);
            }
        } catch (URISyntaxException ex) {
            throw new ParsingException("No valid as URI: " + path, ex);
        }
    }
    
    public SemanticLocator(URI uri) {
        this.uri = uri;
    }
    
    public SemanticLocator(SemanticIdentifier semanticIdentifier, URI uri) {
        this(uri);
        this.semanticIdentifier = semanticIdentifier;
    }

    @Override
    public ISemanticIdentifier getSemanticIdentifier() {
        return semanticIdentifier;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public String toString()
    {
        return this.getURI().toString();
    }
    
    @Override
    public String getLastName()
    {
        String[] split = this.getURI().toString().split("/");
        return split[split.length - 1];
    }
        
    
}
