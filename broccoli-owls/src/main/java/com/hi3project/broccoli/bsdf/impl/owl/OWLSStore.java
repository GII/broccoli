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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hi3project.broccoli.io.BSDFLogger;
import com.hi3project.broccoli.bsdf.impl.owls.conf.OWLSConfigurations;
import java.io.IOException;
import org.mindswap.owl.OWLKnowledgeBase;

/**
 *  A Jena based Store to persist OWLS-S services using OWLSAPI
 * 
 * 
 */
public class OWLSStore {
    
    private static Model persistentModel = null;
    private static OWLKnowledgeBase owlKB = null;
    
    public static Model persistentModel() {
        if (null == persistentModel) {
            try {
                persistentModel = TDBFactory.createModel(OWLSConfigurations.instance().dataStorePath());                
            } catch (IOException ex) {
                return null;
            }
        }
        BSDFLogger.getLogger().info("Loads jena model to memory from filesystem");
        return persistentModel;
    }
    
    public static void closeModel() {        
        persistentModel.close();
        persistentModel = null;
        owlKB = null;
    }
    
    public static void storeKB(OWLKnowledgeBase owlKB) {
        persistentModel().add(ObjectOWLSTranslator.owlModelToJenaModel(owlKB));
    }
    
    public static Model removeAllContents() {
        return persistentModel().removeAll();
    }
    
    public static OWLKnowledgeBase persistentModelAsOWLKB() {
        if (null == owlKB) {
            owlKB = KBWithReasonerBuilder.newKB();
        }        
        ObjectOWLSTranslator.owlModelToJenaModel(owlKB).add(persistentModel());
        return owlKB;
    }
    
}
