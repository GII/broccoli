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

package com.hi3project.broccoli.bsdl.impl.parsing.xml.dom;

import com.hi3project.broccoli.bsdl.api.ISyntaxElement;
import com.hi3project.broccoli.bsdl.api.parsing.IElementParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.XMLAbstractElementParser;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;

/**
 *
 * <p>
 * <b>Creation date:</b>
 * 24-11-2014 </p>
 *
 * <p>
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 24-11-2014 - Initial release</li>
 * </ul>
 *
 *
 * 
 * @version 1
 */
public abstract class XMLDOMElementParser extends XMLAbstractElementParser implements IElementParser
{

    protected int nodeType;

    public XMLDOMElementParser(String att, BSDLRegistry bsdlRegistry, int nodeType)
    {
        super(att, bsdlRegistry);
        this.nodeType = nodeType;
    }

    @Override
    public boolean canParse(Object token) throws ParsingException
    {
        if (!(token instanceof DOMNodeContainer))
        {
            return false;
        }
        DOMNodeContainer node = (DOMNodeContainer) token;
        return node.getNode().getNodeName().equalsIgnoreCase(this.name()) && this.nodeType == node.getType();
    }

    @Override
    public ISyntaxElement parse(Object token) throws ParsingException
    {
        if (!(token instanceof DOMNodeContainer))
        {
            throw new ParsingException("Not a DOM Node: " + token, null);
        }
        return parse((DOMNodeContainer) token);
    }

    protected abstract ISyntaxElement parse(DOMNodeContainer node) throws ParsingException;

}
