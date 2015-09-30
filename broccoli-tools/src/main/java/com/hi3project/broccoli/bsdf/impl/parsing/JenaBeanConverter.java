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

package com.hi3project.broccoli.bsdf.impl.parsing;


import com.hi3project.broccoli.bsdl.impl.InstanceURI;
import com.hi3project.broccoli.bsdl.impl.Concept;
import com.hi3project.broccoli.bsdl.impl.InstanceArrayList;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.InstanceLiteral;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.Property;
import com.hi3project.broccoli.bsdl.impl.PropertyValue;
import com.mytechia.commons.util.classloading.ClassLoaders;
import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.api.registry.IBSDLRegistry;
import com.hi3project.broccoli.bsdm.api.parsing.IParameterConverter;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IAnnotatedValue;
import com.hi3project.broccoli.bsdm.api.profile.functionality.IOutputValue;
import com.hi3project.broccoli.bsdf.impl.implementation.OntologyToJavaReference;
import com.hi3project.broccoli.bsdl.impl.AbstractInstanceArrayList;
import com.hi3project.broccoli.bsdl.impl.InstanceLiteralArrayList;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException;
import com.hi3project.broccoli.bsdm.impl.functionality.ParameterValue;
import com.hi3project.broccoli.io.BSDFLogger;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

/**
 * Implementation of IParameterConverter that can translate between Java objects
 * with jenabean annotations referring to OWL ontologies, and BSDL instances
 */
public class JenaBeanConverter implements IParameterConverter
{

    private final Collection<OntologyToJavaReference> ontoloyToJavaReferences = new ArrayList<OntologyToJavaReference>();
    IBSDLRegistry registry = null;
    private final Map<String, String> namespaces = new HashMap<String, String>();

    public JenaBeanConverter(IBSDLRegistry registry)
    {
        this.registry = registry;
    }

    public void addOntologyToJavaReference(OntologyToJavaReference ontologyToJavaReference)
    {
        ontologyToJavaReferences().add(ontologyToJavaReference);
        registerNamespace(ontologyToJavaReference.ontologyURI().getURI().toString(), ontologyToJavaReference.javaNamespace());
    }

    public final Collection<OntologyToJavaReference> ontologyToJavaReferences()
    {
        return ontoloyToJavaReferences;
    }

    /**
     * ******************************************************************************************************
     * @param value
     * @return 
     * @throws com.hi3project.broccoli.bsdl.impl.exceptions.ModelException
     */
    @Override
    public IAnnotatedValue createAnnotatedValue(String value) throws ModelException
    {
        return new ParameterValue(new InstanceLiteral(value));
    }

    @Override
    public IAnnotatedValue createAnnotatedValue(Object value) throws ModelException
    {
        Instance valueInstance = createInstanceFor(value);
        if (null == valueInstance)
        {
            return null;
        }
        return new ParameterValue(valueInstance);
    }

    @Override
    public IAnnotatedValue createAnnotatedValue(Collection value) throws ModelException
    {
        AbstractInstanceArrayList valueInstance = createInstanceFor(value);
        if (null == valueInstance)
        {
            return null;
        }
        return new ParameterValue(valueInstance);
    }

    @Override
    public Object outputToObject(IOutputValue outputValue) throws ModelException
    {
        if (outputValue.value() instanceof InstanceLiteral)
        {
            return ((InstanceLiteral) outputValue.value()).getValue();
        }
        if (outputValue.value() instanceof Instance)
        {
            return createObjectFor((Instance) outputValue.value());
        }
        if (outputValue.value() instanceof InstanceArrayList)
        {
            return createListFor((InstanceArrayList) outputValue.value());
        }
        if (outputValue.value() instanceof InstanceLiteralArrayList)
        {
            return createListFor((InstanceLiteralArrayList) outputValue.value());
        }
        return null;
    }

