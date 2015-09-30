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

package com.hi3project.broccoli.bsdl.impl.parsing.xml.stax;

import com.hi3project.broccoli.bsdl.api.ISyntaxElement;
import com.hi3project.broccoli.bsdl.api.parsing.IElementParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.XMLAbstractElementParser;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

/**
 * <p>
 *  Generalization of IElementParser implementations for BSDL XML syntax, based on StAX.
 * <p>
 *  Any specilization must know how to parse a kind of XMLEvents, following the vocabulary
 * of XMLSyntax
 *
 * 
 * 
 */
public abstract class XMLStAXElementParser extends XMLAbstractElementParser implements IElementParser {
            
    
    
    public XMLStAXElementParser(String att, BSDLRegistry bsdlRegistry) {
        super(att, bsdlRegistry);
    }
    
    @Override
    public boolean canParse(Object token) {        
        return (token instanceof XMLEvent && canParse ((XMLEvent)token));
    }
    
    protected boolean canParse(XMLEvent xmlEvent) {
        if (xmlEvent.isAttribute()) {
            return ((Attribute)xmlEvent).getName().getLocalPart().equalsIgnoreCase(name());
        }
        return (xmlEvent.asStartElement().getName().getLocalPart().equalsIgnoreCase(name()));
    }
    
    @Override
    public ISyntaxElement parse(Object token) throws ParsingException {
        if (!(token instanceof XMLEvent)) {
            throw new ParsingException("Not an XMLEvent: " + token, null);
        }
        return parse((XMLEvent)token);
    }
    
    abstract protected ISyntaxElement parse(XMLEvent xmlEvent) throws ParsingException;        
    
}
