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

import com.hi3project.broccoli.bsdl.api.meta.IVersionNumber;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * <p>
 *  Implementation for IVersionNumber that holds "current" as default value, to allow
 * references to the most current version of something, without indication of its number
 *
 * <p>
 *  It uses a format like: "N1:N2:N3..."
 *
 * 
 * 
 */
public class MetaPropertyVersion implements IVersionNumber {

    public static final String CURRENT = "current";
    public static final String VERSION_SEPARATOR = "\\.";
    private String version = CURRENT;

    public MetaPropertyVersion() {}

    public MetaPropertyVersion(String version) throws SemanticModelException {
        this();
        if (!isVersionFormatValid(version)) {
            throw new SemanticModelException("Not a valid version number: " + version, null);
        }
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof MetaPropertyVersion) {
            MetaPropertyVersion otherVersion = (MetaPropertyVersion) o;            
            if (isCurrent()) return isCurrent()?0:1;
            if (otherVersion.isCurrent()) return isCurrent()?0:-1;
            return compareTo(new ArrayDeque<String>(Arrays.asList(version.split(VERSION_SEPARATOR))),
                    new ArrayDeque<String>(Arrays.asList(otherVersion.getVersion().split(VERSION_SEPARATOR))));
        }
        return 1;
    }

    /**     
     *  Compares two string ordered lists containing numbers.
     * @return -1 when first group is higher, 0 if equals, 1 when second group is higher
     */
    private static int compareTo(Deque<String> first, Deque<String> second) {
        if (0 == first.size() && 0 == second.size()) {
            return 0;
        }
        if (0 == first.size()) {
            return 1;
        }
        if (0 == second.size()) {
            return -1;
        }
        int headsComparation = (Integer.valueOf(first.remove())).compareTo(Integer.parseInt(second.remove()));
        if (0 == headsComparation) {
            return compareTo(first, second);
        } else {
            return headsComparation;
        }
    }
    
    private boolean isCurrent() {
        return this.version.equals(CURRENT);
    }

    private static boolean isVersionFormatValid(String versionToCheck) {
        String[] split = versionToCheck.split(VERSION_SEPARATOR);
        for (int i = 0; i < split.length; i++) {
            if (!isNumericChar(split[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumericChar(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}