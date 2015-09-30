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

import com.hi3project.broccoli.bsdl.api.parsing.IImport;

/**
 *
 * 
 */
public class Import implements IImport {
    
    public final static String SEPARATOR = ":";
    
    private String prefixedOntology = null;
    private String prefix = null;
    private String dialect = null;
    
    public Import() {}
    
    public Import(String prefix, String fullPath, String dialect) {
        this.prefix = prefix;
        this.prefixedOntology = fullPath;
        this.dialect = dialect;
    }
    
    @Override
    public String getPrefixed() {
        return prefixedOntology;
    }

    public void setPrefixed(String prefixed) {
        this.prefixedOntology = prefixed;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    @Override
    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
    
    public static boolean isImport(String importCandidate) {
        return !importCandidate.startsWith("http://") && importCandidate.contains(SEPARATOR);
    }
    
    public static String getImportNameFrom(String prefixedURI) {
        if (!isImport(prefixedURI)) {
            return null;
        }
        return (prefixedURI.split(SEPARATOR))[0];
    }
    
    public static String getNonImportNameFrom(String prefixedURI) {
        if (!isImport(prefixedURI)) {
            return null;
        }
        return (prefixedURI.split(SEPARATOR))[1];
    }
    
}
