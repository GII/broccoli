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

package com.hi3project.broccoli.bsdm.api.serializing;

import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.asyncronous.IMessage;
import com.hi3project.broccoli.bsdm.impl.exceptions.SerializingException;

/**
 * <p><b>Description:</b></p>
 *  Interface for a serializer that can serialize and deserialize messages,
 * converting between text representation and IMessage.
 *
 *
 * <p><b>Creation date:</b> 
 * 27-06-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 27-06-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public interface IMessageSerializer 
{

    public String serializeMessage(
            IMessage msg,
            IBSDLRegistry bsdlRegistry) throws SerializingException;
    
    public IMessage deserializeMessage(
            String msg,
            IBSDLRegistry bsdlRegistry) throws SerializingException;
    
}
