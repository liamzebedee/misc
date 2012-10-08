/****************************************************************************
 * Copyright (c) 2001, 2009 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors: B.Brem 
 * **************************************************************************/
package org.emonic.base.buildmechanism;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;

/**
 * This class wraps the operations You can do with a IBuildMechanismDescriptor and the interfaces
 * it normally implements: 
 * IBuildSourceTargetManipulator (Store source files in targets)
 * IBuildFrameworkManipulator (Allow to switch between frameworks)
 * @author brem
 *
 */
public class BuildMechanismManipulator {

	private IProject project;
	private IBuildMechanismDescriptor buildMechanism;

	public BuildMechanismManipulator(IBuildMechanismDescriptor buildMechanism, IProject project) {
		this.project=project;
		this.buildMechanism=buildMechanism;
	}
	
	
	public BuildMechanismManipulator(IBuildMechanismDescriptor buildMechanism) {
		this.buildMechanism=buildMechanism;
	}

	public IBuildMechanismDescriptor getBuildMechanism() {
		return buildMechanism;
	}

	public void setBuildMechanism(IBuildMechanismDescriptor buildMechanism) {
		this.buildMechanism = buildMechanism;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	/**
	 * Is the build mechanism a sourcetarget-manipulator? I.e. can it store the source in targets?
	 * @return true if IBuildSourceTargetManipulator is implemented
	 */
	public boolean isSourceTargetManipulator() {
		
		return (IBuildSourceTargetManipulator.class.isAssignableFrom(buildMechanism.getClass()));
	}
	
	/**
	 * Same like getAllTargetNamesOfFile(IFile fl), but for a array of affected files
	 * @param fl
	 * @return
	 */
	public String[] getAllTargetNamesOfFiles(IFile[] fl){
		ArrayList res = new ArrayList();
		for (int i =0; i < fl.length;i++){
		   String[] targetNames = getAllTargetNamesOfFile(fl[i]);
		   for (int j=0;j<targetNames.length;j++){
			   boolean isAlreadyThere=false;
			   for (int k =0; k <res.size();k++){
				   if (res.get(k).equals(targetNames[j])){
					   isAlreadyThere=true;
				   }
			   }
			   if (! isAlreadyThere){
				   res.add(targetNames[j]);
			   }
		   }
		}
		String[] result = new String[res.size()];
		result=(String[]) res.toArray(result);
		return result;
	}
	
	/**
	 * If the build mechanism is a SourceTargetManipulator, get all target names of the target which 
	 * use a specific language file
	 * @param fileName
	 * @return
	 */
	public String[] getAllTargetNamesOfFile(IFile fl) {
		if (isSourceTargetManipulator()){
			Target[] tgts = getAllTargets();
			ArrayList resultNames = new ArrayList();
			for (int i =0; i < tgts.length;i++){
				if (tgts[i] instanceof SourceTarget){
					IFile[] fls= (((SourceTarget)tgts[i]).getSources());
					boolean alreadyAdded=false;
					for (int j =0; j < fls.length;j++){
						
					  if (fls[j].equals(fl) && ! alreadyAdded)	{
						  resultNames.add(((SourceTarget)tgts[i]).getName());
						  alreadyAdded=true;
					   }
					}
				}
			}
			String[] res = new String[resultNames.size()];
			res=(String[]) resultNames.toArray(res);
			return res;
		}
		return new String[0];
	}



	/**
	 * Is the build mechanusm a framework manipulator? I.e. can the framework be choosen? 
	 * I.e. is IBuildFrameworkManipulator implemented?
	 */
	public boolean isFrameworkManipulator() {
		return (IBuildFrameworkManipulator.class.isAssignableFrom(buildMechanism.getClass()));
	}

	public String getTargetFrameworkType() {
		if (IBuildFrameworkManipulator.class.isAssignableFrom(buildMechanism.getClass())){
			return ((IBuildFrameworkManipulator)buildMechanism).getTargetFrameworkType();
		}
		return IBuildMechanismDescriptor.FRAMEWORK_NA;
	}



	/**
	 * Get the release of the framework if it can be determined
	 * Get IBuildMechanismDescriptor.FRAMEWORK_NA if not
	 * @return
	 */
	public String getTargetFrameworkRelease() {
		if (IBuildFrameworkManipulator.class.isAssignableFrom(buildMechanism.getClass())){
			return ((IBuildFrameworkManipulator)buildMechanism).getTargetFrameworkRelease();
		}
		return IBuildMechanismDescriptor.FRAMEWORK_NA;
	}


	/**
	 * Supports the mechanism file layout? I.e. is it able to store the location of the source and the
	 * build directory? Returns true if the build mechanism implements IBuildFileLayoutManipulator
	 * @return
	 */
	public boolean isFileLayoutManipulator() {
		return IBuildFileLayoutManipulator.class.isAssignableFrom(buildMechanism.getClass());
		
	}

	/**
	 * Sets the src dir if the mechanism supports FileLayout (i.e. implements IBuildFileLayoutManipulator,
	 * isFileLayoutManipulator()==true). Does nothing if not.
	 * @param dir
	 */
	public void setSrcDir(String dir) {
		if (isFileLayoutManipulator()){
			 ((IBuildFileLayoutManipulator)buildMechanism).setSrcDir(dir);
		}
		
	}
	
	/**
	 * Sets the bin dir if the mechanism supports FileLayout (i.e. implements IBuildFileLayoutManipulator,
	 * isFileLayoutManipulator()==true). Does nothing if not.
	 * @param dir
	 */
	public void setBinDir(String dir) {
		if (isFileLayoutManipulator()){
			 ((IBuildFileLayoutManipulator)buildMechanism).setBinDir(dir);
		}
		
	}
	
	/**
	 * Saves the build file mechanism is buildfile-based, i.e. implements  IBuildFileManipulator
	 * Does nothing if  the mechanism is _not_ buildfile-based.
	 * Since it is expected that the build file mechanism does not save its changes itself 
	 * You have to call this function after every change You want to make permanent.
	 * @return
	 */
	public void save() {
		if (isBuildFileManipulator()){
			((IBuildFileManipulator)buildMechanism).save();
		}
		
	}

	/**
	 * Returns true if the mechanism is buildfile-based, i.e. implements  IBuildFileManipulator
	 * @return
	 */
	public boolean isBuildFileManipulator() {
		return IBuildFileManipulator.class.isAssignableFrom(buildMechanism.getClass());
	}

	/**
	 * Get the src dir if the mechanism supports FileLayout (i.e. implements IBuildFileLayoutManipulator,
	 * isFileLayoutManipulator()==true). Returns a empty string else.
	 * @return
	 */
	public String getSrcDir() {
		if (isFileLayoutManipulator()){
			 return ((IBuildFileLayoutManipulator)buildMechanism).getSrcDir();
		}
		return "";
	}

	/**
	 * Get the bin dir if the mechanism supports FileLayout (i.e. implements IBuildFileLayoutManipulator,
	 * isFileLayoutManipulator()==true). Returns a empty string else.
	 * @return
	 */
	public String getBinDir() {
		if (isFileLayoutManipulator()){
			 return ((IBuildFileLayoutManipulator)buildMechanism).getBinDir();
		}
		return "";
	}

	/**
	 * If the mechanism supports it, set the target framework
	 * Do nothing if not
	 * @param targetFramework
	 * @param release
	 */
	public void setTargetFramework(String targetFramework, String release) {
		if (isFrameworkManipulator()){
			((IBuildFrameworkManipulator)buildMechanism).setTargetFramework(targetFramework, release);
		}
		
	}

	/**
	 * Get a named source target; if it is expected any other than a empty one
	 * the underlaying build mechanism must support it (check with isSourceTargetManipulator!)
	 * @param targetName
	 * @return
	 */
	public Target getTarget(String targetName) {
		if (isSourceTargetManipulator()){
			IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) this.buildMechanism;
			Target[] allTgts=btm.getAllTargets();
			for (int i =0; i<allTgts.length;i++){
				if (allTgts[i].getName().equals(targetName)){
					return allTgts[i];
				}
			}

		}
		return null;
	}

