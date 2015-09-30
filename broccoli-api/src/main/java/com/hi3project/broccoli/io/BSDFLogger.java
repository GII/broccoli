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

package com.hi3project.broccoli.io;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <b>Description:</b></p>
 *
 *
 * <p>
 * <b>Creation date:</b>
 * 11-02-2015 </p>
 *
 * <p>
 * <b>Changelog:</b>
 *
 * <ul>
 * <li> 1 , 11-02-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class BSDFLogger
{

    public static Logger getLogger()
    {
        return LoggerFactory.getLogger("BSDF-logger");
    }

    public static void initInfoLogger()
    {
        org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
        if (!rootLogger.getAllAppenders().hasMoreElements())
        {
            rootLogger.setLevel(Level.INFO);
            rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%t:%C{1}.%M] - %m%n")));
        }
    }

}