    /**
     * ******************************************************************************************************
     * @param jenaBeans
     * @return 
     * @throws com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException 
     */
    public AbstractInstanceArrayList createInstanceFor(Collection jenaBeans) throws SemanticModelException
    {
        if (jenaBeans.isEmpty())
        {
            return null;
        }
        Object firstJenaBean = jenaBeans.iterator().next();
        Instance instance = createInstanceFor(firstJenaBean);
        if (null != instance && jenaBeans instanceof List)
        {
            List<Instance> instances = new ArrayList<Instance>();
            for (Object jenaBean : jenaBeans)
            {
                
                instances.add(createInstanceFor(jenaBean));
            }
            return new InstanceArrayList(instances, instance.getConcept());
        }
        if (firstJenaBean.getClass().isPrimitive() || firstJenaBean instanceof String)
        {
            List<InstanceLiteral> instances = new ArrayList<InstanceLiteral>();
            for (Object jenaBean : jenaBeans)
            {
                
                instances.add(new InstanceLiteral(jenaBean.toString()));
            }
            return new InstanceLiteralArrayList(instances);
        }
        return null;
    }

    public AbstractInstanceArrayList createInstanceForList(List jenaBeans) throws SemanticModelException
    {
        return createInstanceFor(jenaBeans);
    }

    public Instance createInstanceFor(Object jenaBean) throws SemanticModelException
    {
        final String errorMsg = "Problem creating instance for: " + jenaBean.toString();
        Namespace jenaBeanNamespace = getJenaBeanAnnotation(jenaBean);
        if (null == jenaBeanNamespace)
        {
            BSDFLogger.getLogger().debug("Cannot create an Instance out of: " + jenaBean.toString());
            return null;
        }
        IAxiom axiomForNamespace = null;
        RdfType rdfTypeAnnotation = jenaBean.getClass().getAnnotation(RdfType.class);
        if (null != rdfTypeAnnotation)
        {
            axiomForNamespace = registry.axiomFor(
                    new SemanticIdentifier(jenaBeanNamespace.value() + rdfTypeAnnotation.value()));
        }
        if (null == axiomForNamespace)
        {
            axiomForNamespace = registry.axiomFor(
                    new SemanticIdentifier(jenaBeanNamespace.value() + jenaBean.getClass().getSimpleName()));
        }

        if (null == axiomForNamespace)
        {
            BSDFLogger.getLogger().debug("Cannot create an Instance out of: " + jenaBean.toString());
            return null;
        }
        if (axiomForNamespace instanceof Concept)
        {
            Concept conceptForNamespace = (Concept) axiomForNamespace;
            Instance instance = new Instance();
            instance.setConcept(conceptForNamespace);
            for (Property property : conceptForNamespace.allProperties())
            {
                Method propertyGetMethod = extractObjectBeanProperty(jenaBean, property.getName(), "get");
                if (null != propertyGetMethod)
                {
                    PropertyValue propertyValue = new PropertyValue();
                    propertyValue.setName(property.getName());
                    instance.addProperty(propertyValue);
                    Object propertyObject = null;
                    try
                    {
                        propertyObject = propertyGetMethod.invoke(jenaBean, (Object[]) null);
                    } catch (Exception ex)
                    {
                        throw new SemanticModelException(errorMsg, ex);
                    }
                    if (null == propertyObject)
                    {
                        propertyValue.setObject(null);
                    } else
                    {
                        if (propertyGetMethod.getReturnType().isPrimitive() || propertyGetMethod.getReturnType().isInstance(""))
                        {
                            propertyValue.setObject(new InstanceLiteral(propertyObject.toString()));
                        } else
                        {
                            if (propertyObject.getClass().isAssignableFrom(java.util.ArrayList.class))
                            {
                                propertyValue.setObject(createInstanceForList((List) propertyObject));
                            } else
                            {
                                propertyValue.setObject(createInstanceFor(propertyObject));
                            }
                        }
                    }
                }
            }
            BSDFLogger.getLogger().info("For object: " + jenaBean.toString() + " , returns the Instance: " + instance.toString());
            return instance;
        }
        BSDFLogger.getLogger().debug("Cannot create an Instance out of: " + jenaBean.toString());
        return null;
    }

