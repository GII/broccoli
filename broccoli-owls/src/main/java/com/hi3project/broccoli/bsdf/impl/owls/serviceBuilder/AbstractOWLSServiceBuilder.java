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

import com.hi3project.broccoli.bsdf.impl.owls.exceptions.DynamicGroundingBuildingException;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdm.impl.exceptions.BSDMComponentException;
import com.hi3project.broccoli.io.BSDFLogger;
import com.hi3project.broccoli.bsdf.impl.owls.grounding.IOWLSDynamicGrounding;
import com.hi3project.broccoli.bsdf.impl.owls.profile.functionality.OWLSAtomicService;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.service.Service;

/**
 * <p>
 * Base for an implementation of an OWL-S service builder that uses OWLSAPI
 *<p>
 * It knows how to build the AtomicProcess for a OWLSAPI Service with a built
 * Profile
 *<p>
 * It defers to an AbstractDynamicGroundingFactory the creation of a grounding
 *
 *
 * 
 */
public class AbstractOWLSServiceBuilder
{

    protected AbstractDynamicGroundingFactory groundingFactory = null;

    public AbstractOWLSServiceBuilder(AbstractDynamicGroundingFactory dynamicGroundingFactory)
    {
        this.groundingFactory = dynamicGroundingFactory;
    }

    /**
     * Builds an OWLSAtomicService, from a given Service object. It generates an
     * OWL-S Process if there is none. It also generates an OWL-S grounding when
     * the read service lacks one (delegating to a
     * AbstractDynamicGroundingFactory implementation).
     *
     * @param service
     * @return
     * @throws ModelException
     */
    protected OWLSAtomicService completeOWLSServiceIfNeeded(OWLSAtomicService service) throws ModelException
    {
        if (null == service)
        {
            throw new BSDMComponentException("Cannot complete a null OWLS service object");
        }

        // is there a process definition?
        if (!service.hasProcess())
        {
            BSDFLogger.getLogger().info("Generating process for: " + service.name());
            generateAtomicProcessForServiceProfile(service.getService());
        }
        // has the service a grounding ?
        if (!service.hasGrounding())
        {
            BSDFLogger.getLogger().info("Generating grounding for: " + service.name());
            service.setGrounding(generateGroundingForProfile(service.getService()));
        }
        return service;
    }

    protected static AtomicProcess generateAtomicProcessForServiceProfile(Service service)
    {
        AtomicProcess atomicProcess = service.getOntology().createAtomicProcess(null);
        atomicProcess.addOutputs(service.getProfile().getOutputs());
        atomicProcess.addInputs(service.getProfile().getInputs());
        service.setProcess(atomicProcess);
        return atomicProcess;
    }

    protected AbstractDynamicGroundingFactory getFactory()
    {
        return groundingFactory;
    }

    protected IOWLSDynamicGrounding generateGroundingForProfile(Service service) throws DynamicGroundingBuildingException
    {
        return getFactory().generateGrounding(service);
    }

}
