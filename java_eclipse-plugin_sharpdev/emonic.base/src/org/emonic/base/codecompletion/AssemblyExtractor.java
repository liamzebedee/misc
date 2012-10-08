/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2008 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dominik Ertl - Implementation
 */

package org.emonic.base.codecompletion;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.buildmechanism.Target;
import org.emonic.base.codehierarchy.IAssembly;
import org.emonic.base.codehierarchy.ICompilationUnit;
import org.emonic.base.filemanipulators.ProjectPreferencesManipulator;
import org.emonic.base.framework.FrameworkFactory;
import org.emonic.base.framework.IFrameworkInstall;

public class AssemblyExtractor extends Extractor implements IAssemblyExtractor{
	
	
	
	
	
/*
 	private final String DLL_HIGHER_CASE = ".DLL";
	private final String EXE_LOWER_CASE = ".exe";
	private final String EXE_HIGHER_CASE = ".EXE";
*/
	private boolean isGAC = false;
	private HashSet dllDirs;
	
	public AssemblyExtractor(){
		super(null);
	}
	
	public void setProject(IProject project){
		super.setProject(project);
		if(project == null){
			isGAC = true;
		}		
		dllDirs = new HashSet();
	}
	/* (non-Javadoc)
	 * @see org.emonic.base.codecompletion.IAssemblyExtractor#getDirs()
	 */
	public HashSet getDirs(){
		
		dllDirs = new HashSet();
		//Get  GAC-directories and add them to vector
		//FIXME howto gac get?
		//INFO: open directly 1.0 or 2.0 or 2.1 dirs
		//the gac-directory contains only symbolic links to the
		//different BUT GAC contains e.g. two links for PEAPI.dll => 
		//unknown which one has to be used => thus, choose /lib/mono/1.0 e.g. directly
		
		//info => property is in build-file => howto get property out of buildfile?
		//FOR NANT ONL: <property name='nant.settings.currentframework' value='mono-2.0'/>
		//FIXME: GAC project dependent => each project may have GAC 1.0 or GAC 2.0
		//when code completion is used => check which GAC is used for the current file => allow only
		//keys of files for this GAC => search through trie
		if(isGAC){
			
			String frameworkPath ="";
			IFrameworkInstall install = FrameworkFactory.getDefaultFrameworkInstall();
			if (install != null) {
				frameworkPath = install.getGACLocation();
			}
			dllDirs.add(frameworkPath);
		}else{
			//add project specific dll dirs
			//add all dlldirs
			ProjectPreferencesManipulator ppm = new ProjectPreferencesManipulator(usedProject);
			ArrayList dlllst = ppm.getCodeCompletionDirDll();
			Iterator iter = dlllst.iterator();

			while (iter.hasNext()) {
				//recursive search			
				getDirOfDir(new File((String) iter.next()), dllDirs);
			}
		}
			
		return dllDirs;
	}	
	
	/* (non-Javadoc)
	 * @see org.emonic.base.codecompletion.IAssemblyExtractor#addFiles(java.util.HashSet)
	 */
	public HashSet addFiles(HashSet dirs){
		HashSet fileSet = new HashSet();
		
		Iterator dirsIterator = dirs.iterator();
		while(dirsIterator.hasNext()){
			
			File dllDir = new File((String)dirsIterator.next());
			ArrayList dllnames=getRelevantDllNames(usedProject);
			if(dllDir.isDirectory()){
				
				String[] files = dllDir.list();
				
				for(int i = 0; i < files.length; i++){
					for (int j=0; j< dllnames.size();j++){  
                       String actdll = (String)dllnames.get(j);

						if(files[i].toLowerCase().endsWith(actdll)){		
							fileSet.add(new File(dllDir, files[i]));
						}
					}
				}
			}
		}
		//add project specific assemblies
		if(!isGAC){
			ArrayList dlllst = new ProjectPreferencesManipulator(usedProject).getCodeCompletionFileDll();
			Iterator iter = dlllst.iterator();
			while(iter.hasNext()){
				fileSet.add(new File((String) iter.next()));
			}
		}
		return fileSet;
	}
	
	/* (non-Javadoc)
	 * @see org.emonic.base.codecompletion.IAssemblyExtractor#extractFile(org.emonic.base.codehierarchy.ICompilationUnit, boolean, boolean, java.lang.String)
	 */
	public void extractFile(ICompilationUnit unit, boolean isDeep, boolean isDll, String filePath){
		if(isGAC){
			CCAdministration.getInstance().putToGACMap(filePath, 
						buildTrieForFile(unit, isDeep, filePath));
			CCAdministration.getInstance().putToIsGACMap(filePath, new Boolean(true));
			CCAdministration.getInstance().putToTLMap(null, filePath, new Boolean(isDeep));
		}else{
			CCAdministration.getInstance().addTrieToProject(usedProject, filePath, 
						buildTrieForFile(unit, isDeep, filePath));
			CCAdministration.getInstance().putToIsGACMap(filePath, new Boolean(false));
			CCAdministration.getInstance().putToTLMap(usedProject,filePath, new Boolean(isDeep));
		}		
		
	}
	
	/* (non-Javadoc)
	 * @see org.emonic.base.codecompletion.IAssemblyExtractor#extractFile(java.io.File, boolean)
	 */
	public void extractFile(File actualFile, boolean isDeep) {
		Map[] maps = null;
		maps = new Map[]{Collections.EMPTY_MAP, Collections.EMPTY_MAP};
		try {
			String filePath = actualFile.getAbsolutePath();
			IAssembly asse = AssemblyParserFactory.createAssemblyParser().parseAssembly(
								//new FileInputStream(actualFile.getAbsolutePath()),
								new Path(filePath),
								maps);			
			//add each assembly to map
			//if gac => use gacMap
			if(isGAC){
				CCAdministration.getInstance().putToGACMap(filePath, 
							buildTrieForFile(asse, isDeep, actualFile.getAbsolutePath()));
				CCAdministration.getInstance().putToIsGACMap(filePath, new Boolean(true));
				CCAdministration.getInstance().putToTLMap(null, filePath, new Boolean(isDeep));
			}else{
				CCAdministration.getInstance().addTrieToProject(usedProject, filePath, 
							buildTrieForFile(asse, isDeep, actualFile.getAbsolutePath()));
				CCAdministration.getInstance().putToIsGACMap(filePath, new Boolean(false));
				CCAdministration.getInstance().putToTLMap(usedProject,filePath, new Boolean(isDeep));
			}
			
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList getRelevantDllNames(IProject proj){
		ArrayList result=new ArrayList();
		if (proj != null){
			BuildMechanismManipulator buildMani = BuildDescriptionFactory.getBuildMechanismManipulator(proj);
			Target[] targets= buildMani.getAllTargets();
			for (int i =0; i < targets.length;i++ ){
				Target t=targets[i];
				if ( t instanceof SourceTarget ){
					String[] refs= ((SourceTarget)t).getReferences();
					for (int j=0; j<refs.length;j++){
						String dllname = refs[j].toLowerCase();
						if (! (dllname.endsWith("dll") || dllname.endsWith("exe"))){
							dllname +=".dll";
						}
						result.add(dllname);
					}
				}
			}
		}
		result.add("mscorlib.dll");
		return result;
	}

		
}