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

package com.hi3project.broccoli.bsdl.api.parsing;

/**
 *
 * <p><b>Creation date:</b> 
 * 07-05-2015 </p>
 *
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 07-05-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class PreParsingRule 
{
    
    private String targetIdentification;
    
    private String subelementToSearch;
    
    private String subelementToSearchValue;
    
    private String subelementForNewValue;
    
    private String newValue;
    
    
    public PreParsingRule(
            String targetIdentification, 
            String subelementToSearch, 
            String subelementToSearchValue, 
            String subelementForNewValue, 
            String newValue)
    {
        this.targetIdentification = targetIdentification;
        this.subelementToSearch = subelementToSearch;
        this.subelementToSearchValue = subelementToSearchValue;
        this.subelementForNewValue = subelementForNewValue;
        this.newValue = newValue;
    }
    
    public PreParsingRule(String targetIdentification, String newValue)
    {
        this(targetIdentification, null, null, null, newValue);
    }
    
    public PreParsingRule(String targetIdentification, String subelementForNewValue, String newValue)
    {
        this(targetIdentification, null, null, subelementForNewValue, newValue);
    }

    public String getTargetIdentification()
    {
        return targetIdentification;
    }

    public String getSubelementToSearch()
    {
        return subelementToSearch;
    }

    public String getSubelementToSearchValue()
    {
        return subelementToSearchValue;
    }

    public String getSubelementForNewValue()
    {
        return subelementForNewValue;
    }

    public String getNewValue()
    {
        return newValue;
    }

}
