/****************************************************************************
 * Copyright (c) 2001, 2008 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors: B.Brem 
 * **************************************************************************/

package org.emonic.base.buildmechanism;


/**
 * This interface allows to add source files to the build mechanism. The source files are arranged in targets, 
 * it is expected that each language target results in a binary at build time. If You do not implement this target 
 * for a build mechanism, this build mechanism is not able to handle individual source files.
 *  
 * @author bb
 *
 */
public interface IBuildSourceTargetManipulator {
		
	/**
	 * Add a target
	 * It is only expected that the blank target (without dependencies, langugae-specific extensions....) is added
	 * @param defaultTaskName
	 */
	void addTarget(Target target);
	
	void adaptLanguageTarget(Target target);
	

//	void setReferences(String targetName, String[] referenceNames);
	/**
	 * Set the references of a target. It is expected that the implementing buildfilemanipulatores
	 * set the content of target.getReferences in the build file.
	 */
	void setReferences(SourceTarget target);
	

	//void setLanguageTargetType(String targetName,String targetType);
	/**
	 * Set the target type. That means DLL, EXE, WinEXE,... It is expectet that the implementing class uses SourceTarget.getType to do so!
	 */
	void setLanguageTargetType(SourceTarget target);
	
	/**
	 * Set the switch for debugging output. It is expected that the implementing classes use the outcome of target.isDebuggingOutput() to switch the debugging on/off
	 */
	void setDebuggingOutput(SourceTarget target);

	/**
	 * Set the switch for optimization. It is expected that the implementing classes use the outcome of target.isOptimization() to switch the debugging on/off
	 */
	void setOptimization(SourceTarget target);

	
	/**
	 * Set the warning level. It is expected that the implementing classes use the outcome of target.getWarningLevel() to do so.
	 */
	void setWarningLevel(SourceTarget target);

	/**
	 * Set the definitions of a target. It is expected that the result of target.getDefinitions() is set.
	 */
	void setDefinitions(SourceTarget target);



	/**
	 * Set the sources of a language target
	 * @param targetname The target name
	 * @param sources the sources to be compiled
	 */
	void setSourcesToLanguageTarget(SourceTarget target);
	
	
	/**
	 * Set the target dependencies
	 * @param targetName
	 * @param targets
	 */
	void setTargetDependencies(Target target);

	/**
	 *  Delete a target. It is _not_ expected that references to this target are also deleted.
	 * @param targetName
	 */
	void deleteTarget(Target target);

	
	
	
	/**
	 * Rename a target
	 */
	void renameTarget(Target target);
	
	
//-----------------------------------------------------------------------------	
// Functions which return targets or are used to construct source target
// These functions have not SourceTarget as parameter

	Target[] getAllTargets();


	
	/**
	 *  Get the name of the default target, i.e. the target which references all targets which are build as default
	 * @return
	 */
	String getDefaultTargetName();

	/**
	 * This function determines the supported languages
	 * @return the supported languages
	 */
	String[] getSupportedLanguages();

}
