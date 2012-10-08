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
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.filemanipulators.NetProjectManipulator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class MSBuildBuildFileManipulatorTest extends TestCase {
	
	//private static DocumentBuilder builder;
	
	static {
		try {
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (FactoryConfigurationError e) {
			throw new RuntimeException(e);
		}
	}

	private IFile buildFile;

	private InputStream openMainStream() {
		String contents = "class main{}";
		return new ByteArrayInputStream(contents.getBytes());
	}
	
	private InputStream openSecondStream() {
		String contents = "class second{}";
		return new ByteArrayInputStream(contents.getBytes());
	}
	
	//private MSBuildBuildFileManipulator manipulator;
	private BuildMechanismManipulator manipulatorA;
	private IFile MainFile;
	private IFile SecondFile;
	private IFile BuildFile;
	
	protected void setUp() throws Exception {
		super.setUp();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		//
		IProject project = root.getProject("msbuild");
		org.emonic.base.filemanipulators.NetProjectManipulator creator = new NetProjectManipulator(project);
		creator.createProject();
        // TODO That smells like a bug source:
		// You first have to determine the build mechanism,
		// then set the file name so that the build file can be stored
		// and only afterwards You can do the rest
	    // Any other order fails! 
		creator.setBuildMechanism("msbuild");
		creator.setBuildFile("msbuild.proj");
		creator.setSrcDir("src");
		creator.setBinDir("bin");
		creator.setCopyright("cr");
		creator.setNamespace("NS");
		creator.setTargetFramework("","");

		buildFile = project.getFile("msbuild.proj");
	    manipulatorA = BuildDescriptionFactory.getBuildMechanismManipulator(project  );	
        MainFile = project.getFile(new Path("src/main.cs"));
		InputStream stream = openMainStream();
		if (MainFile.exists()) {
			MainFile.setContents(stream, true, true, null);
		} else {
			MainFile.create(stream, true, null);
		}
		SecondFile = project.getFile(new Path("src/second.cs"));
		stream = openSecondStream();
		if (SecondFile.exists()) {
			SecondFile.setContents(stream, true, true, null);
		} else {
			SecondFile.create(stream, true, null);
		}
		BuildFile=project.getFile(new Path("bin/build.exe"));
	}

	protected void tearDown() throws Exception {
		buildFile.setContents(new ByteArrayInputStream(new byte[0]), true,
				false, new NullProgressMonitor());
		super.tearDown();
	}

	public void testGetBuildFile() {
		assertEquals(buildFile, manipulatorA.getBuildFile());
	}

	private Node[] getChildren(Node parent, String name) {
		NodeList list = parent.getChildNodes();
		ArrayList res = new ArrayList();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeName().equalsIgnoreCase(name)) {
				res.add(node);
			}
		}
		Node[] r = new Node[res.size()];
		r=(Node[])res.toArray(r);
		return r;
	}

	

	public void testAddTarget() throws Exception {
		SourceTarget compile = new SourceTarget();
		compile.setName("Compile");
		compile.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
		compile.setArtefact(buildFile);
		manipulatorA.writeNewTargetInTree(compile, true);  //Target("Compile");
        manipulatorA.save();
		IFile file = manipulatorA.getBuildFile();
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(file.getContents());

		Node projectNode = document.getChildNodes().item(0);
		Node[] targetNodes = getChildren(projectNode, "Target");
		Node targetNode=null;
		for (int i =0; i< targetNodes.length;i++){
			if (((Element) targetNodes[i]).getAttribute("Name").equals("Compile")){
				targetNode=targetNodes[i];
			}
		}
		assertNotNull(targetNode);
		assertTrue(targetNode instanceof Element);

		Element targetElement = (Element) targetNode;
		assertEquals("Compile", targetElement.getAttribute("Name"));
	}

	public void testAddPropertyInProject() throws Exception {
		//IBuildFileLayoutManipulator mani = (IBuildFileLayoutManipulator) manipulator;
//		mani.setProject(null);

        manipulatorA.setSrcDir("src");
        manipulatorA.setBinDir("bin");
        manipulatorA.save();
		IFile file = manipulatorA.getBuildFile();
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(file.getContents());

		Node projectNode = document.getChildNodes().item(0);
		Node propertyGroupNode = getChildren(projectNode, "PropertyGroup")[0];

		assertNotNull(propertyGroupNode);

		Node srcNode = null;
		Node buildNode = null;

		NodeList list = propertyGroupNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String name = node.getNodeName();
			if (name.equalsIgnoreCase("src")) {
				srcNode = node;
			} else if (name.equalsIgnoreCase("bin")) {
				buildNode = node;
			}
		}

		assertNotNull(srcNode);
		assertNotNull(buildNode);

		Node node = srcNode.getChildNodes().item(0);
		assertTrue(node instanceof Text);
		assertEquals("src", ((Text) node).getData().trim());

		node = buildNode.getChildNodes().item(0);
		assertTrue(node instanceof Text);
		assertEquals("bin", ((Text) node).getData().trim());
	}

	public void testGetSrcDir() {
		manipulatorA.setSrcDir("src");
		assertEquals("src", manipulatorA.getSrcDir());
	}

	

	public void testSupportsLanguage() {
		assertTrue(manipulatorA.
				supportsLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE));
	}


	
	
	
	public void testSourceHandling(){
		// Create a new target
		SourceTarget target = new SourceTarget();
		target.setName("build2");
		//SourceTarget target = manipulatorA.getSourceTarget("build2");
        target.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
        target.setArtefact(buildFile);
        target.addSource(MainFile);
        target.addSource(SecondFile);
        manipulatorA.writeNewTargetInTree(target, true);
        manipulatorA.save();
		SourceTarget target2 = (SourceTarget) manipulatorA.getTarget("build2");
		// 2 Targets
		assertNotNull("target build2 not found", target2);
		assertEquals(2, target2.getSources().length);
		assertEquals(MainFile, target2.getSources()[0]);
		assertEquals(SecondFile, target2.getSources()[1]);
	}
	
	
	
	public void testPropertyHandling(){
		SourceTarget target = new SourceTarget();
		target.setArtefact(BuildFile);
		target.setName("build3");
        target.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
        target.setDebuggingOutput(true);
        String[] defs = {"a","b"};
        target.setDefinitions(defs);
        String[] refs = {"d","e","f"};
        target.setReferences(refs);
        manipulatorA.writeNewTargetInTree(target, true);
        manipulatorA.save();
        SourceTarget target2 = (SourceTarget) manipulatorA.getTarget("build3");
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
		target.setArtefact(BuildFile);
        target.setLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
        manipulatorA.writeNewTargetInTree(target, true);
		String[] targets = manipulatorA
				.getAllTargetNamesOfLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
		assertEquals(1, targets.length);
		assertEquals("build", targets[0]);

		assertEquals("build", targets[0]);
	}
	
	public void testGetSupportedLanguages() {
		String[] languages = manipulatorA.getSupportedLanguages();
		assertEquals(1, languages.length);
		assertEquals(IBuildMechanismDescriptor.CSHARP_LANGUAGE, languages[0]);
	}
}
