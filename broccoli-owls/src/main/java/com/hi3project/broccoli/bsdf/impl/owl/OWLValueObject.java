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

package com.hi3project.broccoli.bsdf.impl.owl;

import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.ISubject;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.NotYetImplementedException;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLTranslationException;
import impl.jena.OWLListImpl;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLModel;
import org.mindswap.owl.OWLValue;
import org.mindswap.owl.list.OWLList;
import org.mindswap.owls.process.Result;

/**
 *  <p> 
 *  The class acts as a builder of instances, taking a Java object or an
 * OWLValue object.
 *</p>
 *  Each instance holds an OWLValue, and the OWLModel where it s contained.
 *  It also knows: 
 *<ul>
 * <li>its value as a Java object</li>
 * <li>its URI as an OWL individual</li>
 * <li>its OWLURIClass</li>
 * </ul>
 * 
 */
public class OWLValueObject implements IAnnotatedValue {

    private OWLURIClass URIclass;
    private OWLValue object;
    private OWLModel model;

    public OWLValueObject(OWLModel model, OWLURIClass uriClass, OWLValue object) {
        this.model = model;
        this.URIclass = uriClass;
        this.object = object;
    }

    public OWLURIClass owlURIClass() {
        return URIclass;
    }

    public URI uri() {        
        if (object.isIndividual()) {
//            // if it s an OWL individual, it can be a Collection of jenaBean:
//            if (object instanceof OWLListImpl) {
//                OWLList<OWLIndividual> owlList = (OWLListImpl) object;                
//                return owlList.iterator().next().castTo(OWLIndividual.class).getURI();
//            }
            return object.castTo(OWLIndividual.class).getURI();
        }
        return null;        
    }

    public Object objectValue() throws ModelException {
        if (object.isDataValue()) {
            // if it s an OWL basic data type:
            return object.castTo(OWLDataValue.class).getValue();
        }
        if (object.isIndividual()) {
            // if it s an OWL individual, it can be a Collection of jenaBean:
            if (object instanceof OWLListImpl) {
                OWLList<OWLIndividual> owlList = (OWLListImpl) object;
                Iterator<OWLIndividual> iterator = owlList.iterator();
                OWLURIClass owlURIClass = null;
                ArrayList objectsList = new ArrayList();
                while (iterator.hasNext()) {
                    OWLIndividual ind = iterator.next();
                    try {
                        owlURIClass = OWLURIClass.from(ObjectOWLSTranslator.owlIndividualToClass(ind.castTo(OWLIndividual.class)));
                    } catch (ClassNotFoundException ex) {
                        throw new OWLTranslationException("translating to a List: " + ind.toString(), ex);
                    }
                    objectsList.add(ObjectOWLSTranslator.jenaResourceToBean(
                            model,
                            (ResourceImpl) ind.getImplementation(),
                            owlURIClass.getClas()));
                }
                return objectsList;
            }
            // if it is an Individual and not a Collection:
            return ObjectOWLSTranslator.jenaResourceToBean(
                    model,
                    (ResourceImpl) object.castTo(OWLIndividual.class).getImplementation(),
                    owlURIClass().getClas());
        }
        return null;
    }

    public OWLValue owlValue() {
        return object;
    }
    
     @Override
    public Object object() throws ModelException {
        return objectValue();
    }

    @Override
    public ISemanticIdentifier getSemanticAnnotation() {
        return new SemanticIdentifier(URIclass.getURI());
    }

    /****************************************************************************************************************************/
    /**
     * Builds an instance, from a given object
     * @param model
     * @param object
     * @return
     * @throws NotYetImplementedException
     * @throws OWLTranslationException 
     */
    public static OWLValueObject buildFromObject(OWLModel model, Object object) throws NotYetImplementedException, OWLTranslationException {
        return buildFromClasAndObject(model, OWLURIClass.from(object), object);
    }

    /****************************************************************************************************************************/
    /**
     * Builds an instance, from a given collection
     * @param model
     * @param col
     * @return
     * @throws NotYetImplementedException
     * @throws OWLTranslationException 
     */
    public static OWLValueObject buildFromCollection(OWLModel model, Collection col) throws NotYetImplementedException, OWLTranslationException {
        if (col.isEmpty()) {
            return null;
        }
        return buildFromClasAndCollection(model, OWLURIClass.from(col.iterator().next()), col);
    }

