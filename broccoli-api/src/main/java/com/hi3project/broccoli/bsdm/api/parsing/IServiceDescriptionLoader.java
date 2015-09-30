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

package com.hi3project.broccoli.bsdm.api.parsing;

import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IRequestedFunctionality;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.Collection;

/**
 * Interface for an utility class that can read BSDO services from document
 * descriptors. And knows how to register the read axioms to an IBSDLRegistry
 *
 * 
 */
public interface IServiceDescriptionLoader
{

    public Collection<IServiceDescription> readServicesFrom(Collection<ISemanticLocator> locators) throws ModelException;

    public Collection<IServiceDescription> readServicesFrom(ISemanticLocator locator) throws ModelException;
    
    public Collection<IServiceDescription> readServices(Collection<String> serviceDescriptors) throws ModelException;
    
    
    public Collection<IRequestedFunctionality> readFunctionalitiesFrom(ISemanticLocator locator) throws ModelException;
    
    public Collection<IRequestedFunctionality> readFunctionalitiesFrom(Collection<ISemanticLocator> locators) throws ModelException;
    
    public Collection<IRequestedFunctionality> readFunctionalities(Collection<String> contents) throws ModelException;
    
    
    public IParameterConverter getParameterConverter();
    
   
    public IServiceDescriptionLoader addOntology(ISemanticLocator locator) throws ModelException;

    public IServiceDescriptionLoader addOntologyReferences(ISemanticLocator locator) throws SemanticModelException, ModelException;
    
    
    public void setBSDLRegistry(IBSDLRegistry bsdlRegistry);
    
    public IBSDLRegistry getBSDLRegistry();
    
}
