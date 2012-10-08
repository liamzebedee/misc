/*
 * Created on 27.07.2007
 * emonic.test emonic.test FixtureFileDocument.java
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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.emonic.base.filemanipulators.FileDocument;


public class FixtureFileDocument extends TestCase {

	private static final String CONTENT1 = "B123456789E\nabcd";
	private IProject project;

	public FixtureFileDocument(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		buildTestProject();
	}
 
	private void buildTestProject() throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject("project-fdtest");
		if (!project.exists()) {
			project.create(null);
		    project.open(null);
		} else if (!project.isOpen()) {
		    project.open(null);
		}
		IPath p1 =new Path("testfile");
		IFile tstFile=project.getFile(p1);
		ByteArrayInputStream s = new  ByteArrayInputStream(CONTENT1.getBytes());
		if (tstFile.exists()) {
			tstFile.setContents(s, true, false, null);	
		} else {
			tstFile.create(s, true, null);
		}
	}

	public void testFileDocument() {
		IDocument fd = setUpTestFile();
		Assert.assertEquals(CONTENT1,fd.get());
	}
	
	public void testWriteFileDocument(){
		IPath p1 =new Path("testfile1");
		IFile tstFile=project.getFile(p1);
	    IDocument d = new FileDocument(tstFile,false);
	    d.set("abc\ndef");
	    IDocument d1 = new FileDocument(tstFile,true);
	    Assert.assertEquals("abc\ndef",d1.get());
	    try {
			d.replace(1,2,"xy");
		} catch (BadLocationException e) {
		    Assert.fail();
		}
		IDocument d2 = new FileDocument(tstFile,true);
	    Assert.assertEquals("axy\ndef",d2.get());
	}
	
	

	private IDocument setUpTestFile() {
		Path p1 =new Path("testfile");
		IFile tstFile=project.getFile(p1);
		IDocument fd = new FileDocument(tstFile,false);
		return fd;
	}

	public void testGetChar() {
		IDocument fd = setUpTestFile();
		try {
			Assert.assertEquals(fd.getChar(0),'B');
			Assert.assertEquals(fd.getChar(10),'E');
			Assert.assertEquals(fd.getChar(12),'a');
		} catch (BadLocationException e) {
			
			Assert.fail(e.getMessage() + e.getStackTrace().toString());
		}
	}

	public void testGetIntInt() {
		IDocument fd = setUpTestFile();
		try {
			Assert.assertEquals(fd.get(0,3),"B12");
			Assert.assertEquals(fd.get(1,3),"123");
			Assert.assertEquals(fd.get(10,3),"E\na");
		} catch (BadLocationException e) {
			Assert.fail(e.getMessage() + e.getStackTrace().toString());
		}	
	}

	public void testGetLineOfOffset() {
		IDocument fd = setUpTestFile();
		try {
			Assert.assertEquals(fd.getLineOfOffset(2),0);
			Assert.assertEquals(fd.getLineOfOffset(12),1);
		} catch (BadLocationException e) {
			Assert.fail(e.getMessage() + e.getStackTrace().toString());
		}	
	}

	public void testGetLineOffset() {
		IDocument fd = setUpTestFile();
		try {
			Assert.assertEquals(fd.getLineOffset(0),0);
			Assert.assertEquals(fd.getLineOffset(1),12);
		} catch (BadLocationException e) {
			Assert.fail(e.getMessage() + e.getStackTrace().toString());
		}	
	}

	public void testGetLineInformation() {
		IDocument fd = setUpTestFile();
		try {
			IRegion r = fd.getLineInformation(0);
			Assert.assertEquals(0,r.getOffset());
			//LineInformation: The line of the length WITHOUT linebreak
			// Change to the formar FileDocument - Impl: This gave back the LineLengt WITH \n
			Assert.assertEquals(11,r.getLength());
			r = fd.getLineInformation(1);
			Assert.assertEquals(12,r.getOffset());
			Assert.assertEquals(4,r.getLength());
		} catch (BadLocationException e) {
			Assert.fail(e.getMessage() + e.getStackTrace().toString());
		}	
	}

	public void testGetLineInformationOfOffset() {
		IDocument fd = setUpTestFile();
		try {
			IRegion r = fd.getLineInformationOfOffset(1);
			Assert.assertEquals(0,r.getOffset());
			Assert.assertEquals(11,r.getLength());
			r = fd.getLineInformationOfOffset(12);
			Assert.assertEquals(12,r.getOffset());
			Assert.assertEquals(4,r.getLength());
		} catch (BadLocationException e) {
			Assert.fail(e.getMessage() + e.getStackTrace().toString());
		}	
	}

	public void testGetNumberOfLines() {
		IDocument fd = setUpTestFile();
		Assert.assertEquals(2,fd.getNumberOfLines());
	}

	public void testGetNumberOfLinesIntInt()  {
		IDocument fd = setUpTestFile();
		try {
			Assert.assertEquals(2,fd.getNumberOfLines(0,12));
		} catch (BadLocationException e) {
			Assert.fail(e.getMessage() + e.getStackTrace().toString());
		}
	}

	
}
