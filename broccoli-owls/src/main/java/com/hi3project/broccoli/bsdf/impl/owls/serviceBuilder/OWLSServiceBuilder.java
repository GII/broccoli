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

package com.hi3project.broccoli.bsdf.impl.owls.serviceBuilder;

import com.hi3project.broccoli.bsdf.impl.owl.KBWithReasonerBuilder;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLTranslationException;
import com.hi3project.broccoli.io.BSDFLogger;
import com.hi3project.broccoli.bsdf.impl.owls.profile.functionality.OWLSAtomicService;
import com.hi3project.broccoli.bsdf.impl.owl.OWLContainer;
import com.hi3project.broccoli.bsdf.impl.owl.OWLSStore;
import java.net.URI;
import java.util.List;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.service.Service;

/**
 * <p>
 * An implementation of AbstractOWLSServiceBuilder, to build OWL-S services
 * using OWLSAPI
 *<p>
 * It uses OWLSStore to search already persisted services
 *
 *
 * 
 */
public class OWLSServiceBuilder extends AbstractOWLSServiceBuilder
{

    public OWLSServiceBuilder(AbstractDynamicGroundingFactory dynamicGroundingFactory)
    {
        super(dynamicGroundingFactory);
    }

    /**
     * Builds an OWLSAtomicService
     *
     * @param serviceURI the URI that identifies the service
     * @return
     * @throws ModelException
     */
    public OWLSAtomicService buildOWLSServiceFrom(URI serviceURI) throws ModelException
    {
        return buildOWLSServiceFrom(serviceURI, null);
    }

    /**
     * Builds an OWLSAtomicService
     *
     * @param serviceURI the URI that identifies the service
     * @param modelURIs a List of URIs for ontologies that may be used by the
     * service to be loaded
     * @return
     * @throws ModelException
     */
    public OWLSAtomicService buildOWLSServiceFrom(URI serviceURI, List<URI> modelURIs) throws ModelException
    {
        if (isFile(serviceURI))
        {
            return buildOWLSServiceFromLocalOrRemoteURI(serviceURI, modelURIs);
        } else
        {
            Service service = OWLSStore.persistentModelAsOWLKB().getService(serviceURI);
            if (null == service)
            {
                throw new OWLTranslationException("Not found as a local file, nor a valid Service found in data store", null);
            }
            return buildOWLSServiceFrom(service);
        }
    }

    /**
     * Builds an OWLSAtomicService, from a given Service object
     *
     * @param owlsService
     * @return
     * @throws ModelException
     */
    public OWLSAtomicService buildOWLSServiceFrom(Service owlsService) throws ModelException
    {
        OWLSAtomicService service = new OWLSAtomicService(owlsService);
        return completeOWLSServiceIfNeeded(service);
    }

    /**
     * *********************************************************************************************************************
     */
    private boolean isFile(URI uri)
    {
        return uri.getScheme().startsWith("file");
    }

    private boolean isURL(URI uri)
    {
        return uri.getScheme().startsWith("http");
    }

    /**
     * Builds an OWLSAtomicService, from a given URI that has to be a local file
     * or an http URL
     *
     * @param serviceURI the URI for the service location
     * @param modelURIs a List of URIs for ontologies that may be used by the
     * service to be loaded (currently needed when the service is written in
     * functional or turtle syntax)
     * @return
     * @throws ModelException
     */
    private OWLSAtomicService buildOWLSServiceFromLocalOrRemoteURI(URI serviceURI, List<URI> modelURIs) throws ModelException
    {
        BSDFLogger.getLogger().info("Builds OWL service (BSDF functionality) from: " + serviceURI.toString());
        OWLContainer owlSyntaxTranslator = new OWLContainer();
        OWLKnowledgeBase kb = KBWithReasonerBuilder.newKB();
        if (null != modelURIs)
        {
            for (URI uri : modelURIs)
            {
                owlSyntaxTranslator.loadOntologyIfNotLoaded(uri);
            }
        }
        owlSyntaxTranslator.loadAsServiceIntoKB(kb, serviceURI);
        OWLSAtomicService service = new OWLSAtomicService(kb.getServices(false).iterator().next());

        return completeOWLSServiceIfNeeded(service);
    }
 

}
