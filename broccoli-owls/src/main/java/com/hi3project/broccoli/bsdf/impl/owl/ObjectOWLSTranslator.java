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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLTranslationException;
import com.hi3project.broccoli.bsdf.impl.owls.conf.OWLSConfigurations;
import impl.jena.OWLIndividualImpl;
import impl.jena.OWLKnowledgeBaseImpl;
import impl.jena.OWLOntologyImpl;
import java.net.URI;
import java.util.*;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLModel;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.list.OWLList;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.vocabulary.OWLS_1_2;
import thewebsemantic.Bean2RDF;
import thewebsemantic.Namespace;
import thewebsemantic.RDF2Bean;
import thewebsemantic.lazy.LazyList;
import thewebsemantic.lazy.LazySet;

/**
 * Utility functions to translate between: Jena, OWLSAPI, JenaBeans
 *
 * 
 */
public class ObjectOWLSTranslator
{

    public static boolean isJenaBean(Object object)
    {
        return isJenaBean(object.getClass());
    }

    public static boolean isJenaBean(Class clas)
    {
        return (null != clas.getAnnotation(Namespace.class));
    }

    public static String getNamespaceFromJenaBean(Class clas)
    {
        Namespace namespace = (Namespace) clas.getAnnotation(Namespace.class);
        return namespace.value().replace("#", "");
    }

    public static boolean isJenaLazyCollection(Object object)
    {
        return (object.getClass().equals(LazySet.class) || object.getClass().equals(LazyList.class));
    }

    public static Resource beanToJenaResource(OntModel jenaModel, Object bean)
    {
        Bean2RDF jenaModelwriter = new Bean2RDF(jenaModel);
        Resource beanResource = jenaModelwriter.saveDeep(bean);
        return beanResource;
    }

    public static OWLList<OWLIndividual> owlListFromOWLModel(OWLModel owlModel)
    {
        return owlModel.createList(OWLS_1_2.ObjectList.List);
    }

    public static Collection<Resource> lazyBeanToJenaResourceCollection(OntModel jenaModel, Object bean)
    {
        Collection<Resource> resources = null;
        Iterator iterator;
        if (bean.getClass().equals(LazySet.class))
        {
            resources = new HashSet<Resource>();
            iterator = ((LazySet) bean).iterator();
        } else if (bean.getClass().equals(LazyList.class))
        {
            resources = new ArrayList<Resource>();
            iterator = ((LazyList) bean).iterator();
        } else
        {
            return resources;
        }
        while (iterator.hasNext())
        {
            resources.add(beanToJenaResource(jenaModel, iterator.next()));
        }
        return resources;
    }

    public static <T> T jenaResourceToBean(OWLModel owlModel, ResourceImpl jenaResource, Class<T> c)
    {
        OntModel jenaModel = owlModelToJenaModel(owlModel);
        RDF2Bean jenaModelReader = new RDF2Bean(jenaModel);
        return jenaModelReader.load(c, jenaResource);
    }

    public static Resource beanToJenaResource(OWLModel owlModel, Object bean)
    {
        return beanToJenaResource(owlModelToJenaModel(owlModel), bean);
    }

    public static OWLIndividual jenaResourceToOWLIndividual(OWLOntologyImpl owlModelOntology, Resource resource)
    {
        return new OWLIndividualImpl(owlModelOntology, resource);
    }

    public static OWLKnowledgeBaseImpl jenaModelToOWLKnowledgeBaseImpl(OntModel jenaModel)
    {
        return new OWLKnowledgeBaseImpl(jenaModel);
    }

    public static OWLKnowledgeBase jenaModelToOWLKnowledgeBase(OntModel jenaModel)
    {
        return jenaModelToOWLKnowledgeBaseImpl(jenaModel);
    }

    /**
     * To be properly tested...
     *
     * @param owlModel
     * @return
     */
    public static OntModel owlModelToJenaModel(OWLModel owlModel)
    {
        return (OntModel) owlModel.getImplementation();
    }

    public static OWLOntologyImpl owlOntologyToOWLOntologyImpl(OWLOntology owlOntology)
    {
        return new OWLOntologyImpl(
                jenaModelToOWLKnowledgeBaseImpl(owlModelToJenaModel(owlOntology.getKB())),
                owlOntology.getURI(),
                owlModelToJenaModel(owlOntology));
    }

    public static OWLOntologyImpl owlKnowledgeBaseToOwlOntologyImpl(OWLKnowledgeBase owlKnowledgeBase, URI resourceURI) throws OWLTranslationException
    {
        for (OWLModel model : owlKnowledgeBase.getOntologies(true))
        {
            if (null != model.getEntity(resourceURI))
            {
                return owlOntologyToOWLOntologyImpl((OWLOntology) model);
            }
        }
        throw new OWLTranslationException("", null);
    }

    public static OWLOntologyImpl owlModelToOwlOntologyImpl(OWLModel owlModel, URI resourceURI) throws OWLTranslationException
    {
        if (owlModel instanceof OWLOntology)
        {
            return owlOntologyToOWLOntologyImpl(((OWLOntology) owlModel));
        }
        if (owlModel instanceof OWLKnowledgeBase)
        {
            return owlKnowledgeBaseToOwlOntologyImpl(((OWLKnowledgeBase) owlModel), resourceURI);
        }
        throw new OWLTranslationException("Not an OWLOntology nor a OWLKnowledgeBase: " + owlModel.toString(), null);
    }

    /**
     * ***********************************************************************************************************************************************************************
     */
    static final Map<String, String> namespaces = new HashMap<String, String>();

    public synchronized static void registerNamespace(String owlNamespace, String javaNamespace)
    {
        namespaces.put(owlNamespace, javaNamespace);
    }

    public synchronized static String javaNamespaceFor(String owlNamespace)
    {
        return namespaces.get(owlNamespace);
    }

    public static Class owlIndividualToClass(OWLIndividual owlIndividual) throws ClassNotFoundException
    {
        String javaNamespace = owlIndividual.getType().getNamespace().replace("#", "");
        if (OWLSConfigurations.instance().usePellet())
        {
            return Class.forName(javaNamespaceFor(javaNamespace) + "." + owlIndividual.getLocalName().split("/")[0]);
        } else
        {
            return Class.forName(javaNamespaceFor(javaNamespace) + "." + owlIndividual.getType().getLocalName());
        }
    }

    public static Class owlsResultToClass(Result result) throws ClassNotFoundException
    {
        String[] namespaceParts = result.getNamespace().split("#");
        String className = javaNamespaceFor(namespaceParts[0]) + "." + namespaceParts[1].replace("/", "");
        return Class.forName(className);
    }

}
