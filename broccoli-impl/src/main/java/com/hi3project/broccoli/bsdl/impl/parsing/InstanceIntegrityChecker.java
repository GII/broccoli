/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*******************************************************************************
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
import com.hi3project.broccoli.bsdl.api.parsing.IAxiomsChecker;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.PropertyValue;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.Collection;

/**
 *
 * 
 */
public class InstanceIntegrityChecker implements IAxiomsChecker {

    public boolean check(Collection<IAxiom> axioms) throws SemanticModelException {
        for (IAxiom axiom: axioms) {
            if (axiom instanceof Instance) {                
                if (!checkInstanceIntegrity((Instance) axiom)) return false;                
            }
        }
        return true;
    }
    
    public static boolean checkInstanceIntegrity(Instance instance) throws SemanticModelException {
        for (Object property : instance.properties()) {
            if (property instanceof PropertyValue) {
                PropertyValue propertyValue = (PropertyValue)property;
                // check integrity only when referred concept is avaliable
                if (null != instance.getConcept().semanticAxiom()) {
                    if (null == instance.getConcept().semanticAxiom().getProperty(propertyValue.getName()))
                        throw new SemanticModelException("Property: " 
                                + propertyValue.getName()
                                + " is not a valid for instance: "
                                + instance.toString()
                                + " of Concept: "
                                + instance.getConcept().semanticAxiom().toString(), null);
                }
            }
        }        
        return true;
    }
    
}