	/**
	 * Write the target back to the build mechanism Take care: That does mean
	 * the build mechanism is changed, not that a build file is saved! If You
	 * want to make the change permanent, You have to call safe() Also, it does
	 * _not_ create a new target. If the target does not exists, nothing
	 * happens.
	 * 
	 * @param target
	 */
	public void rewriteTarget(Target target) {
		// Get the original sourceTarget
		if (isSourceTargetManipulator()) {
			IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) this.buildMechanism;
			String targetName = target.getName();
			if (hasTarget(targetName)) {
				Target old = getTarget(targetName);
				// For all kinds of targets set the name and the dependencies
				
				
				String[] deps = target.getDependencies();
				String[] oldDeps = old.getDependencies();
				boolean differs = false;
				if (oldDeps == null || deps == null
						|| (oldDeps.length != deps.length)) {
					differs = true;
				} else {
					for (int i = 0; i < oldDeps.length && !differs; i++) {
						if (oldDeps[i] != deps[i])
							differs = true;
					}
				}
				if (differs) {
					btm.setTargetDependencies(target);
				}
				// Is the old and the new target a source target? => Also sync
				// source vals
				if (SourceTarget.class.isAssignableFrom(old.getClass())
						&& SourceTarget.class.isAssignableFrom(target
								.getClass())) {
					// Same sources?
					IFile[] oldSrc = ((SourceTarget) old).getSources();
					IFile[] newSrc = ((SourceTarget) target).getSources();
					boolean hasToSetSources = false;
					if (oldSrc.length != newSrc.length) {
						hasToSetSources = true;
					} else {
						for (int i = 0; i < oldSrc.length; i++) {
							if (!oldSrc[i].equals(newSrc[i])) {
								hasToSetSources = true;
							}
						}
					}
					if (hasToSetSources) {
						btm.setSourcesToLanguageTarget((SourceTarget)target);
					}
					// Type of the target?
					if (!((SourceTarget) old).getType().equals(
							((SourceTarget) target).getType())) {
						btm.setLanguageTargetType((SourceTarget) target);
					}

					// Change in the definitions?
					String[] oldDefs = ((SourceTarget) old).getDefinitions();
					String[] newDefs = ((SourceTarget) target).getDefinitions();
					boolean hasToSetDefs = false;
					if (oldDefs.length != newDefs.length) {
						hasToSetDefs = true;
					} else {
						for (int i = 0; i < oldDefs.length; i++) {
							if (!oldDefs[i].equals(newDefs[i])) {
								hasToSetDefs = true;
							}
						}
					}
					if (hasToSetDefs) {
						btm.setDefinitions((SourceTarget) target);
					}
					// Change in the references?
					String[] oldRefs = ((SourceTarget) old).getReferences();
					String[] newRefs = ((SourceTarget) target).getReferences();
					boolean hasToSetRefs = false;
					if (oldRefs.length != newRefs.length) {
						hasToSetRefs = true;
					} else {
						for (int i = 0; i < oldRefs.length; i++) {
							if (!oldRefs[i].equals(newRefs[i])) {
								hasToSetRefs = true;
							}
						}
					}
					if (hasToSetRefs) {
						btm.setReferences((SourceTarget) target);
					}
					// Change in debugging output?
					if (((SourceTarget) old).isDebuggingOutput() != ((SourceTarget) target)
							.isDebuggingOutput()) {
						btm.setDebuggingOutput((SourceTarget) target);
					}
					// Change in optimization?
					if (((SourceTarget) old).isOptimization() != ((SourceTarget) target)
							.isOptimization()) {
						btm.setOptimization((SourceTarget) target);
					}
					// Warning level
					if (((SourceTarget) old).getWarningLevel() != ((SourceTarget) target)
							.getWarningLevel()) {
						btm.setWarningLevel((SourceTarget) target);
					}
				}
			}
			// For all targets: Change the name of the target
			// For simplicity that is the last operation:
			// So all former operations can be done via the file name
			String newName=target.getNewName();
			if (newName != "" && newName != targetName){
				renameTarget(target,true);
				
			}
		}

	}

	private void renameTarget(Target target, boolean renameDependencies) {
		if (isSourceTargetManipulator()){
			String targetName=target.getName();
			String newName=target.getNewName();
			((IBuildSourceTargetManipulator)buildMechanism).renameTarget(target);
			if (renameDependencies){
				Target[] allTargets = getAllTargets();
				for (int i =0; i < allTargets.length;i++){
					Target actualT= allTargets[i];
					String[] deps = actualT.getDependencies();
					boolean hasDep=false;
					for (int j =0; j < deps.length;j++){
						if (deps[j].equals(targetName)){
							deps[j]=newName;
							hasDep=true;
						}
					}
					if (hasDep){
						actualT.dependencies=deps;
						rewriteTarget(actualT);
					}
				}
			}
		}
	}


	/**
	 * Add a new target to the build mechanism
	 * @param target The target to write
	 * @param ReferenceWithDefault Shall the default depend on this target?
	 */
	public void writeNewTargetInTree(Target target, boolean ReferenceWithDefault) {
		if (isSourceTargetManipulator()) {
			IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) this.buildMechanism;
			
			btm.addTarget(target);
			btm.setTargetDependencies(target);
			
			
			if (target instanceof SourceTarget){
				
				//Auto-calculate theartefactname if not set explcitly
				if (((SourceTarget)target).getArtefact() == null){
					String artefact = getBinDir() + File.separator + target.getName();
					String type = ((SourceTarget)target).getType();
					if (type.equalsIgnoreCase("exe") || type.equalsIgnoreCase("winexe")){
						artefact = artefact + ".exe";
					} else {
						artefact = artefact + ".dll";
					}
					Path path = new Path(artefact);
					IProject proj= getBuildFile().getProject();
					IFile fl = proj.getFile(path);
					((SourceTarget)target).setArtefact(fl);
				}
				btm.adaptLanguageTarget(target);
				btm.setDefinitions((SourceTarget)target);
				btm.setReferences((SourceTarget)target);
				btm.setDebuggingOutput((SourceTarget)target);
				btm.setSourcesToLanguageTarget((SourceTarget)target);
				btm.setOptimization((SourceTarget)target);
				btm.setWarningLevel((SourceTarget)target);
			}
			if (ReferenceWithDefault){
				String targetName = target.getName();
			// Add this target to the dependencie odf the default target
				Target defaultTarget=getTarget(btm.getDefaultTargetName());
				if (defaultTarget!=null){
					String[] deps=defaultTarget.getDependencies();
					String[] newDeps=new String[deps.length+1];
					boolean alreadyIn=false;
					for (int i =0; i < deps.length;i++){
						newDeps[i]=deps[i];
						if (deps[i].equals(targetName)){
							alreadyIn=true;
						}
					}
					if (! alreadyIn){
						// Add as last element
						// Zero-based => length-1
						newDeps[newDeps.length-1]=targetName;
						defaultTarget.setDependencies(newDeps);
						rewriteTarget(defaultTarget);
					}
					
				}
			}

//			
		}
	}
	
	
	
	private boolean hasTarget(String targetName) {
		boolean hasTarget=false;
		if (isSourceTargetManipulator()){
			IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) this.buildMechanism;
			Target[] allTargets=btm.getAllTargets();
		
			for (int i =0; i < allTargets.length&&!hasTarget;i++){
				if(allTargets[i].getName().equals(targetName)){
					hasTarget=true;
				}
			}
		}
		return hasTarget;
	}


	/**
	 * Check the support of a specific language.
	 * @param language
	 * @return the support of the language in case of a IBuildSourceTargetManipulator, false else
	 */
	public boolean supportsLanguage(String language) {
		if (isSourceTargetManipulator()){
			IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) this.buildMechanism;
			//return btm.supportsLanguage(language);
			String[] langs = btm.getSupportedLanguages();
			for (int i =0; i < langs.length;i++){
				if (langs[i].equals(language)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the build file
	 * @return the build file if isBuildFileManipulator, null else
	 */
	public IFile getBuildFile() {
		if (isBuildFileManipulator()){
			IBuildFileManipulator bfm = (IBuildFileManipulator) buildMechanism;
			return bfm.getBuildFile();
		}
		return null;
	}

	public String suggestFileName() {
		if (isBuildFileManipulator()){
			IBuildFileManipulator bfm = (IBuildFileManipulator) buildMechanism;
			return bfm.suggestFileName();
		}
		return "";
	}

	
	/**
	 * Get the names of all source targets of a given language if isSourceTargetManipulator() 
	 * @param language
	 * @return all targets if isSourceTargetManipulator(), a empty array if not
	 */
	public String[] getAllTargetNamesOfLanguage(String language) {
		if (isSourceTargetManipulator()){
			Target[] tgts = getAllTargets();
			ArrayList resultNames = new ArrayList();
			for (int i =0; i < tgts.length;i++){
				if (tgts[i] instanceof SourceTarget){
					if (((SourceTarget)tgts[i]).getLanguage().equals(language)){
						resultNames.add(((SourceTarget)tgts[i]).getName());
					}
				}
			}
			String[] res = new String[resultNames.size()];
			res=(String[]) resultNames.toArray(res);
			return res;
		}
		return new String[0];
	}

	/**
	 * Stores the build mechanism the project name? I.e. is IBuildProjectManipulator implemented?
	 * @return
	 */
	public boolean isBuildProjectManipulator() {
		return IBuildProjectManipulator.class.isAssignableFrom( buildMechanism.getClass());
	}

	/**
	 * Set the project name is isBuildProjectManipulator. Do nothing if not.
	 * @param name
	 */
	public void setBuildProjectName(String name) {
		if (isBuildProjectManipulator()){
			((IBuildProjectManipulator) buildMechanism).setProject(name);
		}
		
	}

	/**
	 * Get the supported languages of a SourceTargetManipulator. Get a empty array else.
	 * @return
	 */
	public String[] getSupportedLanguages() {
		if (isSourceTargetManipulator()){
		  return  ((IBuildSourceTargetManipulator)buildMechanism).getSupportedLanguages();
		}
		return new String[0];
	}

	/**
	 * Can the build performed by running a command?
	 * @return true if IBuildCommandRunner is implemented
	 */
	public boolean isBuildCommandRunner() {
		
		return IBuildCommandRunner.class.isAssignableFrom( buildMechanism.getClass());
	}

	/**
	 * If the mechanism is a BuildCommandRunner, return the command
	 * If not return a empty string
	 * @param affectedTargets
	 * @return
	 */
	public ArrayList<String> getTargetBuildCommand(String[] affectedTargets) {
		if (isBuildCommandRunner()){
			return ((IBuildCommandRunner)buildMechanism).getTargetBuildCommand(affectedTargets);
		}
		return null;
	}
	
	/**
	 * If the mechanism is a BuildCommandRunner, return the command
	 * If not return a empty string
	 * @return
	 */
	public ArrayList<String> getFullBuildCommand() {
		if (isBuildCommandRunner()){
			return ((IBuildCommandRunner)buildMechanism).getFullBuildCommand();
		}
		return null;
	}
	
	public boolean isInternalBuilder(){
		return (IInternalBuilderFactory.class.isAssignableFrom( buildMechanism.getClass()));
	}

	public IInternalBuilder getInternalBuilder() {
		if (isInternalBuilder()){
			return ((IInternalBuilderFactory)buildMechanism).getInternalBuilder();
		}
		return null;
	}


	public String[] getSupportedFrameworks() {
		if (this.isFrameworkManipulator()){
			return ((IBuildFrameworkManipulator)buildMechanism).getSupportedFrameworks();
		}
		return new String[0];
	}


	public String[] getSupportedReleases(String framework) {
		if (this.isFrameworkManipulator()){
			return ((IBuildFrameworkManipulator)buildMechanism).getSupportedReleases(framework);
		}
		return new String[0];
	}

	public boolean isDocumentSyncronisator(){
		return (IBuildDocumentSyncronisator.class.isAssignableFrom( buildMechanism.getClass()));
	}
	
	public IDocument getAsDocument() {
		if (isDocumentSyncronisator()){
			return ((IBuildDocumentSyncronisator)buildMechanism).getAsDocument();
		}
		return null;
	}
	
	public void initFromDocument(IDocument doc){
		if (isDocumentSyncronisator()){
			((IBuildDocumentSyncronisator)buildMechanism).initFromDocument(doc);
		}
		
	}


	public Target[] getAllTargets() {
		if (isSourceTargetManipulator()){
			IBuildSourceTargetManipulator btm = (IBuildSourceTargetManipulator) this.buildMechanism;
			Target[] allTgts=btm.getAllTargets();
			return allTgts;
//			String [] targetNames= ((IBuildSourceTargetManipulator)buildMechanism).getAllTargets();
//			Target[] result = new Target[targetNames.length];
//			for (int i = 0; i< targetNames.length;i++ ){
//				result[i] = getTarget(targetNames[i]);
//			}
//			return result;
		}
		return new Target[0];
	}


	public String getBuildProjectName() {
		if (isBuildProjectManipulator()){
			return ((IBuildProjectManipulator) buildMechanism).getProject();
		}
		return this.getProject().getName();
	}


	public void deleteTarget(Target target) {
		if (isSourceTargetManipulator()){
			if (target != null){
				String targetName=target.getName();
			    if (hasTarget(targetName)){
			    	((IBuildSourceTargetManipulator)buildMechanism).deleteTarget(target);
			    	// If this target is referenced by other targets, delete this reference
                    Target[] tgts= getAllTargets();
                    for (int i =0; i < tgts.length;i++){
                    	String[] dps=tgts[i].dependencies;
                    	int removeCnt=0;
                    	for (int j =0; j < dps.length;j++){
                    		if (dps[j].equals(targetName)){
                    			removeCnt++;
                    		}
                    	}
                    	if (removeCnt>0){
                    		String[] newDps=new String[dps.length-removeCnt];
                    		int newcnt=0;
                    		for (int j =0; j < dps.length;j++){
                        		if (! dps[j].equals(targetName)){
                        			newDps[newcnt]=dps[j];
                        			newcnt++;
                        		}
                        	}
                    		tgts[i].setDependencies(newDps);
                    		rewriteTarget(tgts[i]);
                    	}
                    }
			    }
			}
		}
	}


	public String getDefaultTargetName() {
		if (isSourceTargetManipulator())
			    return ((IBuildSourceTargetManipulator)buildMechanism).getDefaultTargetName();
		return "";
	}
	
	

}
