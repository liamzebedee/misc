/*
 * Created on 29.07.2007
 * emonic.test emonic.test FixtureCSharpFileManipulator.java
 */
package emonic.test;

import java.io.ByteArrayInputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IParent;
import org.emonic.base.filemanipulators.CSharpFileManipulator;
import org.emonic.base.filemanipulators.FileDocument;

/**
 * @author bb
 *
 */
public class FixtureCSharpFileManipulator extends TestCase {
	private static final String TEST1=
		"namespace a{\n"+
		"class aaa{int s;\n"+
		"public void v() {int a;\n"+
		"}\n"+
		"}\n"+
		"}\n";
	private IProject project;
	/**
	 * @param name
	 */
	public FixtureCSharpFileManipulator(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		buildTestProject();
	}
	
	private void buildTestProject() throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject("project-csparsetest");
		if (!project.exists()) {
			project.create(null);
		    project.open(null);
		} else if (!project.isOpen()) {
		    project.open(null);
		}
		IPath p1 =new Path("charpfilemanitest1.cs");
		IFile tstFile=project.getFile(p1);
		ByteArrayInputStream s = new  ByteArrayInputStream(TEST1.getBytes());
		if (tstFile.exists()) {
			tstFile.setContents(s, true, false, null);	
		} else {
			tstFile.create(s, true, null);
		}
	}
	/**
	 * Test method for {@link org.emonic.base.filemanipulators.CSharpFileManipulator#toggleComment(int, int, boolean)}.
	 */
	public void testToggleComment() {
		IPath p1 =new Path("charpfilemanitest1.cs");
		IFile tstFile=project.getFile(p1);
		CSharpFileManipulator mani = new CSharpFileManipulator(tstFile);
		// Comment 2 lines
		mani.toggleComment(16,17,true);
		IDocument d1 = new FileDocument(tstFile,true);
		try {
			String s1 = d1.get(d1.getLineOffset(d1.getLineOfOffset(16)),2);
			Assert.assertEquals("//",s1);
		} catch (BadLocationException e) {
			Assert.fail();
		}
		try {
			String s1 = d1.get(d1.getLineOffset(d1.getLineOfOffset(16+17)),2);
			Assert.assertEquals("//",s1);
		} catch (BadLocationException e) {
			Assert.fail();
		}
		// Uncomment the same lines
		mani.toggleComment(16,17,true);
		IDocument d2 = new FileDocument(tstFile,true);
		Assert.assertEquals(TEST1, d2.get());
	}

	public void testToggleCommentLastLine() throws Exception {
		IPath path = new Path("charpfilemanitest1.cs");
		IFile tstFile = project.getFile(path);
		IDocument document = new FileDocument(tstFile,true);
		int lastLine = document.getNumberOfLines() - 1;
		int offset = document.getLineOffset(lastLine);
		
		CSharpFileManipulator mani = new CSharpFileManipulator(tstFile);
		// comment out the last line
		mani.toggleComment(offset, 0, true);
		document = new FileDocument(tstFile,true);
		assertEquals("//", document.get(offset, 2));
		// make sure that no additional characters were added to the last line
		assertEquals(2, document.getLineLength(lastLine));
	}
	
	public void testInsertCodeElement(){
		CodeElement ns = null;	
		ns = new CodeElement(null, IDotNetElement.NAMESPACE);
		ns.setElementName("namespace");
		
		String className = "MyClass";
		
		CodeElement cls = new CodeElement(ns, IDotNetElement.CLASS);
		cls.setElementName(className);
		cls.setDerived("Object");
		// Add it to the namespace and build it if the namespace exists
		
		CodeElement constructor = new CodeElement(cls, IDotNetElement.CONSTRUCTOR);
		constructor.setElementName(className);
		constructor.setAccessType("public");
		constructor.setSignature("()");
		cls.addChild(constructor);
		CodeElement main = new CodeElement(cls, IDotNetElement.METHOD);
		main.setElementName("Main");
		main.setAccessType("public");
		main.setTypeSignature("void");
		main.staticMod=true;
		main.setSignature("(string[] ArgV)");
		cls.addChild(main);
		ns.addChild(cls);
		
		Document doc = new Document();
		CSharpFileManipulator mani= new CSharpFileManipulator(doc);
		mani.AddCodeElement(ns,0,true);
		CodeElement result = mani.getRoot();
		
		IDotNetElement namespace = result.getChildren()[0];
		assertEquals("namespace", namespace.getElementName());
		assertEquals(IDotNetElement.NAMESPACE, namespace.getElementType());
		
		IDotNetElement aClass = ((IParent) namespace).getChildren()[0];
		assertEquals(className, aClass.getElementName());

		IDotNetElement constr = ((IParent) aClass).getChildren()[0];
		assertEquals(className, constr.getElementName());
		assertEquals(IDotNetElement.CONSTRUCTOR, constr.getElementType());
		
		IDotNetElement method = ((IParent) aClass).getChildren()[1];
		assertEquals("Main", method.getElementName());
		assertEquals(IDotNetElement.METHOD, method.getElementType());
	}

}
