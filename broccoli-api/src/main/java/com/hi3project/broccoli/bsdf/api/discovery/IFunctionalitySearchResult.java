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

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;

/**
 * <p>
 * Represents the result of an IRequestedFunctionalty search for
 * IAdvertisedFunctionalies.
 * <p>
 * It knows:
 * <ul>
 * <li>the IServiceDescription that contains the IAdvertisedFunctionality for
 * this result</li>
 * <li>the name of the IAdvertisedFunctionality for this result</li>
 * <li>optionally, an IFunctionalitySearchEvaluation</li>
 * </ul>
 *
 * 
 */
public interface IFunctionalitySearchResult
{

    public IServiceDescription getServiceDescription();

    public void setServiceDescription(IServiceDescription serviceDescription);

    public ISemanticIdentifier getServiceIdentifier() throws SemanticModelException;

    public String getAdvertisedFunctionalityName();

    public IFunctionalitySearchEvaluation getEvaluation();

}
