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


import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.asyncronous.IChannelConsumer;
import com.hi3project.broccoli.bsdm.api.asyncronous.IChannelProducer;
import com.hi3project.broccoli.bsdm.api.asyncronous.IMessage;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceGroundingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * 
 */
public class InternalChannel implements IChannelConsumer, IChannelProducer
{
    
    private IAsyncMessageClient receiver = null;    
    private final Queue<IMessage> queue;
    private Collection<IAsyncMessageClient> clientCallbacks;
    
    
    public InternalChannel(IAsyncMessageClient receiver)
    {       
        this.receiver = receiver;
        this.queue = new LinkedList<IMessage>();
        this.clientCallbacks = new ArrayList<IAsyncMessageClient>();
    }        
    

    @Override
    public boolean send(IMessage data) throws ServiceGroundingException
    {
        synchronized(this.queue) 
        {
            this.queue.add(data);
            this.queue.notify();
        }
        return true;
    }
    
    
    @Override
    public void signalReception() throws ModelException
    {
        synchronized(this.queue) 
        {
            IMessage receivedMessage = this.getLastReceivedMessage();
            if (null != receivedMessage)
            {
                this.receiver.receiveMessage(receivedMessage);
            }
            for (IAsyncMessageClient clientCallback : this.clientCallbacks)
            {
                clientCallback.receiveMessage(receivedMessage);
            }
        }
    }

    @Override
    public IMessage getLastReceivedMessage()
    {
        synchronized(this.queue) 
        {
            return this.queue.poll();
        }
    }

    @Override
    public IMessage receiveOrWait()
    {
        synchronized(this.queue) 
        {

            while (this.queue.isEmpty()) 
            {
                try 
                {
                        this.queue.wait();
                }
                catch (InterruptedException e) {}
            }

            return this.queue.poll();
        }
    }      
    
    @Override
    public IMessage receiveOrWait(long ms)
    {
        synchronized(this.queue) 
        {

            while (this.queue.isEmpty()) 
            {
                try 
                {
                        this.queue.wait(ms);
                }
                catch (InterruptedException e) {}
            }

            return this.queue.poll();
        }
    }    

    @Override
    public void addClientCallback(IAsyncMessageClient clientCallback)
    {
        this.clientCallbacks.add(clientCallback);
    }

    @Override
    public void close() throws ServiceGroundingException
    {
        // hmmmm
    }

}
