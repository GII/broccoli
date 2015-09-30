/*******************************************************************************
 * Copyright (C) 2015 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright (C) 2015 Alejandro Paz <alejandropl@lagostelle.com>
 * <p>
 * This file is part of Broccoli.
 * <p>
 * Broccoli is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * Broccoli is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with Broccoli. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.hi3project.broccoli.bsdf.impl.serializing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.serializing.IMessageSerializer;
import com.hi3project.broccoli.bsdm.api.asyncronous.IMessage;
import com.hi3project.broccoli.bsdf.api.discovery.IFunctionalitySearchEvaluation;
import com.hi3project.broccoli.bsdf.api.discovery.IFunctionalitySearchResult;
import com.hi3project.broccoli.bsdf.impl.asyncronous.RegisterServiceRequestMessage;
import com.hi3project.broccoli.bsdf.impl.asyncronous.SearchFunctionalityRequestMessage;
import com.hi3project.broccoli.bsdf.impl.asyncronous.SearchFunctionalityResultMessage;
import com.hi3project.broccoli.bsdf.impl.discovery.BasicFunctionalitySearchEvaluation;
import com.hi3project.broccoli.bsdf.impl.parsing.ServiceDescriptionLoader;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IParameterValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdl.impl.Concept;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.InstanceArrayList;
import com.hi3project.broccoli.bsdl.impl.InstanceLiteral;
import com.hi3project.broccoli.bsdl.impl.InstanceLiteralArrayList;
import com.hi3project.broccoli.bsdl.impl.InstanceURI;
import com.hi3project.broccoli.bsdl.impl.PropertyValue;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.parsing.ReferenceToSemanticAxiom;
import com.hi3project.broccoli.bsdm.impl.asyncronous.DescriptorData;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityExecMessage;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityResultMessage;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGroundingFactory;
import com.hi3project.broccoli.bsdm.api.grounding.IServiceGroundingFactory;
import com.hi3project.broccoli.bsdm.impl.exceptions.SerializingException;
import com.hi3project.broccoli.bsdm.impl.functionality.OutputValue;
import com.hi3project.broccoli.bsdm.impl.functionality.ParameterValue;
import com.hi3project.broccoli.io.BSDFLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>
 * <b>Description:</b></p>
 * JSON implementation of IMessageSerializer. IMessage instances are transformed
 * to and from a JSON text implementation, using the "raw" data binding mode of
 * the jackson library. FunctionalityExecMessage and FunctionalityResultMessage
 * are currently handled. The basic idea is the same for both: messages are
 * composed of lists of elements that can be: normal instances,
 * instanceArrayList, instanceLiteral or InstanceURI. Each instance element is a
 * hashMap that has a type, a concept, and a value/values. Normal instances can
 * have propertyValues, and each propertyValue has a name and a value that can
 * be any kind of instance.
 *
 *
 * <p>
 * <b>Creation date:</b>
 * 27-06-2014 </p>
 *
 * <p>
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 27-06-2014 - Initial release</li>
 * </ul>
 *
 *
 *
 * @version 1
 */
public class JSONMessageSerializer implements IMessageSerializer {

    private ObjectMapper mapper;
    private IServiceGroundingFactory serviceGroundingFactory;
    private IFunctionalityGroundingFactory funcGroundingFactory;

    public static final String TYPE_NAME = "type";
    public static final String CONCEPT_NAME = "concept";
    public static final String PROPERTY_VALUES_NAME = "propertyValues";
    public static final String VALUE_NAME = "value";
    public static final String LIST_OF_VALUES_NAME = "values";
    public static final String PROPERTY_NAME_NAME = "name";

    public static final String MESSAGE_TYPE_NAME = "messageType";
    public static final String MESSAGE_TYPE_EXECFUNCTIONALITY_NAME = "messageExecFunctionality";
    public static final String MESSAGE_TYPE_RESULTS_NAME = "messageResults";
    public static final String MESSAGE_TYPE_SEARCHFUNCTIONALITY_REQUEST = "messageSearchFunctionalityRequest";
    public static final String MESSAGE_TYPE_SEARCHFUNCTIONALITY_RESPONSE = "messageSearchFunctionalityResponse";
    public static final String MESSAGE_TYPE_REGISTERSERVICE_REQUEST = "messageRegisterServiceRequest";

