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

package com.hi3project.broccoli.bsdm.impl.profile.functionality;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.condition.IRestartAction;
import com.hi3project.broccoli.bsdm.api.profile.functionality.condition.IRuntimeException;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import java.util.ArrayList;
import java.util.Collection;


/**
 *
 * 
 */
public class RuntimeException implements IRuntimeException {
        
    private Throwable exception = null;    
    private IRuntimeException wrappedRuntimeException = null;
           
    
    public RuntimeException(Throwable exception) {
        this.exception = exception;
        if (null != exception.getCause()) {
            wrappedRuntimeException = new RuntimeException(exception.getCause());
        }
    }        

    public Object object() throws ModelException {
        return exception;
    }
    
    public IOutput getOutput() {
        return null;
    }

    public ISubject value() throws ModelException {
        return null;
    }
    
    public ISemanticIdentifier getSemanticAnnotation() throws ModelException {
        return new SemanticIdentifier("http://gii.udc.es/semanticServices#bsdo/profile/functionality/runtimeException");
    }

    public IRuntimeException getWrappedException() {
        return wrappedRuntimeException;
    }

    public Collection<IRestartAction> restarts() {
        return new ArrayList<IRestartAction>();
    }

    @Override
    public String toString() {
        return "RuntimeException{" + "exception=" + exception + '}';
    }
    
}
