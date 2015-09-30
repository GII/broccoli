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

import com.hp.hpl.jena.rdf.model.Resource;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLTranslationException;
import impl.jena.OWLIndividualImpl;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import org.mindswap.exceptions.TransformationException;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLModel;
import org.mindswap.owl.OWLTransformator;
import org.mindswap.owl.OWLValue;
import org.mindswap.owl.list.OWLList;


/**
 * <p>
 *  An implementation of OWLTransformator interface from OWLSAPI.
 *<p>
 *  It translates between OWL individuals and Java complex objects
 * using JenaBeans to do it
 *
 * 
 * 
 */
public class BeanTransformator implements OWLTransformator {

    public BeanTransformator() {
    }

    @Override
    public OWLValue toOWL(Object source, OWLModel model) throws TransformationException {
        final String errorMsg = "Cannot transformate object [" + source.toString() + "] to OWL";
        if (ObjectOWLSTranslator.isJenaBean(source)) {
            Resource resource = ObjectOWLSTranslator.beanToJenaResource(ObjectOWLSTranslator.owlModelToJenaModel(model), source);
            return resourceToOWLIndividual(resource, model);
        }
        if (ObjectOWLSTranslator.isJenaLazyCollection(source)) {
            Collection<Resource> resources =
                    ObjectOWLSTranslator.lazyBeanToJenaResourceCollection(ObjectOWLSTranslator.owlModelToJenaModel(model), source);
            OWLList<OWLIndividual> owlList = ObjectOWLSTranslator.owlListFromOWLModel(model);
            for (Resource r : resources) {
                owlList = owlList.cons(resourceToOWLIndividual(r, model));
            }
            return owlList;
        }
        throw new TransformationException(errorMsg);
    }

    private static OWLIndividual resourceToOWLIndividual(Resource resource, OWLModel owlModel) throws TransformationException {
        try {
            return new OWLIndividualImpl(ObjectOWLSTranslator.owlModelToOwlOntologyImpl(owlModel, new URI(resource.getURI())), resource);
        } catch (OWLTranslationException ex) {
            throw new TransformationException(ex);
        } catch (URISyntaxException ex) {
            throw new TransformationException(ex);
        }
    }

    @Override
    public Object fromOWL(OWLValue source, OWLModel model) throws TransformationException {
        try {
            return OWLValueObject.buildFromOWLValue(model, source).objectValue();
        } catch (ModelException ex) {
            throw new TransformationException(ex);
        }
    }
}