    public static final String MESSAGE_FUNCTIONALITY_NAME_NAME = "functionalityName";
    public static final String MESSAGE_FUNCTIONALITY_EVALUATION_BOOLEAN_VALUE = "evaluationBool";
    public static final String MESSAGE_FUNCTIONALITY_EVALUATION_NUMERIC_VALUE = "evaluationValue";
    public static final String MESSAGE_SERVICE_IDENTIFIER_NAME = "serviceSemanticIdentifier";
    public static final String MESSAGE_CLIENT_NAME_NAME = "clientName";
    public static final String MESSAGE_CONVERSATION_ID_NAME = "conversationID";
    public static final String MESSAGE_VALUES_NAME = "messageValues";
    public static final String MESSAGE_CONTENTS_NAME = "messageContents";
    public static final String MESSAGE_SERVICE_DESCRIPTOR_NAME_NAME = "serviceDescriptorName";
    public static final String MESSAGE_SERVICE_DESCRIPTOR_CONTENTS_NAME = "serviceDescriptorContents";
    public static final String MESSAGE_SERVICE_DESCRIPTORS_NAMES_NAME = "serviceDescriptorNames";
    public static final String MESSAGE_SERVICE_DESCRIPTORS_CONTENTS_NAME = "serviceDescriptorsContents";
    public static final String MESSAGE_SERVICE_ONTOLOGIES_NAMES_NAME = "serviceOntologiesNames";
    public static final String MESSAGE_SERVICE_ONTOLOGIES_CONTENTS_NAME = "serviceOntologiesContents";

    public static final String TYPE_VALUE_INSTANCE = "instance";
    public static final String TYPE_VALUE_INSTANCE_ARRAYLIST = "instanceArrayList";
    public static final String TYPE_VALUE_INSTANCE_URI = "instanceURI";
    public static final String TYPE_VALUE_INSTANCE_LITERAL = "instanceLiteral";
    public static final String TYPE_VALUE_INSTANCE_LITERAL_ARRAYLIST = "instanceLiteralArrayList";


    public JSONMessageSerializer(
            IServiceGroundingFactory serviceGroundingFactory,
            IFunctionalityGroundingFactory funcGroundingFactory) {
        this.mapper = new ObjectMapper();
        this.serviceGroundingFactory = serviceGroundingFactory;
        this.funcGroundingFactory = funcGroundingFactory;
    }

    @Override
    public String serializeMessage(IMessage msg, IBSDLRegistry bsdlRegistry) throws SerializingException {
        BSDFLogger.getLogger().info("Serializes message using JSON: " + msg.toString());
        if (msg instanceof FunctionalityExecMessage) {
            FunctionalityExecMessage feMsg = (FunctionalityExecMessage) msg;
            List serializedParameters = this.parametersToList(feMsg.values());
            return serializeFunctionalityMessage(
                    MESSAGE_TYPE_EXECFUNCTIONALITY_NAME,
                    feMsg.getFunctionalityName(),
                    feMsg.getClientName(),
                    feMsg.getConversationId(),
                    serializedParameters);
        }
        if (msg instanceof FunctionalityResultMessage) {
            FunctionalityResultMessage frMsg = (FunctionalityResultMessage) msg;
            List serializedParameters = this.resultsToList(frMsg.getResult());
            return serializeFunctionalityMessage(
                    MESSAGE_TYPE_RESULTS_NAME,
                    frMsg.getFunctionalityName(),
                    frMsg.getClientName(),
                    frMsg.getConversationId(),
                    serializedParameters);
        }
        if (msg instanceof SearchFunctionalityRequestMessage) {
            SearchFunctionalityRequestMessage sfRequestMessage = (SearchFunctionalityRequestMessage) msg;
            return serializeMessage(
                    MESSAGE_TYPE_SEARCHFUNCTIONALITY_REQUEST,
                    sfRequestMessage.getClientName(),
                    sfRequestMessage.getConversationId(),
                    sfRequestMessage.getRequestedFunctionalityContents(),
                    null);
        }
        if (msg instanceof SearchFunctionalityResultMessage) {
            SearchFunctionalityResultMessage sfResultMessage = (SearchFunctionalityResultMessage) msg;
            List searchResults = this.searchResultsToList(sfResultMessage.getSearchResults());
            return serializeServiceWithDescriptorsMessage(
                    MESSAGE_TYPE_SEARCHFUNCTIONALITY_RESPONSE,
                    sfResultMessage.getClientName(),
                    sfResultMessage.getConversationId(),
                    sfResultMessage.getServiceDescriptorsContents(),
                    sfResultMessage.getOntologiesDescriptorsContents(),
                    searchResults);
        }
        if (msg instanceof RegisterServiceRequestMessage) {
            RegisterServiceRequestMessage rsRequestMessage = (RegisterServiceRequestMessage) msg;
            return serializeServiceWithDescriptorsMessage(
                    MESSAGE_TYPE_REGISTERSERVICE_REQUEST,
                    rsRequestMessage.getClientName(),
                    rsRequestMessage.getConversationId(),
                    rsRequestMessage.getServiceDescriptorContents(),
                    rsRequestMessage.getOntologiesDescriptorsContents(),
                    null);
        }
        return null;
    }

