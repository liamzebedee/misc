/****************************************************************************
 * Copyright (c) 2001, 2007 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Dec 23, 2005
 * emonic org.emonic.base.FileManipulators BuildfileManipulatorFactory.java
 ****************************************************************************/
package org.emonic.base.buildmechanism;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.filemanipulators.ProjectPreferencesManipulator;
import org.emonic.base.preferences.DefaultPrefsSetter;

/**
 * @author bb
 *
 */
public class BuildDescriptionFactory {

	/**
	 * @param buildFile
	 * @return
	 * @throws CoreException
	 */
	

	/**
	 * Get the prefs mani
	 */
	private static ProjectPreferencesManipulator getPrefsMani(IResource res) throws CoreException{
		return new ProjectPreferencesManipulator(res);
		
	}
	
	private static IBuildMechanismDescriptor searchInPlugins(String mechanism){
		IBuildMechanismDescriptor result = new NoneBuildfileManipulator();
//		 Iterate the available mechanisms for the right one
		try{
			IExtensionRegistry er = Platform.getExtensionRegistry(); 
			IExtensionPoint ep = er.getExtensionPoint("org.emonic.base","buildmechanism");
			IExtension[] extensions=ep.getExtensions();
			for (int i =0; i<extensions.length;i++){
				IConfigurationElement[] ConfigElements = extensions[i].getConfigurationElements();
				for (int j=0; j<ConfigElements.length;j++){
					IConfigurationElement actual = ConfigElements[j];
					if (actual.getAttribute("mechanism").equals(mechanism)){
						result= (IBuildMechanismDescriptor) actual.createExecutableExtension("class");
						break;
					}

				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static BuildMechanismManipulator getBuildMechanismManipulator(IProject project){
		return new BuildMechanismManipulator(getBuildMechanism(project),project);
	}
	
	/**
	 * Get the buildfilemanipulator according to the preferences, progress monitor
	 * @param res
	 * @param monitor
	 * @return
	 */
	private static IBuildMechanismDescriptor getBuildMechanism(IProject project) {

		String mechanism = NoneBuildfileManipulator.getName();
		IBuildMechanismDescriptor result = new NoneBuildfileManipulator();
		if (project!=null){
			try{
				ProjectPreferencesManipulator prefs = getPrefsMani(project);
				mechanism = prefs.getBuildMechanism();
				result= searchInPlugins(mechanism);
				// Set the build file
				if (IBuildFileManipulator.class.isAssignableFrom(result.getClass())){
					String fn = prefs.getBuildFileName();
					IFile fl = null;
					if (project != null ) fl = project.getFile(fn);
					if (fl != null) ((IBuildFileManipulator) result).setBuildFile(fl);
				}
			} catch (Exception e){};
		}
		
		return result;
	}

	

	/** This can be called to get a build file manipulator without a existing project
	 * Used to get infos about the build mechanism
	 * @param mechamism
	 * @return the dummy build file manipulator
	 */ 
	public static BuildMechanismManipulator createNewBuildMechamism(String mechanism){
		return new BuildMechanismManipulator(searchInPlugins(mechanism));
		
	}

	public static String[] getAvailableMechanisms() {
		try{
			String defaultMech = EMonoPlugin.getDefault().getPreferenceStore().getString(DefaultPrefsSetter.DEFAULTBUILDMECHANISM);
			IExtensionRegistry er = Platform.getExtensionRegistry(); 
			IExtensionPoint ep = er.getExtensionPoint("org.emonic.base","buildmechanism");
			IExtension[] extensions=ep.getExtensions();
			ArrayList result=new ArrayList();
			for (int i =0; i<extensions.length;i++){
				IConfigurationElement[] ConfigElements = extensions[i].getConfigurationElements();
				for (int j=0; j<ConfigElements.length;j++){
					IConfigurationElement actual = ConfigElements[j];
					result.add(actual.getAttribute("mechanism"));
				}
			}
			// Add the none-mechaism (as a  fallback it is not in a plugin)
			result.add(NoneBuildfileManipulator.getName());
			// Sort so that the preferenced is the top
			for (int i = 0; i < result.size(); i++){
				if (((String)result.get(i)).equals(defaultMech)){
					result.remove(i);
					result.add(0, defaultMech);
				}
			}
			
			
			String[] res = new String[result.size()];
			res=(String[])result.toArray(res);
			return res;
		} catch (Exception e){
			e.printStackTrace();
		}
		return new String[0];
	}

}
