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

package com.hi3project.broccoli.bsdf.api.discovery;

import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IRequestedFunctionality;

/**
 *
 * 
 */
public interface IRanker
{
    
    public void configureFor(IRequestedFunctionality requestedFunctionality) throws ModelException;
    
    public void inputRequiredIOPE(int distanceTo);
    
    public void inputRequiredNFP(int distanceTo);
    
    public void inputOptionalIOPE(int distanceTo);
    
    public void inputOptionalNFP(int distanceTo);
    
    public void inputPreferredIOPE(int distanceTo);
    
    public void inputPreferredNFP(int distanceTo);
    
    public IFunctionalitySearchEvaluation getComputedEvaluation();

}
