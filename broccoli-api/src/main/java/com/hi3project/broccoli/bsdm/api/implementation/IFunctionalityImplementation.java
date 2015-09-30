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

package com.hi3project.broccoli.bsdm.api.implementation;

import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import java.util.Collection;

/**
 * <p><b>Description:</b></p>
 *
 *
 * <p><b>Creation date:</b> 
 * 18-06-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 18-06-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public interface IFunctionalityImplementation 
{
    
    public IServiceImplementation getServiceImplementation();
    
    public void setServiceImplementation(IServiceImplementation serviceImplementation);

    public Collection<IResult> executeWithValues(Collection<IAnnotatedValue> values, FunctionalityExecutionVO executionData) throws ModelException;
    
    public IOutputValue createOutput(Object result, IServiceFunctionality functionality) throws SemanticModelException;
    
    public String name();
    
    public IServiceFunctionality getAdvertisedFunctionality();
    
    public Object getFunctionalityImplementationObject();
    
    public Object loadFunctionalityImplementationObject() throws ServiceExecutionException;
    
}
