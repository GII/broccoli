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

package com.hi3project.broccoli.conf;

import java.util.regex.Pattern;

/**
 *
 * 
 */
public class ProjProperties {
    public static final String project_basic_name = "soc_research";
    public final static Pattern stringActionListPattern = Pattern.compile(project_basic_name + ".+");
    public final static String BSDL = "BSDL";
    public final static String BSDL_CURRENT_VERSION_NUMBER = "0.5.0";
    public final static String OWL = "OWL";
    public final static String WSML = "WSML";
    public static final String BSDM_FILE_EXTENSION = "bsdm";
    public static final String BSDL_FILE_EXTENSION = "bsdl";
    
}
