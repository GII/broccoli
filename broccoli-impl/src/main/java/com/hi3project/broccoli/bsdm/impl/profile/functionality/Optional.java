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

import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdl.impl.ConceptBehaviourImplementation;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOptional;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutput;
import com.hi3project.broccoli.bsdm.api.profile.nonFunctionalProperties.INonFunctionalProperty;
import com.hi3project.broccoli.bsdm.impl.functionality.Input;
import com.hi3project.broccoli.bsdm.impl.functionality.Output;
import com.hi3project.broccoli.bsdm.impl.profile.nonFunctionalProperties.NonFunctionalProperty;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * <p>
 * <b>Creation date:</b>
 * 13-05-2015 </p>
 *
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 13-05-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class Optional extends ConceptBehaviourImplementation implements IOptional
{
    
    protected Collection<IInput> inputs = new ArrayList<IInput>();
    protected Collection<IOutput> outputs = new ArrayList<IOutput>();
    protected Collection<INonFunctionalProperty> nonFunctionalProperties = new ArrayList<INonFunctionalProperty>();

    public Optional(IBSDLRegistry bsdlRegistry, Instance instance) throws ModelException
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
        for (IObject iobject : getProperties("nonFunctionalProperty"))
        {
            if (iobject instanceof Instance)
            {
                nonFunctionalProperties.add(new NonFunctionalProperty((Instance) iobject));
            }
        }
    }

    @Override
    public Collection<IInput> inputs() throws ModelException
    {
        return this.inputs;
    }

    @Override
    public Collection<IOutput> outputs()
    {
        return this.outputs;
    }

    @Override
    public Collection<INonFunctionalProperty> nonFunctionalProperties()
    {
        return this.nonFunctionalProperties;
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/profile#optional");
    }

}
