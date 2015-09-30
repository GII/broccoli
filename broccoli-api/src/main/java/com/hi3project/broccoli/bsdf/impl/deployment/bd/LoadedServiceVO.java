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

package com.hi3project.broccoli.bsdf.impl.deployment.bd;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdf.impl.deployment.ServiceLocatorsVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p><b>Description:</b></p>
 *
 *
 * <p><b>Creation date:</b> 
 * 15-05-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 15-05-2014 - Initial release</li>
 * </ul>
 * 
 * @version 1
 */
public class LoadedServiceVO 
{

    private int id;
    
    private Collection<ISemanticIdentifier> ontologies;
    
    private Collection<ISemanticIdentifier> services;
    
    private List<Class<?>> loadedClasses;
    
    private ServiceLocatorsVO serviceLocators;
    
    private boolean unregistered;
    
    
    
    public LoadedServiceVO()
    {
        this(-1);
    }
    
    public LoadedServiceVO(int id)
    {
        this.id = id;
        this.unregistered = false;
        this.ontologies = new ArrayList<ISemanticIdentifier>();
        this.services = new ArrayList<ISemanticIdentifier>();
        this.loadedClasses = new ArrayList<Class<?>>();
        this.serviceLocators = new ServiceLocatorsVO();
    }

    public int getId()
    {
        return id;
    }
    
    public String getIdSt()
    {
        return String.valueOf(id);
    }

    public void setId(int id)
    {
        this.id = id;
    }
    
    public boolean hasBeenUnregistered()
    {
        return this.unregistered;
    }
    
    public ServiceLocatorsVO getServiceLocators()
    {
        return this.serviceLocators;
    }
    
    public void setServiceLocators(ServiceLocatorsVO serviceLocators)
    {
        this.serviceLocators = serviceLocators;
    }

    public Collection<ISemanticIdentifier> getOntologies()
    {
        return ontologies;
    }

    public void setOntologies(Collection<ISemanticIdentifier> ontologies)
    {
        this.ontologies = ontologies;
    }

    public Collection<ISemanticIdentifier> getServices()
    {
        return services;
    }

    public void setServices(Collection<ISemanticIdentifier> services)
    {
        this.services = services;
    }

    public List<Class<?>> getLoadedClasses()
    {
        return loadedClasses;
    }

    public void setLoadedClasses(List<Class<?>> loadedClasses)
    {
        this.loadedClasses = loadedClasses;
    }
    
    public boolean removeService(ISemanticIdentifier serviceIdentifier)
    {
        boolean remove = this.getServices().remove(serviceIdentifier);
        if (this.getServices().isEmpty())
        {
            this.unregistered = true;
        }
        return remove;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 79 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final LoadedServiceVO other = (LoadedServiceVO) obj;
        return this.id == other.id;
    }
    
}
