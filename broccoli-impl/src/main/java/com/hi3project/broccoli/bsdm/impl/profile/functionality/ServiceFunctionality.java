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

import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.AbstractBSDLJavaFunctionalityImplementation;
import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.BSDLJavaServiceFunctionalityBSDLRealization;
import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.BSDLJavaServiceFunctionalityOWLSRealization;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdm.api.IServiceDescription;
import com.hi3project.broccoli.bsdm.api.asyncronous.IAsyncMessageClient;
import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGrounding;
import com.hi3project.broccoli.bsdm.api.implementation.IFunctionalityImplementation;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdm.impl.profile.ServiceProfile;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.implementation.FunctionalityExecutionVO;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * <p><b>Creation date:</b> 
 * 10-09-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 10-09-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class ServiceFunctionality extends Functionality implements IServiceFunctionality
{

    private Collection<IFunctionalityGrounding> functionalityGroundings = new ArrayList<IFunctionalityGrounding>();
    private ISemanticIdentifier identifier = null;
    private ServiceProfile serviceProfile = null;
    
    private int counter = 1;

    
    private String noGroundingMSG()
    {
        return "Trying to execute functionality with no grounding" + this.toString();
    }

    public ServiceFunctionality(Instance instance, ServiceProfile serviceProfile) throws SemanticModelException
    {
        super(instance);
        this.identifier = instance.getSemanticIdentifier();
        this.serviceProfile = serviceProfile;
    }

    public ServiceProfile getServiceProfile()
    {
        return serviceProfile;
    }

    @Override
    public ISemanticIdentifier getIdentifier()
    {
        return identifier;
    }

    @Override
    public synchronized void setFunctionalityGrounding(IFunctionalityGrounding functionalityGrounding)
    {
        if (!this.contains(functionalityGrounding))
        {
            functionalityGroundings.add(functionalityGrounding);
        }
    }
    
    private boolean contains(IFunctionalityGrounding functionalityGroundingToSearch)
    {
        for (IFunctionalityGrounding funcGrounding : this.functionalityGroundings)
        {
            if (funcGrounding.name().equals(functionalityGroundingToSearch.name()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<IFunctionalityGrounding> getFunctionalityGroundings()
    {
        return functionalityGroundings;
    }
    
    private synchronized IFunctionalityGrounding getFunctionalityGrounding()
    {
        if (functionalityGroundings.isEmpty()) return null;
        return this.getFunctionalityGroundings().iterator().next();
    }

    
    @Override
    public IFunctionalityImplementation getFunctionalityImplementation() throws ModelException
    {
        return this.getJavaFunctionalityImplementation();
    }
    
    public AbstractBSDLJavaFunctionalityImplementation getJavaFunctionalityImplementation() throws ModelException
    {
        AbstractBSDLJavaFunctionalityImplementation implementationFound = null;
        
        for (IServiceImplementation serviceImplementation : this.getServiceDescription().getImplementations())
        {
            IFunctionalityImplementation functionalityImplementation = serviceImplementation.getFunctionalityImplementation(this.name());
            if (null != functionalityImplementation &&
                    (functionalityImplementation instanceof BSDLJavaServiceFunctionalityBSDLRealization
                    || functionalityImplementation instanceof BSDLJavaServiceFunctionalityOWLSRealization))
            {
                return (AbstractBSDLJavaFunctionalityImplementation) functionalityImplementation;
            } 
        }
        
        return implementationFound;
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/profile#serviceFunctionality");
    } 

    @Override
    public Collection<IResult> executeWithValuesSyncronously(Collection<IAnnotatedValue> values) throws ModelException
    {
        if (null != getFunctionalityImplementation())
        {
            
            try
            {
                return getFunctionalityImplementation().executeWithValues(
                        values,
                        new FunctionalityExecutionVO(this.name(), String.valueOf(this.counter++)));
            } catch (ModelException e)
            {
                Collection<IResult> runtimeException = new ArrayList<IResult>();
                runtimeException.add(new RuntimeException(e));
                return runtimeException;
            }
        }
        
        if (null != getFunctionalityGrounding())
        {
            return this.getFunctionalityGrounding().executeWithValuesSyncronously(values);
        }
                
        throw new ServiceExecutionException(noGroundingMSG(), null);
    }
    
    @Override
    public IServiceDescription getServiceDescription()
    {
        if (null != getServiceProfile())
        {
            return getServiceProfile().getServiceDescription();
        }
        return null;
    }

    @Override
    public void executeWithValues(Collection<IAnnotatedValue> values, IAsyncMessageClient client) throws ModelException
    {        
        this.getFunctionalityGrounding().executeWithValues(values, client);
    }
    
    @Override
    public void subscribeTo(IAsyncMessageClient client) throws ModelException
    {        
        this.getFunctionalityGrounding().subscribeTo(client);
    }
    
}
