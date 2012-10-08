/*******************************************************************************
 *  Copyright (c) 2001, 2007 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Mar 24, 2005
 *
 *******************************************************************************/
package org.emonic.base.filemanipulators;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.emonic.base.EMonoPlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author bb Window - Preferences - Java - Code Style - Code Templates
 */
public class ProjectPreferencesManipulator {

	/**
	 * The original name of the file that was used to store project preferences
	 * for Emonic.
	 */
	// Since the file name was commented, legacy to old prefs mechanism was broken before
	// Remove it complete: No more XMLFileManipulator!
	// private static final String LEGACY_FILE_NAME = "org.emonic.base.prefs";//DEPRECATED:".emonicpreferences.xml"; //$NON-NLS-1$

	private static final String COPYRIGHT_PREFS = EMonoPlugin.PLUGIN_ID
			+ ".project.copyright"; //$NON-NLS-1$
	private static final String DEFAULT_NAMESPACE_PREFS = EMonoPlugin.PLUGIN_ID
			+ ".project.defaultNamespace"; //$NON-NLS-1$
	private static final String BUILD_MECHANISM_PREFS = EMonoPlugin.PLUGIN_ID
			+ ".build.mechanism"; //$NON-NLS-1$
	private static final String BUILD_FILE_PREFS = EMonoPlugin.PLUGIN_ID
			+ ".build.file"; //$NON-NLS-1$

	private static final String CODE_COMPLETION_SRC = EMonoPlugin.PLUGIN_ID
			+ ".codecompletion.src";

	private static final String CODE_COMPLETION_DLL_DIR = EMonoPlugin.PLUGIN_ID
			+ ".codecompletion.dll.dir";

	private static final String CODE_COMPLETION_DLL_FILE = EMonoPlugin.PLUGIN_ID
			+ ".codecompletion.dll.file";

	private static final String TOTAL_SRC = EMonoPlugin.PLUGIN_ID
			+ ".codecompletion.total.src";

	private static final String TOTAL_DLL_DIR = EMonoPlugin.PLUGIN_ID
			+ ".codecompletion.total.dll.dir";

	private static final String TOTAL_DLL_FILE = EMonoPlugin.PLUGIN_ID
			+ ".codecompletion.total.dll.file";


	private IEclipsePreferences preferences;

	public ProjectPreferencesManipulator(IResource res) {
		super();
		// Get the project of the resource
		if (res != null){
			IProject proj = res.getProject();
			preferences = new ProjectScope(proj).getNode(EMonoPlugin.PLUGIN_ID);
		}
	}

	/**
	 * 
	 * @param string
	 * @param string
	 * @param projName
	 */
	public void setPrefs(String copyright, String namespace, String mechanism,
			String buildFile) {
		if (preferences != null){
			if (copyright != null)
				preferences.put(COPYRIGHT_PREFS, copyright);
			if (namespace != null)
				preferences.put(DEFAULT_NAMESPACE_PREFS, namespace);
			if (mechanism!=null)
				preferences.put(BUILD_MECHANISM_PREFS, mechanism);
			if (buildFile!=null)
				preferences.put(BUILD_FILE_PREFS, buildFile);
			write();
		}
	}


	public IEclipsePreferences  getPreferences(){
		return preferences;
	}

	/**
	 * 
	 * @param list
	 * @param dll
	 */
	public void setCodeCompletionProperties(ArrayList src, ArrayList dirDll, ArrayList fileDll){

		//save stored preferences
		String copyright = this.getCopyright();
		String namespace = this.getNamespace();
		String mechanism = this.getBuildMechanism();
		String buildFile = this.getBuildFileName();
		if (preferences != null){
			//clear preferences, as prior code-completion values are obsolete
			try {
				preferences.clear();
			} catch (BackingStoreException e) {

			}

			Iterator iter = src.iterator();
			int i = 0;
			while(iter.hasNext()){
				preferences.put(CODE_COMPLETION_SRC+i, (String)iter.next());
				i++;
			}

			preferences.putInt(TOTAL_SRC, i);

			iter = dirDll.iterator();
			i=0;
			while(iter.hasNext()){
				preferences.put(CODE_COMPLETION_DLL_DIR+i, (String)iter.next());
				i++;
			}
			preferences.putInt(TOTAL_DLL_DIR, i);

			iter = fileDll.iterator();
			i=0;
			while(iter.hasNext()){
				preferences.put(CODE_COMPLETION_DLL_FILE+i, (String)iter.next());
				i++;
			}
			preferences.putInt(TOTAL_DLL_FILE, i);

			//store prefs with temporarily saved cr, ns, mech, builFile
			setPrefs(copyright, namespace, mechanism, buildFile);
		}
	}

	public void write() {
		if (preferences != null){
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	public String getCopyright() {
		if (preferences != null){
			return preferences.get(COPYRIGHT_PREFS, ""); //$NON-NLS-1$
		}
		return "";
	}

	public String getNamespace() {
		if (preferences!=null ) 	return preferences.get(DEFAULT_NAMESPACE_PREFS, ""); //$NON-NLS-1$
		return "";
	}

	public String getBuildMechanism() {
		if (preferences != null) 		return preferences.get(BUILD_MECHANISM_PREFS, ""); //$NON-NLS-1$
		return "";
	}

	public String getBuildFileName() {
		if (preferences != null)return preferences.get(BUILD_FILE_PREFS, ""); //$NON-NLS-1$
		return "";
	}

	public ArrayList getCodeCompletionSrc(){
		ArrayList src = new ArrayList();
		if (preferences != null){
			int totalSrc = preferences.getInt(TOTAL_SRC, 0);	
			for(int i = 0; i<totalSrc; i++){
				src.add(preferences.get(CODE_COMPLETION_SRC+i, ""));
			}
		}
		return src; //$NON-NLS-1$
	}



	public ArrayList getCodeCompletionDirDll(){
		ArrayList dll = new ArrayList();
		if (preferences != null){
			int totalDll = preferences.getInt(TOTAL_DLL_DIR, 0);

			for(int i = 0; i<totalDll; i++){
				dll.add(preferences.get(CODE_COMPLETION_DLL_DIR+i, ""));
			}
		}
		return dll; //$NON-NLS-1$
	}

	public ArrayList getCodeCompletionFileDll(){

		ArrayList dll = new ArrayList();
		if (preferences != null){

			int totalDll = preferences.getInt(TOTAL_DLL_FILE, 0);

			for(int i = 0; i<totalDll; i++){
				dll.add(preferences.get(CODE_COMPLETION_DLL_FILE+i, ""));
			}
		}
		return dll; //$NON-NLS-1$
	}

	public void setBuildFileName(String buildFile) {
		if (buildFile!=null && preferences!=null)
			preferences.put(BUILD_FILE_PREFS, buildFile);
		write();

	}

	public void setBuildMechanism(String buildmechanism) {
		if (buildmechanism!=null && preferences != null)
			preferences.put(BUILD_MECHANISM_PREFS, buildmechanism);
		write();
	}

	public void setNamespace(String namespace) {
		if (namespace != null && preferences!= null)
			preferences.put(DEFAULT_NAMESPACE_PREFS, namespace);
		write();

	}

	public void setCopyright(String copyright) {
		if (copyright != null && preferences != null)
			preferences.put(COPYRIGHT_PREFS, copyright);
		write();

	}	
}
