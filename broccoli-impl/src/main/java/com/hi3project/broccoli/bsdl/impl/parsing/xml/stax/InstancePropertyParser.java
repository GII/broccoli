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

import com.hi3project.broccoli.bsdl.impl.PropertyValue;
import com.hi3project.broccoli.bsdl.api.ISyntaxElement;
import com.hi3project.broccoli.bsdl.api.parsing.IElementParser;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.XMLSyntax;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * 
 */
public class InstancePropertyParser extends XMLStAXElementParser
{

    public InstancePropertyParser(BSDLRegistry bsdlRegistry)
    {
        super(XMLSyntax.PROPERTY_VALUE(), bsdlRegistry);
    }

    @Override
    protected ISyntaxElement parse(XMLEvent attribute) throws ParsingException
    {
        return (PropertyValue)(this.parsedElement = new PropertyValue());
    }

    @Override
    public IElementParser newInstance()
    {
        return new InstancePropertyParser(bsdlRegistry);
    }
    
    @Override
    public boolean assignParsedElement(String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException
    {
        return this.axiomAssign.assignParsedElement((PropertyValue)this.parsedElement, syntaxElementName, syntaxElement);
    }
}
