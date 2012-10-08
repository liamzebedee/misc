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

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.emonic.base.codecompletion.SourceExtractor;

public class SourceDeepExtractorJob extends Job{

	private String filePath = null; 
	private IProject usedProject = null;
	
	public SourceDeepExtractorJob(String name, IProject project) {
		super(name);
		filePath = name;
		usedProject = project;
	}

	protected IStatus run(IProgressMonitor monitor) {
		try{
			monitor.beginTask("Analyzing src " + filePath, 3);
					
			SourceExtractor srcEx = new SourceExtractor(usedProject);			
			monitor.worked(1);
			srcEx.extractFile(new File(filePath), true);		
			System.out.println("SourceDeepExtractor " + filePath);
			monitor.worked(2);
			return Status.OK_STATUS;
			
		}catch(Exception e){
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}finally{
			monitor.done();
		}		
		

	}
	
}