    private static Namespace getJenaBeanAnnotation(Object object)
    {
        return getJenaBeanAnnotation(object.getClass());
    }

    private static Namespace getJenaBeanAnnotation(Class<?> clas)
    {
        return clas.getAnnotation(Namespace.class);
    }

    public static Method extractObjectBeanProperty(Object object, String propertyName, String prefix)
    {
        return extractObjectBeanProperty(object.getClass(), propertyName, prefix);
    }

    public static Method extractObjectBeanProperty(Class clas, String propertyName, String prefix)
    {
        Method method = extractClassMethodByName(clas, prefix + capitalizeFirstLetter(propertyName));
        if (null == method)
        {
            method = extractClassMethodByRdfProperty(clas, propertyName);
            if (null != method && !method.getName().contains(prefix))
            {
                method = extractClassMethodByName(clas, prefix + capitalizeFirstLetter(method.getName().substring(3)));
            }
        }
        return method;
    }

    public static Method extractClassMethodByName(Class clas, String methodName)
    {
        Method[] methods = clas.getMethods();
        for (Method method : methods)
        {
            if (method.getName().equals(methodName))
            {
                return method;
            }
        }
        return null;
    }

    public static Method extractClassMethodByRdfProperty(Class clas, String annotationRdfPropertyValue)
    {
        Method[] methods = clas.getMethods();
        for (Method method : methods)
        {
            RdfProperty annotation = method.getAnnotation(RdfProperty.class);
            if (null != annotation
                    && annotation.value().contains("#")
                    && annotation.value().split("#")[1].equalsIgnoreCase(annotationRdfPropertyValue))
            {
                return method;
            }
        }
        return null;
    }

    /**
     * *********************************************************************************************************************************************************************
     * @param identifier
     * @return 
     * @throws com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException 
     */
    public String identifierToJavaClassName(ISemanticIdentifier identifier) throws SemanticModelException
    {
        if (identifier.equals(new SemanticIdentifier("http://hi3project.com/broccoli/bsdl#literal")))
        {
            return "java.lang.String";
        }
        String owlURInamespace = identifier.getAuthorityName();
        String javaNamespace = namespaces.get(owlURInamespace);
        if (null != javaNamespace)
        {
            return javaNamespace + "." + identifier.getName();
        }
        return null;
    }

    /**
     * *********************************************************************************************************************************************************************
     * @param instanceArrayList
     * @return 
     * @throws com.hi3project.broccoli.bsdl.impl.exceptions.SemanticModelException
     */
    public List createListFor(InstanceArrayList instanceArrayList) throws SemanticModelException
    {
        List objects = new ArrayList();
        for (Instance instance : instanceArrayList.values())
        {
            objects.add(createObjectFor(instance));
        }
        return objects;
    }
    
    
    
    public List createListFor(InstanceLiteralArrayList instanceLiteralArrayList)
    {
        List objects = new ArrayList();
        for (InstanceLiteral instanceLiteral: instanceLiteralArrayList.values())
        {
            objects.add(instanceLiteral.getValue());
        }
        return objects;
    }
    

