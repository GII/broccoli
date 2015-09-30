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

import com.hi3project.broccoli.bsdl.api.ISemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.exceptions.NotYetImplementedException;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLTranslationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.mindswap.owl.vocabulary.XSD;
import thewebsemantic.Namespace;

/**
 * <p>
 * The class works as a builder, using from(obj) to create an OWLURIClass
 * instance that holds data that relates a Java class with an OWL class (via its
 * URI)
 *<p>
 * Internally, if manages a list of OWLURIClass instances, so an OWL URI has a
 * OWLURIClass "singleton"
 *
 *
 * 
 */
public class OWLURIClass implements ISemanticIdentifier
{

    private Class clas = null;
    private SemanticIdentifier semanticAnnotation = null;

    public OWLURIClass(URI uri, Class clas)
    {
        this.semanticAnnotation = new SemanticIdentifier(uri);
        this.clas = clas;
    }

    public Class getClas()
    {
        return clas;
    }

    @Override
    public URI getURI()
    {
        return semanticAnnotation.getURI();
    }

    @Override
    public String getAuthorityName()
    {
        String[] namespaceParts = getURI().toString().split("#");
        return namespaceParts[0];

    }

    @Override
    public String getName()
    {
        String[] namespaceParts = getURI().toString().split("#");
        return namespaceParts.length > 0 ? namespaceParts[1] : null;
    }

    @Override
    public String getLastName()
    {
        return this.getName();
    }

    /**
     *
     * @param obj A java object: it can be a basic type (including Strings), or
     * a complex object in the form of a JenaBean
     * @return
     * @throws NotYetImplementedException
     * @throws OWLTranslationException
     */
    public static OWLURIClass from(Object obj) throws NotYetImplementedException, OWLTranslationException
    {
        // if obj is a basid datatype
        if (obj instanceof Integer)
        {
            return from(XSD.xsdInt, obj);
        }
        if (obj instanceof Double)
        {
            return from(XSD.xsdDouble, obj);
        }
        if (obj instanceof String)
        {
            return from(XSD.xsdString, obj);
        }
        // if obj uses jenabeans:
        if (ObjectOWLSTranslator.isJenaBean(obj))
        {
            String baseURI = obj.getClass().getAnnotation(Namespace.class).value();
            try
            {
                return from(new URI(baseURI + obj.getClass().getSimpleName()), obj);
            } catch (URISyntaxException ex)
            {
                throw new OWLTranslationException("instancing a JenaBean object: " + obj.toString(), ex);
            }
        }
        throw new NotYetImplementedException("new " + OWLURIClass.class.toString() + " from a non-primitive object");
    }

    public static OWLURIClass from(Class clas) throws NotYetImplementedException, OWLTranslationException
    {
        String baseURI = ((Namespace) clas.getAnnotation(Namespace.class)).value();
        try
        {
            return new OWLURIClass(new URI(baseURI + clas.getSimpleName()), clas);
        } catch (URISyntaxException ex)
        {
            throw new OWLTranslationException("registering a JenaBean class: " + clas.toString(), ex);
        }
    }

    /**
     * *************************************************************************************************************************
     */
    private static List<OWLURIClass> classes;

    private static void initIfNeeded()
    {
        if (null == classes)
        {
            classes = new ArrayList<OWLURIClass>();
        }
    }

    private static OWLURIClass findclassWithURI(URI uri)
    {
        initIfNeeded();
        for (OWLURIClass uriClass : classes)
        {
            if (uri.equals(uriClass.getURI()))
            {
                return uriClass;
            }
        }
        return null;
    }

    private static OWLURIClass from(URI uri, Object obj)
    {
        OWLURIClass clas = null;
        clas = findclassWithURI(uri);
        if (null == clas)
        {
            clas = new OWLURIClass(uri, obj.getClass());
            classes.add(clas);
        }
        return clas;
    }

}
