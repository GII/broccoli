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

package com.hi3project.broccoli.bsdl.api.registry;

import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.IOntology;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.Collection;

/**
 * <p>
 *  A cache/registry of IAxioms, where each one is identified by an ISemanticIdentifier
 *<p>
 *  It also knows the IOntologies that contain the IAxioms
 *<p>
 *  Works with IBSDLConverters in order to access to BSDL representations of
 * axioms contained in non-BSDL ontologies
 *
 * 
 */
public interface IBSDLRegistry {
    
    public IAxiom addAxiom(IAxiom axiom) throws SemanticModelException;
    public Collection<IAxiom> addAxioms(Collection<IAxiom> axioms) throws SemanticModelException;
    
    public IAxiom axiomFor(ISemanticIdentifier semanticAnnotation) throws SemanticModelException;
    public Collection<IAxiom> axiomsFor(String partialIdentifier);
    
    public IAxiom annotatedObjectFor(ISemanticIdentifier identifier);
    
    public boolean deleteAxiom(ISemanticIdentifier identifier);
    
    public void clean();
    
    public Collection<IBSDLConverter> converters();
    
    public void registerConverter(IBSDLConverter converter);
    
    public void registerImportsForConverter(Collection<IImport> imports) throws ModelException;
            
    public Collection<IOntology> ontologies() throws SemanticModelException;
            
    public Collection<ISemanticIdentifier> addOntology(ISemanticLocator locator) throws ModelException;
    public Collection<ISemanticIdentifier> addOntology(ISemanticLocator locator,  ISemanticIdentifier identifier, String ontologyLangID) throws ModelException;
    
    public void removeOntology(ISemanticIdentifier ontologyIdentifier) throws SemanticModelException;
    
//    public void removeOntologyOfThis(ISemanticIdentifier ontologyIdentifier) throws SemanticModelException;
    
    public ILocatorsRegistry getLocatorsRegistry();
}
