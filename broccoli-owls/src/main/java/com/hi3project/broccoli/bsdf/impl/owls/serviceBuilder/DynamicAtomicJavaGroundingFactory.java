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

package com.hi3project.broccoli.bsdf.impl.owls.serviceBuilder;

import com.hi3project.broccoli.bsdf.impl.owls.exceptions.DynamicGroundingBuildingException;
import com.hi3project.broccoli.bsdf.impl.owls.grounding.DynamicJavaGrounding;
import com.hi3project.broccoli.bsdf.impl.owls.grounding.IOWLSDynamicGrounding;
import com.hi3project.broccoli.bsdf.impl.owl.BeanTransformator;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.grounding.JavaAtomicGrounding;
import org.mindswap.owls.grounding.JavaGrounding;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.service.Service;

/**
 * <p>
 *  Implementation for a AbstractDynamicGroundingFactory that generates a JavaAtomicGrounding
 * for a given OWLS API service.
 *<p>
 *  It only needs to know the names of the class and method to invoke.
 *
 * 
 * 
 */
public class DynamicAtomicJavaGroundingFactory extends AbstractDynamicGroundingFactory {

    private Class clas;
    private String methodName;
    private static String javaTransformatorURL = "http://on.cs.unibas.ch/owl-s/1.2/MoreGroundings.owl#transformatorClass";
    private static String javaTransformatorClass = BeanTransformator.class.getName();

    public DynamicAtomicJavaGroundingFactory(Class clas, String methodName) {
        this.clas = clas;
        this.methodName = methodName;
    }

     @Override
    public IOWLSDynamicGrounding generateGrounding(Service service) throws DynamicGroundingBuildingException {
         return new DynamicJavaGrounding(this);
     }
    
    /**     
     *  It automatically creates Output and Inputs assigning them a Java Transformator class
     * 
     * @param service
     * @return
     * @throws DynamicGroundingBuildingException 
     */    
    public Grounding generateOWLGrounding(Service service) throws DynamicGroundingBuildingException {
        JavaGrounding javaGrounding = service.getOntology().createJavaGrounding(null);
        JavaAtomicGrounding javaAtomicGrounding = service.getOntology().createJavaAtomicGrounding(null);
        AtomicProcess atomicProcess = service.getProcess().castTo(AtomicProcess.class);
        URI javaTransformatorURI = null;
        try {
            javaTransformatorURI = new URI(javaTransformatorURL);
        } catch (URISyntaxException ex) {
            throw new DynamicGroundingBuildingException("cannot load needed ontology on " + javaTransformatorURL);
        }

        // set Output with transformatorClass
        javaAtomicGrounding.setOutput(null, method().getReturnType().getName(), atomicProcess.getOutput());
        javaAtomicGrounding.getOutput().addProperty(javaTransformatorURI, service.getKB().createDataValue(javaTransformatorClass));

        // add Inputs with transformatorClass        
        OWLIndividualList<Input> inputs = atomicProcess.getInputs();
        Class<?>[] methodParameterTypes = method().getParameterTypes();
        if (inputs.size() != methodParameterTypes.length) {
            throw new DynamicGroundingBuildingException("the method (" + ") and the service have different number of input parameters");
        }
        for (int i = 0; i < inputs.size(); i++) {
            javaAtomicGrounding.addInputParameter(null, methodParameterTypes[i].getName(), i, inputs.get(i));
        }        
        for (Iterator<Input> it = inputs.iterator(); it.hasNext();) {
            javaAtomicGrounding.getInputParamter(it.next()).addProperty(javaTransformatorURI, service.getKB().createDataValue(javaTransformatorClass));
        }

        // set Class and Method
        javaAtomicGrounding.setClazz(clas.getName());
        javaAtomicGrounding.setMethod(methodName);

        javaAtomicGrounding.setProcess(atomicProcess);
        javaGrounding.addGrounding(javaAtomicGrounding);
        javaGrounding.setService(service);
        service.addGrounding(javaGrounding);
        return javaGrounding;
    }

    
    /***********************************************************************************************************************/
    
    private Method method() throws DynamicGroundingBuildingException {
        Method[] methods = clas.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new DynamicGroundingBuildingException("clas " + clas.getSimpleName()
                + " has no method with that name (" + methodName + ")");
    }
}