    /**
     * Builds an instance
     * @param model
     * @param object
     * @return
     * @throws NotYetImplementedException
     * @throws OWLTranslationException 
     */
    public static OWLValueObject buildAsResultFromObject(OWLModel model, Object object) throws NotYetImplementedException, OWLTranslationException {
        if (ObjectOWLSTranslator.isJenaBean(object)) {
            try {
                return new OWLValueObject(
                        model,
                        OWLURIClass.from(object),
                        model.createResult(new URI(ObjectOWLSTranslator.beanToJenaResource(model, object).getURI())));
            } catch (URISyntaxException ex) {
                throw new OWLTranslationException("translating to Jena: " + object.toString(), ex);
            }
        }
        throw new NotYetImplementedException("fail to build new " + OWLValueObject.class.toString()
                + " from a non JenaBeans compliant object: " + object.toString());
    }

    /**
     * Builds an instance
     * @param model
     * @param value
     * @return
     * @throws NotYetImplementedException
     * @throws OWLTranslationException 
     */
    public static OWLValueObject buildFromOWLValue(OWLModel model, OWLValue value) throws NotYetImplementedException, OWLTranslationException {
        if (value.isDataValue()) {
            return new OWLValueObject(model, OWLURIClass.from(value.castTo(OWLDataValue.class).getValue()), value);
        }
        if (value.isIndividual()) {
            try {                
                if (value instanceof OWLListImpl) {
                    OWLList<OWLIndividual> owlList = (OWLListImpl) value;
                    OWLURIClass owlURIClass = OWLURIClass.from(ObjectOWLSTranslator.owlIndividualToClass(owlList.iterator().next()));
                    return new OWLValueObject(model, owlURIClass, value);                    
                }
                if (value.canCastTo(Result.class)) {
                    return buildFromOWLValueAndClass(model, value, ObjectOWLSTranslator.owlsResultToClass(value.castTo(Result.class)));
                }
                if (value.canCastTo(OWLIndividual.class)) {
                    return buildFromOWLValueAndClass(model, value, ObjectOWLSTranslator.owlIndividualToClass(value.castTo(OWLIndividual.class)));
                }                
            } catch (ClassNotFoundException ex) {
                throw new OWLTranslationException("casting OWLValue: " + value.toString(), ex);
            }
        }
        throw new NotYetImplementedException("fail to build new " + OWLValueObject.class.toString() + " from: " + value.toString());
    }

    /****************************************************************************************************************************/
    /**
     * Builds an instance
     * @param model
     * @param value
     * @param clas
     * @return
     * @throws NotYetImplementedException
     * @throws OWLTranslationException 
     */
    private static OWLValueObject buildFromOWLValueAndClass(OWLModel model, OWLValue value, Class clas) throws NotYetImplementedException, OWLTranslationException {
        Object objectValue = ObjectOWLSTranslator.jenaResourceToBean(
                model,
                (ResourceImpl) value.getImplementation(),
                clas);
        return new OWLValueObject(model, OWLURIClass.from(objectValue), value);
    }

    /**
     * Builds an instance
     * @param model
     * @param uriClass
     * @param object
     * @return
     * @throws NotYetImplementedException
     * @throws OWLTranslationException 
     */
    private static OWLValueObject buildFromClasAndObject(OWLModel model, OWLURIClass uriClass, Object object) throws NotYetImplementedException, OWLTranslationException {
        // if object is a primitive data type:
        if (object.getClass().isPrimitive() || object instanceof String) {
            return new OWLValueObject(model, uriClass, model.createDataValue(object, uriClass.getURI()));
        }
        // if object uses jenabeans:
        if (ObjectOWLSTranslator.isJenaBean(object)) {
            try {
                return new OWLValueObject(
                        model,
                        uriClass,
                        model.createIndividual(uriClass.getURI(), new URI(ObjectOWLSTranslator.beanToJenaResource(model, object).getURI())));
            } catch (URISyntaxException ex) {
                throw new OWLTranslationException("translating to Jena: ", ex);
            }
        }
        throw new NotYetImplementedException("new " + OWLValueObject.class.toString() + " from a non-primitive object");
    }

    /**
     * Builds an instance
     * @param model
     * @param uriClass
     * @param collection
     * @return
     * @throws NotYetImplementedException
     * @throws OWLTranslationException 
     */
    private static OWLValueObject buildFromClasAndCollection(OWLModel model, OWLURIClass uriClass, Collection collection) throws NotYetImplementedException, OWLTranslationException {        
        OWLList<OWLIndividual> owlList =  ObjectOWLSTranslator.owlListFromOWLModel(model);
        Object[] collectionAsArray = collection.toArray();
        for (int i = collectionAsArray.length - 1; i >= 0; i--) {            
            try {
                owlList = owlList.cons(model.createIndividual(
                        uriClass.getURI(), 
                        new URI(ObjectOWLSTranslator.beanToJenaResource(model, collectionAsArray[i]).getURI())));
            } catch (URISyntaxException ex) {
                throw new OWLTranslationException("translating to Jena: ", ex);
            }
        }     
        return new OWLValueObject(
                model,
                uriClass,
                owlList);
    }

    @Override
    public ISubject value() throws ModelException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
