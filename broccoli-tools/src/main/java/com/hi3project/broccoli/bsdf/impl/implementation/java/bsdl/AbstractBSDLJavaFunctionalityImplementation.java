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

package com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl;

import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdm.api.implementation.IFunctionalityImplementation;
import com.hi3project.broccoli.bsdm.api.implementation.IServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IServiceFunctionality;
import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdf.impl.implementation.OntologyToJavaReference;
import com.hi3project.broccoli.bsdl.impl.InstanceLiteral;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Generalization of OWL-to-Java based subgroundings
 *
 * 
 */
public abstract class AbstractBSDLJavaFunctionalityImplementation extends ConceptBehaviourImplementation implements IFunctionalityImplementation
{

    private Collection<OntologyToJavaReference> ontologyToJavaReferences = new ArrayList<OntologyToJavaReference>();
    private String className = null;
    private String methodName = null;
    private String name = null;
    private IServiceImplementation serviceImplementation;
    private IServiceFunctionality advertisedFunctionality;
    protected HashMap<String,String> configurationProperties = new HashMap<String, String>();
    
    public static final String CONFIG_PROP_SEPARATOR = "-";

    public AbstractBSDLJavaFunctionalityImplementation(Instance instance) throws SemanticModelException
    {
        setInstance(instance);
        className = getSinglePropertyAsInstanceLiteralValue("class");
        methodName = getSinglePropertyAsInstanceLiteralValue("method");
        for (IObject iobject : getProperties("ontologyToJava"))
        {
            if (iobject instanceof Instance)
            {
                ontologyToJavaReferences.add(new OntologyToJavaReference((Instance) iobject));
            }
        }
        for (IObject iobject : getProperties("configuration"))
        {
            if (iobject instanceof InstanceLiteral)
            {
                InstanceLiteral prop = (InstanceLiteral)iobject;
                if (prop.getValue().contains(CONFIG_PROP_SEPARATOR))
                {
                    String[] split = prop.getValue().split(CONFIG_PROP_SEPARATOR);
                    configurationProperties.put(split[0], split[1]);
                }
            }
        }
    }
        
    @Override
    public void setServiceImplementation(IServiceImplementation serviceImplementation)
    {
        this.serviceImplementation = serviceImplementation;
    }
    
    @Override
    public IServiceImplementation getServiceImplementation()
    {
        return this.serviceImplementation;
    }
    
    @Override
    public IServiceFunctionality getAdvertisedFunctionality()
    {
        return this.advertisedFunctionality;
    }
    
    public void setAdvertisedFunctionality(IServiceFunctionality advertisedFunctionality)
    {
        this.advertisedFunctionality = advertisedFunctionality;
        this.name = advertisedFunctionality.name();
    }

    public String className()
    {
        return className;
    }

    public String methodName()
    {
        return methodName;
    }
    
    
    @Override
    public String name()
    {
        return this.name;
    }
         

    public Collection<OntologyToJavaReference> ontologyToJavaReferences()
    {
        return ontologyToJavaReferences;
    }                                   
    
    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/implementation#functionalityImplementation");
    }

    @Override
    public String toString()
    {
        return "AbstractBSDLJavaFunctionalityImplementation{"
                + "className=" + className 
                + ", methodName=" + methodName 
                + ", name=" + name 
                + ", serviceImplementation=" + serviceImplementation 
                + ", advertisedFunctionality=" + advertisedFunctionality 
                + super.toString()
                + '}';
    }
    
}
