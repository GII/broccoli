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
import com.hi3project.broccoli.bsdf.api.discovery.IFunctionalitySearchResult;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * <b>Description:</b></p>
 * Message to represent the result of a functionality search, sent from a
 * service container to a service client.
 *
 *
 * <p>
 * <b>Creation date:</b>
 * 19-09-2014 </p>
 *
 * <p>
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 19-09-2014 - Initial release</li>
 * </ul>
 *
 *
 * 
 * @version 1
 */
public class SearchFunctionalityResultMessage extends AbstractMessage implements IMessage, Cloneable
{

    private Collection<IFunctionalitySearchResult> searchResults;

    private Collection<DescriptorData> serviceDescriptorContents;

    private Collection<DescriptorData> ontologiesContents;
        

    public SearchFunctionalityResultMessage(
            String clientName,
            String conversationId)
    {
        this(clientName, conversationId, new ArrayList<IFunctionalitySearchResult>());
    }

    public SearchFunctionalityResultMessage(
            String clientName,
            String conversationId,
            Collection<IFunctionalitySearchResult> searchResults)
    {
        super(clientName, conversationId);
        this.searchResults = searchResults;
        this.serviceDescriptorContents = new ArrayList<DescriptorData>();
        this.ontologiesContents = new ArrayList<DescriptorData>();
    }

    public Collection<IFunctionalitySearchResult> getSearchResults()
    {
        return searchResults;
    }

    public void addSearchResult(IFunctionalitySearchResult searchResult)
    {
        this.searchResults.add(searchResult);
    }

    public Collection<DescriptorData> getServiceDescriptorsContents()
    {
        return serviceDescriptorContents;
    }

    public void addServiceDescriptorContents(DescriptorData descriptorConent)
    {
        this.serviceDescriptorContents.add(descriptorConent);
    }

    public void setServiceDescriptorsContents(Collection<DescriptorData> descriptorContent)
    {
        this.serviceDescriptorContents = descriptorContent;
    }

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

    public SearchFunctionalityResultMessage merge(SearchFunctionalityResultMessage msg)
    {
        if (msg.getClientName().equals(this.getClientName())
                && msg.getConversationId().equals(this.getConversationId()))
        {
            this.searchResults.addAll(msg.getSearchResults());
            this.serviceDescriptorContents.addAll(msg.getServiceDescriptorsContents());
            this.ontologiesContents.addAll(msg.getOntologiesDescriptorsContents());
        }
        return this;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 23 * hash + (this.searchResults != null ? this.searchResults.hashCode() : 0);
        hash = 23 * hash + (this.serviceDescriptorContents != null ? this.serviceDescriptorContents.hashCode() : 0);
        hash = 23 * hash + (this.ontologiesContents != null ? this.ontologiesContents.hashCode() : 0);
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
        final SearchFunctionalityResultMessage other = (SearchFunctionalityResultMessage) obj;
        if (this.searchResults != other.searchResults && (this.searchResults == null || !this.searchResults.equals(other.searchResults)))
        {
            return false;
        }
        if (this.serviceDescriptorContents != other.serviceDescriptorContents && (this.serviceDescriptorContents == null || !this.serviceDescriptorContents.equals(other.serviceDescriptorContents)))
        {
            return false;
        }
        return !(this.ontologiesContents != other.ontologiesContents && (this.ontologiesContents == null || !this.ontologiesContents.equals(other.ontologiesContents)));
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        SearchFunctionalityResultMessage sfrMessage
                = new SearchFunctionalityResultMessage(
                        this.getClientName(),
                        this.getConversationId(),
                        this.getSearchResults());

        sfrMessage.setServiceDescriptorsContents(this.getServiceDescriptorsContents());

        sfrMessage.setOntologyDescriptorContents(this.getOntologiesDescriptorsContents());

        return sfrMessage;
    }

    @Override
    public String toString()
    {
        return "SearchFunctionalityResultMessage{" + "searchResults=" + searchResults
                + ", serviceDescriptorContents=" + serviceDescriptorContents
                + ", ontologiesContents=" + ontologiesContents
                + super.toString()
                + '}';
    }

}
