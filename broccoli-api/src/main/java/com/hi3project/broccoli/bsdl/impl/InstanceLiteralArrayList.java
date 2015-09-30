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

package com.hi3project.broccoli.bsdl.impl;

import com.hi3project.broccoli.bsdl.api.IObject;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.List;

/**
 *
 * <p><b>Creation date:</b> 
 * 26-03-2015 </p>
 *
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 26-03-2015 - Initial release</li>
 * </ul>
 * 
 * @version 1
 */
public class InstanceLiteralArrayList extends AbstractInstanceArrayList implements ISubject, IObject
{

    private List<InstanceLiteral> values = null;
    
    
    public InstanceLiteralArrayList(List<InstanceLiteral> values)
    {
        this.values = values;
    }
    
    @Override
    public void setInstance(ISubject instance) throws SemanticModelException
    {
        if (instance instanceof InstanceLiteralArrayList)
        {
            super.setInstance(instance);
            this.values = ((InstanceLiteralArrayList) instance).values();
        }
        throw new SemanticModelException("Cannot set instance of: " + instance.toString(), null);
    }

    public List<InstanceLiteral> values()
    {
        return values;
    }

    @Override
    public List getValues()
    {
        return values();
    }
    
}
