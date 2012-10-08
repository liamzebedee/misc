/************************************************************************* 
 * Copyright (c) 2005, 2007, 2008 IBM, emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors: B. Brem
 *************************************************************************/
package org.emonic.base.framework;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class FrameworkFactory {
	public static IFrameworkInstall getDefaultFrameworkInstall() {
		try {
			Preferences preferences = new InstanceScope()
					.getNode(EMonoPlugin.PLUGIN_ID);

			String framework = preferences.get(
					IDotNetDebugConstants.DEFAULT_FRAMEWORK_INSTALL, null);

			if (framework != null) {
				String[] frameworkIds = preferences.childrenNames();
				if (frameworkIds.length != 0) {
					for (int i = 0; i < frameworkIds.length; i++) {
						if (framework.equals(frameworkIds[i])) {
							FrameworkInstall install = new FrameworkInstall();
							install.setId(framework);
							Preferences node = preferences.node(framework);
							install
									.setName(node
											.get(
													IDotNetDebugConstants.FRAMEWORK_INSTALL_NAME,
													"")); //$NON-NLS-1$
							install
									.setType(node
											.get(
													IDotNetDebugConstants.FRAMEWORK_INSTALL_TYPE,
													"")); //$NON-NLS-1$
							install
									.setInstallLocation(new File(
											node
													.get(
															IDotNetDebugConstants.FRAMEWORK_INSTALL_LOCATION,
															""))); //$NON-NLS-1$
							install.setDocumentationLocation(node.get(IDotNetDebugConstants.FRAMEWORK_DOCUMENTATION_LOCATION, "")); //$NON-NLS-1$
							install
									.setRuntimeArguments(node
											.get(
													IDotNetDebugConstants.FRAMEWORK_INSTALL_RUNTIME_ARGUMENTS,
													"")); //$NON-NLS-1$
							install.setGACLocation(node.get(IDotNetDebugConstants.FRAMEWORK_GAC_LOCATION, ""));
							return install;
						}
					}
				}
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IFrameworkInstall[] getFrameworkInstalls() {
		try {
			IEclipsePreferences debugPreferences = new InstanceScope()
					.getNode(EMonoPlugin.PLUGIN_ID);
			String[] keys = debugPreferences.childrenNames();
			int length = keys.length;
			FrameworkInstall[] installs = new FrameworkInstall[length];
			for (int i = 0; i < length; i++) {
				installs[i] = new FrameworkInstall();
				installs[i].setId(keys[i]);
				Preferences node = debugPreferences.node(keys[i]);
				installs[i].setName(node.get(
						IDotNetDebugConstants.FRAMEWORK_INSTALL_NAME, "")); //$NON-NLS-1$
				installs[i].setType(node.get(
						IDotNetDebugConstants.FRAMEWORK_INSTALL_TYPE, "")); //$NON-NLS-1$
				installs[i].setInstallLocation(new File(node.get(
						IDotNetDebugConstants.FRAMEWORK_INSTALL_LOCATION, ""))); //$NON-NLS-1$
				installs[i].setDocumentationLocation(node.get(IDotNetDebugConstants.FRAMEWORK_DOCUMENTATION_LOCATION, "")); //$NON-NLS-1$
				installs[i]
						.setRuntimeArguments(node
								.get(
										IDotNetDebugConstants.FRAMEWORK_INSTALL_RUNTIME_ARGUMENTS,
										"")); //$NON-NLS-1$
			}
			return installs;
		} catch (BackingStoreException e) {
			return new IFrameworkInstall[0];
		}
	}

	public static IFrameworkInstall getFrameworkAccordingToBuild(BuildMechanismManipulator bfm) {
		if (bfm == null){
			return null;
		}
		IFrameworkInstall install = null;
		if (bfm.isFrameworkManipulator()){
			String type=bfm.getTargetFrameworkType();
			String release=bfm.getTargetFrameworkRelease();
			IFrameworkInstall[] installs = FrameworkFactory.getFrameworkInstalls();
			for (int i = 0; i < installs.length; i++){
				String installtype=installs[i].getType();
				if (type.equals(IBuildMechanismDescriptor.FRAMEWORK_MONO) && installtype.equals(IFrameworkInstall.MONO_FRAMEWORK)){
					// This framework fits, let's take it!
					install=installs[i];
//					TODO It can be that more then 1 mono framework is installed, handle this!
				}
				if (type.equals(IBuildMechanismDescriptor.FRAMEWORK_MS) && installtype.equals(IFrameworkInstall.MICROSOFT_DOT_NET_FRAMEWORK ) ){
					// Has it the right version? 
					// In the case of ms we get this by the inst path
					String p = install.getInstallLocation().getAbsolutePath();
					// The naming of the microsoft install dir should be something like C:\Windows\Microsoft.NET\Framework\v.1.1.zzz 
					if (p.toLowerCase().indexOf("framework") != -1){
						//TODO Verify wether this works correct! Programmed without testing!!!!
						p=p.substring(p.toLowerCase().indexOf("framework")+9);
						Pattern regexp=Pattern.compile("v[^\\d](\\d)\\d*\\.(\\d)");
						Matcher mat = regexp.matcher(p);
						String version = mat.group(1)+"."+mat.group(2);
						if (version.equals(release)){
							// Right framework
							installs[i]=install;
						}

					}
				}
			}
		}
		// Do we have a framework now? If not: Take the default framework
		if (install == null){
			install=FrameworkFactory.getDefaultFrameworkInstall();
		}	

		// TODO Auto-generated method stub
		return install;
	}
}
