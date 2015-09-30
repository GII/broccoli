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

import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.ISyntaxElement;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentParser;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentPreParser;
import com.hi3project.broccoli.bsdl.api.parsing.IElementParser;
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import com.hi3project.broccoli.bsdm.impl.parsing.BSDLBSDMLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 * <p>
 * Implementation of IDocumentParser for XML Syntax using StAX and processing
 * XMLEvents. It defers on a Collection of IElementParsers the parsing of any
 * XMLEvent (each IElementParser knows whether it can parse that XMLEvent or
 * not) </p><p>
 * It holds a stack of IElementParsers that are currently working on "open" BSDL
 * XML elements </p>
 *
 * 
 */
public class BSDLXMLStAXParser implements IDocumentParser
{

    final static String baseErrorMsg = "Problem parsing BSDL in XML format";
    private Collection<IElementParser> parsers = new ArrayList<IElementParser>();
    private Map<String, IImport> imports = new HashMap<String, IImport>();

    @Override
    public boolean validate(InputStream source) throws ParsingException
    {
        String schemaLang = "http://www.w3.org/2001/XMLSchema";

        // get validation driver:
        SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
        try
        {
            // create schema by reading it from an XSD file:
            Schema schema = factory.newSchema(new StreamSource(BSDLBSDMLoader.ontologyXMLDir + "bsdlSchema.xsd"));
            // at last perform validation:
            schema.newValidator().validate(new StreamSource(source));
        } catch (SAXException ex)
        {
            throw new ParsingException("Validation error", ex);
        } catch (IOException ex)
        {
            throw new ParsingException("Validation cannot be done, IO problems", ex);
        }
        return true;
    }

    @Override
    public Collection<IAxiom> readFrom(InputStream source) throws ModelException
    {

        Collection<IAxiom> readAxioms = new ArrayList<IAxiom>();
        Stack<IElementParser> parsersStack = new Stack<IElementParser>();
        XMLEventReader eventReader = instanceEventReader(source);
        XMLEvent xmlEvent = nextEvent(eventReader);

        while (!xmlEvent.isEndDocument())
        {
            if (xmlEvent.isStartElement())
            {
                readAxioms = addIfNotNullAxiom(readAxioms, processXMLEvent(xmlEvent, parsersStack));
                // process attributes               
                Iterator<Attribute> attributesIterator = xmlEvent.asStartElement().getAttributes();
                while (attributesIterator.hasNext())
                {
                    readAxioms = addIfNotNullAxiom(readAxioms, processXMLEvent(attributesIterator.next(), parsersStack));
                }
            }
            if (xmlEvent.isEndElement() && !parsersStack.empty())
            {
                parsersStack.pop();
            }
            xmlEvent = nextEvent(eventReader);
        }
        imports = new HashMap<String, IImport>();
        return readAxioms;
    }

    private Collection<IAxiom> addIfNotNullAxiom(Collection<IAxiom> collection, Object objectToAdd)
    {
        if (null != objectToAdd)
        {
            if (objectToAdd instanceof IAxiom)
            {
                collection.add((IAxiom) objectToAdd);
            }
        }
        return collection;
    }

    /*
     *   Parses an XMLEvent, delegating on the configured parsers.
     *   The parsersStack is used to hold the ancestor elements whose properties or contained elements are being parsed.
     */
    private ISyntaxElement processXMLEvent(XMLEvent event, Stack<IElementParser> parsersStack) throws ModelException
    {
        for (IElementParser parser : parsers)
        {
            if (parser.canParse(event))
            {
                IElementParser newParser = parser.createNewInstance();
                ISyntaxElement parsedElement = newParser.parse(event);
                if (!parsersStack.empty())
                {
                    parsersStack.peek().assignParsedElement(newParser.name(), parsedElement);
                }
                if (event.isStartElement())
                {
                    parsersStack.push(newParser);
                }
                return parsedElement;
            }
        }
        return null;
    }

    private XMLEventReader instanceEventReader(InputStream source) throws ParsingException
    {
        XMLEventReader eventReader = null;
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try
        {
            eventReader = inputFactory.createXMLEventReader(source);
        } catch (XMLStreamException ex)
        {
            throw new ParsingException("Not a valid XML document", ex);
        }
        return eventReader;
    }

    private static XMLEvent nextEvent(XMLEventReader eventReader) throws ParsingException
    {
        try
        {
            return eventReader.nextEvent();
        } catch (XMLStreamException ex)
        {
            throw new ParsingException(baseErrorMsg, ex);
        }
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
