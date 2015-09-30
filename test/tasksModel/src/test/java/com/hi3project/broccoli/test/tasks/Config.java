/**
 * *****************************************************************************
 *
 * Copyright (C) 2015 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright (C) 2015 Alejandro Paz <alejandropl@lagostelle.com>
 *
 * This file is part of Vineyard.
 *
 * Vineyard is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Vineyard is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Vineyard. If not, see <http://www.gnu.org/licenses/>.
 *
 *****************************************************************************
 */

package com.hi3project.broccoli.test.tasks;

import java.io.File;


public class Config 
{
    
    private static final String testImplDir = System.getProperty("user.dir") 
            + File.separator + "src" + File.separator + "test" + File.separator + "java"
            + File.separator + "com" + File.separator + "hi3project" + File.separator + "broccoli" 
            + File.separator + "test" + File.separator + "tasks" + File.separator;
    
    
    public static String testImplDir()
    {
        return Config.testImplDir;
    }

}
