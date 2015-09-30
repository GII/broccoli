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

package com.hi3project.broccoli.bsdf.impl.implementation;

import com.hi3project.broccoli.bsdm.api.implementation.IFunctionalityImplementationFactory;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.AbstractBSDLJavaFunctionalityImplementation;
import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.BSDLJavaServiceFunctionalityBSDLRealization;
import com.hi3project.broccoli.bsdf.impl.implementation.java.bsdl.BSDLJavaServiceFunctionalityOWLSRealization;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.io.BSDFLogger;

/**
 * <p>
 * <b>Description:</b></p>
 * Factory type class that builds an AbstractBSDLJavaFunctionalityImplementation
 * for a given implementationType
 *
 *
 * <p>
 * <b>Creation date:</b>
 * 18-06-2014 </p>
 *
 * <p>
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 18-06-2014 - Initial release</li>
 * </ul>
 *
 *
 * 
 * @version 1
 */
public class FunctionalityImplementationFactory implements IFunctionalityImplementationFactory
{

    private static FunctionalityImplementationFactory singletonInstance;

    public FunctionalityImplementationFactory()
    {
    }

    public static FunctionalityImplementationFactory getSingleton()
    {
        if (null == singletonInstance)
        {
            singletonInstance = new FunctionalityImplementationFactory();
        }
        return singletonInstance;
    }

    @Override
    public AbstractBSDLJavaFunctionalityImplementation instanceFor(String implementationType, Instance instance) throws SemanticModelException
    {
        if (implementationType.equalsIgnoreCase("javaJenaBeansOWLS"))
        {
            BSDFLogger.getLogger().info("Instances a BSDLJavaServiceFunctionalityOWLSRealization for functionality implementation");
            return new BSDLJavaServiceFunctionalityOWLSRealization(instance);
        }
        if (implementationType.equalsIgnoreCase("javaJenaBeansBSDL"))
        {
            try
            {
                BSDFLogger.getLogger().info("Instances a BSDLJavaServiceFunctionalityBSDLRealization for functionality implementation");
                return new BSDLJavaServiceFunctionalityBSDLRealization(instance);
            } catch (SemanticModelException ex)
            {
                BSDFLogger.getLogger().info("Cannot instance a functionality implementation for: " + implementationType);
                throw new SemanticModelException(ex.getMessage(), ex);
            }
        }
        return null;
    }

}
