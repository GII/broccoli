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

package com.hi3project.broccoli.bsdm.impl.profile.functionality;

import com.hi3project.broccoli.bsdm.api.profile.functionality.IAdvertisedSubscription;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdm.impl.profile.ServiceProfile;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;

/**
 * <p><b>Creation date:</b> 
 * 29-08-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 29-08-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class AdvertisedSubscription extends ServiceFunctionality implements IAdvertisedSubscription
{

    public AdvertisedSubscription(Instance instance, ServiceProfile serviceProfile) throws SemanticModelException
    {
        super(instance, serviceProfile);
    }
    
    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/profile#advertisedSubscription");
    } 
    
}
