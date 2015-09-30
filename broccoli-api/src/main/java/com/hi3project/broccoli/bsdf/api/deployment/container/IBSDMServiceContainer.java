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

package com.hi3project.broccoli.bsdf.api.deployment.container;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.asyncronous.IMessageBroker;
import com.hi3project.broccoli.bsdf.api.deployment.bd.IServicesDB;
import com.hi3project.broccoli.bsdf.api.discovery.IMatchmaker;
import com.hi3project.broccoli.bsdf.api.discovery.IServiceRegistry;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;

/**
 * <p>
 * <b>Description:</b></p>
 *  Interface for a closed service container that manages the whole lifecycle of
 * BSDF services.
 *
 * 
 * <p>
 *  Colaborations:
 *
 * <ul>
 * <li>with an IMessageBroker, that will communicate with services clients and 
 * also with other service containers.</li>
 * <li>with a BSDLRegistry to manage the loaded BSDL axioms.</li>
 * <li>with an IServiceRegistry that will handle the BSDM services.</li>
 * <li>with an IServicesDB that manages the metainformation of the loaded services.</li>
 * </ul>
 *
 * <p>
 *  Responsabilities:
 *
 * <ul>
 * <li>install a packed BSDF service.</li>
 * <li>start and stop the management.</li>
 * <li>know and can change the working location (to deploy loaded services...).</li>
 * </ul>
 *
 * <p><b>Creation date:</b> 
 * 17-07-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 17-07-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public interface IBSDMServiceContainer 
{
    
    public void addRemoteContainer(ISemanticIdentifier remoteBrokerIdentifier)
            throws ServiceExecutionException;
    
    
    public void start() throws ServiceExecutionException;
    
    public void stop() throws ServiceExecutionException;
    
    public void restart() throws ServiceExecutionException;
    
    
    public void registerService(ISemanticLocator packedServiceDescriptor) 
            throws ServiceExecutionException;
    
    
    public ISemanticLocator getWorkingLocation();
    
    public void setWorkingLocation(ISemanticLocator location);
    
    
    public IMessageBroker getMessageBroker();
        
    public IBSDLRegistry getBSDLRegistry();
    
    public IServiceRegistry getBSDMServiceRegistry();
    
    public IServicesDB getServicesDB();
    
    public IMatchmaker getMatchmaker();
    
    public void setMatchmaker(IMatchmaker matchmaker);
    
    
    public void activateControlChannels() throws ModelException;

}
