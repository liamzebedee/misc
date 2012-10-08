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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.emonic.base.codecompletion.jobs.SourceShallowExtractorJob;

/**
 * This class triggers file/projects create/open and deletions of projects/files to the Codecompletion
 * 
 * It may be used in the future for the building process, as it triggers file saves, file renaming, file 
 * movement etc
 * 
 * mechanism inside Emonic
 * @author dertl
 *
 */
public class CCResourceListener implements IResourceChangeListener{

	private static final int FILE_SAVE = 0;
	private static final int FILE_CREATE = FILE_SAVE + 1;
	private static final int FILE_DELETE = FILE_CREATE + 1;
	private static final int PROJECT_CLOSE = FILE_DELETE + 1;
	private static final int PROJECT_CREATE = PROJECT_CLOSE + 1;
	private static final int PROJECT_DELETE = PROJECT_CREATE + 1;
	private static final int PROJECT_OPEN = PROJECT_DELETE + 1;
	private static final int PROJECTPROPERTIES_CHANGED = PROJECT_OPEN + 1;
	
	
	private IResourceChangeEvent actualEvent = null;
	private IResource resource = null;

	public CCResourceListener(){
		//register CCListener...
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		
	}
	
	public void resourceChanged(IResourceChangeEvent event) {
		this.actualEvent = event;		
		this.resource = actualEvent.getResource();

		//resource (IProjecT) is always null, except the type is close or delete....	
		if(resource != null){
			//it does not matter for code completion if
			//project or files are closed/deleted
			//case IResourceChangeEvent.PRE_DELETE:
			//case IResourceChangeEvent.PRE_CLOSE:
			//project
			if(resource.getType() == IResource.PROJECT){
				projectDeletedOrClosed();		
			}//srcfile
			/*else if(resource.getType() == IResource.FILE){
				fileDeleted();					
			}*/
		}//check for project create/open, file create
		else if(event.getDelta() != null){
			
			//IResourceDeltaParser to parse through Array of array of resources
			HashMap rdResults = parseResourceDelta(event);
			HashSet fileSet = null;
			IProject project = null;
			Iterator iter = null;
			switch(((Integer)rdResults.get("status")).intValue()){
				case FILE_SAVE:
					//handled in CCExecutionListener.java
					System.out.println("DEBUG: FILE_SAVE in CCResourcelistener missing");
					break;
				case FILE_CREATE:
					fileSet = (HashSet)rdResults.get("file");
					iter = fileSet.iterator();
					project =  (IProject)rdResults.get("project");
					while(iter.hasNext()){
						fileCreated((File)iter.next(), project);
					}
					break;
				case PROJECT_CREATE:
				case PROJECT_OPEN:
					//only if project is closed
					//reverse if-condition => it is post-change
					//thus, when the project is opened, the isOpen() condition
					//is checked afterward
					if(((IProject)rdResults.get("project")).isOpen()){
						//System.out.println("Flags for IResourceDelta " + resDelta[0].getKind());
						projectCreatedOrOpened((IProject)rdResults.get("project"));
					}
					break;	
				case PROJECTPROPERTIES_CHANGED:
					projectPropertiesChanged();					
					break;
				case FILE_DELETE:
					//do not delete if project is deleted itself
					//this would lead to a double-delete of files...not possible
					int projFlags = getProjectFlags(event.getDelta());
					if(projFlags != 0x4000){
						fileSet = (HashSet)rdResults.get("file");
						iter = fileSet.iterator();
						project = (IProject)rdResults.get("project");
						while(iter.hasNext()){
							fileDeleted((File)iter.next(), project);		
						}
					}
					break;
				default:
					//System.out.println("DEBUG: DEFAULT level reached. Missing Implementation in CCResourcelistener.");
					break;				
			}
			

						
		}
	}
	
	private int getProjectFlags(IResourceDelta delta) {
		IResourceDelta[] resDeltaChildren = delta.getAffectedChildren();
		return resDeltaChildren[0].getFlags();
	}

