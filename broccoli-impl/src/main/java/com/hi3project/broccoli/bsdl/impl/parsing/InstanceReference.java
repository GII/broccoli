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

package com.hi3project.broccoli.bsdl.impl.parsing;

import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * An specialization of Instance is needed that handles a reference still not
 * loaded. Meant to be used during parsing
 *
 *
 * 
 */
public class InstanceReference extends Instance
{

    private ReferenceToSemanticAxiom<Instance> instance;

    public InstanceReference(ReferenceToSemanticAxiom<Instance> instance) throws SemanticModelException
    {
        super();
        this.instance = instance;
        this.setSemanticIdentifier(instance.getSemanticIdentifier());
    }

    @Override
    public Collection properties() throws SemanticModelException
    {
        if (null != instance.semanticAxiom())
        {
            return instance.semanticAxiom().properties();
        }
        return new ArrayList();
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
        final InstanceReference other = (InstanceReference) obj;
        return this.instance == other.instance || (this.instance != null && this.instance.equals(other.instance));
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 31 * hash + (this.instance != null ? this.instance.hashCode() : 0);
        return hash;
    }

}
