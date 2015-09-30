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

package com.hi3project.broccoli.bsdl.api.parsing;

import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import java.util.Collection;

/**
 * <p>
 *  Helper objects are needed to parse a set of documents that form some logical unit 
 * (ie: a service, a language ontology...)
 *<p>
 *  It will use IDocumentParsers to perform the job, and a IBSDLRegistry where the read axioms
 * are to be registered
 *<p>
 *  It will accept ISemanticLocators as info to where the documents are located
 *
 * 
 * 
 */
public interface IDocumentLoader {
    
    public void registerDocumentParser(IDocumentParser parser);    
    public void setRegistry(IBSDLRegistry registry);
    public IBSDLRegistry getRegistry();
    public Collection<IAxiom> readFrom(Collection<ISemanticLocator> locatorCollection) throws ModelException;
    public Collection<IAxiom> readFrom(ISemanticLocator locator) throws ModelException;
    public Collection<IAxiom> readFrom(String content) throws ModelException;
    public void registerAxiomsChecker(IAxiomsChecker axiomsChecker);
    
}