    @Override
    public IMessage deserializeMessage(
            String msg,
            IBSDLRegistry bsdlRegistry) throws SerializingException {
        BSDFLogger.getLogger().info("De-serializes message using JSON: " + msg);
        try {
            Map<String, Object> readValues = mapper.readValue(msg, Map.class);
            String msgType = (String) readValues.get(MESSAGE_TYPE_NAME);
            String funcName = (String) readValues.get(MESSAGE_FUNCTIONALITY_NAME_NAME);
            String clientName = (String) readValues.get(MESSAGE_CLIENT_NAME_NAME);
            String convId = (String) readValues.get(MESSAGE_CONVERSATION_ID_NAME);
            if (msgType.equalsIgnoreCase(MESSAGE_TYPE_EXECFUNCTIONALITY_NAME)) {
                Collection<IAnnotatedValue> deserializedParameters
                        = this.deserializeParameters(
                        (List<Map<String, Object>>) readValues.get(MESSAGE_VALUES_NAME),
                        bsdlRegistry);
                return new FunctionalityExecMessage(deserializedParameters, funcName, clientName, convId);
            }
            if (msgType.equalsIgnoreCase(MESSAGE_TYPE_RESULTS_NAME)) {
                Collection<IResult> deserializedResults
                        = this.deserializeResults(
                        (List<Map<String, Object>>) readValues.get(MESSAGE_VALUES_NAME),
                        bsdlRegistry);
                return new FunctionalityResultMessage(deserializedResults, funcName, clientName, convId);
            }
            if (msgType.equalsIgnoreCase(MESSAGE_TYPE_SEARCHFUNCTIONALITY_REQUEST)) {
                Collection<String> deserializedContents
                        = this.deserializeContents((List<Map<String, Object>>) readValues.get(MESSAGE_CONTENTS_NAME));
                return new SearchFunctionalityRequestMessage(clientName, convId, deserializedContents);
            }
            if (msgType.equalsIgnoreCase(MESSAGE_TYPE_SEARCHFUNCTIONALITY_RESPONSE)) {

                Collection<IFunctionalitySearchResult> deserializedSearchResults
                        = this.deserializeSearchResults((List<Map<String, Object>>) readValues.get(MESSAGE_VALUES_NAME));

                Collection<DescriptorData> deserializedServices
                        = this.deserializeDescriptorsContents((Map<String, Object>) readValues.get(MESSAGE_SERVICE_DESCRIPTORS_CONTENTS_NAME));

                Collection<DescriptorData> deserializedOntologies
                        = this.deserializeDescriptorsContents((Map<String, Object>) readValues.get(MESSAGE_SERVICE_ONTOLOGIES_CONTENTS_NAME));

                deserializedSearchResults
                        = loadServiceDescripcionForDeserializedSearchResults(
                        deserializedSearchResults,
                        getServiceDescriptors(deserializedServices),
                        bsdlRegistry);

                SearchFunctionalityResultMessage sfrMessage
                        = new SearchFunctionalityResultMessage(clientName, convId, deserializedSearchResults);
                sfrMessage.setServiceDescriptorsContents(deserializedServices);
                sfrMessage.setOntologyDescriptorContents(deserializedOntologies);

                return sfrMessage;

            }
            if (msgType.equalsIgnoreCase(MESSAGE_TYPE_REGISTERSERVICE_REQUEST)) {
                RegisterServiceRequestMessage rsrMessage = new RegisterServiceRequestMessage(clientName, convId);
                rsrMessage.setServiceDescriptorContents(
                        new DescriptorData(
                                (String) readValues.get(MESSAGE_SERVICE_DESCRIPTOR_NAME_NAME),
                                (String) readValues.get(MESSAGE_SERVICE_DESCRIPTOR_CONTENTS_NAME)));
                Collection<DescriptorData> deserializedOntologies
                        = this.deserializeDescriptorsContents((Map<String, Object>) readValues.get(MESSAGE_SERVICE_ONTOLOGIES_CONTENTS_NAME));
                rsrMessage.setOntologyDescriptorContents(deserializedOntologies);
                return rsrMessage;
            }
            return null;
        } catch (IOException ex) {
            throw new SerializingException("Cannot deserialize message: " + msg, ex);
        } catch (SemanticModelException ex) {
            throw new SerializingException("Cannot deserialize message: " + msg, ex);
        }

    }

