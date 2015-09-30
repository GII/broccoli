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

package com.hi3project.broccoli.bsdm.impl.profile;

import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdm.impl.ServiceDescription;
import com.hi3project.broccoli.bsdm.impl.profile.functionality.AdvertisedFunctionality;
import com.hi3project.broccoli.bsdm.impl.profile.nonFunctionalProperties.NonFunctionalProperty;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdm.api.profile.IServiceProfile;
import com.hi3project.broccoli.bsdm.api.profile.IServiceType;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.nonFunctionalProperties.INonFunctionalProperty;
import com.hi3project.broccoli.bsdm.impl.profile.functionality.AdvertisedSubscription;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * 
 */
public class ServiceProfile extends ConceptBehaviourImplementation implements IServiceProfile {
    
    private Collection<IServiceFunctionality> advertisedFunctionalities = new ArrayList<IServiceFunctionality>();
    private Collection<INonFunctionalProperty> nonFunctionalProperties = new ArrayList<INonFunctionalProperty>();
    private String name = null;
    ServiceDescription serviceDescription = null;
    

    public ServiceProfile(Instance instance, ServiceDescription serviceDescription) throws SemanticModelException {
        setInstance(instance);        
        for (IObject iobject : getProperties("advertisedFunctionality")) {
            if (iobject instanceof Instance) {
                advertisedFunctionalities.add(new AdvertisedFunctionality((Instance)iobject, this));
            }
        }
        for (IObject iobject : getProperties("advertisedSubscription")) {
            if (iobject instanceof Instance) {
                advertisedFunctionalities.add(new AdvertisedSubscription((Instance)iobject, this));
            }
        }
        for (IObject iobject : getProperties("nonFunctionalProperty")) {
            if (iobject instanceof Instance) {
                nonFunctionalProperties.add(new NonFunctionalProperty((Instance)iobject));
            }
        }
        name = getSinglePropertyAsInstanceLiteralValue("name");
        this.serviceDescription = serviceDescription;
    }
    
    public ServiceDescription getServiceDescription() {
        return serviceDescription;
    }
    
    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm#serviceProfile");
    }

    @Override
    public Collection<IServiceFunctionality> advertisedFunctionalities() {
        return advertisedFunctionalities;
    }

    @Override
    public Collection<INonFunctionalProperty> nonFuntionalProperties() {
        return nonFunctionalProperties;
    }

    @Override
    public IServiceType getServiceType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String name() {
        return name;
    }        
    
}
