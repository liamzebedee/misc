/*****************************************************************************************************
 * * Copyright (c) 2007 emonic.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Oct 9, 2005
 * emonic org.emonic.base.FileManipulators CSharpEditorExamineJob.java
 * 
 * Contributors:
 *   Bernhard Brem - Initial implementation
 *   Harald Krapfenbauer, TU Vienna - Removed automatic rescheduling, since this job is now 
 *     called only if the document really changed. Creating hashes of the code is also unnecessary
 *     therefore. 
 ***************************************************************************************************/
package org.emonic.base.infostructure;


import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.builders.ParserJob;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.editors.CSharpEditor;
import org.emonic.base.preferences.DefaultPrefsSetter;


/**
 * This class represents a job that is called whenever the editor's input document changed.
 * It triggers scanning the code for the outline view and folding, and starting the MONO
 * compiler in parsing mode.
 */
public class CSharpEditorExamineJob extends Job {


	protected CSharpEditor editor;
	protected IDocument doc;
	/**
	 * The list of registered tasks that must be run
	 */
	private ArrayList registeredForCodeElementTasks;
	/**
	 * The job which starts the MONO compiler in parsing mode
	 */
	private ParserJob parserJob;
	private ArrayList registeredForBracketInfoTask;
    // Runs the job already?
	static boolean  alreadyRunning = false;
	
	/**
	 * Constructor
	 * @param editor - The instance of the CSharp Editor
	 * @param name - The name of this job
	 */
	public CSharpEditorExamineJob(CSharpEditor editor, String name) {
		super(name);
		this.editor=editor;
		//Hide the progress bar of this job
		this.setSystem(true);
		registeredForCodeElementTasks = new ArrayList();
		registeredForBracketInfoTask = new ArrayList();
	}
	
	/**
	 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
	 */
	//public IStatus runInUIThread(IProgressMonitor monitor) {
	public IStatus run(IProgressMonitor monitor) {
		if (! alreadyRunning){
			try{
				alreadyRunning=true;  
				if (editor != null){
					IDocumentProvider provider = editor.getDocumentProvider();
					if (provider != null){
						doc = provider.getDocument(editor.getEditorInput());
						if (doc != null){
							
							
							// Generate a BracketInfo-object and update the classes subscribed to this
							BracketInfo info = new BracketInfo(doc);
							for (int count = 0; count < registeredForBracketInfoTask.size(); count++){
								if (!monitor.isCanceled()) {
									SimpleTaskInterface task = (SimpleTaskInterface) registeredForBracketInfoTask.get(count);
									task.runTask(info);
								}
							}
							// Parse the Doc in a root element
							IEditorInput input = editor.getEditorInput();
							IFile file = (IFile) input.getAdapter(IFile.class);
							CSharpCodeParser parser;
							if (file !=null){
								parser = new CSharpCodeParser(doc,file.getName());
							} else {
								parser = new CSharpCodeParser(doc,"UNKNOWN");
							}
							// Start the changes of the surface in a foreground-job
							// Luckily, these are exact the changes which expect a code element as argument
							// This might in future change - be aware of this!
							ISourceUnit root = parser.parseDocument();
							CSharpEditorExamineForegroundJob updater = new CSharpEditorExamineForegroundJob(registeredForCodeElementTasks,(CodeElement)root);
							updater.schedule();
							if (file != null) {
								// parse current file with compiler
								if (EMonoPlugin.getDefault().getPreferenceStore().getBoolean(DefaultPrefsSetter.USEPARSING)) {
									if (parserJob == null) {
										parserJob = new ParserJob(file, doc);
										parserJob.setPriority(LONG);
									}
									// Scedule only if not already running
									if (! ParserJob.isAlreadyRunning()){
										parserJob.schedule();
									}
								}
							}
							// Update the cache of the CodeInformator
//							if (! CodeInformatorCacheUpdateJob.isAlreadyRunningOrSceduled()){
//								CodeInformatorCacheUpdateJob.setSceduled();
//								CodeInformatorCacheUpdateJob codeinfoupdater=new CodeInformatorCacheUpdateJob(this.getName(),this.doc,this.editor);
//								codeinfoupdater.setPriority(LONG);
//								codeinfoupdater.schedule();
//							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			alreadyRunning=false;
		}

		return Status.OK_STATUS;
	}

	/**
	 * Registers a task to be run when the IDocument changes.
	 * @param iface An instance that implements SimpleTaskInterface
	 */
	public void registerForCodeElement(SimpleTaskInterface iface) {
		if (iface != null) {
			registeredForCodeElementTasks.add(iface);
		}
	}

	/**
	 * Removes a task from the list.
	 * @param page The instance of SimpleTaskInterface
	 */
	public void free(SimpleTaskInterface page) {
		if (page != null) {
			if (registeredForCodeElementTasks.contains(page)) 
				registeredForCodeElementTasks.remove(page);
			if (registeredForBracketInfoTask.contains(page)) 
				registeredForBracketInfoTask.remove(page);
		}
	}

	public void registerForBracketInfo(SimpleTaskInterface iface) {
		if (iface != null) {
			registeredForBracketInfoTask.add(iface);
		}
	}
}