    private String serializeFunctionalityMessage(
            String msgType,
            String funcName,
            String clientName,
            String conversationID,
            List serializedValues) throws SerializingException {
        Map<String, Object> serializedMessage = new LinkedHashMap<String, Object>();
        serializedMessage.put(MESSAGE_TYPE_NAME, msgType);
        serializedMessage.put(MESSAGE_FUNCTIONALITY_NAME_NAME, funcName);
        serializedMessage.put(MESSAGE_CLIENT_NAME_NAME, clientName);
        serializedMessage.put(MESSAGE_CONVERSATION_ID_NAME, conversationID);
        serializedMessage.put(MESSAGE_VALUES_NAME, serializedValues);
        try {
            return mapper.writeValueAsString(serializedMessage);
        } catch (JsonProcessingException ex) {
            throw new SerializingException("Cannot serialize message for: " + funcName + ", " + conversationID, ex);
        }
    }

    private String serializeServiceWithDescriptorsMessage(
            String msgType,
            String clientName,
            String conversationID,
            DescriptorData serviceData,
            Collection<DescriptorData> ontologiesData,
            List serializedValues) throws SerializingException {
        Map<String, Object> serializedMessage = new LinkedHashMap<String, Object>();
        serializedMessage.put(MESSAGE_TYPE_NAME, msgType);
        serializedMessage.put(MESSAGE_CLIENT_NAME_NAME, clientName);
        serializedMessage.put(MESSAGE_CONVERSATION_ID_NAME, conversationID);
        serializedMessage.put(MESSAGE_SERVICE_DESCRIPTOR_NAME_NAME, serviceData.getName());
        serializedMessage.put(MESSAGE_SERVICE_DESCRIPTOR_CONTENTS_NAME, serviceData.getContents());
        serializedMessage.put(MESSAGE_VALUES_NAME, serializedValues);

        List ontologiesNames = new ArrayList();
        for (DescriptorData ontologyData : ontologiesData) {
            ontologiesNames.add(ontologyData.getName());
        }
        serializedMessage.put(MESSAGE_SERVICE_ONTOLOGIES_NAMES_NAME, ontologiesNames);

        Map<String, Object> ontologiesContentsMap = new LinkedHashMap<String, Object>();
        for (DescriptorData ontologyData : ontologiesData) {
            ontologiesContentsMap.put(ontologyData.getName(), ontologyData.getContents());
        }
        serializedMessage.put(MESSAGE_SERVICE_ONTOLOGIES_CONTENTS_NAME, ontologiesContentsMap);

        try {
            return mapper.writeValueAsString(serializedMessage);
        } catch (JsonProcessingException ex) {
            throw new SerializingException("Cannot serialize search request message for: " + clientName + ", " + conversationID, ex);
        }
    }

    private String serializeServiceWithDescriptorsMessage(
            String msgType,
            String clientName,
            String conversationID,
            Collection<DescriptorData> servicesData,
            Collection<DescriptorData> ontologiesData,
            List serializedValues) throws SerializingException {
        Map<String, Object> serializedMessage = new LinkedHashMap<String, Object>();
        serializedMessage.put(MESSAGE_TYPE_NAME, msgType);
        serializedMessage.put(MESSAGE_CLIENT_NAME_NAME, clientName);
        serializedMessage.put(MESSAGE_CONVERSATION_ID_NAME, conversationID);
        serializedMessage.put(MESSAGE_VALUES_NAME, serializedValues);

        List descriptorsNames = new ArrayList();
        for (DescriptorData descriptorData : servicesData) {
            descriptorsNames.add(descriptorData.getName());
        }
        serializedMessage.put(MESSAGE_SERVICE_DESCRIPTORS_NAMES_NAME, descriptorsNames);

        Map<String, Object> descriptorsContentsMap = new LinkedHashMap<String, Object>();
        for (DescriptorData descriptorData : servicesData) {
            descriptorsContentsMap.put(descriptorData.getName(), descriptorData.getContents());
        }
        serializedMessage.put(MESSAGE_SERVICE_DESCRIPTORS_CONTENTS_NAME, descriptorsContentsMap);

        List ontologiesNames = new ArrayList();
        for (DescriptorData ontologyData : ontologiesData) {
            ontologiesNames.add(ontologyData.getName());
        }
        serializedMessage.put(MESSAGE_SERVICE_ONTOLOGIES_NAMES_NAME, ontologiesNames);

        Map<String, Object> ontologiesContentsMap = new LinkedHashMap<String, Object>();
        for (DescriptorData ontologyData : ontologiesData) {
            ontologiesContentsMap.put(ontologyData.getName(), ontologyData.getContents());
        }
        serializedMessage.put(MESSAGE_SERVICE_ONTOLOGIES_CONTENTS_NAME, ontologiesContentsMap);

        try {
            return mapper.writeValueAsString(serializedMessage);
        } catch (JsonProcessingException ex) {
            throw new SerializingException("Cannot serialize search request message for: " + clientName + ", " + conversationID, ex);
        }
    }

