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

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hi3project.broccoli.bsdf.impl.owls.exceptions.OWLIOException;
import com.hi3project.broccoli.io.BSDFLogger;
import com.hi3project.broccoli.bsdf.impl.owls.conf.OWLSConfigurations;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.pellet.jena.PelletReasonerFactory;

/**
 * Utility methods to build OWLKnowledgeBase instances with an OWL reasoner
 *
 * 
 */
public class KBWithReasonerBuilder
{

    public static OWLKnowledgeBase newKB()
    {
        if (!OWLSConfigurations.instance().usePellet())
        {
            BSDFLogger.getLogger().info("Creates a KB using OWLMicroReasoner");
            OWLKnowledgeBase kb = OWLFactory.createKB();
            kb.setReasoner(ReasonerRegistry.getOWLMicroReasoner());
            return kb;
        } else
        {
            BSDFLogger.getLogger().info("Creates a KB using Pellet");
            return ObjectOWLSTranslator.jenaModelToOWLKnowledgeBase(ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC));
        }
    }

    public static OWLKnowledgeBase newKB(List<URI> modelURIs) throws OWLIOException
    {
        OWLKnowledgeBase kb = newKB();
        for (URI uri : modelURIs)
        {
            try
            {
                kb.read(uri);
            } catch (IOException ex)
            {
                throw new OWLIOException("Exception reading an ontology model", ex);
            }
        }
        return kb;
    }

}
