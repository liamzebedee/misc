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
import org.emonic.base.codecompletion.SourceExtractor;


/**
 * job which is triggered for extracting shallow information
 * out of src files
 * @author dertl
 *
 */
public class SourceShallowExtractorJob extends Job {

	private HashSet srcDirs;
	
	private HashSet src;
	
	private IProject usedProject;
	
	public SourceShallowExtractorJob(String name, IProject project) {
		super(name);
		srcDirs = new HashSet();		
		usedProject = project;
	}
	
	protected IStatus run(IProgressMonitor monitor) {
		try{
			monitor.beginTask("Analyzing source files...", 10);
					
			SourceExtractor srcEx = new SourceExtractor(usedProject);			
			//Get src List
			this.srcDirs = srcEx.getDirs();
			this.src = srcEx.addFiles(this.srcDirs);			
			monitor.worked(2);					
			srcEx.iterateFilesToTrieMapAtStartup(src);
			
			//DEBUG
//			Iterator iter = src.iterator();
//			while(iter.hasNext()){
//				System.out.println("SourceShallowExtractor " + ((File)iter.next()).getAbsolutePath()  + " " + this.getThread().getName());
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
