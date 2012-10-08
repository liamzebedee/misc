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
package org.emonic.base.codecompletion.session;

import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextViewer;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.codehierarchy.IType;
import org.emonic.base.editors.CSharpCursorPositionContext;

/**
 * represents a session triggered by "this."
 * @author dertl
 *
 */
public class ThisSession extends Session{

	public ThisSession(ITextViewer viewer){
		super(viewer);
	}
	
	
	public LinkedList getCompletionproposal(String wordToComplete_, CSharpCursorPositionContext cpc_,
											int documentOffset_, ISourceUnit actualSrcUnit_, IFile actualFile_) {
		
		//isThis!
		this.isThis = true;
				
		this.documentOffset = documentOffset_;
		this.cpc = cpc_;
		this.actualSrcUnit = actualSrcUnit_;
		this.actualFile = actualFile_;
		//reset unique entry
		this.uniqueEntry = new LinkedList();		
		LinkedList results = new LinkedList();
		//set actual project
		if(actualFile != null){
			this.actualProject = actualFile.getProject();
		}
		
		//this is not allowed in static method
		if(cpc.isStatic()){
			return new LinkedList();
		}
			
		//add case sensitivity && remove preambel this.
		this.wordToComplete = wordToComplete_.toLowerCase().trim();
		if (this.wordToComplete.indexOf("this") == 0){
			this.wordToComplete=this.wordToComplete.substring(5);
		}
		
		
		this.suffix = wordToComplete;
		//System.out.println("DEBUG Word to complete: " + wordToComplete);
		
		//set flags
		setFlags(true);
		
		if(this.wordToComplete.endsWith("].")){
			//FIXME
			System.out.println("DEBUG Code Completion for ...[] not implemented");
			return new LinkedList();
		}
		
		IType itype = cpc.getIType();
		String filepath = actualFile.getLocation().toPortableString();
		results = getThisElements(itype, false, false, filepath, actualProject);
		
		return results;
	}

	
	/**
	 * get all the elements of a given type from a dll or src file
	 * @param itype
	 * @param isGAC
	 * @return
	 */
	protected LinkedList getThisElements(IType itype, boolean isGAC, boolean isDll, 
										String filepath, IProject project) {
		
		return this.getElementsOfTypeRecursively(null, itype, isGAC, isDll, filepath, project, false);
		
	}

}
