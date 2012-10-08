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

package org.emonic.base.codecompletion.jobs;


import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.emonic.base.codecompletion.AssemblyExtractor;
import org.emonic.base.codecompletion.IAssemblyExtractor;

public class AssemblyShallowExtractorJob extends Job{

	/*
	 * Vector assemblyList holds a list of strings for 
	 * assemblies which have to be extracted via MBEL 
	 */	
	private HashSet assemblyDirs;
	
	private HashSet assemblies;
	
	private IProject usedProject;
	boolean isGAC = false;
	
	public AssemblyShallowExtractorJob(String name, IProject project) {
		super(name);
		assemblyDirs = new HashSet();
		if(project != null){
			usedProject = project;
		}else{
			isGAC = true;
		}
	}

	protected IStatus run(IProgressMonitor monitor) {
		try{
			monitor.beginTask("Analyzing assemblies...", 10);
				
			IAssemblyExtractor assEx = new AssemblyExtractor();
			assEx.setProject(usedProject);
			//Get assembly list
			this.assemblyDirs = assEx.getDirs();
			this.assemblies = assEx.addFiles(this.assemblyDirs);			
			monitor.worked(2);					
			assEx.iterateFilesToTrieMapAtStartup(assemblies);
			
			//DEBUG
//			Iterator iter = assemblies.iterator();
//			while(iter.hasNext()){
//				System.out.println("AssemblyShallowExtractor " + ((File)iter.next()).getAbsolutePath() + " " + this.getThread().getName());
//			}
			//DEBUG
			
			monitor.worked(8);			
			return Status.OK_STATUS;
			
		}catch(Exception e){
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}finally{
			monitor.done();
		}		
		
	}	

}
