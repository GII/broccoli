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

import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.asyncronous.IMessage;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityResultMessage;
import com.hi3project.broccoli.io.BSDFLogger;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * <b>Creation date:</b>
 * 09-06-2015 </p>
 *
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 09-06-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class Callback implements IAsyncMessageClient
{
    
    public static final int NumberOfIterations = 150;

    private Collection<IResult> results;

    private String conversationId;

    private boolean received = false;

    private boolean onlyMsgsWithResults = false;

    public Callback(String conversationId)
    {
        this.results = new ArrayList<>();
        this.conversationId = conversationId;
    }

    public Callback(String conversationId, boolean onlyMsgsWithResults)
    {
        this(conversationId);
        this.onlyMsgsWithResults = onlyMsgsWithResults;
    }

    @Override
    public synchronized void receiveMessage(IMessage msg) throws ModelException
    {
        if (msg instanceof FunctionalityResultMessage
                && (null != ((FunctionalityResultMessage) msg).getResult()))
        {
            if (!(onlyMsgsWithResults && ((FunctionalityResultMessage) msg).getResult().isEmpty()))
            {
                results = ((FunctionalityResultMessage) msg).getResult();
                this.markReceived();
            }
        }
    }

    @Override
    public String getName()
    {
        return "False sync for " + conversationId;
    }

    public Collection<IResult> getResults()
    {
        return this.results;
    }

    private synchronized boolean wasReceived()
    {
        return this.received;
    }

    private synchronized void markReceived()
    {
        this.received = true;
    }

    public void runAndWait() throws InterruptedException
    {
        int iteration = 0;
        
        while (!wasReceived() && iteration < NumberOfIterations)
        {
            iteration++;
            Thread.sleep(250);
        }
        
        BSDFLogger.getLogger().info("Callback has ended: " + this.getName() 
                + ", with convId: " + this.conversationId
                + ", was received: " + this.wasReceived()
                + ", iteration: " + iteration);
    }
}
