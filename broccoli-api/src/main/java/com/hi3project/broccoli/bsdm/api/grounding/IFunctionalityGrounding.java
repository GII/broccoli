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

package com.hi3project.broccoli.bsdm.api.grounding;


import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IExecutableFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityResultMessage;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;

/**
 *
 * 
 */
public interface IFunctionalityGrounding extends IExecutableFunctionality
{

    public IServiceFunctionality getAdvertisedFunctionality();

    public void setAdvertisedFunctionality(IServiceFunctionality advertisedFunctionality) throws ModelException;
    
    public IServiceImplementation getServiceImplementation() throws ModelException;
    
    public void setServiceGrounding(IServiceGrounding serviceGrounding);

    public IOutputValue createOutput(Object result, IServiceFunctionality functionality) throws SemanticModelException;
    
    public String name();
    
    public void registerResult(String requesterID,
            String conversationID, FunctionalityResultMessage resultMsg)
                throws ServiceExecutionException;
    
    public void registerNotification(FunctionalityResultMessage resultMsg)
                throws ServiceExecutionException;
}
