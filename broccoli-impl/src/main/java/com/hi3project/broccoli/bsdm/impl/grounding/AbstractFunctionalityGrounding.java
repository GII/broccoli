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

import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.asyncronous.IChannelConsumer;
import com.hi3project.broccoli.bsdm.api.asyncronous.IChannelProducer;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGrounding;
import com.hi3project.broccoli.bsdm.api.implementation.IFunctionalityImplementation;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdm.impl.asyncronous.ChannelManager;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityExecMessage;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityResultMessage;
import com.hi3project.broccoli.bsdm.impl.asyncronous.InternalChannel;
import com.hi3project.broccoli.bsdm.impl.profile.functionality.ServiceFunctionality;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGrounding;
import com.hi3project.broccoli.bsdm.api.implementation.FunctionalityExecutionVO;
import com.hi3project.broccoli.bsdm.api.serializing.IMessageSerializer;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceGroundingException;
import com.hi3project.broccoli.io.BSDFLogger;
import java.util.Collection;


/**
 * <p>
 * Generic functionality grounding. It handles the execution of an advertised
 * functionality, that can be archieved using a functionality implementation or
 * communicating using the grounding parameters.
 * </p>
 * 
 * Colaborations:
 *
 * <ul>
 * <li>a ChannelManager that manages IChannelConsumer, used to receive result
 * and notification messages.</li>
 * <li>a ChannelManager that manages IChannelProducer, used to send result and
 * notification messages. It is shared with the related service
 * implementation.</li>
 * <li>an IServiceImplementation, when the grounding knows a local
 * implementation for its functionality.</li>
 * <li>an AbstractServiceGrounding where the functionality grounding
 * belongs.</li>
 * <li>a SimpleIdGenerator that helps to calculate an incremental id for each
 * channel that needs to be created. That id will be used as "conversation ID"
 * to relate result messages with already opened channels.</li>
 * </ul>
 *
 * Responsabilities:
 *
 * <ul>
 * <li>Execute syncronously, given the inputs. This only makes sense when there
 * is a local implementation available.</li>
 * <li>Execute (asyncronously), given the inputs and a IAsyncMessageClient
 * callback object. This callback is used to send back the N result messages to
 * the client(s) where they belong.</li>
 * <li>Register a result, given its outputs with a FunctionalityResultMessage.
 * The AbstractFunctionalityGrounding object knows which channel this message
 * must be routed through.</li>
 * </ul>
 *
 * <p>
 * The methods to create consumer and producer channels are left as abstract to
 * be implemented by a class that depends on a specific messaging technology.
 * </p>
 *
 * 
 */
public abstract class AbstractFunctionalityGrounding extends ConceptBehaviourImplementation implements IFunctionalityGrounding
{

    private IServiceFunctionality advertisedFunctionality = null;
    private ChannelManager<IChannelConsumer> channelConsumersHelper = null;
    private ChannelManager<IChannelProducer> channelProducersHelper = null;
    private IChannelConsumer subscriptionsConsumerChannel = null;
    private IChannelProducer subscriptionsProducerChannel = null;
    private IServiceImplementation serviceImplementation = null;
    private SimpleIdGenerator idGenerator = null;
    protected AbstractServiceGrounding serviceGrounding = null;

    public AbstractFunctionalityGrounding(
            Instance instance,
            AbstractServiceGrounding serviceGrounding,
            ChannelManager channelProducersHelper,
            IMessageSerializer messageSerializer) throws SemanticModelException
    {
        setInstance(instance);
        this.serviceGrounding = serviceGrounding;
        this.channelConsumersHelper = new ChannelManager<>();
        this.channelProducersHelper = channelProducersHelper;
        this.idGenerator = new SimpleIdGenerator();
    }

