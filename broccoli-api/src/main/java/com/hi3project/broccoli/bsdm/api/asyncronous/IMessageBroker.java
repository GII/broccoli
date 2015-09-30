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

package com.hi3project.broccoli.bsdm.api.asyncronous;

import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdf.api.deployment.container.IBSDMServiceContainer;
import com.hi3project.broccoli.bsdf.exceptions.MessageBrokerException;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceGroundingException;

/**
 * <p>
 * <b>Description:</b></p>
 *  Interface for a message broker for BSDF services. The main intent is to manage
 * asyncronous messages.
 *
 *
 * <p>
 *  Responsabilities:
 *
 * <ul>
 * <li>start and stop the execution.</li>
 * <li>register a BSDF service, linking each of its groundings to the broker.</li>
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
public interface IMessageBroker 
{

    public void start() throws MessageBrokerException;
    
    public void stop() throws MessageBrokerException;
    
    public void restart() throws MessageBrokerException;
    
    public void setMainControlChannel(IBSDMServiceContainer serviceContainer) throws MessageBrokerException;
    
    public void registerService(IServiceDescription serviceDescription) 
            throws MessageBrokerException, ServiceGroundingException;
    
}
