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

package com.hi3project.broccoli.bsdl.impl.meta;

import com.hi3project.broccoli.bsdl.api.meta.INaturalLanguage;
import java.util.Locale;

/**
 *  Implementation if INaturalLanguage metainfo based on Java Locales
 * 
 * 
 */
public class MetaPropertyNaturalLanguage implements INaturalLanguage {
    
    private Locale locale = null;
    
    public MetaPropertyNaturalLanguage() {
        locale = Locale.ENGLISH;
    }
    
    public MetaPropertyNaturalLanguage(String locale) {        
        this.locale = new Locale(locale);
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String langID() {
        return locale.getLanguage();
    }
}
