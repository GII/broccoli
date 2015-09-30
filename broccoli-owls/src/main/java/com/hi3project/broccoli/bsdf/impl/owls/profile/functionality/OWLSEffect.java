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

import com.hi3project.broccoli.bsdm.api.profile.functionality.IEffect;
import com.hi3project.broccoli.bsdl.impl.exceptions.NotYetImplementedException;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLTranslationException;
import com.hi3project.broccoli.bsdf.impl.owl.OWLValueObject;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLModel;
import org.mindswap.owls.expression.Expression.SWRL;
import org.mindswap.owls.process.Result;
import org.mindswap.swrl.Atom;
import org.mindswap.swrl.SWRLFactory;
import org.mindswap.swrl.SWRLFactory.ISWRLFactory;
import org.mindswap.swrl.SWRLIndividual;

/**
 * <p>
 *  An implementation of IEffect for OWL-S, based on OWLSParameter
 *
 * 
 * 
 */
public class OWLSEffect extends OWLSParameter implements IEffect {

    public OWLSEffect(OWLModel model, String name, OWLValueObject uriObject) {
        super(model, name, uriObject);
    }    

    public OWLSEffect(OWLModel model, Result result) throws NotYetImplementedException, OWLTranslationException {
        super(model, result.getQName(), OWLValueObject.buildFromOWLValue(model, result));
    }
    
    public OWLSEffect(OWLModel model, String name, Object uriObject) throws NotYetImplementedException, OWLTranslationException {
        this(model, name, OWLValueObject.buildAsResultFromObject(model, uriObject));
    }     

    /**
     *  TODO
     *  Currently acts as an example of how to add an SWRL expression to the OWL-S result
     * an instance of this class represents
     */
    public void addExpression() {
        SWRL expression = getOWLModel().createSWRLExpression(null);
        if (getOWLValueObject().owlValue().isIndividual()) {
            SWRLIndividual swrlIndividual = swrl().wrapIndividual(getOWLValueObject().owlValue().castTo(OWLIndividual.class));
            Atom atom = swrl().createSameIndividualAtom(swrlIndividual, swrlIndividual);
            expression.setBody(swrl().createList(atom));
            asOWLSResult().addEffect(expression);
        }       
    }
    
    private ISWRLFactory swrl = null;
    private ISWRLFactory swrl() {
        if (null == swrl) {
            swrl = SWRLFactory.createFactory(getOWLModel());
        }
        return swrl;
    }
    
    private Result asOWLSResult() {
        return (Result) getOWLValueObject().owlValue();
    }
}
