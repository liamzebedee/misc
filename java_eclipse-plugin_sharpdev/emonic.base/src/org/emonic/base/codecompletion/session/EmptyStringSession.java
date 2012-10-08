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
 * represents a session which is created with 
 * CTRL+SPACE on an empty string
 * @author dertl
 *
 */
public class EmptyStringSession extends Session{

	public EmptyStringSession(ITextViewer viewer){
		super(viewer);
	}
	
	public LinkedList getCompletionproposal(String wordToComplete_,
			CSharpCursorPositionContext cpc_, int documentOffset_,
			ISourceUnit actualSrcUnit_, IFile actualFile_) {

		//isEmpty!
		this.isEmpty = true;
		
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
				
		//add case sensitivity && remove preambel this.
		this.wordToComplete = wordToComplete_.toLowerCase().trim();
		this.suffix = wordToComplete;
		//System.out.println("DEBUG Word to complete: " + wordToComplete);
		
		//set flags
		setFlags(false); //false => not this!
		
		if(this.wordToComplete.endsWith("].")){
			//FIXME
			System.out.println("DEBUG Code Completion for ...[] not implemented");
			return new LinkedList();
		}
		
		IType itype = cpc.getIType();
		String filepath = actualFile.getLocation().toOSString();
		results = getEmptyCompletionElements(itype, false, false, filepath, actualProject);
		
		return results;
	}

	/**
	 * grab all the necessary Elements for EmptyCompletion 
	 * @param itype
	 * @param isGAC
	 * @param isDll
	 * @param filepath
	 * @param project
	 * @return
	 */
	protected LinkedList getEmptyCompletionElements(IType itype, boolean isGAC,
			boolean isDll, String filepath, IProject project) {

		//get elements of base type
		LinkedList returnList = this.getElementsOfTypeRecursively(null, itype, isGAC, isDll, filepath, project, false);

		//add entries from method-signature
		returnList.addAll(this.getSignatureParamsOfDocOffsetMethod());
		
		//add entries from method => method variables
		returnList.addAll(this.getVariablesOfDocOffsetMethod());
				
		return returnList;
	}

}
