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
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInputValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IParameterValue;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.InstanceArrayList;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;

/**
 *
 * 
 */
public class InputValue extends ParameterValue implements IParameterValue, IAnnotatedValue, IInputValue {
    
    protected IInput input = null;
    
    public InputValue(Instance instance) throws SemanticModelException {
        super(instance);
        input = new Input(getSinglePropertyAsInstance("input"));
    }
    
    public InputValue(Instance instance, IInput input) throws SemanticModelException {
        super(instance);
        this.input = input;
    }
    
    public InputValue(InstanceArrayList instanceList, IInput input) throws SemanticModelException  {
        super(instanceList);
        this.input = input;
    }
       
    public InputValue(IParameterValue parameterValue, final String inputName, final ISemanticIdentifier inputType) throws ModelException  {
        super(parameterValue.object(), parameterValue.getSemanticAnnotation());        
        input = new IInput() {

            @Override
            public String name() {
                return inputName;
            }

            @Override
            public ISemanticIdentifier getSemanticAnnotation() {
                return inputType;
            }
        };
    }
    
    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/profile#inputValue");
    }

    @Override
    public IInput getInput() {
        return input;
    }
    
}
