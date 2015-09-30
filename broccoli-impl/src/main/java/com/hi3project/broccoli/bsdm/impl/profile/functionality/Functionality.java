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

import com.hi3project.broccoli.bsdm.impl.functionality.Output;
import com.hi3project.broccoli.bsdm.impl.functionality.Input;
import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdm.impl.profile.nonFunctionalProperties.NonFunctionalProperty;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IEffect;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IFunctionalityCategory;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutput;
import com.hi3project.broccoli.bsdm.api.profile.nonFunctionalProperties.INonFunctionalProperty;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * 
 */
public class Functionality extends ConceptBehaviourImplementation implements IFunctionality
{

    protected Collection<IInput> inputs = new ArrayList<IInput>();
    protected Collection<IOutput> outputs = new ArrayList<IOutput>();
    protected FunctionalityCategory category = null;
    protected Collection<INonFunctionalProperty> nonFunctionalProperties = new ArrayList<INonFunctionalProperty>();
    protected Collection<IImport> imports = new ArrayList<IImport>();
    
    private String name = null;

    public Functionality(Instance instance) throws SemanticModelException
    {
        setInstance(instance);
        for (IObject iobject : getProperties("input"))
        {
            if (iobject instanceof Instance)
            {
                inputs.add(new Input((Instance) iobject));
            }
        }
        for (IObject iobject : getProperties("output"))
        {
            if (iobject instanceof Instance)
            {
                outputs.add(new Output((Instance) iobject));
            }
        }
        category = new FunctionalityCategory(getSinglePropertyAsInstance("functionalityCategory"));
        for (IObject iobject : getProperties("nonFunctionalProperty"))
        {
            if (iobject instanceof Instance)
            {
                nonFunctionalProperties.add(new NonFunctionalProperty((Instance) iobject));
            }
        }
        name = getSinglePropertyAsInstanceLiteralValue("name");
    }
    
    
    public Functionality(String name, Collection<IInput> inputs, Collection<IOutput> outputs, 
            Collection<INonFunctionalProperty> nFPs, FunctionalityCategory functionalityCategory,
            Collection<IImport> imports)
    {
        this.name = name;
        this.inputs = inputs;
        this.outputs = outputs;
        this.nonFunctionalProperties = nFPs;
        this.category = functionalityCategory;
    }
    

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/profile#functionality");
    }

    @Override
    public Collection<IInput> inputs() throws ModelException
    {
        return inputs;
    }

    public IInput inputByName(String name) throws ModelException
    {
        for (IInput input : inputs())
        {
            if (input instanceof Input)
            {
                if (((Input) input).name().equals(name))
                {
                    return input;
                }
            }
        }
        return null;
    }

    @Override
    public Collection<IOutput> outputs()
    {
        return outputs;
    }

    public IOutput outputByName(String name) throws ModelException
    {
        for (IOutput output : outputs())
        {
            if (output instanceof IOutput)
            {
                if (((IOutput) output).name().equals(name))
                {
                    return output;
                }
            }
        }
        return null;
    }

    @Override
    public Collection<IEffect> effects() throws ModelException
    {
        return new ArrayList<IEffect>();
    }

    @Override
    public IFunctionalityCategory category()
    {
        return category;
    }

    @Override
    public Collection<INonFunctionalProperty> nonFunctionalProperties()
    {
        return nonFunctionalProperties;
    }

    @Override
    public String name()
    {
        return name;
    }

}
