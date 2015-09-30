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
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.parsing.Import;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.XMLSyntax;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import java.net.URI;
import java.net.URISyntaxException;
import org.w3c.dom.Node;

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
public class AttributeURIParser extends XMLDOMElementParser
{

    public AttributeURIParser(BSDLRegistry bsdlRegistry)
    {
        super(XMLSyntax.URI(), bsdlRegistry, Node.ATTRIBUTE_NODE);
    }
    
    public AttributeURIParser(String name, BSDLRegistry bsdlRegistry)
    {
        super(name, bsdlRegistry, Node.ATTRIBUTE_NODE);
    }

    @Override
    public IElementParser newInstance()
    {
        return new AttributeURIParser(name(), this.bsdlRegistry);
    }

    @Override
    public boolean assignParsedElement(String syntaxElementName, ISyntaxElement syntaxElement) throws ModelException
    {
        return false;
    }

    @Override
    protected ISyntaxElement parse(DOMNodeContainer node) throws ParsingException
    {
        try
        {
            String URIcandidate = node.getNode().getNodeValue();
            if (!Import.isImport(URIcandidate))
            {
                return new SemanticIdentifier(new URI(URIcandidate));
            } else
            {
                IImport anImport = getDocumentParser().hasImportFor(Import.getImportNameFrom(URIcandidate));
                if (null != anImport)
                {
                    return new SemanticIdentifier(new URI(anImport.getPrefixed() + Import.getNonImportNameFrom(URIcandidate)));
                } else
                {
                    throw new ParsingException("Cannot parse prefix from: " + URIcandidate, null);
                }
            }
        } catch (URISyntaxException ex)
        {
            throw new ParsingException("Cannot parse as URI: " + node.toString(), ex);
        }
    }

}