    /*
     This method is meant to be invoked by functionality implementations to register results
     using FunctionalityResultMessage and, optionally, client ID and conversationID.
     It works with the channelProducersHelper to search for a channel previously created
     that relate to the given parameters.    
     */
    @Override
    public void registerResult(String requesterID, String conversationID, FunctionalityResultMessage resultMsg) throws ServiceExecutionException
    {

        BSDFLogger.getLogger().info(
                "Registers a result for requester: " + requesterID
                + ", conversation: " + conversationID);

        IChannelProducer channel = this.channelProducersHelper.getChannel(resultMsg.getFunctionalityName(), requesterID, conversationID);
        try
        {
            if (null != channel && (channel instanceof InternalChannel))
            {

                channel.send(resultMsg);
                channel.signalReception();

            } else
            {
                Collection<IChannelProducer> channels = this.channelProducersHelper.getChannels(resultMsg.getFunctionalityName());

                if (null != channels && !channels.isEmpty())
                {
                    for (IChannelProducer ch : channels)
                    {
                        if (ch instanceof InternalChannel)
                        {
                            ch.send(resultMsg);
                            ch.signalReception();
                        }

                    }
                } else
                {
                    for (IChannelProducer ch : this.channelProducersHelper.getUnidentifiedChannels())
                    {

                        if (!(ch instanceof InternalChannel))
                        {
                            ch.send(resultMsg);
                            ch.signalReception();
                        }

                    }
                }
            }
        } catch (ModelException ex)
        {
            throw new ServiceExecutionException(ex.getMessage(), ex);
        }

    }
    
    
    @Override
    public void registerNotification(FunctionalityResultMessage resultMsg)
                throws ServiceExecutionException
    {
        if (null != this.getProviderChannelForSubscription(resultMsg.getFunctionalityName()))
        {
            this.subscriptionsProducerChannel.send(resultMsg);
            try
            {
                this.subscriptionsProducerChannel.signalReception();
            } catch (ModelException ex)
            {
                throw new ServiceExecutionException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public Collection<IResult> executeWithValuesSyncronously(Collection<IAnnotatedValue> values) throws ModelException
    {
        // a new conversation ID is generated
        String conversationId = this.idGenerator.getSId();
        Callback cb = new Callback("syncCB" + conversationId, true);
        
        if (null != this.getServiceImplementation())
        {
            return this.getServiceImplementation().executeWithValues(
                    this.name(),
                    values,
                    new FunctionalityExecutionVO(cb.getName(), conversationId));
        }       

        this.executeWithValues(values, cb);

        try
        {
            cb.runAndWait();
        } catch (InterruptedException ex)
        {
            throw new ServiceExecutionException("Problem in executeWithValuesSyncronously", ex);
        }

        return cb.getResults();
    }

    /*
     It executes the functionality, using the given inputs and communicating the result
     via the given IAsyncMessageClient callback.
     The execution can be resolved in two ways: using a functionality implementation
     (if there is any) or communicating throught the grounding.
     */
    @Override
    public void executeWithValues(Collection<IAnnotatedValue> values, IAsyncMessageClient clientCallback) throws ModelException
    {
        // a new conversation ID is generated
        String conversationId = this.idGenerator.getSId();

        if (null != this.getServiceImplementation())
        {
            IFunctionalityImplementation functionalityImplementation
                    = this.getServiceImplementation().getFunctionalityImplementation(this.name());
            if (null != functionalityImplementation)
            {
                // if there is an implementation for this functionality, its execution is invoked
                //and the results are sent back using the clientCallback
                this.registerInternalChannelsFor(this.name(), clientCallback, conversationId);
                Collection<IResult> results = 
                        functionalityImplementation.executeWithValues(
                                values,
                                new FunctionalityExecutionVO(clientCallback.getName(), conversationId));
                BSDFLogger.getLogger().info("Sends functionality implementation result to callback");
                clientCallback.receiveMessage(
                        new FunctionalityResultMessage(results,
                                this.name(),
                                clientCallback.getName(),
                                conversationId));
                return;
            }
        }

        // a clientChannel is obtained to receive result messages
        this.getClientChannelFor(this.name(), clientCallback, conversationId);
        // when there is not an implementation, the control channel is used to send and execution message...
        this.serviceGrounding.getControlChannelProducer().send(
                new FunctionalityExecMessage(values, this.name(), clientCallback.getName(), conversationId));
        BSDFLogger.getLogger().info("Sends FunctionalityExecMessage to grounding: " + this.serviceGrounding.toString());

    }
    
    
    @Override
    public void subscribeTo(IAsyncMessageClient clientCallback) throws ModelException
    {
        
        
        if (null != this.getServiceImplementation())
        {
            IFunctionalityImplementation functionalityImplementation
                    = this.getServiceImplementation().getFunctionalityImplementation(this.name());
            if (null != functionalityImplementation)
            {
                // a new conversation ID is generated
                String conversationId = this.idGenerator.getSId();
        
                // the results are sent back using the clientCallback
                this.registerInternalChannelsFor(this.name(), clientCallback, conversationId);
                return;
            }
        }

        // a clientChannel is obtained to receive result messages
        this.getClientChannelForSubscription(this.name(), clientCallback);
    }
    

    @Override
    public IServiceFunctionality getAdvertisedFunctionality()
    {
        return advertisedFunctionality;
    }

    @Override
    public void setAdvertisedFunctionality(IServiceFunctionality advertisedFunctionality) throws ModelException
    {
        this.advertisedFunctionality = advertisedFunctionality;
        advertisedFunctionality.setFunctionalityGrounding(this);
    }

    @Override
    public void setServiceGrounding(IServiceGrounding serviceGrounding)
    {
        if (serviceGrounding instanceof AbstractServiceGrounding)
        {
            this.serviceGrounding = (AbstractServiceGrounding) serviceGrounding;
        }
    }

    @Override
    public String name()
    {
        return this.getAdvertisedFunctionality().name();
    }


    /*
     When there is no asigned IServiceImplementation yet,
     asks its AdvertisedFunctionality for it.
     This scenario can happen when the implementation is not loaded together
     with the service grounding descriptor.
     */
    @Override
    public synchronized IServiceImplementation getServiceImplementation() throws ModelException
    {
        if (null == this.serviceImplementation)
        {
            IServiceFunctionality advFunc = this.getAdvertisedFunctionality();
            if (advFunc instanceof ServiceFunctionality)
            {
                IFunctionalityImplementation functionalityImplementation = ((ServiceFunctionality) advFunc).getFunctionalityImplementation();
                if (null != functionalityImplementation)
                {
                    this.serviceImplementation = functionalityImplementation.getServiceImplementation();
                }
            }
        }
        return this.serviceImplementation;
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/grounding#functionalityGrounding");
    }

    /*
     Asks the channelProducersHelper for a channel with the given parameters,
     and creates one when there is none available yet.
     */
    public IChannelProducer getProviderChannelFor(
            String functionalityName,
            String clientName,
            String conversationID) throws ServiceGroundingException
    {
        IChannelProducer channel = this.channelProducersHelper.getChannel(functionalityName, clientName, conversationID);
        if (null != channel && !(channel instanceof InternalChannel))
        {
            return channel;
        }

        IChannelProducer newChannel = createProviderChannelFor(functionalityName, clientName, conversationID);
        this.channelProducersHelper.addChannel(newChannel, functionalityName, clientName, conversationID);
        return newChannel;
    }
    
    public IChannelProducer getProviderChannelForSubscription(
            String functionalityName) throws ServiceGroundingException
    {
        if (null != this.subscriptionsProducerChannel)
        {
            return this.subscriptionsProducerChannel;
        }
        
        this.subscriptionsProducerChannel = createProviderSubscriptionChannelFor(functionalityName);
        return this.subscriptionsProducerChannel;
    }

    @Override
    public IOutputValue createOutput(Object result, IServiceFunctionality functionality) throws SemanticModelException
    {
        throw new UnsupportedOperationException("Not supported.");
    }

    protected abstract IChannelConsumer createClientChannelFor(String functionalityName, String client, String conversationID) throws ServiceGroundingException;
    
    protected abstract IChannelConsumer createClientSubscriptionChannelFor(String functionalityName) throws ServiceGroundingException;

    protected abstract IChannelProducer createProviderChannelFor(String functionalityName, String client, String conversationID) throws ServiceGroundingException;
    
    protected abstract IChannelProducer createProviderSubscriptionChannelFor(String functionalityName) throws ServiceGroundingException;

    /*
     Asks the channelConsumersHelper for a channel with the given parameters,
     and creates one when there is none available yet.
     */
    private IChannelConsumer getClientChannelFor(
            String functionalityName,
            IAsyncMessageClient client,
            String conversationID) throws ServiceGroundingException
    {
        IChannelConsumer channel = this.channelConsumersHelper.getChannel(functionalityName, client.getName(), conversationID);
        if (null != channel)
        {
            return channel;
        }

        IChannelConsumer newChannel = createClientChannelFor(functionalityName, client.getName(), conversationID);
        newChannel.addClientCallback(client);
        this.channelConsumersHelper.addChannel(newChannel, functionalityName, client.getName(), conversationID);
        return newChannel;
    }
    
    /*
     Asks the channelConsumersHelper for a channel with the given parameters,
     and creates one when there is none available yet.
     */
    private IChannelConsumer getClientChannelForSubscription(
            String functionalityName,
            IAsyncMessageClient client) throws ServiceGroundingException
    {
        if (null != this.subscriptionsConsumerChannel)
        {
            return this.subscriptionsConsumerChannel;
        }

        this.subscriptionsConsumerChannel = createClientSubscriptionChannelFor(functionalityName);
        this.subscriptionsConsumerChannel.addClientCallback(client);
        return this.subscriptionsConsumerChannel;
    }


    /*
     Creates an internal channel that will be used to
     send results back to an IAsyncMessageClient.
     */
    private void registerInternalChannelsFor(
            String functionalityName,
            IAsyncMessageClient client,
            String conversationID) throws ServiceGroundingException
    {
        InternalChannel ch = new InternalChannel(client);
        this.channelProducersHelper.addChannel(ch, functionalityName, conversationID, conversationID);
        this.channelConsumersHelper.addChannel(ch, functionalityName, conversationID, conversationID);
    }

}
