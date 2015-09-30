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

package com.hi3project.broccoli.bsdm.api;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGrounding;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.IServiceProfile;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import java.util.Collection;

/**
 *
 * 
 */
public interface IServiceDescription extends IComponent {
        
    public Collection<IServiceGrounding> getGroundings();
    
    public void activateGroundings() throws ModelException;
        
    public Collection<IServiceImplementation> getImplementations();
    
    public void disposeImplementations();
    
    public IServiceProfile getProfile();
    
    public Collection<IResult> executeSingleResultFunctionalitySyncronously(
            String advertisedFunctionalityName, 
            Collection<IAnnotatedValue> values) 
            throws ModelException;
    
    public void executeMultipleResultFunctionalityAsyncronously(
            String advertisedFunctionalityName, 
            Collection<IAnnotatedValue> values,
            IAsyncMessageClient client) 
            throws ModelException;
    
    public void executeSingleResultFunctionalityAsyncronously(
            String advertisedFunctionalityName, 
            Collection<IAnnotatedValue> values,
            IAsyncMessageClient client) 
            throws ModelException;
    
    public void subscribeTo(
            String advertisedSubscriptionName, 
            IAsyncMessageClient client) 
            throws ModelException;
    
    public ISemanticIdentifier getIdentifier();
    
    public IServiceFunctionality getFunctionality(String functionalityName) throws ModelException;
    
    public IBSDLRegistry getBSDLRegistry();
    
    public IServiceDescription applyConnectorURL(String connectorURL) throws ModelException;
    
}
