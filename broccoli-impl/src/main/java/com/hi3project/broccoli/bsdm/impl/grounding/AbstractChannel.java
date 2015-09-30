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

package com.hi3project.broccoli.bsdm.impl.grounding;

import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.serializing.IMessageSerializer;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdm.impl.parsing.BSDLBSDMLoader;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.io.BSDFLogger;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * <p>
 *  <b>Description:</b></p>
 *  Basic definition for an asyncronous channel, that may be implemented
 * as consumer or producer.
 *
 * 
 * <p>
 *  Colaborations:
 *
 * <ul>
 * <li>a IBSDLRegistry and IMessageSerializer can help with serialization and
 * deserialization of messages</li>
 * </ul>
 * 
 * <p>
 *  Responsabilities:
 *
 * <ul>
 * <li>it knows its name</li>
 * <li>it handles a list of callbacks (mainly to send them a received message notification)</li>
 * </ul>
 *
 * <p><b>Creation date:</b> 
 * 16-01-2015 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 16-01-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public abstract class AbstractChannel 
{
    
    protected String name = null;
    
    protected Collection<IAsyncMessageClient> clientCallbacks;
    
    protected IBSDLRegistry bsdlRegistry = null;
    
    protected IMessageSerializer parameterValuesSerializer = null;
    
    
    
    public AbstractChannel(IMessageSerializer parameterValuesSerializer, IBSDLRegistry bsdlRegistry, String name)
    {
        BSDFLogger.getLogger().info("Instances a channel: " + name);
        this.clientCallbacks = new ArrayList<IAsyncMessageClient>();
        this.parameterValuesSerializer = parameterValuesSerializer;
        this.bsdlRegistry = bsdlRegistry;
        this.name = name;
    }
    
    
    public AbstractChannel(IMessageSerializer parameterValuesSerializer, String name) throws ModelException
    {
        this(parameterValuesSerializer, new BSDLRegistry(new BSDLBSDMLoader()), name);
    }
    
    
    
    public void addClientCallback(IAsyncMessageClient clientCallback)
    {
        this.clientCallbacks.add(clientCallback);
    }

}
