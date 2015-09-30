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

package com.hi3project.broccoli.bsdl.api.parsing;

import com.hi3project.broccoli.bsdl.api.ISyntaxElement;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;

/**
 * <p>
 *  An implementation of IElementParser knows how to parse a BSDL element with distinc syntax. So,
 * it must know:
 *<ul>
 *  <li>which elements it can parse</li>
 *  <li>the IDocumentParser it is working for</li>
 *  <li>how to parse a valid BSDL syntax element of the appropiate type</li>
 *  <li>as an IElementParser instance is meant to work addding references, properties, etc in several
 * and separated steps, it must hold reference to the "being parsed" element, and know how to add 
 * new "sub-elements" to it</li>
 * </ul>
 * 
 * @author lumsais
 */
public interface IElementParser {
    public String name();
    public boolean canParse(Object token) throws ParsingException;
    public ISyntaxElement parse(Object token) throws ParsingException;
    public boolean assignParsedElement(String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException;
    public IElementParser createNewInstance();
    public void setDocumentParser(IDocumentParser documentParser);
}
