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

package com.hi3project.broccoli.bsdl.impl.meta;

import com.hi3project.broccoli.conf.ProjProperties;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.meta.IOntologyLanguage;
import com.hi3project.broccoli.bsdl.api.meta.IVersionNumber;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;


/**
 * <p>
 *  Implementation for IOntologyLanguage
 *<p>
 *  It uses BSDL "current" as default
 * <p>
 * 
 * 
 */
public class MetaPropertyOntologyLanguage implements IOntologyLanguage {
    
    public final static String SEPARATOR = ":";    
    
    private MetaPropertyVersion version = null;
    private String id = null;
    private SemanticIdentifier definitionIdentifier = null;
    private MetaPropertyOntologyLanguage previous = null;
    
    
    public MetaPropertyOntologyLanguage() throws SemanticModelException {
        this(ProjProperties.BSDL + SEPARATOR + ProjProperties.BSDL_CURRENT_VERSION_NUMBER);
    }
    
    public MetaPropertyOntologyLanguage(String dialectWithVersionOptionally) throws SemanticModelException {
        String[] split = dialectWithVersionOptionally.split(SEPARATOR);
        if (split.length <= 0 || split.length > 2) {
            throw new SemanticModelException("Invalid dialect[:version] specified: " + dialectWithVersionOptionally, null);
        }
        setId(split[0]);
        if (split.length == 2) {
            setVersion(new MetaPropertyVersion(split[1]));
        } else {
            setVersion(new MetaPropertyVersion());
        }
    }
    
    public MetaPropertyOntologyLanguage(SemanticIdentifier semanticIdentifier) throws SemanticModelException {
        this(semanticIdentifier.getName());
        setDefinitionIdentifier(semanticIdentifier);
    }

    public String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public MetaPropertyVersion getVersion() {
        return version;
    }

    public final void setVersion(MetaPropertyVersion version) {
        this.version = version;
    }
    
    public MetaPropertyOntologyLanguage getPrevious() {
        return previous;
    }

    public void setPrevious(MetaPropertyOntologyLanguage previous) {
        this.previous = previous;
    }
    
    public SemanticIdentifier getDefinitionIdentifier() {
        return definitionIdentifier;
    }

    public final void setDefinitionIdentifier(SemanticIdentifier definitionIdentifier) {
        this.definitionIdentifier = definitionIdentifier;
    }
                

    @Override
    public String identifier() {
        return getId();
    }

    @Override
    public String identifierWithVersion() {
        return null==version?id:id+SEPARATOR+version;
    }

    @Override
    public ISemanticIdentifier definition() {
        return getDefinitionIdentifier();
    }

    @Override
    public IVersionNumber version() {
        return getVersion();
    }

    @Override
    public IOntologyLanguage previous() {
        return previous;
    }
    
    
    @Override
    public String toString() {
        return identifierWithVersion();
    }

    @Override
    public boolean compatibleWith(IOntologyLanguage ontologyLanguage)
    {
        if (!ontologyLanguage.identifier().equals(this.identifier()))
        {
            return false;
        }
        return 0 <= this.getVersion().compareTo(ontologyLanguage.version());
    }
}