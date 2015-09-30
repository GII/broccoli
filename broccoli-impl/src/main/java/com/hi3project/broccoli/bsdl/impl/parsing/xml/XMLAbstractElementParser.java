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

import com.hi3project.broccoli.bsdl.api.ISyntaxElement;
import com.hi3project.broccoli.bsdl.api.parsing.IDocumentParser;
import com.hi3project.broccoli.bsdl.api.parsing.IElementParser;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;


/**
 *
 * <p><b>Creation date:</b> 
 * 21-11-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 21-11-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public abstract class XMLAbstractElementParser implements IElementParser {
    
    protected String name = null;
    protected IDocumentParser documentParser = null;
    protected BSDLRegistry bsdlRegistry = null;
    protected AxiomAssign axiomAssign;    
    
    protected ISyntaxElement parsedElement = null;
                       
    
    public XMLAbstractElementParser(String att, BSDLRegistry bsdlRegistry) {
        this.name = att;
        this.bsdlRegistry = bsdlRegistry;
        this.axiomAssign = new AxiomAssign(bsdlRegistry);
    }
    
    @Override
    public String name() {
        return name;
    }
    
    @Override
    public void setDocumentParser(IDocumentParser documentParser) {
        this.documentParser = documentParser;
        this.axiomAssign.setDocumentParser(documentParser);
    }
    
    protected IDocumentParser getDocumentParser() {
        return documentParser;
    }
    
    @Override
    public IElementParser createNewInstance() {
        IElementParser newParser =  newInstance();
        newParser.setDocumentParser(getDocumentParser());
        return newParser;
    }
    
    public abstract IElementParser newInstance();        
    
}
