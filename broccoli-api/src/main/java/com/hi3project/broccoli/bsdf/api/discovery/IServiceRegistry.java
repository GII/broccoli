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
import com.hi3project.broccoli.bsdm.api.profile.functionality.IRequestedFunctionality;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import java.util.Collection;

/**
 * <p>
 * Extension of IComponentsRegistry, for IServiceDescription components.
 * </p>
 * Knows:
 * <ul>
 * <li>the ISemanticIdentifiers that identify the contained
 * IServiceDescriptions</li>
 * <li>how to search for functionalies that math to an
 * IRequestedFunctionality</li>
 * <li>an IMatchmaker to use for that search</li>
 * <li>how to clean the previously registered services</li>
 * </ul>
 *
 * 
 */
public interface IServiceRegistry extends IRegistryLoader
{

    public Collection<IFunctionalitySearchResult> searchFor(IRequestedFunctionality requestedFunctionality) throws ModelException;

    public Collection<IFunctionalitySearchResult> searchFor(IOntologyLanguage ontologyLanguage,
            ISemanticLocator requestedFunctionality) throws ModelException;

    public IMatchmaker getMatchmaker();

    public void setMatchmaker(IMatchmaker matchmaker);

    public void addExternalRegistry(IExternalServiceRegistryMatchmaker externalRegistry);
    
    public void initServiceImplementations() throws ServiceExecutionException;

}
