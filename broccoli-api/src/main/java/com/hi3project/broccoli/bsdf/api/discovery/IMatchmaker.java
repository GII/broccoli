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

package com.hi3project.broccoli.bsdf.api.discovery;

import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;

/**
 *  Interface for a matchmaker that can check:
 * <ul>
 * <li>whether two axioms represent the same thing</li>
 * <li>whether one axioms subsumes another</li>
 * </ul>
 * 
 */
public interface IMatchmaker {
    
    public boolean same(IAxiom axiom1, IAxiom axiom2) throws SemanticModelException;
    public boolean same(ISemanticIdentifier identifier1, ISemanticIdentifier identifier2) throws SemanticModelException;
    
    public int subsumes(IAxiom axiomThatSubsumes, IAxiom axiomSubsumed) throws SemanticModelException;
    public int subsumes(ISemanticIdentifier identifierThatSubsumes, ISemanticIdentifier identifierSubsumed) throws SemanticModelException;
    
}
