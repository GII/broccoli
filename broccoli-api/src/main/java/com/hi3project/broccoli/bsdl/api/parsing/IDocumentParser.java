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

import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import java.io.InputStream;
import java.util.Collection;

/**
 * <p>
 *  A parser for a BSDL syntax that:
 *<ul>
 *  <li>can read the axioms contained in a InputStream</li>
 *  <li>can validate whether an InputStream is a well formed BSDL document</li>
 *  <li>manage the imports read from that source</li>
 *  <li>manage a collection of IElementParsers. Each one will be speciallized
 * in parsing a kind of BSDL element</li>
 * </ul>
 * 
 * 
 */
public interface IDocumentParser {
    
    public boolean validate(InputStream source) throws ParsingException;
    public Collection<IAxiom> readFrom(InputStream source) throws ModelException;    
    public void registerParser(IElementParser parser);
    public IImport hasImportFor(String key);
    public void addImport(IImport imp);
    public void registerPreParser(IDocumentPreParser documentPreParser);
    
}

