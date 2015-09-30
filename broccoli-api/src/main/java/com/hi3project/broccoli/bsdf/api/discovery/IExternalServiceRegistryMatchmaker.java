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

import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.meta.IOntologyLanguage;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import java.util.Collection;

/**
 *
 * 
 */
public interface IExternalServiceRegistryMatchmaker 
{

    public void addService(ISemanticLocator locator, IServiceDescription service) throws ModelException;
    
    public Collection<IFunctionalitySearchResult> searchFor(ISemanticLocator locator) throws ModelException;
    
    public boolean understands(IOntologyLanguage ontologyLanguage);
    
}
