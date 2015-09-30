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
import com.hi3project.broccoli.bsdl.api.parsing.IImport;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.profile.IServiceType;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IInput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutput;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IRequestedFunctionality;
import com.hi3project.broccoli.bsdm.api.profile.nonFunctionalProperties.INonFunctionalProperty;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOptional;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IPreferred;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * 
 */
public class RequestedFunctionality extends Functionality implements IRequestedFunctionality
{

    private IOptional optionalProperties = null;
    private IPreferred preferredProperties = null;

    public RequestedFunctionality(IBSDLRegistry bsdlRegistry, Instance instance) throws ModelException
    {
        super(instance);
        
        IObject instanceForOptionalProperties = this.getSingleProperty("optional");
        if (instanceForOptionalProperties instanceof Instance)
        {
            this.optionalProperties = new Optional(bsdlRegistry, (Instance)instanceForOptionalProperties);
        }
        IObject instanceForPreferredProperties = this.getSingleProperty("preferred");
        if (instanceForPreferredProperties instanceof Instance)
        {
            this.preferredProperties = new Preferred(bsdlRegistry, (Instance)instanceForPreferredProperties);
        }
    }

    public RequestedFunctionality(String name, Collection<IInput> inputs, Collection<IOutput> outputs,
            Collection<INonFunctionalProperty> nFPs, FunctionalityCategory functionalityCategory,
            Collection<IImport> imports)
    {
        super(name, inputs, outputs, nFPs, functionalityCategory, imports);
    }

    public RequestedFunctionality(String name)
    {
        super(name, 
                new ArrayList<IInput>(),
                new ArrayList<IOutput>(),
                new ArrayList<INonFunctionalProperty>(), 
                null,
                new ArrayList<IImport>());
    }

    public void addImport(IImport imp)
    {
        super.imports.add(imp);
    }

    public void addInput(IInput input)
    {
        super.inputs.add(input);
    }

    public void addOutput(IOutput output)
    {
        super.outputs.add(output);
    }

    @Override
    public SemanticIdentifier conceptIdentifier() throws SemanticModelException
    {
        return new SemanticIdentifier("http://hi3project.com/broccoli/bsdm/profile#requestedFunctionality");
    }

    @Override
    public IServiceType addServiceTypeToSearch(IServiceType serviceType)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IOptional getOptionalProperties()
    {
        return this.optionalProperties;
    }

    @Override
    public IPreferred getPreferredProperties()
    {
        return this.preferredProperties;
    }

}
