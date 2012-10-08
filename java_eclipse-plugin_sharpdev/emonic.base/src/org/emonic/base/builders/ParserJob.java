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
 *      Harald Krapfenbauer - Implementation
 */

package org.emonic.base.builders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IBuildFrameworkManipulator;
import org.emonic.base.framework.FrameworkBinaryFinder;
import org.emonic.base.framework.FrameworkFactory;
import org.emonic.base.framework.IFrameworkInstall;
import org.emonic.base.helpers.SimpleCommandRunner;
import org.emonic.base.preferences.DefaultPrefsSetter;

public class ParserJob extends Job {

	public static final String PARSER_JOB_NAME = "CSharpParserJob";
	public static final String PARSER_FILE_CONCAT = "_temp";
	
	private IFile file;
	private IDocument doc;
	private static boolean alreadyRunning = false;
	private BuildMechanismManipulator bfm;
	/**
	 * Constructor
	 * @param editor 
	 * @param name
	 */
	public ParserJob(IFile file, IDocument doc) {
		super(PARSER_JOB_NAME);
		this.file = file;
		this.doc = doc;
		bfm = BuildDescriptionFactory.getBuildMechanismManipulator (file.getProject());
		
	}
	
    
	
	protected IStatus run(IProgressMonitor monitor) {
		if (!alreadyRunning){
			try{	
				
				alreadyRunning=true;
				String fileTemp = file.getLocation().toOSString().concat(PARSER_FILE_CONCAT);
				try
				{
					
					// save content to temporary file
					FileOutputStream fStream = new FileOutputStream(fileTemp);
					fStream.write(doc.get().getBytes());
					fStream.close();
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}

				// run the compiler in parsing mode
				String workingDir = (new File(fileTemp)).getParent();
				String result = "";
				try
				{
					String parsingCommand = "";
					if (EMonoPlugin.getDefault().getPreferenceStore().getBoolean(DefaultPrefsSetter.USEPARSINGWITHCOMMAND)){
						parsingCommand = EMonoPlugin.getDefault().getPreferenceStore().getString(
							DefaultPrefsSetter.PARSINGCOMMAND);
					} else {
						IFrameworkInstall framework= FrameworkFactory.getFrameworkAccordingToBuild(bfm);
						if (framework != null && framework.equals(IFrameworkInstall.MICROSOFT_DOT_NET_FRAMEWORK)){
							// We are not able to use it, try to get the a mono!
							IFrameworkInstall[] possible=FrameworkFactory.getFrameworkInstalls();
							for (int i =0;i<possible.length;i++){
								if (possible[i].getType().equals(IFrameworkInstall.MONO_FRAMEWORK)){
									framework=possible[i];
								}
							}
						}
						FrameworkBinaryFinder finder = new FrameworkBinaryFinder(framework);
						if (bfm.isFrameworkManipulator()) {
							IBuildFrameworkManipulator ibfm =
								(IBuildFrameworkManipulator)(bfm.getBuildMechanism());
							File compiler = finder.getCompiler(ibfm.getTargetFrameworkRelease());
							if (compiler != null) {
								parsingCommand = compiler.getAbsolutePath();
							}
						}
					}
					if (!parsingCommand.equals(""))
						result = SimpleCommandRunner.runAndGetOutput(new String[] { parsingCommand, fileTemp, " --parse" },
								workingDir,monitor);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				// update markers
				DocumentMarker.markDocument(result, file);
				// delete file
				(new File(fileTemp)).delete();
			}catch(Exception e){
				e.printStackTrace();
			}
			alreadyRunning=false;
		}

		return Status.OK_STATUS;
	}



	/**
	 * @return the alreadyRunning
	 */
	public static boolean isAlreadyRunning() {
		return alreadyRunning;
	}
}
