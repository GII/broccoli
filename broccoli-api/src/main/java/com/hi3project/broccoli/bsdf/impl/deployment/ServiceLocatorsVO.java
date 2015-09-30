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

package com.hi3project.broccoli.bsdf.impl.deployment;

import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * 
 */
public class ServiceLocatorsVO
{

    private ISemanticLocator serviceDescriptorLocator;
    private ISemanticLocator serviceImplementationLocator;
    private Collection<ISemanticLocator> ontologiesDescriptorLocators;
    private Collection<ISemanticLocator> ontologiesImplementationLocators;
    private Collection<ISemanticLocator> librariesLocators;

    public ServiceLocatorsVO()
    {
        this.ontologiesDescriptorLocators = new ArrayList<ISemanticLocator>();
        this.ontologiesImplementationLocators = new ArrayList<ISemanticLocator>();
        this.librariesLocators = new ArrayList<ISemanticLocator>();
    }

    public ISemanticLocator getServiceDescriptorLocator()
    {
        return this.serviceDescriptorLocator;
    }

    public void setServiceDescriptorLocator(ISemanticLocator serviceDescriptorLocator)
    {
        this.serviceDescriptorLocator = serviceDescriptorLocator;
    }

    public ISemanticLocator getServiceImplementationLocator()
    {
        return this.serviceImplementationLocator;
    }

    public void setServiceImplementationLocator(ISemanticLocator serviceImplementationLocator)
    {
        this.serviceImplementationLocator = serviceImplementationLocator;
    }

    public Collection<ISemanticLocator> getOntologiesDescriptorLocators()
    {
        return this.ontologiesDescriptorLocators;
    }

    public Collection<ISemanticLocator> addOntologyDescriptorLocator(ISemanticLocator ontologyDescriptorLocator)
    {
        this.ontologiesDescriptorLocators.add(ontologyDescriptorLocator);
        return this.ontologiesDescriptorLocators;
    }
    
    public Collection<ISemanticLocator> getOntologiesImplementationLocators()
    {
        return this.ontologiesImplementationLocators;
    }

    public Collection<ISemanticLocator> addOntologyImplementationLocator(ISemanticLocator ontologyImplementationLocator)
    {
        this.ontologiesImplementationLocators.add(ontologyImplementationLocator);
        return this.ontologiesImplementationLocators;
    }
    
    public Collection<ISemanticLocator> getLibrariesLocators()
    {
        return this.librariesLocators;
    }
    
    public Collection<ISemanticLocator> addLibraryLocator(ISemanticLocator libraryLocator)
    {
        this.librariesLocators.add(libraryLocator);
        return this.librariesLocators;
    }
    
}