    private String serializeMessage(
            String msgType,
            String clientName,
            String conversationID,
            Collection<String> requestContents,
            List serializedValues) throws SerializingException {
        Map<String, Object> serializedMessage = new LinkedHashMap<String, Object>();
        serializedMessage.put(MESSAGE_TYPE_NAME, msgType);
        serializedMessage.put(MESSAGE_CLIENT_NAME_NAME, clientName);
        serializedMessage.put(MESSAGE_CONVERSATION_ID_NAME, conversationID);
        serializedMessage.put(MESSAGE_CONTENTS_NAME, new ArrayList(requestContents));
        serializedMessage.put(MESSAGE_VALUES_NAME, serializedValues);
        try {
            return mapper.writeValueAsString(serializedMessage);
        } catch (JsonProcessingException ex) {
            throw new SerializingException("Cannot serialize search request message for: " + clientName + ", " + conversationID, ex);
        }
    }

    public String serializeParameters(Collection<IAnnotatedValue> parameters) throws SerializingException {
        try {
            return mapper.writeValueAsString(parametersToList(parameters));
        } catch (JsonProcessingException ex) {
            throw new SerializingException("Cannot serialize: " + parameters.toString(), ex);
        }
    }

    private List parametersToList(Collection<IAnnotatedValue> parameters) throws SerializingException {
        List listOfInputValuesToSerialize = new ArrayList();

        for (IAnnotatedValue parameter : parameters) {
            try {
                if (null == parameter) {
                    listOfInputValuesToSerialize.add(new LinkedHashMap<String, Object>());
                } else {
                    listOfInputValuesToSerialize.add(serializeParameter(parameter));
                }
            } catch (ModelException ex) {
                throw new SerializingException("Cannot serialize: " + (null != parameter ? parameter.toString() : ""), ex);
            }
        }

        return listOfInputValuesToSerialize;
    }

    private List resultsToList(Collection<IResult> results) throws SerializingException {
        List listOfResultsToSerialize = new ArrayList();

        for (IResult result : results) {
            try {
                listOfResultsToSerialize.add(serializeParameter(result));
            } catch (ModelException ex) {
                throw new SerializingException("Cannot serialize: " + result.toString(), ex);
            }
        }

        return listOfResultsToSerialize;
    }

    private List searchResultsToList(Collection<IFunctionalitySearchResult> searchResults) throws SerializingException {
        List listOfSearchResultsToSerialize = new ArrayList();

        if (null == searchResults) {
            listOfSearchResultsToSerialize.add(new LinkedHashMap<String, Object>());
        } else {

            for (IFunctionalitySearchResult searchResult : searchResults)
            {
                try
                {
                    listOfSearchResultsToSerialize.add(serializeSearchResult(searchResult));
                } catch (ModelException ex) {
                    throw new SerializingException("Cannot serialize: " + searchResult.toString(), ex);
                }
            }
        }

        return listOfSearchResultsToSerialize;
    }

    public Collection<IAnnotatedValue> deserializeParameters(
            String parameters,
            IBSDLRegistry bsdlRegistry) throws SerializingException {
        try {
            List<Map<String, Object>> readValues = mapper.readValue(parameters, List.class);
            return deserializeParameters(readValues, bsdlRegistry);
        } catch (IOException ex) {
            throw new SerializingException("Cannot deserialize: " + parameters, ex);
        } catch (SemanticModelException ex) {
            throw new SerializingException("Cannot deserialize: " + parameters, ex);
        }

    }

    private Collection<IAnnotatedValue> deserializeParameters(
            List<Map<String, Object>> parameters,
            IBSDLRegistry bsdlRegistry) throws SemanticModelException {
        Collection<IAnnotatedValue> deserializedParameters = new ArrayList<IAnnotatedValue>();
        for (Map<String, Object> value : parameters) {
            IAxiom deserializedParameter = this.deserializeParameter(value, bsdlRegistry);
            if (null != deserializedParameter) {
                if (deserializedParameter instanceof Instance) {
                    deserializedParameters.add(new ParameterValue((Instance) deserializedParameter));
                }
                if (deserializedParameter instanceof InstanceLiteral) {
                    deserializedParameters.add(new ParameterValue((InstanceLiteral) deserializedParameter));
                }
                if (deserializedParameter instanceof InstanceLiteralArrayList) {
                    deserializedParameters.add(new ParameterValue((InstanceLiteralArrayList) deserializedParameter));
                }
                if (deserializedParameter instanceof InstanceArrayList) {
                    deserializedParameters.add(new ParameterValue((InstanceArrayList) deserializedParameter));
                }
                if (deserializedParameter instanceof InstanceURI) {
                    deserializedParameters.add(new ParameterValue((InstanceURI) deserializedParameter));
                }
            } else {
                deserializedParameters.add(null);
            }
        }
        return deserializedParameters;
    }

