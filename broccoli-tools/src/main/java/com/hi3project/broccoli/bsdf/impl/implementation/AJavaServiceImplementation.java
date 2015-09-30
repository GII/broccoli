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

package com.hi3project.broccoli.bsdf.impl.implementation;


import com.hi3project.broccoli.bsdm.api.grounding.IFunctionalityGrounding;
import com.hi3project.broccoli.bsdm.api.implementation.IFunctionalityImplementation;
import com.hi3project.broccoli.bsdm.api.implementation.IJavaServiceImplementation;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IResult;
import com.hi3project.broccoli.bsdm.impl.asyncronous.FunctionalityResultMessage;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.impl.exceptions.ServiceExecutionException;
import com.hi3project.broccoli.io.BSDFLogger;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * <p>
 *  <b>Description:</b></p>
 *  <p>Basic implementation to be subclassed by a class that will
 * act as a BSDF service implementation.</p>
 *  <p>The basic idea is that there are methods that represent service functionality implementations,
 * and for each one another method that will be invoked when results for that functionality are obtained.</p>
 *  <p>The "result methods" are annotated with @ResponseTo("functionalityMethodName") and should call
 * processResultFor.</p>
 *
 *
 * <p>
 *  Colaborations: 
 * </p>
 *
 * <ul>
 * <li>with registered IFunctionalityImplementation instances, to send results
 * related with a previously invoked functionality method.</li>
 * </ul>
 * 
 * <p>
 *  Responsabilities:
 *  </p>
 * 
 * <ul>
 * <li>knows how to process functionality execution results obtained from an annotated method.</li>
 * </ul>
 * 
 *
 *
 * <p>
 * <b>Creation date:</b>
 * 23-05-2014 </p>
 *
 * <p>
 * <b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 23-05-2014 - Initial release</li>
 * </ul>
 *
 *
 * 
 * @version 1
 */
public abstract class AJavaServiceImplementation implements IJavaServiceImplementation
{

    protected HashMap<String, IFunctionalityImplementation> functionalityImplementations;
    
    protected HashMap<String, String> properties = null;
    
    
    public AJavaServiceImplementation() throws ServiceExecutionException
    {
        this.functionalityImplementations = new HashMap<String, IFunctionalityImplementation>();
//        this.initService();
    }

    
    
    @Override
    public void addFunctionalityGrounding(IFunctionalityImplementation functionalityImplementation)
    {
        this.functionalityImplementations.put(
                functionalityImplementation.name(),
                functionalityImplementation);
    }
    
    
    public void setConfigurationProperties(HashMap<String, String> properties)
    {
        this.properties = properties;
        this.propertiesWereLoaded();
    }

    
    @Override
    public abstract void initService() throws ServiceExecutionException;
    
    
    protected abstract void propertiesWereLoaded();

    
    /*
        This method should called within methods that obtain results and are annotated
    with @ResponseTo("functionalityMethodName").
    */
    protected synchronized void processResultFor(
            String requesterID,
            String conversationID, 
            Collection dataCollection) 
                throws ServiceExecutionException, SemanticModelException
    {
              
        String methodName = getResponseToFrom(
            Thread.currentThread().getStackTrace()[2].getClassName(),
            Thread.currentThread().getStackTrace()[2].getMethodName()
        );
        
        if (null == methodName) 
        {
            BSDFLogger.getLogger().debug("Cannot process result for requester: " + requesterID
                            + " and conversation: " + conversationID);
            return;
        }        
        
        Collection <IResult> results = new ArrayList<IResult>();

        IFunctionalityImplementation functionalityImplementation
                = this.functionalityImplementations.get(methodName);

        if (null != functionalityImplementation)
        {
            for (Object dataObj : dataCollection)
            {
                results.add(functionalityImplementation.createOutput(
                        dataObj,
                        functionalityImplementation.getAdvertisedFunctionality()));
            }
            
            FunctionalityResultMessage resultMsg = new FunctionalityResultMessage(
                    results, 
                    functionalityImplementation.name());

            BSDFLogger.getLogger().debug(
                    "Registers results as a FunctionalityResultMessage for requester: " + requesterID
                    + " and conversation: " + conversationID);
            
            for (IFunctionalityGrounding funcGr : 
                    functionalityImplementation.getAdvertisedFunctionality().getFunctionalityGroundings())
            {
                funcGr.registerResult(requesterID, conversationID, resultMsg);
            }
            return;
        }
        BSDFLogger.getLogger().debug("Cannot process result for requester: " + requesterID
                            + " and conversation: " + conversationID);
    }
    
    
    /*
        This method should called within methods that obtain results and are annotated
    with @ResponseTo("subscriptionMethodName").
    */
    protected synchronized void processNotificationFor(Collection dataCollection)
                throws ServiceExecutionException, SemanticModelException
    {
              
        String methodName = getResponseToFrom(
            Thread.currentThread().getStackTrace()[2].getClassName(),
            Thread.currentThread().getStackTrace()[2].getMethodName()
        );
        
        if (null == methodName) 
        {
            BSDFLogger.getLogger().debug("Cannot process notification");
            return;
        }        
        
        Collection <IResult> results = new ArrayList<IResult>();

        IFunctionalityImplementation functionalityImplementation
                = this.functionalityImplementations.get(methodName);

        if (null != functionalityImplementation)
        {
            for (Object dataObj : dataCollection)
            {
                results.add(functionalityImplementation.createOutput(
                        dataObj,
                        functionalityImplementation.getAdvertisedFunctionality()));
            }
            
            FunctionalityResultMessage resultMsg = new FunctionalityResultMessage(
                    results, 
                    functionalityImplementation.name());

            BSDFLogger.getLogger().debug(
                    "Registers notification as a FunctionalityResultMessage");
            
            for (IFunctionalityGrounding funcGr : 
                    functionalityImplementation.getAdvertisedFunctionality().getFunctionalityGroundings())
            {
                funcGr.registerNotification(resultMsg);
            }
            return;
        }
        BSDFLogger.getLogger().debug("Cannot process notification");
    }
    
    
    /*
        Utility method to extract the value of @ResponseTo for a method supposedly annotated.
    */
    private static String getResponseToFrom(String callerClassName, String callerMethodName) throws ServiceExecutionException
    {
        try
        {
            
            Class<?> clas = Class.forName(callerClassName);
            Method[] methods = clas.getMethods();
            for (Method method : methods)
            {
                if (method.getName().equalsIgnoreCase(callerMethodName))
                {
                    ResponseTo annotation = method.getAnnotation(ResponseTo.class);
                    return (null!=annotation)?annotation.value():null;                                                            
                }
            }
            
        } catch (ClassNotFoundException ex)
        {
            throw new ServiceExecutionException(callerClassName, ex);
        }
        
        return null;
    }
    

}
