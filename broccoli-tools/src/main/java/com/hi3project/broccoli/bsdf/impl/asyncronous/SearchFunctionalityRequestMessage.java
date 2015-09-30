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

import com.hi3project.broccoli.bsdm.impl.asyncronous.AbstractMessage;
import com.hi3project.broccoli.bsdm.api.asyncronous.IMessage;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 *  <b>Description:</b></p>
 *  Message to represent a request for a functionality search,
 * sent from a service client to a service container.
 *
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
public class SearchFunctionalityRequestMessage extends AbstractMessage implements IMessage, Cloneable
{
    
    private Collection<String> requestedFunctionalityContents;
    

    public SearchFunctionalityRequestMessage(String clientName, String conversationId)
    {
        this(clientName, conversationId, new ArrayList<String>());        
    }
    
    public SearchFunctionalityRequestMessage(String clientName, String conversationId, Collection<String> requestedFunctionalityContents)
    {
        
        super(clientName, conversationId);
        this.requestedFunctionalityContents = requestedFunctionalityContents;
    }

    public Collection<String> getRequestedFunctionalityContents()
    {
        return requestedFunctionalityContents;
    }

    public void addRequestedFunctionality(String requestedFunctionality)
    {
        this.requestedFunctionalityContents.add(requestedFunctionality);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + (this.requestedFunctionalityContents != null ? this.requestedFunctionalityContents.hashCode() : 0);
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
        if (!super.equals(obj))
        {
            return false;
        }
        final SearchFunctionalityRequestMessage other = (SearchFunctionalityRequestMessage) obj;
        if (this.requestedFunctionalityContents != other.requestedFunctionalityContents && (this.requestedFunctionalityContents == null || !this.requestedFunctionalityContents.equals(other.requestedFunctionalityContents)))
        {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return new SearchFunctionalityRequestMessage(
                this.getClientName(),
                this.getConversationId(),
                requestedFunctionalityContents);
    }

    @Override
    public String toString()
    {
        return "SearchFunctionalityRequestMessage{" + "requestedFunctionalityContents=" + requestedFunctionalityContents 
                + super.toString()
                + '}';
    }
        
    
}
