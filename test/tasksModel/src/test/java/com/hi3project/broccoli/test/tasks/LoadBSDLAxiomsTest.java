/**
 * *****************************************************************************
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
 *****************************************************************************
 */
package com.hi3project.broccoli.test.tasks;

import com.hi3project.broccoli.bsdf.impl.parsing.JenaBeanConverter;
import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.impl.Concept;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.SemanticLocator;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdl.impl.parsing.BSDLDocumentLoader;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdm.impl.parsing.BSDLBSDMLoader;
import com.hi3project.broccoli.test.tasksmodel.Task;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *  Tests suite meant to test and show how to load BSDL axioms from the BSDLRegistry
 */
public class LoadBSDLAxiomsTest 
{

    private BSDLRegistry bsdlRegistry;
    private BSDLDocumentLoader documentLoader;
    
    
    public static final String classesOntologyInBSDL = Config.testImplDir() + "tasksOntology.xml";
    public static final String taskConceptURI = "http://hi3project.com/broccoli/test/tasks#Task";
    
    
    public LoadBSDLAxiomsTest() {}
    
    
    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception { }

    @Before
    public void setUp() throws ModelException 
    {
        bsdlRegistry = new BSDLRegistry(new BSDLBSDMLoader());
        documentLoader = new BSDLDocumentLoader(bsdlRegistry);
        documentLoader.registerDocumentParser(new BSDLBSDMLoader(bsdlRegistry).createBSDLDocumentParser());
        documentLoader.readFrom(BSDLBSDMLoader.descriptorLocationsForBSDMCore());
    }

    @After
    public void tearDown() {}


    /**
     * <ul>
     * <li>
     *  Loads a BSDL ontology into BSDLRegistry reading from a descriptor file.
     * </li>
     * <li>
     *  Then, gets a concept (Task) of that ontology from the BSDLRegistry.
     * </li>
     * </ul>
     * @throws Exception
     */
    @Test
    public void loadTaskConcept() throws Exception
    {
        documentLoader.readFrom(new SemanticLocator(classesOntologyInBSDL));
        
        IAxiom taskAxiom = bsdlRegistry.axiomFor(new SemanticIdentifier(taskConceptURI));
        assertNotNull(taskAxiom);
        assertTrue(taskAxiom instanceof Concept);
        assertEquals(((Concept)taskAxiom).getSemanticIdentifier().toString(), taskConceptURI);
    }


    /**
     * <ul>
     * <li>
     *  Loads a BSDL ontology into BSDLRegistry reading from a descriptor file.
     * </li>
     * <li>
     *  An instance of Task (a class that is annotated with a concept from the loaded ontology) is created
     * </li>
     * <li>
     *  Using the JenaBeanConverter, a BSDL instance is created for this Task object
     * </li>
     * <li>
     *  Using the JenaBeanConverter, the generated BSDL instance is used to generate a Java Bean object for it
     * </li>
     * </ul>
     * @throws Exception
     */
    @Test
    public void loadTaskInstanceForObjectAndBackAgain() throws Exception
    {
        documentLoader.readFrom(new SemanticLocator(classesOntologyInBSDL));
        
        Task originalTask = new Task();
        originalTask.setNameOF("A very simple task that anyone can resolve");

        JenaBeanConverter jenaBeanConverter = new JenaBeanConverter(bsdlRegistry);
        Instance taskInstance = jenaBeanConverter.createInstanceFor(originalTask);
        assertTrue(taskInstance.properties().size() > 0);

        jenaBeanConverter.registerNamespace("http://hi3project.com/broccoli/test/tasks", "com.hi3project.broccoli.test.tasksmodel");
        Object convertedTask = jenaBeanConverter.createObjectFor(taskInstance);
        assertTrue(convertedTask instanceof Task);
        assertTrue(((Task) convertedTask).getNameOF().equals(originalTask.getNameOF()));
    }
    
}
