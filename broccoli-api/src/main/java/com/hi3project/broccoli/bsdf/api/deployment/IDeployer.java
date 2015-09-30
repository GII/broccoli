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

package com.hi3project.broccoli.bsdf.api.deployment;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdf.api.discovery.IRegistryLoader;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import java.util.Collection;

/**
 * <p><b>Description:</b></p>
 *
 *
 * <p><b>Creation date:</b> 
 * 16-05-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 16-05-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public interface IDeployer 
{

    public Collection<ISemanticIdentifier> deployService(
            ISemanticLocator serviceLocator,
            IBSDLRegistry bsdlRegistry,
            IRegistryLoader serviceRegistry) throws ModelException;
    
    public Collection<ISemanticIdentifier> deployService(
            IServiceDescriptors serviceDescriptors,
            IBSDLRegistry bsdlRegistry,
            IRegistryLoader serviceRegistry) throws ModelException;
    
    public void undeployService(
            ISemanticIdentifier serviceIdentifier,
            IBSDLRegistry bsdlRegistry,
            IRegistryLoader serviceRegistry) throws ModelException;
    
}
