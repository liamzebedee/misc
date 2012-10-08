package org.emonic.base.builders;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IInternalBuilder;
import org.emonic.base.filemanipulators.NetProjectManipulator;
import org.emonic.base.helpers.DebugUtil;
import org.emonic.base.helpers.SimpleCommandRunner;
/**
 * @author bb
 *
 */
public class EMonoBuilder extends IncrementalProjectBuilder {

	private boolean debugit = false;
	private MessageConsoleStream buildConsoleStream;
	private IConsoleManager buildConsoleManager;
	private MessageConsole buildConsole;
	private CSharpPatternMatchListener listener;
	//private IProject project;
	private final int BUFFLENGTH=100;

	/**
	 * Builders must provide a public, no-argument constructor. This is called by the
	 * Eclipse framework.
	 */
	public EMonoBuilder() {
		super();
		buildConsoleManager = ConsolePlugin.getDefault().getConsoleManager();
		MessageConsole[] cons = new MessageConsole[1];
		buildConsole = new MessageConsole("Build Console", null);
		cons[0] = buildConsole;
		buildConsoleManager.addConsoles(cons);
		buildConsoleStream = buildConsole.newMessageStream();
		listener = new CSharpPatternMatchListener();
		buildConsole.addPatternMatchListener(listener);
		//project = getProject();
	}

