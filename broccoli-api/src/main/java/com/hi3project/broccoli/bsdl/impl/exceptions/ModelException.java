/**
 * ModelException.java
 *//*******************************************************************************
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

package com.hi3project.broccoli.bsdl.impl.exceptions;

import java.util.logging.Level;

public abstract class ModelException extends Exception
{

    private Exception innerException;
    private String message;
    private Level logLevel;

    public Level getLogLevel()
    {
        return logLevel;
    }

    public ModelException setLogLevel(Level logLevel)
    {
        this.logLevel = logLevel;
        return this;
    }

    /**
     * @param message
     * @param exception
     */
    public ModelException(String message, Exception exception)
    {
        this.message = message;
        innerException = exception;
        logLevel = Level.FINE;
    }

    public ModelException(String message)
    {
        this(message, null);
    }

    @Override
    public String getMessage()
    {
        if (null == innerException)
        {
            return message;
        }
        return message + "; " + innerException.getMessage();
    }

}
