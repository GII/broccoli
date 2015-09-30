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

package com.hi3project.broccoli.bsdf.impl.owls.profile.functionality;

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdf.impl.owl.OWLValueObject;
import com.hi3project.broccoli.bsdl.impl.exceptions.NotYetImplementedException;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLTranslationException;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IParameterValue;
import java.util.Collection;
import org.mindswap.owl.OWLModel;
import org.mindswap.owl.OWLValue;

/**
 * <p>
 *  Implementation for IParameter interface, for OWL-S using OWLS API.
 *<p>
 *  Name + parameter, using an OWLValueObject and a String.
 *
 * 
 */
public class OWLSParameter  implements IParameterValue {

    protected String name = null;
    protected OWLValueObject uriObject = null;
    protected OWLModel model = null;

    
    public OWLSParameter(OWLModel model, String name, OWLValueObject uriObject) {
        this.model = model;
        this.name = name;
        this.uriObject = uriObject;
    }

    public OWLSParameter(OWLModel model, String name, Object object) throws NotYetImplementedException, OWLTranslationException  {
        this.model = model;
        this.name = name;
        this.uriObject = OWLValueObject.buildFromObject(model, object);
    }
    
    public OWLSParameter(OWLModel model, String name, Collection collection) throws NotYetImplementedException, OWLTranslationException  {
        this.model = model;
        this.name = name;
        this.uriObject = OWLValueObject.buildFromCollection(model, collection);
    } 
    
    public OWLSParameter(OWLModel model, String name, OWLValue value) throws NotYetImplementedException, OWLTranslationException {
        this.model = model;
        this.name = name;
        this.uriObject = OWLValueObject.buildFromOWLValue(model, value);
    }
    

    public OWLValueObject getOWLValueObject() {
        return uriObject;
    }
    
    public OWLModel getOWLModel() {
        return model;
    }
            
    public String name() {
        return name;
    }
    
    @Override
    public Object object() throws ModelException {
        return getOWLValueObject().objectValue();
    }   
        
    @Override
    public ISemanticIdentifier getSemanticAnnotation() {
        return getOWLValueObject().owlURIClass();
    }
    
    @Override
    public ISubject value() throws ModelException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
