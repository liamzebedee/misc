/****************************************************************************
 * Copyright (c) 2001, 2008 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors: B.Brem 
 * **************************************************************************/

package org.emonic.base.buildmechanism;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;

public class SourceTarget extends Target {

	
	/**
	 * The type of the target
	 */
	private String type;
	
	/**
	 * The language of the files
	 */
	private String language;
	
	private ArrayList languageFiles;

	/**
	 * The references
	 */
	private String[] references;
	
	/**
	 * The definitions
	 */
	private String[] definitions;
	
	/** 
	 * Optimization on?
	 */
	private boolean optimization;
	
	/**
	 * Debugging output on?
	 */
	private boolean debuggingOutput;
	
	/**
	 * The warning level
	 */
	private int warningLevel =0;

	private IFile Artefact;
	
	public SourceTarget() {
		super();
		this.name = "";
		this.languageFiles = new ArrayList();
	}

	
	

	public void addSource(IFile file) {
		languageFiles.add(file);
	}
	
	public void setSources(IFile[] sourceFiles){
		languageFiles.clear();
		for (int i = 0; i < sourceFiles.length; i++){
			languageFiles.add(sourceFiles[i]);
		}
	}
	
	public IFile[] getSources(){
		IFile[] result = new IFile[languageFiles.size()];
		result=(IFile[])languageFiles.toArray(result);
		return result;
	}

	/**
	 * Get the language of the target
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Set the language
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public boolean isOptimization() {
		return optimization;
	}

	public void setOptimization(boolean optimization) {
		this.optimization = optimization;
	}

	/**
	 * Generate debugging output or not?
	 * @return the debuggingOutput
	 */
	public boolean isDebuggingOutput() {
		return debuggingOutput;
	}

	/**
	 * Generate debugging output or not?
	 * @param debuggingOutput the debuggingOutput to set
	 */
	public void setDebuggingOutput(boolean debuggingOutput) {
		this.debuggingOutput = debuggingOutput;
	}

	/**
	 * Get the definitions of the target
	 * @return the definitions
	 */
	public String[] getDefinitions() {
		if (definitions != null){
			return definitions;
		}
		return new String[0];
	}

	/**
	 * @param definitions the definitions to set
	 */
	public void setDefinitions(String[] definitions) {
		this.definitions = definitions;
	}

	/**
	 * @return the references
	 */
	public String[] getReferences() {
		if (references != null){
			return references;
		}
		return new String[0];
	}

	/**
	 * @param references the references to set
	 */
	public void setReferences(String[] references) {
		this.references = references;
	}

	/**
	 * @return the warningLevel
	 */
	public int getWarningLevel() {
		return warningLevel;
	}

	/**
	 * @param warningLevel the warningLevel to set
	 */
	public void setWarningLevel(int warningLevel) {
		this.warningLevel = warningLevel;
	}




	public IFile getArtefact() {
		return this.Artefact;
	}
	
	public void setArtefact(IFile fl) {
		this.Artefact=fl;
	}
	
}
