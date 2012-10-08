/************************************************************************* 
 * Copyright (c) 2005, 2007 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *************************************************************************/

package org.emonic.base.framework;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;

public class FrameworkBinaryFinder {
	private static final String[] MONO_BINARY_CANDIDATES = { "mono", //$NON-NLS-1$
		"mono.exe", "bin" + File.separatorChar + "mono", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"bin" + File.separatorChar + "mono.exe" }; //$NON-NLS-1$ //$NON-NLS-2$
	
	private static final String[] MONO_GMCS_CANDIDATES = { "gmcs",
		"gmcs.exe", "bin" + File.separatorChar + "gmcs",
		"bin" + File.separatorChar + "gmcs.exe"};

	private static final String[] MONO_MCS_CANDIDATES = { "mcs",
		"mcs.exe", "bin" + File.separatorChar + "mcs",
		"bin" + File.separatorChar + "mcs.exe"};
	
	
	private static final String EXEENDING = ".exe"; //$NON-NLS-1$
	private static final String DLLENDING = ".dll"; //$NON-NLS-1$
	
	private IFrameworkInstall theFramework;

	public FrameworkBinaryFinder(IFrameworkInstall framework){
		 theFramework = framework;
	 }
	
	public File findMonoBinary() {
		for (int i = 0; i < MONO_BINARY_CANDIDATES.length; i++) {
			File candidate = new File(theFramework.getInstallLocation(),
					MONO_BINARY_CANDIDATES[i]);
			if (candidate.exists()) {
				return candidate;
			}
		}
		return null;
	}

	/**
	 * Find the GAC accodring to the gven framework
	 * Since mono contains libraries for 1.0 and 2.0 we want to know which to take.
	 * If no valid release is givem we take 1.0 
	 * @param release
	 * @return
	 */
	public File findGacPath(String release) {
		if (theFramework == null)
				return null;
		if (theFramework.getType().equals(IFrameworkInstall.MONO_FRAMEWORK)) {
			File f = theFramework.getInstallLocation();
			String s = f.getAbsolutePath();
			// In case of mono it is allowed to specify a subdir "bin"
			if (s.endsWith(File.separator+"bin") ||
					s.endsWith(File.separator+"bin"+File.separator)) {
				s = s.substring(0,s.lastIndexOf(File.separator+"bin"));
			}
			s=s+File.separator;
			// The GAC dir is this dir + "/lib/mono/" + a version number (mono brings support for different .NET versions)
			File result = null;
			File monodir = new File(s+"lib"+File.separator+"mono");
			if (monodir.exists() && monodir.isDirectory()) {
				result = new File(monodir.getAbsolutePath()+File.separator+release);
				if (!result.exists()) {
					result = new File(monodir.getAbsolutePath()+File.separator+"1.0");
				}
			}
			if (result != null && result.isDirectory()) {
				return result;
			}
		} else {
			if (theFramework.getInstallLocation().isDirectory()) {
				return theFramework.getInstallLocation();
			}
		}
		return null;
	}
	 
	/**
	 * Search a dll in the GAC
	 * @param dllName
	 * @param releasename The release - important for mono!
	 * @return
	 */
	public File getAssemblyLocation(String dllName,String releasename) {
		// Get GAC path
		File path = findGacPath(releasename);
		if (path == null)
			return null;
		String gacPath = path.getAbsolutePath();
		if (gacPath == null)
			return null;
		
		// DLL of the GAC?
		File f = new File(gacPath, dllName);
		if (f.canRead()) {
			return f;
		}
		f = new File(gacPath, dllName + DLLENDING);
		if (f.canRead()) {
			return f;
		}
		// Exe of the GAC?
		f = new File(gacPath, dllName + EXEENDING);
		if (f.canRead()) {
			return f;
		}
		return null;
	}

	/**
	 * Search a dll in the proj and in the GAC
	 * @param proj
	 * @param string
	 * @return
	 */
	public File getAssemblyLocation(final String dllName, final IProject proj) {
		// dll and local?
		final String localPath = proj.getLocation().toOSString();
		File f = new File(localPath, dllName);
		if (f.canRead()) {
			return f;
		}
		f = new File(localPath, dllName + DLLENDING);
		if (f.canRead()) {
			return f;
		}
		// exe and local?
		f = new File(localPath, dllName + EXEENDING);
		if (f.canRead()) {
			return f;
		}
		BuildMechanismManipulator bfm= BuildDescriptionFactory.getBuildMechanismManipulator(proj);
		if (bfm.isFrameworkManipulator()){
			return getAssemblyLocation(dllName, bfm.getTargetFrameworkRelease());
		}
		return null;
	}

	public File getCompiler(String release) {
		if (theFramework != null){
			if (theFramework.getType().equals(IFrameworkInstall.MICROSOFT_DOT_NET_FRAMEWORK)){
				// .NET
				File f = new File(theFramework.getInstallLocation(),"csc.exe");
				if (f.canRead()){
					return f;
				}
			} else if (theFramework.getType().equals(IFrameworkInstall.MONO_FRAMEWORK)){
				// Mono
				String exSuffix = "";
				if(System.getProperty("os.name").startsWith("Windows")) {
					exSuffix = ".bat";
				}
				if(release.startsWith("2") || release.startsWith("3") ){
					for (int i = 0; i < MONO_GMCS_CANDIDATES.length; i++) {
						File candidate = new File(theFramework.getInstallLocation(),
								MONO_GMCS_CANDIDATES[i]+exSuffix);
						if (candidate.exists()) {
							return candidate;
						}
					}
				} else {
					for (int i = 0; i < MONO_MCS_CANDIDATES.length; i++) {
						File candidate = new File(theFramework.getInstallLocation(),
								MONO_MCS_CANDIDATES[i]+exSuffix);
						if (candidate.exists()) {
							return candidate;
						}
					} 
				}

			}
		}
		return null;
	}
	

}
