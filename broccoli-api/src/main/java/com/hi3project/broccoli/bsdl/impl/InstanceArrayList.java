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

package com.hi3project.broccoli.bsdl.impl;

import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.impl.parsing.ReferenceToSemanticAxiom;
import java.util.List;

/**
 *
 * 
 */
public class InstanceArrayList extends AbstractInstanceArrayList implements ISubject, IObject
{

    private List<Instance> values = null;
    private ReferenceToSemanticAxiom<Concept> concept;

    public InstanceArrayList(List<Instance> values, ReferenceToSemanticAxiom<Concept> concept)
    {
        this.values = values;
        this.concept = concept;
    }

    public List<Instance> values()
    {
        return values;
    }
    

    @Override
    public void setInstance(ISubject instance) throws SemanticModelException
    {
        if (instance instanceof InstanceArrayList)
        {
            super.setInstance(instance);
            this.values = ((InstanceArrayList) instance).values();
            this.concept = ((InstanceArrayList) instance).getConcept();
        }
        throw new SemanticModelException("Cannot set instance of: " + instance.toString(), null);
    }

    public ReferenceToSemanticAxiom<Concept> getConcept()
    {
        return concept;
    }
    
    @Override
    public List getValues()
    {
        return values();
    }

}
