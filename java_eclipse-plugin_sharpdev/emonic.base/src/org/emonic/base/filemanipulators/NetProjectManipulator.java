package org.emonic.base.filemanipulators;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.emonic.base.Constants;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.Target;

public class NetProjectManipulator {

	// Constructor for manipulating _old_ projects
	public NetProjectManipulator(IProject projectHandle){
		this.project=projectHandle;
		this.monitor=new NullProgressMonitor();
		try {
			description=project.getDescription();
		} catch (CoreException e) {
			// OK if the project does not exist
			// e.printStackTrace();
		}
	}


	//Constructor for creating _new_ Projects
	public NetProjectManipulator( IProjectDescription description, IProject projectHandle, IProgressMonitor monitor){
		this.setMonitor(monitor);
		this.setDescription(description);
		this.project=projectHandle;
	}

	private IProjectDescription description;
	private IProject project;
	private IProgressMonitor monitor;
	private String srcDir;
	private String binDir;

	private String targetFramework;
	private String release;



	public void createProject(){
		monitor.beginTask("", 2000); //$NON-NLS-1$

		try {
			// Create a empty description if description = null
			if ( description == null){
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				description =
						workspace.newProjectDescription(project.getName());
			}
			if (!project.exists()){
				project.create(
						description,
						new SubProgressMonitor(monitor, 400));
			}
			project.open(new SubProgressMonitor(monitor, 400));
			createNature(new SubProgressMonitor(monitor, 400));
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param submonitor 
	 * 
	 */
	private void createNature(SubProgressMonitor submonitor) throws CoreException {
		IProjectDescription projdescription = description;
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = Constants.EmonicNatureID;
		projdescription.setNatureIds(newNatures);
		project.setDescription(projdescription, IResource.FORCE, submonitor);
	}



	private void createDir(String dir, IProgressMonitor submonitor){
		IFolder folder = project.getFolder (dir);
		if (! folder.exists()){
			try {
				folder.create(true,true,null);
			} catch (CoreException e){
				e.printStackTrace();
			}
		}
	}

	private void setDescription(IProjectDescription description) {
		this.description=description;
	}


	public void setMonitor(IProgressMonitor monitor) {
		this.monitor=monitor;

	}

	public void setSrcDir(String dir) {		
		BuildMechanismManipulator buildFM = BuildDescriptionFactory.getBuildMechanismManipulator(this.project);
		if (buildFM.isFileLayoutManipulator()) {			
			this.srcDir=dir;
			buildFM.setSrcDir(dir);
			if (dir != null && !dir.equals(""))
				createDir(srcDir,monitor);
		}
		buildFM.save();
	}

	public void setBinDir(String dir) {
		BuildMechanismManipulator buildFM = BuildDescriptionFactory.getBuildMechanismManipulator(this.project);
		this.binDir=dir;
		if (buildFM.isFileLayoutManipulator()){
			buildFM.setBinDir(dir);
			//createDir(new Path(binDir + File.separatorChar), new SubProgressMonitor(monitor, 100));
			if (dir != null && !dir.equals(""))
				createDir(binDir, monitor);
			buildFM.save();
		}

	}

	public String getSrcDir(){
		BuildMechanismManipulator buildFM = BuildDescriptionFactory.getBuildMechanismManipulator(this.project);
		if (buildFM.isFileLayoutManipulator()){
			srcDir = buildFM.getSrcDir();
		}
		return this.srcDir;
	}

	public String getBinDir(){
		BuildMechanismManipulator buildFM = BuildDescriptionFactory.getBuildMechanismManipulator(this.project);
		if (buildFM.isFileLayoutManipulator()){
			binDir = buildFM.getBinDir();
		}
		return this.binDir;
	}

	public void setCopyright(String copyright) {
		//this.copyRight=copyright;
		ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
		mani.setCopyright(copyright);
		mani.write();
	}

	public void setNamespace(String namespace) {
		ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
		mani.setNamespace(namespace);
		mani.write();
	}

	public void setBuildMechanism(String buildmechanism) {
		ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
		mani.setBuildMechanism(buildmechanism);
		mani.write();
	}

	public void setBuildFile(String buildFile) {

		ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
		mani.setBuildFileName(buildFile);
		if (project != null ){
			IFile fl = project.getFile(buildFile);
			if (!fl.exists() ){
				createBuldFile();
			}
		}
	}


	private void createBuldFile() {
		BuildMechanismManipulator buildFM = BuildDescriptionFactory.getBuildMechanismManipulator(project  );
		if (buildFM != null &&  buildFM.isBuildProjectManipulator()){
			//IBuildProjectManipulator bpm = (IBuildProjectManipulator) buildFM;
			buildFM.setBuildProjectName(description.getName());
		}
		if (buildFM.isFileLayoutManipulator()){

			buildFM.setSrcDir(srcDir);
			buildFM.setBinDir(binDir);
			if (binDir != null && !binDir.equals("")) { //$NON-NLS-1$
				createDir(binDir, new SubProgressMonitor(monitor, 100));
			}
			if (srcDir != null && !srcDir.equals("")) { //$NON-NLS-1$
				createDir(srcDir , new SubProgressMonitor(monitor, 100));
			}
		}
		if (targetFramework != null && buildFM.isFrameworkManipulator()){
			buildFM.setTargetFramework(targetFramework,release);
		}

		if (buildFM.isSourceTargetManipulator()){
			Target defaultTgt=new Target();

			defaultTgt.setName(buildFM.getDefaultTargetName());
			buildFM.writeNewTargetInTree(defaultTgt, false);
		}
		buildFM.save();
	}



	public void setTargetFramework(String buildTargetFramework, String release) {
		this.targetFramework=buildTargetFramework;
		this.release=release;
		BuildMechanismManipulator buildFM = BuildDescriptionFactory.getBuildMechanismManipulator(project );
		if (buildFM != null  && buildFM.isFrameworkManipulator()){
			buildFM.setTargetFramework(buildTargetFramework,release);
		}
		buildFM.save();
	}


	public String getCopyRight() {
		ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
		return mani.getCopyright();
	}

	public String getNamespace() {
		ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
		return mani.getNamespace();
	}

	public String getBuildMechanism() {
		ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
		return mani.getBuildMechanism();
	}

	public String getBuildFile() {
		ProjectPreferencesManipulator mani = new ProjectPreferencesManipulator(project);
		return mani.getBuildFileName();
	}


}
