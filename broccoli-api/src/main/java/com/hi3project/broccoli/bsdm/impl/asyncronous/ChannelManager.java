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

import com.hi3project.broccoli.bsdm.api.asyncronous.IChannel;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * <b>Description:</b></p>
 * Runner class that manages IChannel with different levels of identification.
 * Some channels have at least a functionalityName, and/or a requesterID, and/or
 * a conversationID. Other channels have nothing to identify them.
 *
 *
 * <p>
 * Colaborations:
 *
 * <ul>
 * <li>with IChannel instances.</li>
 * </ul>
 *
 * <p>
 * Responsabilities:
 *
 * <ul>
 * <li></li>
 * <li></li>
 * </ul>
 *
 * <p>
 * Internally, channels with no identification are handled separatelly in a
 * Collection. Channels with functionalityName at least are stored in two
 * different HashMap: one where functionalityName links with a Collection of
 * channels, and another one where a calculated ID links with one and only one
 * channel.
 *
 *
 * 
 * @param <T>
 */
public class ChannelManager<T extends IChannel> extends Thread
{

    private static final int WAIT = 250;
    private boolean processing = false;

    private final Collection<T> channels;
    private final HashMap<String, T> channelsWithId;
    private final HashMap<String, Collection<T>> channelsWithFunctionalityName;

    public ChannelManager()
    {
        this.processing = false;
        this.channels = new ArrayList();
        this.channelsWithId = new HashMap<String, T>();
        this.channelsWithFunctionalityName = new HashMap<String, Collection<T>>();
    }

    /*
     Retrieve only the channels with no identification
     */
    public Collection<T> getUnidentifiedChannels()
    {
        return this.channels;
    }

    /*
     Retrieve all the stored channels
     */
    public synchronized Collection<T> getAllChannels()
    {
        Collection<T> allChannels = new ArrayList<T>();
        allChannels.addAll(this.channels);
        allChannels.addAll(this.channelsWithId.values());
        return allChannels;
    }

    /*
     Adds a channel without identification
     */
    public void addChannel(T channel)
    {
        synchronized (this.channels)
        {
            this.channels.add(channel);
        }
    }

    /*
     Removes a channel without identification
     */
    public void removeChannel(T channel)
    {
        synchronized (this.channels)
        {
            this.channels.remove(channel);
        }
    }

    /*
     Adds a channel with identification.
     @requesterID and @conversationID are optional.
     */
    public void addChannel(
            T channel,
            String functionalityName,
            String requesterID,
            String conversationID)
    {
        T oldCh = null;
        synchronized (this.channelsWithId)
        {
            oldCh = this.channelsWithId.get(calculateId(functionalityName, requesterID, conversationID));
            this.channelsWithId.put(calculateId(functionalityName, requesterID, conversationID), channel);
        }
        synchronized (this.channelsWithFunctionalityName)
        {
            Collection<T> channelsForFunctionalityName = this.channelsWithFunctionalityName.get(functionalityName);
            if (null == channelsForFunctionalityName)
            {
                channelsForFunctionalityName = new ArrayList<T>();
            }
            // channels are stored in
            if (null != oldCh)
            {
                channelsForFunctionalityName.remove(oldCh);
            }
            channelsForFunctionalityName = this.removeInternalChannels(channelsForFunctionalityName);
            channelsForFunctionalityName.add(channel);
            this.channelsWithFunctionalityName.put(functionalityName, channelsForFunctionalityName);
        }
    }
    
    
    private Collection<T> removeInternalChannels(Collection<T> channelsList)
    {
        Collection<T> channelsToRemove = new ArrayList();
        for (T ch : channelsList)
        {
            if (ch instanceof InternalChannel)
            {
                channelsToRemove.add(ch);
            }
        }
        
        channelsList.removeAll(channelsToRemove);
        return channelsList;
    }
    

    /*
     Retrieves a channel by its identification.
     */
    public T getChannel(
            String functionalityName,
            String requesterID,
            String conversationID)
    {
        synchronized (this.channelsWithId)
        {
            return this.channelsWithId.get(calculateId(functionalityName, requesterID, conversationID));
        }
    }

    /*
     Retrieves all channels identified with the given @functionalityName.
     */
    public Collection<T> getChannels(String functionalityName)
    {
        synchronized (this.channelsWithFunctionalityName)
        {
            return this.channelsWithFunctionalityName.get(functionalityName);
        }
    }

    /*
     Removes a channel with its given identification.
     */
    public void removeChannel(
            T channel,
            String functionalityName,
            String requesterID,
            String conversationID)
    {
        synchronized (this.channelsWithId)
        {
            this.channelsWithId.remove(calculateId(functionalityName, requesterID, conversationID));
        }
        synchronized (this.channelsWithFunctionalityName)
        {
            this.channelsWithFunctionalityName.get(functionalityName).remove(channel);
        }
    }

    public void startProcessing()
    {
        this.processing = true;
        this.start();
    }

    public void stopProcessing()
    {
        this.processing = false;
    }

    @Override
    public void run()
    {

        while (this.processing)
        {

            for (T channel : this.getAllChannels())
            {
                try
                {
                    channel.signalReception();
                } catch (ModelException ex)
                {
                    Logger.getLogger(ChannelManager.class.getName()).log(Level.WARNING, "channel.signalReception()", ex);
                }
            }

            pause();
        }

    }

    private void pause()
    {
        try
        {
            Thread.sleep(WAIT);
        } catch (InterruptedException ex)
        {
        }
    }

    private String calculateId(
            String functionalityName,
            String requesterID,
            String conversationID)
    {
        return functionalityName
                + "-"
                + (null == requesterID ? "" : requesterID)
                + "-"
                + (null == conversationID ? "" : conversationID);
    }
}
