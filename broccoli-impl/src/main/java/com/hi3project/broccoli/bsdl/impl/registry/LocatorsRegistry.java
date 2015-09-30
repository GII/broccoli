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

package com.hi3project.broccoli.bsdl.impl.registry;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import com.hi3project.broccoli.bsdl.api.registry.ILocatorsRegistry;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 
 */
public class LocatorsRegistry implements ILocatorsRegistry {
    
    private Map<ISemanticIdentifier, Collection<ISemanticLocator>> locatorsForIdentifierMap = new HashMap<ISemanticIdentifier, Collection<ISemanticLocator>>();
    
    @Override
    public synchronized Collection<ISemanticLocator> addOrResetIdentifier(ISemanticIdentifier identifier) {
        return locatorsForIdentifierMap.put(identifier, new ArrayList<ISemanticLocator>());
    }
    
    @Override
    public synchronized Collection<ISemanticLocator> addLocatorFor(ISemanticLocator locator,  ISemanticIdentifier identifier) {
        Collection<ISemanticLocator> locators = null;
        if (null == (locators = locatorsForIdentifierMap.get(identifier))) {
            locators = new ArrayList<ISemanticLocator>();
            locatorsForIdentifierMap.put(identifier, locators);
        }
        if (!locators.contains(locator)) {
            locators.add(locator);
        }        
        return locators;
    }
    
    @Override
    public Collection<ISemanticLocator> getLocatorsFor(ISemanticIdentifier identifier) {
        return locatorsForIdentifierMap.get(identifier);
    }
    
    @Override
    public Collection<ISemanticLocator> getLocatorsFor(String identifier) throws SemanticModelException {
        return getLocatorsFor(new SemanticIdentifier(identifier));
    }
    
    @Override
    public synchronized void clean() {
        locatorsForIdentifierMap = new HashMap<ISemanticIdentifier, Collection<ISemanticLocator>>();
    }
    
}
