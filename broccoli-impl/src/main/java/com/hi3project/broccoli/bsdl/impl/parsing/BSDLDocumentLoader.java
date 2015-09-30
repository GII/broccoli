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

package com.hi3project.broccoli.bsdl.impl.parsing;

import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.parsing.IAxiomsChecker;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentLoader;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentParser;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentPreParser;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.SemanticAxiom;
import com.hi3project.broccoli.bsdm.impl.exceptions.CompositeParsingException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Implementation for IDocumentLoader that can read from "http" and "file"
 * source locators, and defers to a Collection of IDocumentParser the actual
 * parsing
 *
 * 
 */
public class BSDLDocumentLoader implements IDocumentLoader
{
    
    public static final String JAR_RESOURCE_PREFIX = "jarres://";
    public static final String JAR_RESOURCE_SEPARATOR = "-";

    private IBSDLRegistry registry = null;
    private Collection<IDocumentParser> parsers = new ArrayList<IDocumentParser>();
    private Collection<IAxiomsChecker> checkers = new ArrayList<IAxiomsChecker>();

    public BSDLDocumentLoader(IBSDLRegistry registry)
    {
        setRegistry(registry);
    }

    @Override
    public void registerDocumentParser(IDocumentParser parser)
    {
        parsers.add(parser);
    }

    @Override
    public void registerAxiomsChecker(IAxiomsChecker axiomsChecker)
    {
        checkers.add(axiomsChecker);
    }

    @Override
    public final void setRegistry(IBSDLRegistry registry)
    {
        this.registry = registry;
    }

    @Override
    public IBSDLRegistry getRegistry()
    {
        return registry;
    }

    @Override
    public Collection<IAxiom> readFrom(Collection<ISemanticLocator> locatorCollection) throws ModelException
    {
        HashMap<ISemanticIdentifier, IAxiom> mapToReadAxioms = new HashMap<ISemanticIdentifier, IAxiom>();
        for (ISemanticLocator locator : locatorCollection)
        {
            Collection<IAxiom> axioms = readFrom(locator);
            for (IAxiom axiom : axioms)
            {
                if (null != axiom && axiom instanceof SemanticAxiom)
                {
                    SemanticAxiom sa = (SemanticAxiom) axiom;
                    if (sa.semanticAxiom().getSemanticIdentifier() != null)
                    {
                        mapToReadAxioms.put(sa.semanticAxiom().getSemanticIdentifier(), sa);
                    }
                }
            }
        }
        return mapToReadAxioms.values();
    }

    @Override
    public Collection<IAxiom> readFrom(ISemanticLocator locator) throws ModelException
    {
        Collection<ParsingException> exceptions = new ArrayList<ParsingException>();
        Iterator<IDocumentParser> parsersIterator = parsers.iterator();
        InputStream inputStream = null;
        IDocumentParser documentParser = null;

        while (parsersIterator.hasNext())
        {
            documentParser = parsersIterator.next();
            inputStream = null;
            if (locator.getURI().getScheme().contains("file"))
            {
                try
                {
                    inputStream = new FileInputStream(locator.getURI().getPath());
                    documentParser.validate(inputStream);
                    inputStream = new FileInputStream(locator.getURI().getPath());
                } catch (FileNotFoundException ex)
                {
                    throw new ParsingException("Not a valid file in: " + locator.getURI().getPath(), ex);
                }
            }
            if (null == inputStream && locator.getURI().toString().startsWith(JAR_RESOURCE_PREFIX))
            {
                String resPath = locator.getURI().toString().replace(JAR_RESOURCE_PREFIX, "");
                String[] split = resPath.split(JAR_RESOURCE_SEPARATOR);
                if (split.length == 2)
                {
                    try
                    {
                        inputStream = Class.forName(split[0]).getResourceAsStream(split[1]);
                        documentParser.validate(inputStream);
                        inputStream = Class.forName(split[0]).getResourceAsStream(split[1]);
                    } catch (ClassNotFoundException ex)
                    {
                        throw new ParsingException("Cannot load resource: " + split[1] + " using class: " + split[0], ex);
                    }
                }
            }
            if (null == inputStream && locator.getURI().getScheme().contains("http"))
            {
                try
                {
                    inputStream = locator.getURI().toURL().openStream();
                    documentParser.validate(inputStream);
                    inputStream = locator.getURI().toURL().openStream();
                } catch (MalformedURLException ex)
                {
                    throw new ParsingException("Not a valid URL: " + locator.getURI().toString(), ex);
                } catch (IOException ex)
                {
                    throw new ParsingException("Problem reading from URL: " + locator.getURI().toString(), ex);
                }
            }
        }
        if (null != inputStream && null != documentParser)
        {
            try
            {
                return this.readFrom(inputStream, documentParser);
            } catch (ParsingException ePE)
            {
                exceptions.add(ePE);
            }
        }
        throw new CompositeParsingException("Not able to read from: " + locator.toString(), exceptions);
    }

    @Override
    public Collection<IAxiom> readFrom(String content) throws ModelException
    {
        Iterator<IDocumentParser> parsersIterator = parsers.iterator();

        while (parsersIterator.hasNext())
        {
            IDocumentParser documentParser = parsersIterator.next();
            InputStream inputStream = new ByteArrayInputStream(content.getBytes(Charset.defaultCharset()));
            documentParser.validate(inputStream);
            inputStream = new ByteArrayInputStream(content.getBytes(Charset.defaultCharset()));
            return this.readFrom(inputStream, documentParser);
        }

        throw new ParsingException("Cannot parse String: " + content);
    }

    private Collection<IAxiom> readFrom(InputStream inputStream, IDocumentParser documentParser) throws ParsingException, ModelException
    {
        Collection<IAxiom> readAxioms = documentParser.readFrom(inputStream);
        try
        {
            inputStream.close();
        } catch (IOException ex)
        {
            throw new ParsingException("Problem closing input: " + inputStream.toString(), ex);
        }
        for (IAxiomsChecker axiomsChecker : checkers)
        {
            axiomsChecker.check(readAxioms);
        }
        Collection<IAxiom> returnedAxioms = getRegistry().addAxioms(readAxioms);
        return returnedAxioms;
    }

}