	/**
	 * Runs this builder in the specified manner.
	 * @param kind of build
	 * @param args table of builder-specific arguments
	 * @param monitor a progress monitor
	 * @return null because this builder would not like deltas the next time it is run
	 */
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) {
		String dir = "";
		IProject project = getProject();
		monitor.beginTask("Building",6);
		DebugUtil.DebugPrint("Build for project " + project.getName(), debugit);
		// get Path of Project
		IPath bf = project.getLocation();
		// get string with project path (= working directory)
		dir = bf.makeAbsolute().toOSString();
		DebugUtil.DebugPrint("Build in dir: " + dir, debugit);

		IFile[] files = findAllCsFiles(getDelta(getProject()),monitor);
		monitor.worked(1);
		boolean problems;
		listener.setProject(getProject());
		if (kind == IncrementalProjectBuilder.FULL_BUILD ||
				kind == IncrementalProjectBuilder.CLEAN_BUILD) {
			// delete derived resources
			try {
				IResource[] derivedResources = getDerivedResources();
				if (derivedResources != null )
					deleteDerivedResources(getDerivedResources());
			} catch (CoreException e) {
				e.printStackTrace();
			}
			// Do a Full Build: Use a ResourceVisitor to process the tree.
			problems = performFullBuild(monitor,dir);
		} else { 
			// Build it with a delta
			problems = performIncBuild(monitor,dir,files);
		}

		// open problems view on errors/warnings
		if (problems) {
			PlatformUI.getWorkbench().getDisplay().syncExec(
					new Runnable()
					{
						public void run()
						{
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
								showView(IPageLayout.ID_PROBLEM_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
							} catch (Exception e) {
								// The workbench or view can be null if the build is done
								// during the shutdown process
							}
						}
					});
		}

		monitor.worked(5);
		try {
			markResourcesAsDerived(getDerivedResources());
			// refresh resources so that derived files show up
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		monitor.done();
		return null;
	}


	private IResource[] getDerivedResources() throws CoreException {
		IProject proj = getProject();
		IResource[] binFiles = null;
		NetProjectManipulator mani = new NetProjectManipulator(proj);
		String binDirString = mani.getBinDir();
		if (binDirString != null) {
			IFolder binDir = proj.getFolder(binDirString);
			if (binDir != null && binDir.exists() ){
				binFiles = binDir.members(IResource.FILE);
			}  else {
				binFiles = new IResource[0];
			}
		}
		return binFiles;
	}


	private void markResourcesAsDerived(IResource[] files) throws CoreException {
		if(files != null) {
			for (int i = 0; i < files.length; i++)
				if (files[i].exists())
					files[i].setDerived(true, null);
		}
	}

	private void deleteDerivedResources(IResource[] files) throws CoreException {

		for (int i=0; i<files.length; i++)
			if (files[i].exists() && ! files[i].getResourceAttributes().isReadOnly())
				files[i].delete(true, null);
	}


	/**
	 * Forms a string containing all C# files that changed out of the IResourceDelta.
	 * @param d The resource delta.
	 * @param moni A progress monitor.
	 * @return The string.
	 */
	private IFile[] findAllCsFiles(IResourceDelta d, IProgressMonitor moni) {
		ArrayList<IResource> res = new ArrayList<IResource>();
		if (d != null) {
			IResourceDelta[] rdc = d.getAffectedChildren();
			for (int i = 0; i < rdc.length; i++){
				String ext = rdc[i].getResource().getFileExtension();
				if (ext != null) { 
					if (ext.equals("cs")) {
						IResource reso = rdc[i].getResource();
						if (IFile.class .isAssignableFrom(reso.getClass())){
							//res = res + " " +  rdc[i].getResource().getName();
							res.add(reso);
						}
					}
				}

				// recursively get all changed files
				IFile[]additionals =findAllCsFiles(rdc[i],moni);
				for (int j =0; j < additionals.length;j++){
					res.add(additionals[j]);
				}
			}
			IFile[] result=new IFile[res.size()];
			result=(IFile[]) res.toArray(result);
			return result;
		}
		return new IFile[0];
	}


	/**
	 * Performs an incremental build
	 * @param monitor The progress monitor
	 * @param dir The working directory
	 * @param affectedFiles Files that were changed
	 * @return true if a problem was found
	 */
	private boolean performIncBuild(IProgressMonitor monitor, final String dir, IFile[] affectedFiles) {
		DebugUtil.DebugPrint("INCREMENTAL BUILD",debugit);
		buildConsoleManager.showConsoleView(buildConsole);
		//Get the relevant files
		String[] affectedTargets = new String[0];		

		BuildMechanismManipulator bFM = BuildDescriptionFactory.getBuildMechanismManipulator(getProject());
		if (bFM.isSourceTargetManipulator() ){
			affectedTargets = bFM.getAllTargetNamesOfFiles(affectedFiles);
		}
		monitor.worked(2);
		final String[] affTgt=affectedTargets ;
		String res = "";
		try {
			IProject project = getProject();
			BuildMechanismManipulator mechanism = BuildDescriptionFactory.getBuildMechanismManipulator(project);
			if (mechanism.isInternalBuilder()){
				final IInternalBuilder builder = mechanism.getInternalBuilder();
				if (builder != null){
					PipedInputStream in = new PipedInputStream();
					final PipedOutputStream out = new PipedOutputStream(in);
					monitor.beginTask("IncrementalBuild", 10);
					final MarcReady marcReady = new MarcReady(false);
					new Thread(
							new Runnable(){
								public void run(){
									//class1.putDataOnOutputStream(out);
									builder.buildInc(dir,affTgt,out);
									try {
										out.close();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									try {
										while (! marcReady.isReady())
											Thread.sleep(500);
									} catch (InterruptedException e) {

										e.printStackTrace();
									}
								}
							}
							).start();

					//builder.buildInc(dir,affectedTargets, monitor,buildConsoleStream);
					StringBuffer contents = new StringBuffer(); 
					//boolean mayBeClosed= false;
					try {
						byte[] bytes=new byte[BUFFLENGTH]; 
						//int current=0;
						int read=-1; 	
						while   (  (  read= in.read(bytes)  )  != -1 ){
							char[] asChar= new char[read];
							for (int i = 0; i < asChar.length;i++){
								asChar[i]=(char) bytes[i];
							}
							contents.append(asChar);
							if (buildConsoleStream!=null){
								buildConsoleStream.print(new String(asChar));
							}
							//Thread.sleep(5);

						} 
						marcReady.setReady(true);
						res=contents.toString();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (mechanism.isBuildCommandRunner()){
				// We build only if a target has to be build
				if (affectedTargets.length!=0) {
					ArrayList<String> retArgs = mechanism.getTargetBuildCommand(affectedTargets);
					String[] args = new String[retArgs.size()];
					args = retArgs.toArray(args);
					res = SimpleCommandRunner.runAndGetOutput(args, dir, monitor,buildConsoleStream);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		monitor.worked(3);
		//buildConsoleStream.println(res);
		monitor.worked(4);
		boolean errorWarningFound = DocumentMarker.markDocument(res,getProject());
		//this.notifyAll();
		return errorWarningFound;
	}


	/**
	 * Performs a full build
	 * @param monitor The progress monitor
	 * @param dir The working directory
	 * @return true if a problem was found
	 */
	private boolean performFullBuild(IProgressMonitor monitor, String dir) {
		DebugUtil.DebugPrint("FULL BUILD",debugit);
		buildConsoleManager.showConsoleView(buildConsole);
		String res = "";
		try {
			monitor.worked(2);
			IProject project = getProject();
			BuildMechanismManipulator mechanism = BuildDescriptionFactory.getBuildMechanismManipulator(project);
			if (mechanism.isInternalBuilder()){
				IInternalBuilder builder = mechanism.getInternalBuilder();
				if (builder != null){
					builder.buildFull(dir, monitor,buildConsoleStream);
				}

			} else	if (mechanism.isBuildCommandRunner()){
				ArrayList<String> tempArgs = mechanism.getFullBuildCommand();
				String[] cmd = new String[tempArgs.size()];
				cmd = tempArgs.toArray(cmd);
				res = SimpleCommandRunner.runAndGetOutput(cmd, dir, monitor,buildConsoleStream);
			}

		} catch (Exception e){
			e.printStackTrace();
		}
		monitor.worked(3);
		monitor.worked(4);
		boolean errorWarningFound = DocumentMarker.markDocument(res,getProject());
		return errorWarningFound;
	}	
	class MarcReady{
		boolean isReady;

		MarcReady(boolean state){
			isReady=state;
		}
		/**
		 * @return the isReady
		 */
		public boolean isReady() {
			return isReady;
		}

		/**
		 * @param isReady the isReady to set
		 */
		public void setReady(boolean isReady) {
			this.isReady = isReady;
		}
	}
}
