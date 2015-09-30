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

package com.hi3project.broccoli.bsdf.impl.asyncronous;

import com.hi3project.broccoli.bsdm.impl.asyncronous.DescriptorData;
import com.hi3project.broccoli.bsdm.impl.asyncronous.AbstractMessage;
import com.hi3project.broccoli.bsdm.api.asyncronous.IMessage;
import com.hi3project.broccoli.bsdf.api.deployment.IServiceDescriptors;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 *  <b>Description:</b></p>
 *  Message to represent a request to register a service descriptor,
 * sent from a service client to a service container.
 *
 *
 * <p><b>Creation date:</b> 
 * 27-01-2015 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 27-01-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class RegisterServiceRequestMessage extends AbstractMessage implements IMessage, IServiceDescriptors
{
    
    private DescriptorData serviceDescriptorContents;
    private Collection<DescriptorData> ontologiesContents;

    public RegisterServiceRequestMessage(String clientName, String conversationId)
    {
        super(clientName, conversationId);
        this.ontologiesContents = new ArrayList<DescriptorData>();
    }

    @Override
    public DescriptorData getServiceDescriptorContents()
    {
        return serviceDescriptorContents;
    }
    
    public void setServiceDescriptorContents(DescriptorData descriptorContent)
    {
        this.serviceDescriptorContents = descriptorContent;
    }
    
    @Override
    public Collection<DescriptorData> getOntologiesDescriptorsContents()
    {
        return ontologiesContents;
    }
    
    public void addOntologyDescriptorContents(DescriptorData descriptorContent)
    {
        this.ontologiesContents.add(descriptorContent);
    }
    
    public void setOntologyDescriptorContents(Collection<DescriptorData> descriptorContents)
    {
        this.ontologiesContents = descriptorContents;
    }
    
    @Override
    public String getName()
    {
        return this.getClientName();
    }

    @Override
    public String toString()
    {
        return "RegisterServiceRequestMessage{" + "serviceDescriptorContents=" + serviceDescriptorContents 
                + ", ontologiesContents=" + ontologiesContents 
                + super.toString()
                + '}';
    }        
    
}