    private Collection<IResult> deserializeResults(
            List<Map<String, Object>> results,
            IBSDLRegistry bsdlRegistry) throws SemanticModelException {
        Collection<IResult> deserializedResults = new ArrayList<IResult>();
        for (Map<String, Object> value : results) {
            IAxiom deserializedParameter = this.deserializeParameter(value, bsdlRegistry);
            if (null != deserializedParameter) {
                if (deserializedParameter instanceof Instance) {
                    deserializedResults.add(new OutputValue((Instance) deserializedParameter));
                }
                if (deserializedParameter instanceof InstanceLiteral) {
                    deserializedResults.add(new OutputValue((InstanceLiteral) deserializedParameter));
                }
                if (deserializedParameter instanceof InstanceLiteralArrayList) {
                    deserializedResults.add(new OutputValue((InstanceLiteralArrayList) deserializedParameter));
                }
                if (deserializedParameter instanceof InstanceArrayList) {
                    deserializedResults.add(new OutputValue((InstanceArrayList) deserializedParameter));
                }
                if (deserializedParameter instanceof InstanceURI) {
                    deserializedResults.add(new OutputValue((InstanceURI) deserializedParameter));
                }
            }
        }
        return deserializedResults;
    }

    private Collection<DescriptorData> deserializeDescriptorsContents(Map<String, Object> ontologiesMap) {
        Collection<DescriptorData> descriptorsData = new ArrayList<DescriptorData>();
        for (Entry<String, Object> entry : ontologiesMap.entrySet()) {
            descriptorsData.add(new DescriptorData(entry.getKey(), (String) entry.getValue()));
        }
        return descriptorsData;
    }

    private Collection<String> deserializeContents(List contents) throws SemanticModelException {
        Collection<String> deserializedContents = new ArrayList<String>(contents);
        return deserializedContents;
    }

    private Collection<IFunctionalitySearchResult> deserializeSearchResults(List<Map<String, Object>> results) throws SemanticModelException {
        Collection<IFunctionalitySearchResult> deserializedSearchResults = new ArrayList<IFunctionalitySearchResult>();
        for (Map<String, Object> result : results) {
            if (null != result) {
                IFunctionalitySearchResult searchResult = this.deserializeSearchResult(result);
                deserializedSearchResults.add(searchResult);
            }
        }
        return deserializedSearchResults;
    }

    private Collection<IFunctionalitySearchResult> loadServiceDescripcionForDeserializedSearchResults(
            Collection<IFunctionalitySearchResult> searchResults,
            Collection<String> serviceDescriptors,
            IBSDLRegistry bsdlRegistry) throws SemanticModelException {

        ServiceDescriptionLoader loader
                = new ServiceDescriptionLoader(serviceGroundingFactory, funcGroundingFactory, bsdlRegistry, this);
        try {
            // load the services
            Collection<IServiceDescription> readServices = loader.readServices(serviceDescriptors);
            // for each search result, assign the IServiceDescription
            for (IFunctionalitySearchResult searchResult : searchResults) {
                for (IServiceDescription service : readServices) {
                    if (service.getIdentifier().equals(searchResult.getServiceIdentifier())) {
                        searchResult.setServiceDescription(service);

                        // remove the received implementations, because this service lacks any local implementation
                        service.disposeImplementations();
                        break;
                    }
                }

            }
        } catch (ModelException ex) {
            throw new SemanticModelException(ex.getMessage(), ex);
        }
        return searchResults;
    }

    private static Collection<String> getServiceDescriptors(
            Collection<DescriptorData> serviceDescriptorsData) throws SemanticModelException {

        Collection<String> serviceDescriptors = new ArrayList<String>();

        for (DescriptorData descriptorData : serviceDescriptorsData) {
            serviceDescriptors.add(descriptorData.getContents());
        }

        return serviceDescriptors;
    }

    private Map<String, Object> serializeParameter(IParameterValue parameter) throws ModelException {
        return serializeAxiom(parameter.value());
    }

