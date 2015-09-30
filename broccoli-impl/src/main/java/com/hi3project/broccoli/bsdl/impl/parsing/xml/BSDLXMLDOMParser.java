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

package com.hi3project.broccoli.bsdl.impl.parsing.xml;

import com.hi3project.broccoli.Mark;
import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.ISyntaxElement;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentParser;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentPreParser;
import com.hi3project.broccoli.bsdl.api.parsing.IElementParser;
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdl.impl.parsing.xml.dom.DOMNodeContainer;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * <b>Creation date:</b>
 * 21-11-2014 </p>
 *
 * <p>
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 21-11-2014 - Initial release</li>
 * </ul>
 *
 *
 * 
 * @version 1
 */
public class BSDLXMLDOMParser implements IDocumentParser
{

    final static String baseErrorMsg = "Problem parsing BSDL in XML format";

    private Collection<IElementParser> parsers = new ArrayList<IElementParser>();
    private Map<String, IImport> imports = new HashMap<String, IImport>();
    private IDocumentPreParser documentPreParser = null;
    

    @Override
    public boolean validate(InputStream source) throws ParsingException
    {

        try
        {
            // get validation driver:
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // create schema by reading it from an XSD file:
            Schema schema
                    = factory.newSchema(new StreamSource(Mark.class.getResourceAsStream(
                                            "ontology" + File.separator + "bsdlSchema.xsd")));

            // at last perform validation:
            schema.newValidator().validate(new StreamSource(source));
        } catch (SAXException ex)
        {
            throw new ParsingException("Validation error", ex);
        } catch (IOException ex)
        {
            throw new ParsingException("Validation cannot be done, IO problems", ex);
        } catch (IllegalArgumentException ex)
        {
            // TODO: try to load another Schema (ie: Xerces for Android, sic...)
            return true;
        }
        return true;
    }

    @Override
    public Collection<IAxiom> readFrom(InputStream source) throws ModelException
    {
        Collection<IAxiom> readAxioms = new ArrayList<IAxiom>();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = builderFactory.newDocumentBuilder();
            Document dom = builder.parse(source);
            
            if (null != documentPreParser
                    && documentPreParser.canProcess(dom))
            {
                dom = (Document) documentPreParser.process(dom);
            }
            
            Element rootElement = dom.getDocumentElement();

            readAxioms.addAll(
                    this.parseNode(
                            new DOMNodeContainer(rootElement, Node.ELEMENT_NODE), null));

        } catch (ParserConfigurationException ex)
        {
            throw new ParsingException(baseErrorMsg, ex);
        } catch(SAXException ex)
        {
            throw new ParsingException(baseErrorMsg, ex);
        } catch (IOException ex)
        {
            throw new ParsingException(baseErrorMsg, ex);
        }

        return readAxioms;
    }

    Collection<IAxiom> parseNode(DOMNodeContainer node, IElementParser parentParser) throws ParsingException, ModelException
    {
        Collection<IAxiom> axioms = new ArrayList<IAxiom>();

        IElementParser newParser = null;

        for (IElementParser parser : parsers)
        {
            if (parser.canParse(node))
            {
                newParser = parser.createNewInstance();
                ISyntaxElement parsedElement = newParser.parse(node);
                if (null != parentParser)
                {
                    parentParser.assignParsedElement(newParser.name(), parsedElement);
                }
                if (parsedElement instanceof IAxiom)
                {
                    axioms.add((IAxiom) parsedElement);
                }
            }
        }

        if (node.getNode().hasAttributes())
        {
            NamedNodeMap attributes = node.getNode().getAttributes();
            for (int i = 0; i < attributes.getLength(); i++)
            {
                Node attributeNode = attributes.item(i);
                if (!attributeNode.getNodeName().equals("#text"))
                {
                    axioms.addAll(this.parseNode(new DOMNodeContainer(attributeNode, Node.ATTRIBUTE_NODE), newParser));
                }
            }
        }

        if (node.getNode().hasChildNodes())
        {
            NodeList childNodeList = node.getNode().getChildNodes();
            for (int i = 0; i < childNodeList.getLength(); i++)
            {
                Node childNode = childNodeList.item(i);
                if (!childNode.getNodeName().equals("#text"))
                {
                    axioms.addAll(this.parseNode(new DOMNodeContainer(childNode, Node.ELEMENT_NODE), newParser));
                }
            }
        }
        return axioms;
    }

    @Override
    public void registerParser(IElementParser parser)
    {
        parsers.add(parser);
        parser.setDocumentParser(this);
    }

    @Override
    public IImport hasImportFor(String key)
    {
        return imports.get(key);
    }

    @Override
    public void addImport(IImport imp)
    {
        imports.put(imp.getPrefix(), imp);
    }

    @Override
    public void registerPreParser(IDocumentPreParser documentPreParser)
    {
        this.documentPreParser = documentPreParser;
    }

}
