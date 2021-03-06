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

import com.hi3project.broccoli.bsdf.api.deployment.bd.IServicesDB;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;

/**
 * <p>
 * <b>Description:</b></p>
 *  Interface for "pico" container, that has neither broker, nor service registry
 * and no connection with other container either.
 *
 *
 * <p>
 *  Responsabilities:
 *
 * <ul>
 * <li>can register a service</li>
 * </ul>
 *
 * <p><b>Creation date:</b> 
 * 06-03-2015 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 06-03-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public interface IBSDMServicePicoContainer 
{
    public void registerService(ISemanticLocator packedServiceDescriptor) 
            throws ServiceExecutionException;
    
    public ISemanticLocator getWorkingLocation();
    
    public void setWorkingLocation(ISemanticLocator location);
    
    public IServicesDB getServicesDB();
    
    
    public void start() throws ServiceExecutionException;
    
    public void stop() throws ServiceExecutionException;
    
    public void restart() throws ServiceExecutionException;
    
    
    public boolean hasStarted();
}
