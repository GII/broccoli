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

package com.hi3project.broccoli.bsdm.impl.asyncronous;

import com.hi3project.broccoli.bsdm.api.asyncronous.IMessage;

/**
 *
 * <p><b>Creation date:</b> 
 * 23-09-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 23-09-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class AbstractMessage implements IMessage
{

    protected String clientName;
    
    protected String conversationId;
    

    public AbstractMessage(String clientName, String conversationId)
    {
        this.clientName = clientName;
        this.conversationId = conversationId;
    }
    
    public String getClientName()
    {
        return clientName;
    }

    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

    public String getConversationId()
    {
        return conversationId;
    }

    public void setConversationId(String conversationId)
    {
        this.conversationId = conversationId;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 79 * hash + (this.clientName != null ? this.clientName.hashCode() : 0);
        hash = 79 * hash + (this.conversationId != null ? this.conversationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final AbstractMessage other = (AbstractMessage) obj;
        if ((this.clientName == null) ? (other.clientName != null) : !this.clientName.equals(other.clientName))
        {
            return false;
        }
        return !((this.conversationId == null) ? (other.conversationId != null) : !this.conversationId.equals(other.conversationId));
    }

    @Override
    public String toString()
    {
        return "AbstractMessage{" + "clientName=" + clientName + ", conversationId=" + conversationId + '}';
    }
            
}
