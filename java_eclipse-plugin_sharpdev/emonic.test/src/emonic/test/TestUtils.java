package emonic.test;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.FileEditorInput;
import org.emonic.base.editors.CSharpEditor;
import org.emonic.base.filemanipulators.FileDocument;
import org.emonic.base.filemanipulators.NetProjectManipulator;

public class TestUtils {
	
	public static final String DEFAULTBUILDFILE = "build.xml";
	public static final String DEFAULTFRAMEWORK = "Mono-1.0";
	public static final String DEFAULTBUILDMECHANISM = "nant";
	public static final String DEFAULTNAMESPACE = "Test";
	public static final String DEFAULTCOPYRIGHT = "CopyRight";
	public static final String DEFAULTBINDIR = "bin";
	public static final String DEFAULTSRCDIR = "src";
	private static final String DEFAULTRELEASE = "1.0";

	/**
	 * Set up a basic project
	 * @param projectname
	 * @return
	 * @throws CoreException 
	 */
	public static IProject buildTestProject(String projectname) throws CoreException  {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectname);
		if (!project.exists()) {
			project.create(null);
			project.open(null);
		} else if (!project.isOpen()) {
			project.open(null);
		}
		return project;
	}
	
	/**
	 * Create a file in the project
	 * @param filename The filename to creata
	 * @param content The content in the file
	 * @param project The project in  which the file has to be created 
	 * @throws CoreException
	 */
	public static void createProjectFile(String filename, String content, IProject project) throws CoreException{
		IPath p1 =new Path(filename);
		IFile tstFile=project.getFile(p1);
		ByteArrayInputStream s = new  ByteArrayInputStream(content.getBytes());
		if (tstFile.exists()) {
			tstFile.setContents(s, true, false, null);	
		} else {
			tstFile.create(s, true, null);
		}
	}
	
	/**
	 * Read the content if a file in a IDocument
	 * @param fn
	 * @param project
	 * @return
	 */
	public static  IDocument readFileInDocument(String fn, IProject project) {
		Path p1 =new Path(fn);
		IFile tstFile=project.getFile(p1);
		IDocument fd = new FileDocument(tstFile,true);
		return fd;
	}

	/**
	 * Get the active workbench-page
	 * @return
	 */
	public static IWorkbenchPage getPage(){
		IWorkbench workbench=PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		return window.getActivePage();
	}
	
	public static CSharpEditor openInCSharpEditor(String filename, IProject project) throws WorkbenchException{
		Path p1 =new Path(filename);
		IFile tstFile=project.getFile(p1);
		FileEditorInput fi = new FileEditorInput(tstFile);
		IEditorPart editor=getPage().openEditor(fi, org.emonic.base.editors.CSharpEditor.EMONIC_CSHARP_EDITOR_ID);
		return (CSharpEditor) editor;
		
	}

	public static IProject createDefaultProject(String projectname) {
		IProject project= ResourcesPlugin.getWorkspace().getRoot().getProject(projectname);
		org.emonic.base.filemanipulators.NetProjectManipulator creator = new NetProjectManipulator(project);
		creator.createProject();
        // TODO That smells like a bug source:
		// You first have to determine the build mechanism,
		// then set the file name so that the build file can be stored
		// and only afterwards You can do the rest
	    // Any other order fails! 
		creator.setBuildMechanism(DEFAULTBUILDMECHANISM);
		creator.setBuildFile(DEFAULTBUILDFILE);
		creator.setSrcDir(DEFAULTSRCDIR);
		creator.setBinDir(DEFAULTBINDIR);
		creator.setCopyright(DEFAULTCOPYRIGHT);
		creator.setNamespace(DEFAULTNAMESPACE);
		creator.setTargetFramework(DEFAULTFRAMEWORK,DEFAULTRELEASE);
		
		return project;
	}
	
	public static void deleteProject(String projectName){
		IProject project= ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			project.delete(true, true, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}
	
}
