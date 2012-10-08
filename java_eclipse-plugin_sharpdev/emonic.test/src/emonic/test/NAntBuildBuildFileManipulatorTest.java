/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package emonic.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.buildmechanism.SourceTarget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NAntBuildBuildFileManipulatorTest extends TestCase {

	//private IFile buildFile;
    private IProject project;
	private BuildMechanismManipulator manipulator;
	private IFile BuildFile;
	private IFile MainFile;
	private IFile SecondFile;

	private InputStream openMainStream() {
		String contents = "class main{}";
		return new ByteArrayInputStream(contents.getBytes());
	}
	
	private InputStream openSecondStream() {
		String contents = "class second{}";
		return new ByteArrayInputStream(contents.getBytes());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
        project=TestUtils.createDefaultProject("nanttestproject");
        manipulator = BuildDescriptionFactory.getBuildMechanismManipulator(project  );
        BuildFile=project.getFile(new Path("bin/build.exe"));
        MainFile = project.getFile(new Path("src/main.cs"));
		InputStream stream = openMainStream();
		if (MainFile.exists()) {
			MainFile.setContents(stream, true, true, null);
		} else {
			MainFile.create(stream, true, null);
		}
		SecondFile = project.getFile(new Path("src/sec.cs"));
		stream = openSecondStream();
		if (SecondFile.exists()) {
			SecondFile.setContents(stream, true, true, null);
		} else {
			SecondFile.create(stream, true, null);
		}
	}

	protected void tearDown() throws Exception {
		TestUtils.deleteProject("nanttestproject");
		super.tearDown();
	}

	

	private Node[] getChildrenListOfName(Node parent, String name) {
		NodeList list = parent.getChildNodes();
		ArrayList matchingChildren = new ArrayList(); 
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeName().equals(name)) {
				matchingChildren.add(node);
			}
		}
		Node[] result = new Node[matchingChildren.size()];
		result=(Node[]) matchingChildren.toArray(result);
		return result;
	}

//	

	public void testAddTarget() throws Exception {
		//manipulator.setProject(null);
		//IBuildSourceTargetManipulator tmanipulator=(IBuildSourceTargetManipulator) manipulator;	
		SourceTarget target = new SourceTarget();
		target.setName("compile");
        target.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
        
        target.setArtefact(BuildFile);
        target.setDebuggingOutput(true);
        target.setOptimization(true);
        target.setWarningLevel(4);
        manipulator.writeNewTargetInTree(target, true);
        SourceTarget target1 = new SourceTarget();
		target1.setName("compile1");
        target1.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
        
        target1.setArtefact(BuildFile);
        target1.setDebuggingOutput(false);
        target1.setOptimization(false);
        target1.setWarningLevel(3);
        manipulator.writeNewTargetInTree(target1, true);
        manipulator.save();
		IFile file = manipulator.getBuildFile();
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(file.getContents());

		Node projectNode = document.getChildNodes().item(0);
		Node[] targetNodes = getChildrenListOfName(projectNode, "target");
		assertNotNull(targetNodes);
		assertEquals(3, targetNodes.length);
		boolean hasCompile=false;
		boolean hasCompile1=false;
		boolean hasAll=false;
		for (int i = 0; i < 3; i++){
			assertTrue(targetNodes[i] instanceof Element);
			Element targetElement = (Element) targetNodes[i];
		    if (targetElement.getAttribute("name").equals("all")){
		    	hasAll=true;
		    }
		    if (targetElement.getAttribute("name").equals("compile")){
		    	hasCompile=true;
		    }
		    if (targetElement.getAttribute("name").equals("compile1")){
		    	hasCompile1=true;
		    }
		}
		assertEquals(true,hasAll);
		assertEquals(true,hasCompile);
		assertEquals(true,hasCompile1);
		SourceTarget target2 = (SourceTarget) manipulator.getTarget("compile");
		assertTrue(target2.isDebuggingOutput());
		assertTrue(target2.isOptimization());
		assertEquals(4, target2.getWarningLevel());
		SourceTarget target3 = (SourceTarget) manipulator.getTarget("compile1");
		assertFalse (target3.isDebuggingOutput());
		assertFalse(target3.isOptimization());
		assertEquals(3, target3.getWarningLevel());
	}

	public void testAddPropertyInProject() throws Exception {
		manipulator.setSrcDir("src");
        manipulator.setBinDir("bin");
		assertEquals("bin",manipulator.getBinDir());
		assertEquals("src",manipulator.getSrcDir());
	}
	




	public void testSupportsLanguage() {
		assertTrue(manipulator
				.supportsLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE));
	}
    
	public void testGetCodeFilesOf() throws Exception {
		
		
	}

	
	public void testSourceHandling(){
		SourceTarget target = new SourceTarget();
		target.setName("build2");
		//SourceTarget target = manipulatorA.getSourceTarget("build2");
        target.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
        target.setArtefact(BuildFile);
        target.addSource(MainFile);
        target.addSource(SecondFile);
        manipulator.writeNewTargetInTree(target, true);
        manipulator.save();
		SourceTarget target2 = (SourceTarget) manipulator.getTarget("build2");
		// 2 Targets
		assertEquals(2, target2.getSources().length);
		assertEquals(MainFile, target2.getSources()[0]);
		assertEquals(SecondFile, target2.getSources()[1]);
	}
	
	public void testPropertyHandling(){
		SourceTarget target = new SourceTarget();
		target.setName("build3");
        target.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
        target.setDebuggingOutput(true);
        target.setArtefact(BuildFile);
        String[] defs = {"a","b"};
        target.setDefinitions(defs);
        String[] refs = {"d","e","f"};
        target.setReferences(refs);
        manipulator.writeNewTargetInTree(target, true);
        manipulator.save();
        SourceTarget target2 = (SourceTarget) manipulator.getTarget("build3");
        assertEquals(2, target2.getDefinitions().length);
        assertEquals(3, target2.getReferences().length);
        assertEquals(true, target2.isDebuggingOutput());
        assertEquals("a", target2.getDefinitions()[0]);
        assertEquals("b", target2.getDefinitions()[1]);
        assertEquals("d", target2.getReferences()[0]);
        assertEquals("e", target2.getReferences()[1]);
        assertEquals("f", target2.getReferences()[2]);
        
	}
	
	public void testGetAllTargetsOfLanguage() {
		SourceTarget target = new SourceTarget();
		target.setName("build");
        target.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
        target.setArtefact(BuildFile);
        manipulator.writeNewTargetInTree(target, true);
		String[] targets = manipulator
				.getAllTargetNamesOfLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
		assertEquals(1, targets.length);
		assertEquals("build", targets[0]);

		assertEquals("build", targets[0]);
	}
	
	public void testGetSupportedLanguages() {
		String[] languages = manipulator.getSupportedLanguages();
		assertEquals(1, languages.length);
		assertEquals(IBuildMechanismDescriptor.CSHARP_LANGUAGE, languages[0]);
	}
}
