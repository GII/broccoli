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

import com.hi3project.broccoli.bsdl.api.IAxiom;
import com.hi3project.broccoli.bsdl.impl.Concept;
import com.hi3project.broccoli.bsdl.impl.Instance;
import com.hi3project.broccoli.bsdl.impl.SemanticIdentifier;
import com.hi3project.broccoli.bsdl.impl.parsing.BSDLDocumentLoader;
import com.hi3project.broccoli.bsdl.impl.registry.BSDLRegistry;
import com.hi3project.broccoli.bsdm.impl.parsing.BSDLBSDMLoader;
import com.hi3project.broccoli.bsdf.impl.parsing.JenaBeanConverter;
import com.hi3project.broccoli.bsdf.impl.owls.parsing.OWLConverter;
import com.hi3project.broccoli.bsdl.impl.exceptions.ModelException;
import com.hi3project.broccoli.bsdf.impl.owl.OWLContainer;
import com.hi3project.broccoli.test.tasksmodel.Task;
import java.io.File;
import static org.junit.Assert.assertTrue;
import org.junit.*;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * A few tests that work with OWL axioms loaded from a valid OWL ontology and
 * then accessed from a BSDLRegistry to use with a JenaBeanConverter
 */
public class LoadAndExecuteOWLAxiomsTest
{

    private static BSDLRegistry bsdlRegistry;
            
    
    public static final String classesOntologyInOWL = Config.testImplDir() + "tasksOntology.owl";
    public static final String taskConceptURI = "http://hi3project.com/broccoli/test/tasks#Task";
    

    public LoadAndExecuteOWLAxiomsTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception
    {

        bsdlRegistry = new BSDLRegistry(new BSDLBSDMLoader());
        BSDLDocumentLoader documentLoader = new BSDLDocumentLoader(bsdlRegistry);
        documentLoader.registerDocumentParser(new BSDLBSDMLoader(bsdlRegistry).createBSDLDocumentParser());
        documentLoader.readFrom(BSDLBSDMLoader.descriptorLocationsForBSDMCore());
    }

    @AfterClass
    public static void tearDownClass() throws Exception { }

    @Before
    public void setUp() throws ModelException {}

    @After
    public void tearDown() {}


    /**
     * <ul>
     * <li>
     *  Loads an OWL ontology with an OWLContainer
     * </li>
     * <li>
     *  Using an OWLContainer with an OWLConverter and the BSDLRegistry, checks for an OWL class from that ontology
     *  and then loads it as a BSDL Concept
     * </li>
     * </ul>
     * @throws Exception
     */
    @Test
    public void loadTaskConcept1() throws Exception
    {
        OWLContainer owlContainer = new OWLContainer(new RDFXMLOntologyFormat());
        owlContainer.loadOntologyFromFile(new File(classesOntologyInOWL));
        owlContainer.createMergedOntology();

        OWLConverter owlConverter = new OWLConverter(bsdlRegistry, owlContainer);

        assertTrue(owlContainer.contains(taskConceptURI));
        OWLClass taskClass = owlContainer.getClass(taskConceptURI);
        Concept taskConcept = owlConverter.classToConcept(taskClass);
        assertTrue(taskConcept.properties().size() > 0);
    }


    /**
     * <ul>
     * <li>
     *  Loads an OWL ontology with an OWLContainer
     * </li>
     * <li>
     *  Using the BSDLRegistry with an OWLConverter, checks for an OWL class from that ontology
     *  and then loads it as a BSDL Concept
     * </li>
     * </ul>
     * @throws Exception
     */
    @Test
    public void loadTaskConcept2() throws Exception
    {
        OWLContainer owlContainer = new OWLContainer(new RDFXMLOntologyFormat());
        owlContainer.loadOntologyFromFile(new File(classesOntologyInOWL));
        owlContainer.createMergedOntology();

        OWLConverter owlConverter = new OWLConverter(bsdlRegistry, owlContainer);
        bsdlRegistry.registerConverter(owlConverter);

        IAxiom taskAxiom = bsdlRegistry.axiomFor(new SemanticIdentifier(taskConceptURI));
        assertTrue(taskAxiom instanceof Concept);
        Concept taskConcept = (Concept) taskAxiom;
        assertTrue(taskConcept.properties().size() > 0);
    }


    /**
     * <ul>
     * <li>
     *  Loads an OWL ontology with an OWLContainer
     * </li>
     * <li>
     *  Using the BSDLRegistry with an OWLConverter, checks for an OWL class from that ontology
     *  and then loads it as a BSDL Concept
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
        OWLContainer owlContainer = new OWLContainer(new RDFXMLOntologyFormat());
        owlContainer.loadOntologyFromFile(new File(classesOntologyInOWL));
        owlContainer.createMergedOntology();

        OWLConverter owlConverter = new OWLConverter(bsdlRegistry, owlContainer);
        bsdlRegistry.registerConverter(owlConverter);

        Concept taskConcept = (Concept) bsdlRegistry.axiomFor(new SemanticIdentifier(taskConceptURI));
        assertTrue(taskConcept.properties().size() > 0);

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
