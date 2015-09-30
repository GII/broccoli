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

import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentPreParser;
import com.hi3project.broccoli.bsdl.api.parsing.PreParsingRule;
import com.hi3project.broccoli.bsdl.impl.exceptions.ParsingException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * <b>Description:</b>
 * </p>
 *
 * <p>
 * <b>Creation date:</b>
 * 07-05-2015 </p>
 *
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 07-05-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class DocumentPreParser implements IDocumentPreParser
{

    private Collection<PreParsingRule> preparsingRules = new ArrayList<PreParsingRule>();
    
    private ISemanticLocator descriptorLocator = null;
    
    private Object preParsedModel = null;
    
    
    public DocumentPreParser(ISemanticLocator descriptorLocator)
    {
        this.descriptorLocator = descriptorLocator;
    }
    
    public DocumentPreParser(ISemanticLocator descriptorLocator, Collection<PreParsingRule> preparsingRules)
    {
        this.descriptorLocator = descriptorLocator;
        this.preparsingRules = preparsingRules;
    }
    
    public DocumentPreParser(Collection<PreParsingRule> preparsingRules)
    {
        this.preparsingRules = preparsingRules;
    }
    

    @Override
    public boolean canProcess(Object loadedModel)
    {
        return loadedModel instanceof Document;
    }

    @Override
    public Object process(Object loadedModel)
    {

        Document doc = (Document) loadedModel;

        for (PreParsingRule rule : this.preparsingRules)
        {

            NodeList elementsByTagName = doc.getElementsByTagName(rule.getTargetIdentification());
            for (int i = 0; i < elementsByTagName.getLength(); i++)
            {
                Node node = elementsByTagName.item(i);
                if (node instanceof Element)
                {
                    boolean valid = true;

                    if (null != rule.getSubelementToSearch())
                    {

                        Node namedItemElementToSearch = node.getAttributes().getNamedItem(rule.getSubelementToSearch());
                        valid = null != namedItemElementToSearch
                                && (null == rule.getSubelementToSearchValue()
                                || namedItemElementToSearch.getTextContent().equalsIgnoreCase(rule.getSubelementToSearchValue()));

                    }

                    if (valid)
                    {

                        if (null != rule.getSubelementForNewValue())
                        {
                            Node nodeSubElementForNewValue = node.getAttributes().getNamedItem(rule.getSubelementForNewValue());
                            nodeSubElementForNewValue.setTextContent(rule.getNewValue());
                        } else
                        {
                            node.setTextContent(rule.getNewValue());
                        } // if (null != rule.getSubelementForNewValue())

                    } // if (valid)

                } // if (node instanceof Element)

            } // for elementsByTagName

        } // for preparsingRules

        doc.normalizeDocument();

        this.preParsedModel = doc;
        
        return doc;
    }

    @Override
    public void writeModelTo(Object loadedModel, ISemanticLocator locator) throws ParsingException
    {
        try
        {
            Document doc = (Document) loadedModel;
            //for output
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            transformer.transform(source, new StreamResult(new File(locator.getURI().getPath())));
        } catch (TransformerConfigurationException ex)
        {
            throw new ParsingException("Error writing preParsed document to: " + locator.toString(), ex);
        } catch (TransformerException ex)
        {
            throw new ParsingException("Error writing preParsed document to: " + locator.toString(), ex);
        }

    }

    @Override
    public void addPreParsingRule(PreParsingRule rule)
    {
        this.preparsingRules.add(rule);
    }
    
    public void addPreParsingRules(Collection<PreParsingRule> rules)
    {
        this.preparsingRules.addAll(rules);
    }
    
    public void writeModel() throws ParsingException
    {
        this.writeModelTo(this.preParsedModel, this.descriptorLocator);
    }

}
