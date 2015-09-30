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

package com.hi3project.broccoli.bsdm.impl.functionality;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdl.impl.AbstractInstanceArrayList;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IParameterValue;
import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.InstanceArrayList;
import com.hi3project.broccoli.bsdl.impl.InstanceLiteral;
import com.hi3project.broccoli.bsdl.impl.InstanceURI;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;

/**
 *
 * 
 */
public class ParameterValue extends ConceptBehaviourImplementation implements IParameterValue, IAnnotatedValue
{

    protected Object value = null;
    private ISemanticIdentifier type = null;

    public ParameterValue(Instance instance) throws SemanticModelException
    {
        setInstance(instance);
        type = instance.getConcept().semanticAxiom().getSemanticIdentifier();
        value = instance;
    }

    public ParameterValue(InstanceArrayList instanceList) throws SemanticModelException
    {
        setInstance(instanceList);
//        type = instanceList.getConcept().semanticAxiom().getSemanticIdentifier();
        type = instanceList.getConcept().getSemanticIdentifier();
        value = instanceList.values();
    }
    
    public ParameterValue(AbstractInstanceArrayList instanceList) throws SemanticModelException
    {
        setInstance(instanceList);
//        type = instanceList.getConcept().semanticAxiom().getSemanticIdentifier();
        type = instanceList.conceptIdentifier();
        value = instanceList.getValues();
    }

    public ParameterValue(InstanceLiteral instanceLiteral) throws SemanticModelException
    {
        setInstance(instanceLiteral);
        type = instanceLiteral.conceptIdentifier();
        value = instanceLiteral;
    }
    
    public ParameterValue(InstanceURI instanceURI) throws SemanticModelException
    {
        setInstance(instanceURI);
        type = instanceURI.conceptIdentifier();
        value = instanceURI;
    }

    public ParameterValue(Object value, ISemanticIdentifier type)
    {
        this.value = value;
        this.type = type;
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/profile#parameterValue");
    }

    @Override
    public Object object() throws ModelException
    {
        return value;
    }

    @Override
    public ISemanticIdentifier getSemanticAnnotation()
    {
        return type;
    }

    @Override
    public ISubject value() throws ModelException
    {
        return this.instance;
    }

}