    private Map<String, Object> serializeAxiom(IAxiom axiom) throws ModelException {
        Map<String, Object> serializedParameter = new LinkedHashMap<String, Object>();

        if (axiom instanceof Instance) {
            Instance parameterAsInstance = (Instance) axiom;
            serializedParameter.put(TYPE_NAME, TYPE_VALUE_INSTANCE);
            serializedParameter.put(
                    CONCEPT_NAME,
                    parameterAsInstance.getConcept().semanticAxiom().getSemanticIdentifier().getURI().toString());
            List<Map<String, Object>> listOfPropertyValues = new ArrayList<Map<String, Object>>();
            for (PropertyValue propertyValue : parameterAsInstance.getPropertiesAsPropertyValue()) {
                Map<String, Object> propertyValueAsMap = new LinkedHashMap<String, Object>();
                propertyValueAsMap.put(PROPERTY_NAME_NAME, propertyValue.getName());
                propertyValueAsMap.put(VALUE_NAME, serializeAxiom(propertyValue.getObject()));
                listOfPropertyValues.add(propertyValueAsMap);
            }
            serializedParameter.put(PROPERTY_VALUES_NAME, listOfPropertyValues);
        }

        if (axiom instanceof InstanceURI) {
            InstanceURI parameterAsInstanceURI = (InstanceURI) axiom;
            serializedParameter.put(TYPE_NAME, TYPE_VALUE_INSTANCE_URI);
            serializedParameter.put(CONCEPT_NAME, parameterAsInstanceURI.conceptIdentifier().getURI().toString());
            serializedParameter.put(VALUE_NAME, parameterAsInstanceURI.getValue().getURI().toString());
        }

        if (axiom instanceof InstanceLiteral) {
            InstanceLiteral parameterAsInstanceLiteral = (InstanceLiteral) axiom;
            serializedParameter.put(TYPE_NAME, TYPE_VALUE_INSTANCE_LITERAL);
            serializedParameter.put(CONCEPT_NAME, parameterAsInstanceLiteral.conceptIdentifier().getURI().toString());
            serializedParameter.put(VALUE_NAME, parameterAsInstanceLiteral.getValue());
        }

        if (axiom instanceof InstanceLiteralArrayList) {
            InstanceLiteralArrayList parameterAsInstanceLiteralArrayList = (InstanceLiteralArrayList) axiom;
            serializedParameter.put(TYPE_NAME, TYPE_VALUE_INSTANCE_LITERAL_ARRAYLIST);
            serializedParameter.put(CONCEPT_NAME, parameterAsInstanceLiteralArrayList.conceptIdentifier().getURI().toString());
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            for (InstanceLiteral instanceLiteral : parameterAsInstanceLiteralArrayList.values()) {
                values.add(serializeAxiom(instanceLiteral));
            }
            serializedParameter.put(LIST_OF_VALUES_NAME, values);
        }

        if (axiom instanceof InstanceArrayList) {
            InstanceArrayList parameterAsInstanceArrayList = (InstanceArrayList) axiom;
            serializedParameter.put(TYPE_NAME, TYPE_VALUE_INSTANCE_ARRAYLIST);
            serializedParameter.put(
                    CONCEPT_NAME,
                    parameterAsInstanceArrayList.getConcept().semanticAxiom().getSemanticIdentifier().getURI().toString());
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            for (Instance instance : parameterAsInstanceArrayList.values()) {
                values.add(serializeAxiom(instance));
            }
            serializedParameter.put(LIST_OF_VALUES_NAME, values);
        }

        return serializedParameter;
    }

    private Map<String, Object> serializeSearchResult(IFunctionalitySearchResult searchResult) throws ModelException {
        Map<String, Object> serializedSearchResult = new LinkedHashMap<String, Object>();
        serializedSearchResult.put(MESSAGE_FUNCTIONALITY_NAME_NAME, searchResult.getAdvertisedFunctionalityName());
        if (null != searchResult.getServiceDescription()) {
            serializedSearchResult.put(MESSAGE_SERVICE_IDENTIFIER_NAME, searchResult.getServiceDescription().getIdentifier().toString());
        }
        if (null != searchResult.getEvaluation() && searchResult.getEvaluation() instanceof BasicFunctionalitySearchEvaluation) {
            BasicFunctionalitySearchEvaluation bevaluation = (BasicFunctionalitySearchEvaluation) searchResult.getEvaluation();
            serializedSearchResult.put(
                    MESSAGE_FUNCTIONALITY_EVALUATION_BOOLEAN_VALUE,
                    Boolean.toString(bevaluation.areRequiredPropertiesPresent()));
            serializedSearchResult.put(
                    MESSAGE_FUNCTIONALITY_EVALUATION_NUMERIC_VALUE,
                    bevaluation.getValue());
        }
        return serializedSearchResult;
    }

