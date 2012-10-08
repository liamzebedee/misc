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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;

/**
 * This class triggers creations/saves of projects/files to the Codecompletion
 * mechanism inside Emonic
 * @author dertl
 *
 */
public class CCExecutionListener implements IExecutionListener {

	private final String FILE_SAVED = "org.eclipse.ui.file.save";

	//FIXME DO NOT EXIST
//	private final String FILE_CREATED = "org.eclipse.ui.file.create";
	//private final String PROJECT_OPENED = "org.eclipse.ui.project.open";
	//private final String PROJECT_CREATED = "org.eclipse.ui.project.create";
	// FIXME DO NOT EXIST END
	
	public void notHandled(String commandId, NotHandledException exception) {
		// TODO Auto-generated method stub
		
	}

	public void postExecuteFailure(String commandId,
			ExecutionException exception) {
		// TODO Auto-generated method stub
		
	}

	public void postExecuteSuccess(String commandId, Object returnValue) {
		// TODO Auto-generated method stub
		
	}

	public void preExecute(String commandId, ExecutionEvent event) {
//		System.out.println("preExecute for " + event.toString());
		//this.actualCommandId = commandId;
		//this.actualExecutionEvent = event;
			
		//IHandler handler = this.actualExecutionEvent.getCommand().getHandler();
		
		//FIXME good idea to create a new HandlerUtil() @each event?
		//IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		IEditorPart editorPart = getActiveEditor(event);
		if(editorPart != null){
			IEditorInput editorInput = editorPart.getEditorInput();
			IResource resource = (IResource) editorInput.getAdapter(IResource.class);
			if(event.getCommand() != null){
				
				//case save File
				String event_command = event.getCommand().getId(); 
				if(event_command.equals(FILE_SAVED)){
					IFile file = (IFile) resource;
					fileSaved(file, file.getProject());
				}
				//case create file
				/*else if(event_command.equals(FILE_CREATED)){
					IFile file = (IFile) resource;
					fileCreated(file, file.getProject());						
				}*/
				//case open project || Case create project
				/*else if(event_command.equals(PROJECT_OPENED) || 
						 event_command.equals(PROJECT_CREATED)){
					IProject project = (IProject) resource;
					projectCreatedOrOpened(project);
				}*/			
				//case save project preferences
				/*else if()
				{
					
				}*/
				
			}
		}
	}
	
	/*private void projectCreatedOrOpened(IProject project){
		//add project to cc
		CCAdministration.getInstance().addProject(project);
		//read in srces from project
		SourceShallowExtractorJob srcProjExtractJob = 
				new SourceShallowExtractorJob("Extract project src...", project);
		srcProjExtractJob.schedule();
	}*/
	

	private void fileSaved(IFile file, IProject project){
	
		//check if file is shallow or deep
		boolean isDeep = CCAdministration.getInstance().getProjectTypeLoadedMap(project).
										containsValue(project.getFullPath().toPortableString());
		SourceExtractor srcEx = new SourceExtractor(project);
		srcEx.extractFile(new File(file.getLocation().toPortableString()), isDeep);
		
	}
	
	/*
	private void fileCreated(IFile file, IProject project){
		SourceExtractor srcEx = new SourceExtractor(project);
		srcEx.extractFile(new File(file.getLocation().toPortableString()), false);		
	}*/
		
	/*
	private void projectPropertiesChanged(){
		//FIXME
	}*/

	
	/**
	 * Extract the variable.
	 * 
	 * @param event
	 *            The execution event that contains the application context
	 * @param name
	 *            The variable name to extract.
	 * @return The object from the application context, or <code>null</code>
	 *         if it could not be found.
	 */
	public static Object getVariable(ExecutionEvent event, String name) {
		if (event.getApplicationContext() instanceof IEvaluationContext) {
			return ((IEvaluationContext) event.getApplicationContext())
					.getVariable(name);
		}
		return null;
	}
	
	/**
	 * Return the active editor.
	 * 
	 * @param event
	 *            The execution event that contains the application context
	 * @return the active editor, or <code>null</code>.
	 */
	public static IEditorPart getActiveEditor(ExecutionEvent event) {
		Object o = getVariable(event, ISources.ACTIVE_EDITOR_NAME);
		if (o instanceof IEditorPart) {
			return (IEditorPart) o;
		}
		return null;
	}
	
	
}