	private HashMap parseResourceDelta(IResourceChangeEvent event) {
		HashMap retMap = new HashMap();
		//add dummy value
		retMap.put("status", new Integer(0xFFFF));
		IResourceDelta resDelta = event.getDelta();
		IResourceDelta[] resDeltaChildren = resDelta.getAffectedChildren();
		int type = event.getType();
		
		if(hasChildren(resDeltaChildren) && 
		   type == IResourceChangeEvent.POST_CHANGE){
			
			//assoarray contains values for "exists", "file" and "flags"
			IProject project = getProject(resDeltaChildren);
			
			//in case project delete is done previously
			if(!project.exists()){
				return retMap;
			}
				
			String projectPath = project.getLocation().toPortableString();
			HashMap assoArray = recursiveSearchIfFilesInDelta(resDeltaChildren, projectPath+"/");
									
			int flags = 0;
			if (assoArray.containsKey("flags")){	
				flags=((Integer)assoArray.get("flags")).intValue();
			}
			
			//check if file was found...
			if(((Boolean)assoArray.get("exists")).booleanValue()){
				if((flags & IResourceDelta.ADDED) == IResourceDelta.ADDED){
					System.out.println("DEBUG New file(s) added to CCAdministration maps");
					retMap.put("status", new Integer(FILE_CREATE));
					retMap.put("project", project);
					retMap.put("file", assoArray.get("file"));
				}else if((flags & IResourceDelta.CHANGED) == IResourceDelta.CHANGED){
					//System.out.println("DEBUG File or project changed - missing implementation");				
				}else if((flags & IResourceDelta.REMOVED) == IResourceDelta.REMOVED){
					System.out.println("DEBUG File(s) removed from CCAdministration maps");
					retMap.put("status", new Integer(FILE_DELETE));
					retMap.put("project", project);
					retMap.put("file", assoArray.get("file"));								
				}	
			}else if((flags & IResourceDelta.OPEN) == IResourceDelta.OPEN){
				System.out.println("DEBUG Project opened/created in CCAdministration maps");
				retMap.put("status", new Integer(PROJECT_OPEN));
				retMap.put("project", project);
			}else{
				//System.out.println("DEBUG Some other Project stuff => return without extractfiles");
			}
		}
		
		return retMap;
	}

	private HashMap recursiveSearchIfFilesInDelta(IResourceDelta[] resDeltaChildren, String projectPath) {
		HashMap retMap = new HashMap(3);
		retMap.put("exists", new Boolean(false));
		HashSet fileSet = new HashSet(3);
		retMap.put("file", fileSet); 
		
		//search through complete ResourceDelta and add all c# files
		for(int i=0; i<resDeltaChildren.length;i++){
			//is C#-file
			if(resDeltaChildren[i].getFullPath().toString().endsWith(".CS") || 
				resDeltaChildren[i].getFullPath().toString().endsWith(".cs")){
					//add values of first cs file
					String path = resDeltaChildren[i].getProjectRelativePath().toPortableString();					
					retMap.put("exists", new Boolean(true));
					((HashSet)retMap.get("file")).add(new File(projectPath+path));
					retMap.put("flags", new Integer(resDeltaChildren[i].getKind()));
			}else if(resDeltaChildren[i].getAffectedChildren().length > 0) {
				retMap = recursiveSearchIfFilesInDelta(resDeltaChildren[i].getAffectedChildren(), projectPath);			
			}
		}
		
		return retMap;
	}


	private IProject getProject(IResourceDelta[] resDelta) {
		//FIXME hardcoded for first element in resDeltaChildren... ok?
		return	(IProject)resDelta[0].getResource();		
	}

	private boolean hasChildren(IResourceDelta[] resDelta) {
		//search through all elements of IResourceDelta and check if not null => != build.xml
		for(int i=0; i<resDelta.length;i++){
			if(resDelta[i].getAffectedChildren().length > 0){
				//System.out.println("DEBUG " +resDelta.getClass().toString());
				return true;						
			}
		}
			
		return false;
	}

	private void projectPropertiesChanged(){
		//FIXME
		System.out.println("DEBUG projectpropertieschanged not implemented");
	}
	
	private void fileCreated(File file, IProject project){
		SourceExtractor srcEx = new SourceExtractor(project);
		srcEx.extractFile(file, false);		
	}

	private void projectCreatedOrOpened(IProject project){
		//add project to cc
		CCAdministration.getInstance().addProject(project);

		//read in srces from project
		SourceShallowExtractorJob srcProjExtractJob = 
				new SourceShallowExtractorJob("Extract project src...", project);
		srcProjExtractJob.schedule();
	}

	private void projectDeletedOrClosed(){
		
		IProject project = resource.getProject();
		String path = resource.getLocation().toPortableString();

		//delete files from typemap according to projectfiles
		String filename = "";
		Iterator iter = CCAdministration.getInstance().getProjectCCMap(project)
										.keySet().iterator();
		
		//step through every fileentry and delete it => filename is the key of the hashmap
		while(iter.hasNext()){
			filename = (String)iter.next();			
			CCAdministration.getInstance().removeFromMetatypes(filename);
		}
						
		//delete project from map
		CCAdministration.getInstance().removeFromProjectCCMap(path);
						 		
		//delete distinct typeMap for project!
		CCAdministration.getInstance().removeFromProjectTLMap(path);		 					
	}
	
	
	private void fileDeleted(File file, IProject project){
		String filename = file.getAbsolutePath();
		String projectPath = project.getLocation().toPortableString();
		//delete files from typemap according to file
		CCAdministration.getInstance().removeFromMetatypes(filename);
		
		//remove from projectCCMap
		CCAdministration.getInstance().removeFileFromProjectCCMap(projectPath, filename);

		//remove entries from distinct typeMap of project!
		 CCAdministration.getInstance().removeFileFromProjectTLMap(projectPath, filename);		
	}
	


}