    public Object createObjectFor(Instance instance) throws SemanticModelException
    {
        final String errorMsg = "Problem creating object for: " + instance.toString();
        Object objectForInstance = null;
        String className = javaNamespaceFor(instance.getConcept().semanticAxiom().getSemanticIdentifier().getAuthorityName())
                + "." + instance.getConcept().semanticAxiom().getSemanticIdentifier().getName();
        try
        {
            Class classOfInstance = null;
            try
            {
                classOfInstance = instance.getClass().getClassLoader().loadClass(className);
//                classOfInstance = Class.forName(className);
            } catch (ClassNotFoundException ex)
            {
                try
                {
                    List<Class<?>> classesForPackage = ClassLoaders.getClassesForPackage(
                            javaNamespaceFor(instance.getConcept().semanticAxiom().getSemanticIdentifier().getAuthorityName()),
                            instance.getClass());
                    Iterator<Class<?>> iterator = classesForPackage.iterator();
                    while (null == classOfInstance && iterator.hasNext())
                    {
                        Class<?> nextClass = iterator.next();
                        RdfType rdfTypeannotation = nextClass.getAnnotation(RdfType.class);
                        if (null != rdfTypeannotation && rdfTypeannotation.value().equals(instance.getConcept().semanticAxiom().getSemanticIdentifier().getName()))
                        {
                            classOfInstance = nextClass;
                        }
                    }
                } catch (IOException exIO)
                {
                    BSDFLogger.getLogger().debug(
                            "Cannot instance a java object out of: "
                            + instance.toString()
                            + ". Exception: "
                            + exIO.toString());
                    throw new SemanticModelException(errorMsg, exIO);
                }
                if (null == classOfInstance)
                {
                    BSDFLogger.getLogger().debug(
                            "Cannot instance a java object out of: "
                            + instance.toString());
                    return null;
                }
            }
            objectForInstance = classOfInstance.newInstance();
            Iterator instanceProperties = instance.properties().iterator();
            while (instanceProperties.hasNext())
            {
                Object property = instanceProperties.next();
                if (property instanceof PropertyValue)
                {
                    Method propertyMethod = extractObjectBeanProperty(classOfInstance, ((PropertyValue) property).getName(), "set");
                    try
                    {
                        if (((PropertyValue) property).getObject() instanceof InstanceLiteral)
                        {
                            InstanceLiteral propertyObject = (InstanceLiteral) ((PropertyValue) property).getObject();
                            propertyMethod.invoke(objectForInstance, propertyObject.getValue());
                        }
                        if (((PropertyValue) property).getObject() instanceof InstanceURI)
                        {
                            InstanceURI propertyObject = (InstanceURI) ((PropertyValue) property).getObject();
                            propertyMethod.invoke(objectForInstance, propertyObject.getValue());
                        }
                        if (((PropertyValue) property).getObject() instanceof Instance)
                        {
                            Instance propertyObject = (Instance) ((PropertyValue) property).getObject();
                            propertyMethod.invoke(objectForInstance, createObjectFor(propertyObject));
                        }
                        if (((PropertyValue) property).getObject() instanceof InstanceArrayList)
                        {
                            InstanceArrayList propertyObject = (InstanceArrayList) ((PropertyValue) property).getObject();
                            propertyMethod.invoke(objectForInstance, createListFor(propertyObject));
                        }
                    } catch (Exception ex)
                    {
                        BSDFLogger.getLogger().debug(
                            "Cannot instance a java object out of: "
                            + instance.toString()
                            + ". There is a problem with property: "
                            + property.toString());
                        throw new SemanticModelException(errorMsg, ex);
                    }
                }
            }
        } catch (InstantiationException ex)
        {
            BSDFLogger.getLogger().debug(
                            "Cannot instance a java object out of: "
                            + instance.toString());
            throw new SemanticModelException("Not able to create instance for: " + className, ex);
        } catch (IllegalAccessException ex)
        {
            BSDFLogger.getLogger().debug(
                            "Cannot instance a java object out of: "
                            + instance.toString());
            throw new SemanticModelException("Not able to create instance for: " + className, ex);
        }
        BSDFLogger.getLogger().info(
                            "For this instance: "
                            + instance.toString()
                            + " a java object has been loaded: "
                            + objectForInstance.toString());
        return objectForInstance;
    }

    public void registerNamespace(String namespace, String javaNamespace)
    {
        namespaces.put(namespace, javaNamespace);
    }

    public String javaNamespaceFor(String namespace)
    {
        return namespaces.get(namespace);
    }

    private static String capitalizeFirstLetter(String string)
    {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1, string.length());
    }
}