    private IAxiom deserializeParameter(
            Map<String, Object> parameter,
            IBSDLRegistry bsdlRegistry)
            throws SemanticModelException {
        String typeName = (String) parameter.get(TYPE_NAME);
        if (null == typeName) {
            return null;
        }

        if (typeName.equals(TYPE_VALUE_INSTANCE)) {
            String conceptURI = (String) parameter.get(CONCEPT_NAME);
            Instance instance = new Instance(bsdlRegistry, new SemanticIdentifier(conceptURI));
            // property values:
            List<Map<String, Object>> propertyValues = (List<Map<String, Object>>) parameter.get(PROPERTY_VALUES_NAME);
            for (Map<String, Object> propertyValueAsMap : propertyValues) {
                PropertyValue propertyValue = new PropertyValue();
                propertyValue.setName((String) propertyValueAsMap.get(PROPERTY_NAME_NAME));
                IAxiom deserializedValue
                        = deserializeParameter((Map<String, Object>) propertyValueAsMap.get(VALUE_NAME), bsdlRegistry);
                if (null != deserializedValue && deserializedValue instanceof IObject) {
                    propertyValue.setObject((IObject) deserializedValue);
                }
                instance.addProperty(propertyValue);
            }
            return instance;
        }
        if (typeName.equals(TYPE_VALUE_INSTANCE_URI)) {
            String valueURI = (String) parameter.get(VALUE_NAME);
            InstanceURI instanceURI = new InstanceURI(new SemanticIdentifier(valueURI));

            return instanceURI;
        }
        if (typeName.equals(TYPE_VALUE_INSTANCE_LITERAL)) {
            String valueLiteral = (String) parameter.get(VALUE_NAME);
            InstanceLiteral instanceLiteral = new InstanceLiteral(valueLiteral);

            return instanceLiteral;
        }
        if (typeName.equals(TYPE_VALUE_INSTANCE_LITERAL_ARRAYLIST)) {
            String conceptURI = (String) parameter.get(CONCEPT_NAME);
            List<InstanceLiteral> instances = new ArrayList<InstanceLiteral>();
            List<Map<String, Object>> values = (List<Map<String, Object>>) parameter.get(LIST_OF_VALUES_NAME);
            for (Map<String, Object> value : values) {
                IAxiom axiom = deserializeParameter(value, bsdlRegistry);
                if (null != axiom && axiom instanceof InstanceLiteral) {
                    instances.add((InstanceLiteral) axiom);
                }
            }
            InstanceLiteralArrayList instanceLiteralArrayList =
                    new InstanceLiteralArrayList(instances);

            return instanceLiteralArrayList;

        }
        if (typeName.equals(TYPE_VALUE_INSTANCE_ARRAYLIST)) {
            String conceptURI = (String) parameter.get(CONCEPT_NAME);
            List<Instance> instances = new ArrayList<Instance>();
            List<Map<String, Object>> values = (List<Map<String, Object>>) parameter.get(LIST_OF_VALUES_NAME);
            for (Map<String, Object> value : values) {
                IAxiom axiom = deserializeParameter(value, bsdlRegistry);
                if (null != axiom && axiom instanceof Instance) {
                    instances.add((Instance) axiom);
                }
            }
            InstanceArrayList instanceArrayList
                    = new InstanceArrayList(
                    instances,
                    new ReferenceToSemanticAxiom<Concept>(
                            bsdlRegistry,
                            new SemanticIdentifier(conceptURI),
                            Concept.class
                    ));
            return instanceArrayList;
        }

        return null;
    }

    private IFunctionalitySearchResult deserializeSearchResult(
            Map<String, Object> searchResult)
            throws SemanticModelException {
        final String functionalityName = (String) searchResult.get(MESSAGE_FUNCTIONALITY_NAME_NAME);
        final String serviceIdentifier = (String) searchResult.get(MESSAGE_SERVICE_IDENTIFIER_NAME);
        final String booleanEvaluation = (String) searchResult.get(MESSAGE_FUNCTIONALITY_EVALUATION_BOOLEAN_VALUE);
        final boolean boolEvaluation;
        if (null != booleanEvaluation) {
            boolEvaluation = Boolean.valueOf(booleanEvaluation);
        } else {
            boolEvaluation = true;
        }
        final Object numericEvaluation = searchResult.get(MESSAGE_FUNCTIONALITY_EVALUATION_NUMERIC_VALUE);
        final int numEvaluation;
        if (null != numericEvaluation) {
            numEvaluation = (Integer) numericEvaluation;
        } else {
            numEvaluation = 0;
        }

        IFunctionalitySearchResult functionalitySearchResult = new IFunctionalitySearchResult() {

            private IServiceDescription serviceDescription = null;

            @Override
            public IServiceDescription getServiceDescription() {
                return serviceDescription;
            }

            @Override
            public String getAdvertisedFunctionalityName() {
                return functionalityName;
            }

            @Override
            public IFunctionalitySearchEvaluation getEvaluation() {
                return new BasicFunctionalitySearchEvaluation(numEvaluation, boolEvaluation);
            }

            @Override
            public void setServiceDescription(IServiceDescription serviceDescription) {
                this.serviceDescription = serviceDescription;
            }

            @Override
            public ISemanticIdentifier getServiceIdentifier() throws SemanticModelException {
                return new SemanticIdentifier(serviceIdentifier);
            }

        };
        return functionalitySearchResult;
    }

}
