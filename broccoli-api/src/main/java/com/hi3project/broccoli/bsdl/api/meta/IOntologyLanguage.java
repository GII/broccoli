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

package com.hi3project.broccoli.bsdl.api.meta;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;

/**
 * <p>
 *  Metainfo about the ontology language in which an axiom can be described.
 * It must declare:
 *
 * <ul>
 * <li>An identifier for the ontology language</li>
 * <li>Optionally, a number version of that ontology</li>
 * <li>Optionally, the reference of the previous version of that ontology (if any)</li>
 * <li>The ISemanticIdentifier of that ontology language s definition. It is asumed
 * that the ontology language will be an IOntology</li>
 * </ul>
 * <p>
 *  Aditionally, it must know the full "identifier with version number". For example:
 * if identifier is "BSDL" and version number "0.9", it could be "BSDL:0.9"
 *
 * 
 * 
 */
public interface IOntologyLanguage extends ISemanticMetaInfo {
    
    public String identifier();
    public String identifierWithVersion();
    public ISemanticIdentifier definition();
    public IVersionNumber version();
    public IOntologyLanguage previous();
    
    public boolean compatibleWith(IOntologyLanguage ontologyLanguage);
    
}
