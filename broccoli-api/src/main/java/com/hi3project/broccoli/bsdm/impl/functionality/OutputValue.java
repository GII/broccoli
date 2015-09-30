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

import com.hi3project.broccoli.bsdl.impl.AbstractInstanceArrayList;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.InstanceLiteral;
import com.hi3project.broccoli.bsdl.impl.InstanceURI;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.Collection;

/**
 *
 * 
 */
public class OutputValue extends ParameterValue implements IAnnotatedValue, IOutputValue {
    
    protected IOutput output = null;
    
    public OutputValue(Instance instance) throws SemanticModelException {
        super(instance);
        output = new Output(getSinglePropertyAsInstance("output"));
    }
    
    public OutputValue(AbstractInstanceArrayList instance) throws SemanticModelException {
        super(instance);
        output = new Output(getSinglePropertyAsInstance("output"));
    }
    
    public OutputValue(InstanceLiteral instance) throws SemanticModelException {
        super(instance);
        output = new Output(getSinglePropertyAsInstance("output"));
    }
    
    public OutputValue(InstanceURI instance) throws SemanticModelException {
        super(instance);
        output = new Output(getSinglePropertyAsInstance("output"));
    }
    
    public OutputValue(Instance instance, Collection<IOutput> outputs) throws SemanticModelException {
        super(instance);
        if (null != outputs && !outputs.isEmpty()) {
            this.output = outputs.iterator().next();
        }
    }
    
    public OutputValue(InstanceLiteral instance, Collection<IOutput> outputs) throws SemanticModelException {
        super(instance);
        if (null != outputs && !outputs.isEmpty()) {
            this.output = outputs.iterator().next();
        }
    }
    
    public OutputValue(AbstractInstanceArrayList instanceList, Collection<IOutput> outputs) throws SemanticModelException  {
        super(instanceList);              
        if (null != outputs && !outputs.isEmpty()) {
            this.output = outputs.iterator().next();
        }
    }
    
    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/profile#outputValue");
    }

    @Override
    public IOutput getOutput() {
        return output;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
}
