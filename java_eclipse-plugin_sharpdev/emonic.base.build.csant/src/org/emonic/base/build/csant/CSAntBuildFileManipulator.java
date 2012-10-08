/*******************************************************************************
 * Copyright (c) 2007 emonic.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Bernhard Brem: Adaption to emonic
 * Radek Polak: Adaption to csant
 *******************************************************************************/

package org.emonic.base.build.csant;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.emonic.base.buildmechanism.AntLikeFileManipulator;
import org.emonic.base.buildmechanism.IBuildCommandRunner;
import org.emonic.base.buildmechanism.IBuildFileLayoutManipulator;
import org.emonic.base.buildmechanism.IBuildFileManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.buildmechanism.IBuildSourceTargetManipulator;
import org.emonic.base.buildmechanism.SourceTarget;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CSAntBuildFileManipulator extends AntLikeFileManipulator implements
		IBuildMechanismDescriptor, IBuildSourceTargetManipulator,
		IBuildFileLayoutManipulator, IBuildFileManipulator, IBuildCommandRunner {

	private static final String PROJECT_NODE_NAME = "project";

	private static final String PROJECT_NAME_ATTRIBUTE = "name";

	private static final String PROJECT_SRC_DIR_ATTRIBUTE = "srcdir";

	private static final String PROJECT_BUILD_DIR_ATTRIBUTE = "builddir";

	private static final String PROJECT_DEFAULT_TARGET_ATTRIBUTE = "default";

	private static final String TARGET_NODE_NAME = "target";

	private static final String TARGET_NAME_ATTRIBUTE_NAME = "name";

	private static final String COMPILE_NODE_NAME = "compile";

	private static final String OUTPUT_ATTRIBUTE_NAME = "output";

	private static final String DEBUG_ATTRIBUTE_NAME = "debug";

	private static final String DEFINE_NODE_NAME = "define";

	private static final String DEFINE_NAME_ATTRIBUTE_NAME = "name";

	private static final String DEFINE_VALUE_ATTRIBUTE_NAME = "value";

	private static final String SOURCES_NODE_NAME = "sources";

	private static final String INCLUDES_NODE_NAME = "includes";

	private static final String INCLUDES_NAME_ATTRIBUTE_NAME = "name";

	private static final String REFERENCES_NODE_NAME = "references";

	private static final int ACTION_LOAD_FIRST = 0;

	private static final int ACTION_LOAD_NEXT = 1;

	private static final int ACTION_FIND = 2;

	private static final int ACTION_SAVE = 3;

	private Element projectElement;

	private String projectName;

	private String projectScrDir;

	private String projectBuildDir;

	private String defaultTarget;

	private Element targetElement;

	private String targetName;

	private Element compileElement;

	private String compileOutput;

	private String compileDebug;

	private Element defineElement;

	private String defineName;

	private String defineValue;

	private Element sourcesElement;

	private Element sourcesIncludesElement;

	private String sourcesIncludesName;

	private Element referencesElement;

	private Element referencesIncludesElement;

	private String referencesIncludesName;

	// Helper class for working with paths taking in account any path separator
	private class CSAntPath {
		private String path;

		private String dir;

		private String file;

		private String ext;

		public CSAntPath(String path) {
			setPath(path);
		}

		private void parsePath() {
			dir = null;
			file = null;
			ext = null;

			if (path == null || path.length() == 0) {
				return;
			}

			int dirIndex1 = path.lastIndexOf("/");
			int dirIndex2 = path.lastIndexOf("\\");
			int dirIndex = Math.max(dirIndex1, dirIndex2);
			if (dirIndex >= 0) {
				dir = path.substring(0, dirIndex);
			}

			int extIndex = path.lastIndexOf(".");
			if (extIndex > 0) {
				file = path.substring(dirIndex + 1, extIndex);
				if (extIndex + 1 < path.length()) {
					ext = path.substring(extIndex + 1, path.length());
				}
			} else {
				file = path.substring(dirIndex + 1, path.length());
			}
		}

		private void composePath() {
			path = file;
			if (dir != null) {
				path = dir + "/" + path;
			}
			if (ext != null) {
				path += "." + ext;
			}
		}

		public String getPath() {
			return path;
		}

		public void setPath(String value) {
			this.path = value;
			parsePath();
		}

		public String getDir() {
			return dir;
		}

		public void setDir(String value) {
			this.dir = value;
			composePath();
		}

		public String getFile() {
			return file;
		}

		public void setFile(String value) {
			this.file = value;
			composePath();
		}

		public String getExt() {
			return ext;
		}

		public void setExt(String value) {
			this.ext = value;
			composePath();
		}
	} // CSAntPath class

	public CSAntBuildFileManipulator() {
		super();
	}

	// Return first element that matches given name.
	private Element elementMatch(Node node, String elementName) {
		for (;;) {
			if (node == null) {
				return null;
			}
			if (node instanceof Element) {
				if (elementName == null
						|| node.getNodeName().equalsIgnoreCase(elementName)) {
					return (Element) node;
				}
			}
			node = node.getNextSibling();
		}
	}

	// Returns first element with given name.
	private Element getFirstElement(Node parent, String elementName) {
		return elementMatch(parent.getFirstChild(), elementName);
	}

	// Returns next element with given name.
	private Element getNextElement(Node node, String elementName) {
		if (node == null) {
			return null;
		}
		return elementMatch(node.getNextSibling(), elementName);
	}

	// Create and append new element.
	private Element createElement(Node parent, String elementName) {
		Element element = document.createElement(elementName);
		parent.appendChild(element);
		return element;
	}

	// Handle given element action.
	private Element handleElement(Node parent, Node node, String elementName,
			String tagName, String tagValue, int action, boolean create) {
		Element element = null;
		switch (action) {
		case ACTION_LOAD_FIRST:
			element = getFirstElement(parent, elementName);
			break;
		case ACTION_LOAD_NEXT:
			element = getNextElement(node, elementName);
			break;
		case ACTION_FIND:
			element = getFirstElement(parent, elementName);
			if (tagValue != null) {
				for (;;) {
					if (element == null) {
						break;
					}
					String value = element.getAttribute(tagName);
					if (tagValue.equals(value)) {
						break;
					}
					element = getNextElement(element, elementName);
				}
			}
			break;
		case ACTION_SAVE:
			return (Element) node;
		}
		if (element == null && create) {
			element = createElement(parent, elementName);
			if (tagName != null && tagValue != null) {
				element.setAttribute(tagName, tagValue);
			}
		}
		return element;
	}

	// Handle given action with attribute.
	private String handleAttribute(Element element, String attributeName,
			String attributeValue, int action) {
		if (element == null) {
			return null;
		}
		if (action == ACTION_SAVE) {
			if (attributeValue != null && attributeValue.length() > 0) {
				element.setAttribute(attributeName, attributeValue);
			} else {
				element.removeAttribute(attributeName);
			}
			return attributeValue;
		} else {
			return element.getAttribute(attributeName);
		}
	}

	// Functions for manipulating with <project> element.
	private void handleProjectAttributes(int action) {
		projectName = handleAttribute(projectElement, PROJECT_NAME_ATTRIBUTE,
				projectName, action);

		defaultTarget = handleAttribute(projectElement,
				PROJECT_DEFAULT_TARGET_ATTRIBUTE, defaultTarget, action);

		projectScrDir = handleAttribute(projectElement,
				PROJECT_SRC_DIR_ATTRIBUTE, projectScrDir, action);

		projectBuildDir = handleAttribute(projectElement,
				PROJECT_BUILD_DIR_ATTRIBUTE, projectBuildDir, action);
	}

	private void handleProject(int action, boolean create) {
		projectElement = handleElement(document, projectElement,
				PROJECT_NODE_NAME, null, null, action, create);
		handleProjectAttributes(action);
	}

	private void loadOrCreateProject() {
		handleProject(ACTION_LOAD_FIRST, true);
		if (projectName == null || projectName.length() == 0) {
			projectName = new CSAntPath(file.getName()).getFile();
			saveProject();
		}
	}

	private void saveProject() {
		handleProjectAttributes(ACTION_SAVE);
	}

	// private void saveProjectAndWrite() {
	// saveProject();
	// write();
	// }

	// Functions for manipulating with <target> element.
	private void handleTargetAttributes(int action) {
		targetName = handleAttribute(targetElement, TARGET_NAME_ATTRIBUTE_NAME,
				targetName, action);
	}

	private void handleTarget(String tagName, String tagValue, int action,
			boolean create) {
		targetElement = handleElement(projectElement, targetElement,
				TARGET_NODE_NAME, tagName, tagValue, action, create);
		handleTargetAttributes(action);
	}

//	private void loadFirstTarget(boolean create) {
//		handleTarget(null, null, ACTION_LOAD_FIRST, create);
//	}
//
//	private void loadNextTarget() {
//		handleTarget(null, null, ACTION_LOAD_NEXT, false);
//	}

	private void loadTarget(String targetName, boolean create) {
		handleTarget(TARGET_NAME_ATTRIBUTE_NAME, targetName, ACTION_FIND,
				create);
	}

	private void loadOrCreateTarget(String targetName) {
		loadTarget(targetName, true);
	}

	// Functions for manipulating with <compile> element.
	private void handleCompileAttributes(int action) {
		compileOutput = handleAttribute(compileElement, OUTPUT_ATTRIBUTE_NAME,
				compileOutput, action);

		compileDebug = handleAttribute(compileElement, DEBUG_ATTRIBUTE_NAME,
				compileDebug, action);
	}

	private void handleCompile(String tagName, String tagValue, int action,
			boolean create) {
		compileElement = handleElement(targetElement, compileElement,
				COMPILE_NODE_NAME, tagName, tagValue, action, create);
		handleCompileAttributes(action);
	}

	private void loadOrCreateCompile() {
		handleCompile(null, null, ACTION_LOAD_FIRST, true);
		if (compileOutput == null || compileOutput.length() == 0) {
			compileOutput = projectName + ".exe";
			saveCompile();
		}
		if (compileDebug == null || compileDebug.length() == 0) {
			compileDebug = "true";
			saveCompile();
		}
	}

	private void saveCompile() {
		handleCompileAttributes(ACTION_SAVE);
	}

	// Functions for manipulating with <define> element.
	private void handleDefineAttributes(int action) {
		defineName = handleAttribute(defineElement, DEFINE_NAME_ATTRIBUTE_NAME,
				defineName, action);

		defineValue = handleAttribute(defineElement,
				DEFINE_VALUE_ATTRIBUTE_NAME, defineValue, action);
	}

	private void handleDefine(String tagName, String tagValue, int action,
			boolean create) {
		defineElement = handleElement(compileElement, defineElement,
				DEFINE_NODE_NAME, tagName, tagValue, action, create);
		handleDefineAttributes(action);
	}

	private void loadFirstDefine(boolean create) {
		handleDefine(null, null, ACTION_LOAD_FIRST, create);
	}

	private void loadNextDefine() {
		handleDefine(null, null, ACTION_LOAD_NEXT, false);
	}

	private void loadDefine(String defineName, boolean create) {
		handleDefine(DEFINE_NAME_ATTRIBUTE_NAME, defineName, ACTION_FIND,
				create);
	}

	private void loadOrCreateDefine(String defineName) {
		loadDefine(defineName, true);
	}

	private void saveDefine() {
		handleDefineAttributes(ACTION_SAVE);
	}

	// Functions for manipulating with <sources> element.
	private void handleSources(int action, boolean create) {
		sourcesElement = handleElement(compileElement, sourcesElement,
				SOURCES_NODE_NAME, null, null, action, create);
	}

	private void loadOrCreateSources() {
		handleSources(ACTION_LOAD_FIRST, true);
	}

	// Functions for manipulating with <includes> element in <sources> node.
	private void handleSourcesIncludesAttributes(int action) {
		sourcesIncludesName = handleAttribute(sourcesIncludesElement,
				INCLUDES_NAME_ATTRIBUTE_NAME, sourcesIncludesName, action);
	}

	private void handleSourcesIncludes(String tagName, String tagValue,
			int action, boolean create) {
		sourcesIncludesElement = handleElement(sourcesElement,
				sourcesIncludesElement, INCLUDES_NODE_NAME, tagName, tagValue,
				action, create);
		handleSourcesIncludesAttributes(action);
	}

	private void loadFirstSourcesIncludes(boolean create) {
		handleSourcesIncludes(null, null, ACTION_LOAD_FIRST, create);
	}

	private void loadOrCreateNextSourcesIncludes() {
		handleSourcesIncludes(null, null, ACTION_LOAD_NEXT, true);
	}

	private void loadNextSourcesIncludes() {
		handleSourcesIncludes(null, null, ACTION_LOAD_NEXT, false);
	}

//	private void loadSourcesIncludes(String name, boolean create) {
//		handleSourcesIncludes(INCLUDES_NAME_ATTRIBUTE_NAME, name, ACTION_FIND,
//				create);
//	}

	private void saveSourcesIncludes() {
		handleSourcesIncludesAttributes(ACTION_SAVE);
	}

	// Functions for manipulating with <references> element.
	private void handleReferences(int action, boolean create) {
		referencesElement = handleElement(compileElement, referencesElement,
				REFERENCES_NODE_NAME, null, null, action, create);
	}

	private void loadOrCreateReferences() {
		handleReferences(ACTION_LOAD_FIRST, true);
	}

	// Functions for manipulating with <includes> element in <references> node.
	private void handleReferencesIncludesAttributes(int action) {
		referencesIncludesName = handleAttribute(referencesIncludesElement,
				INCLUDES_NAME_ATTRIBUTE_NAME, referencesIncludesName, action);
	}

	private void handleReferencesIncludes(String tagName, String tagValue,
			int action, boolean create) {
		referencesIncludesElement = handleElement(referencesElement,
				referencesIncludesElement, INCLUDES_NODE_NAME, tagName,
				tagValue, action, create);
		handleReferencesIncludesAttributes(action);
	}

	private void loadFirstReferencesIncludes(boolean create) {
		handleReferencesIncludes(null, null, ACTION_LOAD_FIRST, create);
	}

	private void loadNextReferencesIncludes() {
		handleReferencesIncludes(null, null, ACTION_LOAD_NEXT, false);
	}

	private void loadReferencesInclude(String name, boolean create) {
		handleReferencesIncludes(INCLUDES_NAME_ATTRIBUTE_NAME, name,
				ACTION_FIND, create);
	}

	private void loadOrCreateReferencesInclude(String name) {
		loadReferencesInclude(name, true);
	}

	private void saveReferencesInclude() {
		handleReferencesIncludesAttributes(ACTION_SAVE);
	}

	// Add value to list if value is not null.
	private void addToList(ArrayList list, Object value) {
		if (value != null) {
			list.add(value);
		}
	}

	public void addTarget(String targetName) {
		loadOrCreateProject();
		loadOrCreateTarget(targetName);
		if (defaultTarget == null || defaultTarget.length() == 0) {
			defaultTarget = targetName;
			saveProject();
		}
		// write();
	}

// No longer necessary
//	public String getAllTargetsForLanguageFileAsString(String affectedFiles) {
//		return "all";
//	}

//	public String[] getAllTargetsOfLanguage(String language) {
//		loadOrCreateProject();
//		ArrayList list = new ArrayList();
//		if (IBuildMechanismDescriptor.CSHARP_LANGUAGE.equals(language)) {
//			loadFirstTarget(false);
//			while (targetElement != null) {
//				addToList(list, targetName);
//				loadNextTarget();
//			}
//		}
//		return (String[]) list.toArray(new String[list.size()]);
//	}
//
//	public String[] getAllTargetsOfLanguageFile(String filename) {
//		String[] targets = getAllTargetsOfLanguage(IBuildMechanismDescriptor.CSHARP_LANGUAGE);
//		ArrayList list = new ArrayList();
//		for (int i = 0; i < targets.length; i++) {
//			targetName = targets[i];
//			loadTarget(targetName, true);
//			loadOrCreateCompile();
//			loadOrCreateSources();
//			loadSourcesIncludes(filename, false);
//			if (sourcesIncludesElement != null) {
//				list.add(targetName);
//			}
//		}
//		return (String[]) list.toArray(new String[list.size()]);
//	}

	protected Element getCscElementOfTarget(String targetName) {
		Node targetNode = getNodebyTag(TAGTARGET, ATTRNAMENAME, targetName);
		// Element cscNode = (Element) loopSearchForTag(targetNode,
		// TAGCSC,"destFile", binName);
		Node cscNode = null;
		Node compileNode=null;
		if (targetNode == null)
			return null;
		for (int i = 0; i < targetNode.getChildNodes().getLength(); i++) {
			Node compileinode = targetNode.getChildNodes().item(i);
			if (COMPILE_NODE_NAME.equals(compileinode.getNodeName())) {
				compileNode = compileinode;
			}
		}
		if (compileNode != null){
			for (int i = 0; i < compileNode.getChildNodes().getLength(); i++) {
				Node cscinode = compileNode.getChildNodes().item(i);
				if (SOURCES_NODE_NAME.equals(cscinode.getNodeName())) {
					cscNode = cscinode;
				}
			}
		}
		
		
		if (cscNode != null
				&& Element.class.isAssignableFrom(cscNode.getClass())) {
			Element cscElement = (Element) cscNode;
			return cscElement;
		}
		return null;
	}

	
	
	public String getBinDir() {
		loadOrCreateProject();
		return projectBuildDir;
	}

	public String getSrcDir() {
		loadOrCreateProject();
		return projectScrDir;
	}

	public String[] getSupportedLanguages() {
		return new String[] { IBuildMechanismDescriptor.CSHARP_LANGUAGE };
	}

	public void setBinDir(String dir) {
		loadOrCreateProject();
		projectBuildDir = dir;
		saveProject();
		// saveProjectAndWrite();
	}

	public void setSrcDir(String dir) {
		// We dont support this feature. The dir prefix is already in each file
		// path.
		// loadOrCreateProject();
		// projectScrDir = dir;
		// saveProject();
	}

// No longer necessary to implement
//	public boolean supportsLanguage(String language) {
//		return CSHARP_LANGUAGE.equals(language);
//	}

	public void addLanguageTarget(String targetName, String targetType,
			String supportedLanguage) {
		if (!IBuildMechanismDescriptor.CSHARP_LANGUAGE
				.equals(supportedLanguage)) {
			return;
		}
		loadOrCreateProject();
		loadOrCreateTarget(targetName);
		loadOrCreateCompile();

		// Modify compile output
		CSAntPath path = new CSAntPath(compileOutput);
		path.setExt(targetType);
		compileOutput = path.getPath();
		saveCompile();
		// write();
	}

	public IFile[] getSourcesOfLanguageTarget(String target) {
		loadOrCreateProject();
		loadOrCreateTarget(target);
		loadOrCreateCompile();
		loadOrCreateSources();
		loadFirstSourcesIncludes(false);

		ArrayList list = new ArrayList();
		IProject project = getBuildFile().getProject();
		while (sourcesIncludesElement != null) {
			addToList(list, project.getFile(sourcesIncludesName));
			loadNextSourcesIncludes();
		}
		return (IFile[]) list.toArray(new IFile[list.size()]);
	}

	protected boolean getDebuggingOutput(String targetName) {
		loadOrCreateProject();
		loadOrCreateTarget(targetName);
		loadOrCreateCompile();
		return "true".equalsIgnoreCase(compileDebug);
	}

	protected String[] getDefinitions(String targetName) {
		loadOrCreateProject();
		loadOrCreateTarget(targetName);
		loadOrCreateCompile();
		loadFirstDefine(false);

		ArrayList list = new ArrayList();
		while (defineElement != null) {
			if ("true".equalsIgnoreCase(defineValue)) {
				addToList(list, defineName);
			}
			loadNextDefine();
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	protected boolean getOptimization(String targetName) {
		return false;
	}

	public String[] getReferences(String target) {
		loadOrCreateProject();
		loadOrCreateTarget(target);
		loadOrCreateCompile();
		loadOrCreateReferences();
		loadFirstReferencesIncludes(false);

		ArrayList list = new ArrayList();
		while (referencesIncludesElement != null) {
			addToList(list, referencesIncludesName);
			loadNextReferencesIncludes();
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public int getWarningLevel(String targetName) {
		return 0;
	}

	public void setDebuggingOutput(SourceTarget target){
	    String targetName=target.getName();
	    boolean shouldCreateDebugOutput = target.isDebuggingOutput();
		loadOrCreateProject();
		loadOrCreateTarget(targetName);
		loadOrCreateCompile();

		if (shouldCreateDebugOutput) {
			compileDebug = "true";
		} else {
			compileDebug = "false";
		}
		saveCompile();
		// write();
	}

	public void setDefinitions(SourceTarget target) {
		String targetName=target.getName();
		String[] Definitions = target.getDefinitions();
		loadOrCreateProject();
		loadOrCreateTarget(targetName);
		loadOrCreateCompile();

		// Set all defines to false
		loadFirstDefine(false);
		while (defineElement != null) {
			defineValue = "false";
			saveDefine();
			loadNextDefine();
		}

		// Set true for given defines
		for (int i = 0; i < Definitions.length; i++) {
			String name = Definitions[i];
			loadOrCreateDefine(name);
			defineValue = "true";
			saveDefine();
		}
		// write();
	}

	public void setOptimization(SourceTarget target) {
		// Not supported by csant
	}

	//public void setReferences(String targetName, String[] referenceNames) {
	public void setReferences(SourceTarget target){
		targetName=target.getName();
		String[] referenceNames=target.getReferences();
		loadOrCreateProject();
		loadOrCreateTarget(targetName);
		loadOrCreateCompile();
		loadOrCreateReferences();

		// Remove all existing references
		Node child = referencesElement.getFirstChild();
		while (child != null) {
			Node next = child.getNextSibling();
			referencesElement.removeChild(child);
			child = next;
		}

		// Create new references
		for (int i = 0; i < referenceNames.length; i++) {
			loadOrCreateReferencesInclude(referenceNames[i]);
			saveReferencesInclude();
		}
		// write();
	}

	public void setWarningLevel(SourceTarget target) {
		// Not supported by csant
	}

	protected String getLanguageOfLanguageTarget(String targetName) {
		return IBuildMechanismDescriptor.CSHARP_LANGUAGE;
	}

	public String getTypeOfLanguageTarget(String targetName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

//	public boolean hasLanguageTarget(String targetname) {
//		loadOrCreateProject();
//		loadOrCreateTarget(targetName);
//		return targetElement != null;
//	}

	public void setSourcesToLanguageTarget(SourceTarget target) {
		String targetName=target.getName();
		IFile[] newSrc=target.getSources();
		loadOrCreateProject();
		loadOrCreateTarget(targetName);
		loadOrCreateCompile();
		loadOrCreateSources();
		loadFirstSourcesIncludes(false);

		int i = 0;
		for (;;) {
			if (i < newSrc.length) {
				// Create or rewrite existing nodes
				if (sourcesIncludesElement == null) {
					loadOrCreateNextSourcesIncludes();
				}
				IFile file = newSrc[i];
				String relpath = file.getParent().getName();
				String fileName = file.getName();
				sourcesIncludesName = relpath + "/" + fileName;
				saveSourcesIncludes();
				i++;
				loadNextSourcesIncludes();
			} else {
				// Delete unused nodes
				if (sourcesIncludesElement == null) {
					break;
				}
				Element toRemove = sourcesIncludesElement;
				loadNextSourcesIncludes();
				sourcesElement.removeChild(toRemove);
			}
		}
		saveSourcesIncludes();
		// write();
	}

	public ArrayList<String> getFullBuildCommand() {
		ArrayList<String> args = new ArrayList<String>();
		args.add("csant");
		args.add(getBuildFile().getName());
		args.add("-f");
		return args;
	}

	public ArrayList<String> getTargetBuildCommand(String[] affectedTargets) {
		ArrayList<String> result = getFullBuildCommand();
		for (int i = 0; i < affectedTargets.length; i++) {
			result.add(affectedTargets[i]);
		}
		return result;
	}

	public String suggestFileName() {
		return IBuildFileManipulator.BUILDFILENAMEPROJECT + ".csant";
	}

	public void setLanguageTargetType(SourceTarget target){
		//String targetName=target.getName();
		//String targetType=target.getType();
		// Seems not to be supported???
		
	}

	 
	
	protected Element addCSCTag(SourceTarget tgt) {
		// I hope this is not needet here
		return null;
	}

	
}
