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

import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.asyncronous.IChannelConsumer;
import com.hi3project.broccoli.bsdm.api.asyncronous.IChannelProducer;
import com.hi3project.broccoli.bsdm.api.asyncronous.IMessage;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityExecMessage;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityResultMessage;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGrounding;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGroundingFactory;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGrounding;
import com.hi3project.broccoli.bsdm.api.serializing.IMessageSerializer;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceGroundingException;
import com.hi3project.broccoli.io.BSDFLogger;

/**
 * <p>
 * <b>Description:</b></p>
 * Implementation of AbstractServiceGrounding meant for an async communication.
 *
 *
 * <p>
 * Colaborations:
 * </p>
 * <ul>
 * <li>a controlChannelProducer to send result and notification messages.</li>
 * <li>a controlChannelConsumer to receive execution messages.</li>
 * </ul>
 *
 * <p>
 * Responsabilities:
 * </p>
 * <ul>
 * <li>can create an IChannelProducer.</li>
 * <li>can create an IChannelConsumer, initializing it with a messages
 * callback.</li>
 * </ul>
 *
 *
 *
 * <p>
 * <b>Creation date:</b>
 * 23-06-2014 </p>
 *
 * <p>
 * <b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 23-06-2014 - Initial release</li>
 * </ul>
 *
 *
 * 
 * @version 1
 */
public abstract class AsyncMessageServiceGrounding extends AbstractServiceGrounding
{

    protected String connectorURL;
    protected String controlChannelName;

    protected IChannelProducer controlChannelProducer = null;
    protected IChannelConsumer controlChannelConsumer = null;

    public AsyncMessageServiceGrounding(
            IFunctionalityGroundingFactory funcGroundingFactory,
            IMessageSerializer messageSerializer,
            Instance instance,
            IServiceDescription serviceDescription) throws ModelException
    {
        super(funcGroundingFactory, messageSerializer, instance, serviceDescription);
        this.connectorURL = getSinglePropertyAsInstanceLiteralValue("url");
        this.controlChannelName = serviceDescription.getIdentifier().getURI().toString();
    }

    /*
     An IChannelProducer is created with the controlChannelName associated with
     this grounding.
     */
    @Override
    public synchronized IChannelProducer getControlChannelProducer() throws ServiceGroundingException
    {
        if (null == this.controlChannelProducer)
        {
            this.controlChannelProducer = this.newChannelProducer();
        }
        return this.controlChannelProducer;
    }

    public String getConnectorURL()
    {
        return this.connectorURL;
    }

    public void setConnectorURL(String connectorURL)
    {
        this.connectorURL = connectorURL;
    }

    public void addSufix(String sufix)
    {
        this.controlChannelName += sufix;
    }

    public IServiceGrounding setConnectorURLforNewGrounding(String connectorURL) throws ModelException
    {
        this.connectorURL = connectorURL;

        IServiceGrounding newInstance = this.newInstance();

        if (newInstance instanceof AsyncMessageServiceGrounding)
        {
            ((AsyncMessageServiceGrounding) newInstance).setConnectorURL(connectorURL);
            if (null != this.controlChannelConsumer)
            {
                ((AsyncMessageServiceGrounding) newInstance).activateChannelConsumer();
            }
        }

        for (IFunctionalityGrounding functionalityGrounding : this.functionalityGroundings())
        {
            newInstance.addFunctionalityGrounding(functionalityGrounding);
            functionalityGrounding.setServiceGrounding(newInstance);
        }

        return newInstance;
    }

    public AsyncMessageServiceGrounding newGroundingFor(String name) throws ModelException
    {
        AsyncMessageServiceGrounding newGrounding = (AsyncMessageServiceGrounding) this.setConnectorURLforNewGrounding(this.connectorURL);
        newGrounding.controlChannelName += name;
        return newGrounding;
    }

    protected abstract IChannelProducer newChannelProducer() throws ServiceGroundingException;

