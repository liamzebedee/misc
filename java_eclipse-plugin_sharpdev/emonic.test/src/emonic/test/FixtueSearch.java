package emonic.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.actions.CSharpOpenDeclarationAction;
import org.emonic.base.editors.CSharpEditor;
import org.emonic.base.search.CodeSearchQuery;

public class FixtueSearch extends TestCase {
/////////////////////////////////////////////////////////////////////////////////////////////////
//	 These are the c#-files tested in the test.
//	 Don't modify them without knowing exactly what you are doing
//	 Better: Add new files for additional tests!
////////////////////////////////////////////////////////////////////////////////////////////////	/
	
	private static final String test1fn="testsearch1.cs";
	private static final String TEST1 = 
"class HelloWorld{\n" +
"public static void Main(string[] args){\n" + 
"int abc;\n"+
"abc=1;\n" +
"abc=2;\n" +
"}\n"+	      
"}\n";
//2345678901234567890123456789012345678901234567890
//        1         2         3         4         5
	private static final String TEST2 =
"public class OpenDec\n"+
"{\n"+
"public void V () {\n"+
"OpenDec bug;\n"+
"}\n"+
"}";
	
	private static final String test2fn="testsearch2.cs";	
	
	private static final String TEST3=
"public class AnotherClass\n"+
"{\n"+
"public void V () {\n"+
"System.Type t = typeof (OpenDec);\n"+
"}"+
"}";
	
	private static final String test3fn="testsearch3.cs";	
	
	private IProject project;

	protected void setUp() throws Exception {
		super.setUp();
		project = TestUtils.buildTestProject("fixturesearch");
		TestUtils.createProjectFile(test1fn, TEST1, project);
		TestUtils.createProjectFile(test2fn, TEST2, project);
		TestUtils.createProjectFile(test3fn, TEST3, project);
	}

	public void testSearchDocument1() {
		// IDocument doc = TestUtils.readFileInDocument(test1fn,project);
		try {
			CSharpEditor editor = TestUtils
					.openInCSharpEditor(test1fn, project);
			// Query search declaration
			CodeSearchQuery query = new CodeSearchQuery("abc", 78,
					CodeSearchQuery.RELATIONDECLARATION,
					CodeSearchQuery.RELATIONDECLARATION, editor);
			IProgressMonitor monitor = new NullProgressMonitor();
			query.run(monitor);
			ISearchResult result = query.getSearchResult();
			assertNotNull(result);
			IMarker[] searchMarkers = CodeSearchQuery.getMarkerList();
			assertEquals(1, searchMarkers.length);
			IMarker theMarker = searchMarkers[0];
			try {
				int startResult = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_START)).intValue();
				assertEquals(62, startResult);
				int length = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_END)).intValue()
						- startResult;
				assertEquals(3, length);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			// Query search references
			query = new CodeSearchQuery("abc", 62,
					CodeSearchQuery.RELATIONREFERENCES,
					CodeSearchQuery.RELATIONREFERENCES, editor);
			monitor = new NullProgressMonitor();
			query.run(monitor);
			result = query.getSearchResult();
			assertNotNull(result);
			searchMarkers = CodeSearchQuery.getMarkerList();
			// 3 markers expected: The declaration and the both references
			assertEquals(3, searchMarkers.length);
			try {
				theMarker = searchMarkers[0];
				int startResult = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_START)).intValue();
				// Dec starts at pos 62
				assertEquals(62, startResult);
				int length = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_END)).intValue()
						- startResult;
				assertEquals(3, length);
				theMarker = searchMarkers[1];
				// Ref 1 at 67
				startResult = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_START)).intValue();
				assertEquals(67, startResult);
				length = ((Integer) theMarker.getAttribute(IMarker.CHAR_END))
						.intValue()
						- startResult;
				assertEquals(3, length);
				theMarker = searchMarkers[2];
				startResult = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_START)).intValue();
				// Ref 2 at 74
				assertEquals(74, startResult);
				length = ((Integer) theMarker.getAttribute(IMarker.CHAR_END))
						.intValue()
						- startResult;
				assertEquals(3, length);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}

	// Bug 1875884: Jumping to declaration goes wrong
	public void testSearchDocument2() {
		// IDocument doc = TestUtils.readFileInDocument(test1fn,project);
		try {
			CSharpEditor editor = TestUtils
					.openInCSharpEditor(test2fn, project);
			// Query search declaration
			CodeSearchQuery query = new CodeSearchQuery("OpenDec", 45,
					CodeSearchQuery.RELATIONDECLARATION,
					CodeSearchQuery.RELATIONDECLARATION, editor);
			IProgressMonitor monitor = new NullProgressMonitor();
			query.run(monitor);
			ISearchResult result = query.getSearchResult();
			assertNotNull(result);
			IMarker[] searchMarkers = CodeSearchQuery.getMarkerList();
			assertEquals(1, searchMarkers.length);
			IMarker theMarker = searchMarkers[0];
			try {
				int startResult = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_START)).intValue();
				assertEquals(13, startResult);
				int length = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_END)).intValue()
						- startResult;
				assertEquals(7, length);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			// Search the references and jump to it
			query = new CodeSearchQuery("OpenDec", 13,
					CodeSearchQuery.RELATIONREFERENCES,
					CodeSearchQuery.RELATIONREFERENCES, editor);
			monitor = new NullProgressMonitor();
			query.run(monitor);
			result = query.getSearchResult();
			assertNotNull(result);
			searchMarkers = CodeSearchQuery.getMarkerList();
			// 2 markers expected: The declaration and the reference
			assertEquals(2, searchMarkers.length);
			try {
				theMarker = searchMarkers[0];
				int startResult = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_START)).intValue();
				// Dec starts at pos 13
				assertEquals(13, startResult);
				int length = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_END)).intValue()
						- startResult;
				assertEquals(7, length);
				theMarker = searchMarkers[1];
				// Ref 1 at 42
				startResult = ((Integer) theMarker
						.getAttribute(IMarker.CHAR_START)).intValue();
				assertEquals(42, startResult);
				length = ((Integer) theMarker.getAttribute(IMarker.CHAR_END))
						.intValue()
						- startResult;
				assertEquals(7, length);
				editor = (CSharpEditor) IDE.openEditor(EMonoPlugin.getDefault()
						.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage(), theMarker);
				CSharpOpenDeclarationAction action = new CSharpOpenDeclarationAction(
						editor);
				action.run();
				Point p = editor.getViewer().getSelectedRange();
				assertEquals(13, p.x);
				assertEquals(7, p.y);
				editor.getViewer().setSelectedRange(49, 0);
				action = new CSharpOpenDeclarationAction(editor);
				action.run();
				p = editor.getViewer().getSelectedRange();
				assertEquals(13, p.x);
				assertEquals(7, p.y);
			} catch (CoreException e) {
				e.printStackTrace();
			}

		} catch (WorkbenchException e) {
			e.printStackTrace();
		}

	}
}
