/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2007 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dominik Ertl - Implementation
 *      Harald Krapfenbauer - Detection of several tags in one comment
 */

package org.emonic.base.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.infostructure.CSharpCodeParser;
import org.emonic.base.preferences.DefaultPrefsSetter;


public class TaskTracker {

	public TaskTracker(){
		
	}
	
	/**
	 *  Check for tasks in the given file that are marked with the keywords (see preferences)
	 */
	public void checkForTasks(IDocument document, IResource resource){
		
		try {
			// create list of todo tags
			List taskStrings = getTodoTags();
			
			// delete all tasks of the given document
			resource.deleteMarkers(IMarker.TASK, true, IResource.DEPTH_ZERO);
			
			// parse document and create list of comments
			CSharpCodeParser parser = new CSharpCodeParser(document,"");
			ISourceUnit root = parser.parseDocument();
			List commentList = ((CodeElement)root).getCommentList();
			
			CodeElement codeElem;
			String checkTodo;
			// generate regexp string
			String regExpSearch = "(" + (String) taskStrings.get(0);
			for(int j = 1; j<taskStrings.size(); j++) {
				regExpSearch += ")|(";
				regExpSearch += (String) taskStrings.get(j);
			}
			regExpSearch = "(("+regExpSearch + ")) .*?)("+regExpSearch+")|(\\*/)|(\\n)|(\\z))";
			Pattern sep = Pattern.compile(regExpSearch);
			
			// walk through comments
			for(int i=0; i< commentList.size(); i++){
				codeElem = (CodeElement)commentList.get(i);
				// get string with current comment
				checkTodo = document.get(codeElem.getOffset(), codeElem.getLength());
				Matcher mat;
				int additionalOffset = 0;
				while (true) {
					mat = sep.matcher(checkTodo);
					// find tasks
					if (mat.find()) {
						// create marker
						IMarker marker = resource.createMarker(IMarker.TASK);
						marker.setAttribute(IMarker.MESSAGE, mat.group(1));
						marker.setAttribute(IMarker.LINE_NUMBER, document.getLineOfOffset(codeElem.getOffset()+mat.start(1)+additionalOffset)+1);
						marker.setAttribute(IMarker.USER_EDITABLE, false);
						marker.setAttribute(IMarker.CHAR_START,codeElem.getOffset()+mat.start(1)+additionalOffset);
						marker.setAttribute(IMarker.CHAR_END,codeElem.getOffset()+mat.end(1)+additionalOffset);
						// create substring to match subsequent tags
						try {
							checkTodo = checkTodo.substring(mat.end(1), checkTodo.length());
							additionalOffset = mat.end(1);
						} catch (StringIndexOutOfBoundsException e) {
							checkTodo = "";
						}
					} else {
						// nothing found - leave the while loop
						break;
					}
				}
			}
		}
		catch (CoreException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} 
	}	
	
    private List getTodoTags(){    	
    	String string = EMonoPlugin.getDefault().getPreferenceStore().getString(DefaultPrefsSetter.CS_TODO_TAGS);
    	String[] strings = string.split(" ");
	    ArrayList list = new ArrayList();
	    for (int i = 0; i < strings.length; i++) {
	        if(strings[i].length() > 0){
	                list.add(strings[i]);
	        }	   
	    }
	    return list;
    }
}