    protected abstract IChannelConsumer newChannelConsumer() throws ServiceGroundingException;

    protected abstract IServiceGrounding newInstance() throws ModelException;

    @Override
    public void activate() throws ServiceGroundingException
    {
        this.activateChannelConsumer();
    }

    @Override
    public void deactivate() throws ServiceGroundingException
    {
        if (null != this.controlChannelConsumer)
        {
            this.controlChannelConsumer.close();
            this.controlChannelConsumer = null;
        }
        if (null != this.controlChannelProducer)
        {
            this.controlChannelProducer.close();
            this.controlChannelProducer = null;
        }
    }

    /*
     An IChannelConsumer is created with the controlChannelName associated with
     this grounding. 
     A client callback is added to it, that handles the received FunctionalityExecMessage
     and initializes another client callback that will handle the received resulting results, routing
     them accordingly.
     */
    public synchronized IChannelConsumer activateChannelConsumer() throws ServiceGroundingException
    {
        if (null == this.controlChannelConsumer)
        {
            this.controlChannelConsumer = this.newChannelConsumer();
            this.controlChannelConsumer.addClientCallback(new IAsyncMessageClient()
            {

                @Override
                public void receiveMessage(IMessage msg) throws ModelException
                {
                    if (msg instanceof FunctionalityExecMessage)
                    {

                        BSDFLogger.getLogger().info(
                                "Process received FunctionalityExecMessage message: "
                                + msg.toString()
                                + " , callback from: "
                                + AsyncMessageServiceGrounding.this.toString());

                        final FunctionalityExecMessage feMsg = (FunctionalityExecMessage) msg;

                        AbstractFunctionalityGrounding funGrounding
                                = AsyncMessageServiceGrounding.this.getFunctionalityGrounding(feMsg.getFunctionalityName());
                        funGrounding.getProviderChannelFor(
                                        feMsg.getFunctionalityName(),
                                        feMsg.getClientName(),
                                        feMsg.getConversationId());

                        AsyncMessageServiceGrounding.this.
                                getServiceDescription().executeMultipleResultFunctionalityAsyncronously(
                                        feMsg.getFunctionalityName(),
                                        feMsg.values(),
                                        new IAsyncMessageClient()
                                        {

                                            // this callback handles the received results and notifications
                                            @Override
                                            public void receiveMessage(IMessage msg) throws ModelException
                                            {
                                                if (msg instanceof FunctionalityResultMessage)
                                                {

                                                    BSDFLogger.getLogger().info(
                                                            "Process received FunctionalityResultMessage message: "
                                                            + msg.toString()
                                                            + " , callback from: "
                                                            + AsyncMessageServiceGrounding.this.toString());

                                                    FunctionalityResultMessage frMsg = (FunctionalityResultMessage) msg;
                                                    AbstractFunctionalityGrounding funGrounding
                                                    = AsyncMessageServiceGrounding.this.getFunctionalityGrounding(frMsg.getFunctionalityName());
                                                    IChannelProducer providerChannel
                                                    = funGrounding.getProviderChannelFor(
                                                            feMsg.getFunctionalityName(),
                                                            feMsg.getClientName(),
                                                            feMsg.getConversationId());
                                                    frMsg.setConversationId(feMsg.getConversationId());
                                                    providerChannel.send(frMsg);
                                                }
                                            }

                                            @Override
                                            public String getName()
                                            {
                                                return feMsg.getClientName();
                                            }
                                        });
                    }
                }

                @Override
                public String getName()
                {
                    return "Message execution client for " + AsyncMessageServiceGrounding.this.toString();
                }
            });
        }
        return this.controlChannelConsumer;
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/grounding#asyncMessageGrounding");
    }

    @Override
    public String toString()
    {
        return "AsyncMessageServiceGrounding{" + "connectorURL=" + connectorURL + ", controlChannelName=" + controlChannelName
                + super.toString()
                + '}';
    }

}
