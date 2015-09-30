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
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.XMLSyntax;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * 
 */
public class InstanceParser extends XMLStAXElementParser
{

    public InstanceParser(BSDLRegistry bsdlRegistry)
    {
        super(XMLSyntax.INSTANCE(), bsdlRegistry);
    }

    @Override
    protected ISyntaxElement parse(XMLEvent attribute) throws ParsingException
    {
        try
        {
            return (Instance)(this.parsedElement = new Instance());
        } catch (SemanticModelException ex)
        {
            throw new ParsingException(ex.getMessage(), ex);
        }
    }

    @Override
    public IElementParser newInstance()
    {
        return new InstanceParser(bsdlRegistry);
    }

    @Override
    public boolean assignParsedElement(String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException
    {
        return this.axiomAssign.assignParsedElement((Instance)this.parsedElement, syntaxElementName, syntaxElement);
    }

}
