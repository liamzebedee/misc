package org.emonic.base.build.msbuild;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.emonic.base.buildmechanism.AntLikeFileManipulator;
import org.emonic.base.buildmechanism.IBuildCommandRunner;
import org.emonic.base.buildmechanism.IBuildFileLayoutManipulator;
import org.emonic.base.buildmechanism.IBuildFileManipulator;
import org.emonic.base.buildmechanism.IBuildFrameworkManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.buildmechanism.IBuildSourceTargetManipulator;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.buildmechanism.Target;
import org.emonic.base.framework.FrameworkFactory;
import org.emonic.base.framework.IFrameworkInstall;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

// TODO: The msbuild-mechanism seems independent from capital/noncapital letters. How to handle this?

public class MSBuildBuildFileManipulator extends AntLikeFileManipulator implements
IBuildMechanismDescriptor,IBuildSourceTargetManipulator,
IBuildFileLayoutManipulator,IBuildFileManipulator,IBuildCommandRunner,
IBuildFrameworkManipulator{

	private static final String MSBUILD_XML_NAMESPACE = "http://schemas.microsoft.com/developer/msbuild/2003"; //$NON-NLS-1$

	private static final String XMLNS_ATTRIBUTE_NAME = "xmlns"; //$NON-NLS-1$

	private static final String PROJECT_NODE_NAME = "Project"; //$NON-NLS-1$

	private static final String PROPERTY_GROUP_NODE_NAME = "PropertyGroup"; //$NON-NLS-1$

	private static final String TARGET_NODE_NAME = "Target"; //$NON-NLS-1$

	private static final String TARGET_DEPENDSONTARGETS_ATTRIBUTE_NAME = "DependsOnTargets"; //$NON-NLS-1$

	private static final String TARGET_INPUTS_ATTRIBUTE_NAME = "Inputs"; //$NON-NLS-1$

	private static final String TARGET_NAME_ATTRIBUTE_NAME = "Name"; //$NON-NLS-1$

	private static final String TARGET_OUTPUTS_ATTRIBUTE_NAME = "Outputs"; //$NON-NLS-1$

	private static final String CSC_NODE_NAME = "Csc"; //$NON-NLS-1$

	private static final String CSC_DEBUGTYPE_ATTRIBUTE_NAME = "DebugType"; //$NON-NLS-1$

	private static final String CSC_DEFINECONSTANTS_ATTRIBUTE_NAME = "DefineConstants"; //$NON-NLS-1$

	private static final String CSC_DEBUGTYPE_FULL_ATTRIBUTE_VALUE = "full"; //$NON-NLS-1$

	private static final String CSC_OPTIMIZE_ATTRIBUTE_NAME = "Optimize"; //$NON-NLS-1$

	private static final String CSC_OUTPUTASSEMBLY_ATTRIBUTE_NAME = "OutputAssembly"; //$NON-NLS-1$

	private static final String CSC_REFERENCES_ATTRIBUTE_NAME = "References"; //$NON-NLS-1$

	private static final String CSC_SOURCES_ATTRIBUTE_NAME = "Sources"; //$NON-NLS-1$

	private static final String CSC_TARGETTYPE_ATTRIBUTE_NAME = "TargetType"; //$NON-NLS-1$

	private static final String CSC_WARNINGLEVEL_ATTRIBUTE_NAME = "WarningLevel"; //$NON-NLS-1$

	private static final String SRC_VARIABLE = "$(src)"; //$NON-NLS-1$

	//private static final String BIN_VARIABLE = "$(bin)"; //$NON-NLS-1$

	private static final String TAGTARGET = "Target";
	
	protected final static String DefaultTaskName = "All"; //$NON-NLS-1$

//	public MSBuildBuildFileManipulator(IFile file) {
//	super(file, new NullProgressMonitor());
//	Element projectNode = (Element) SearchForNode(PROJECT_NODE_NAME);
//	if (projectNode == null) {
//	projectNode = document.createElement(PROJECT_NODE_NAME);
//	projectNode.setAttribute(XMLNS_ATTRIBUTE_NAME,
//	MSBUILD_XML_NAMESPACE);
//	document.appendChild(projectNode);
//	//write();
//	}
//	}


	public MSBuildBuildFileManipulator(){
		super();
	}

	
	 public String[] getAllTargetNames(){
	    	Node[] nodes = getAllNodesOfTag(TARGET_NODE_NAME);
	    	String[] res= new String[nodes.length];
	    	for (int i =0; i < nodes.length;i++){
	    		res[i]=findTargetNodeName(nodes[i]);
	    	}
	    	return res;
	    }

	 
	 protected Element getCscElementOfTarget(String targetName) {
			Node targetNode = getNodebyTag(TAGTARGET, ATTRNAMENAME, targetName);
			// Element cscNode = (Element) loopSearchForTag(targetNode,
			// TAGCSC,"destFile", binName);
			Node cscNode = null;
			if (targetNode == null)
				return null;
			for (int i = 0; i < targetNode.getChildNodes().getLength(); i++) {
				Node cscinode = targetNode.getChildNodes().item(i);
				if (CSC_NODE_NAME.equals(cscinode.getNodeName())) {
					cscNode = cscinode;
				}
			}
			if (cscNode != null
					&& Element.class.isAssignableFrom(cscNode.getClass())) {
				Element cscElement = (Element) cscNode;
				return cscElement;
			}
			return null;
		}
	 
	 
	 public void addTarget(Target target) {
	        String targetname=target.getName();
			Element projectNode = (Element) SearchForNode(NODENAMEPROJECT);
			// Make sure that the target does not exist
			if (getNodebyTag(TAGTARGET, TARGET_NAME_ATTRIBUTE_NAME, targetname) == null) {
					Element elemNode = document.createElement(TAGTARGET);
					elemNode.setAttribute(TARGET_NAME_ATTRIBUTE_NAME, targetname);
					projectNode.appendChild(elemNode);
			}
		}


		public void deleteTarget(Target targetX) {
			String targetname=targetX.getName();
			Node target = getNodebyTag(TAGTARGET, ATTRNAMENAME, targetname);
			if (target != null){
				target.getParentNode().removeChild(target);
			}
		}

	 
	 protected Element addCSCTag(SourceTarget target) {
			Node targetNode = getNodebyTag(TAGTARGET,ATTRNAMENAME,target.getName());
			//System.out.println("Add csc node....");
			//TODO: Improve to rel. pathname!
			// We can only append if we have a artefact.
			if (target.getArtefact() != null){
				
				
				
				Element cscNode = appendChildWithAttrib((Element)targetNode, CSC_NODE_NAME, CSC_OUTPUTASSEMBLY_ATTRIBUTE_NAME, getRelPath(target.getArtefact()));
				cscNode.setAttribute(CSC_TARGETTYPE_ATTRIBUTE_NAME, target.getType());
				return cscNode;
			}
			return null;
		}
	 
	 protected String findTargetNodeName(Node targetNode) {
			NamedNodeMap attributes = targetNode.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) { // Loop through
																// Attributes
				Node a = attributes.item(i);
				// System.out.println(a.getNodeName()+ " " + a.getNodeValue());
				if (TARGET_NAME_ATTRIBUTE_NAME.equals(a.getNodeName())) {
					return a.getNodeValue();
				}
			}
			return EMPTYSTRING;
		}
		
	 public Target[] getAllTargets(){
	    	Node[] nodes = getAllNodesOfTag(TAGTARGET);
	    	Target[] results  = new Target[nodes.length]; 
	    	for (int i =0; i < nodes.length;i++){
	    		String targetName=findTargetNodeName(nodes[i]);
	    		results[i]= new Target();
	    		if (targetIsSourceTarget(targetName)){
	    			results[i]=new SourceTarget();
	    			((SourceTarget)results[i]).setDebuggingOutput(getDebuggingOutput(targetName));
	    			((SourceTarget)results[i]).setDefinitions(getDefinitions(targetName));
	    			((SourceTarget)results[i]).setLanguage(getLanguageOfLanguageTarget(targetName));
	    			((SourceTarget)results[i]).setOptimization(getOptimization(targetName));
	    			((SourceTarget)results[i]).setReferences(getReferences(targetName));
	    			((SourceTarget)results[i]).setSources(getSourcesOfLanguageTarget(targetName));
	    			((SourceTarget)results[i]).setType(getTypeOfLanguageTarget(targetName));
	    			((SourceTarget)results[i]).setWarningLevel(getWarningLevel(targetName));
	    		}
	    		results[i].setName(targetName);
	    		results[i].setDependencies(getTargetDependencies(targetName));
	    		
	    		// Normal or source target?
	    	}
	    	return results;
	    }

	private void setProject(){
		Element projectNode = (Element) SearchForNode(PROJECT_NODE_NAME);
		if (projectNode == null) {
			projectNode = document.createElement(PROJECT_NODE_NAME);
			projectNode.setAttribute(XMLNS_ATTRIBUTE_NAME,
					MSBUILD_XML_NAMESPACE);
			document.appendChild(projectNode);
		}
	}
//	public void setProject(String name) {
//	Element projectNode = (Element) SearchForNode(PROJECT_NODE_NAME);
//	if (projectNode == null) {
//	projectNode = document.createElement(PROJECT_NODE_NAME);
//	projectNode.setAttribute(XMLNS_ATTRIBUTE_NAME,
//	MSBUILD_XML_NAMESPACE);
//	document.appendChild(projectNode);
//	}
//	// We set the name of the project as default target and ad it as target
//	if (name != null){
//	projectNode.setAttribute("DefaultTargets",name);
//	addTarget(name);
//	// TODO to make this working:
//	// Let this target depend from all language targets
//	// For now, only the unit tests work, no real build!
//	}
//	write();

//	}

	private void addPropertyInProject(String name, String value) {
		Element projectNode = (Element) SearchForNode(PROJECT_NODE_NAME);
		Element propertyGroupNode = (Element) LoopSearch(projectNode,
				PROPERTY_GROUP_NODE_NAME);

		if (propertyGroupNode == null) {
			propertyGroupNode = document
			.createElement(PROPERTY_GROUP_NODE_NAME);
			projectNode.appendChild(propertyGroupNode);
		}
		Node oldProp=null;
		for (int i = 0; i < propertyGroupNode.getChildNodes().getLength();i++){
			Node act = propertyGroupNode.getChildNodes().item(i);
			if (Element.class.isAssignableFrom(act.getClass())){
				if (((Element)act).getTagName().equals(name)){
					oldProp=act;
				}
			}
		}
		if (oldProp!=null){
			propertyGroupNode.removeChild(oldProp);
		}
		Element property = document.createElement(name);
		property.appendChild(document.createTextNode(value));
		propertyGroupNode.appendChild(property);

		//write();
	}

	public void removeSrcFromTarget( String target,String fileToExclude) {
		Element targetNode = (Element) getNodebyTag(TARGET_NODE_NAME,
				TARGET_NAME_ATTRIBUTE_NAME, target);

		if (targetNode != null) {
			String inputs = targetNode
			.getAttribute(TARGET_INPUTS_ATTRIBUTE_NAME);
			String[] inputArray = inputs.split(";"); //$NON-NLS-1$
			for (int i = 0; i < inputArray.length; i++) {
				String fileName = new File(inputArray[i]).getName();
				if (fileName.equals(fileToExclude)) {
					int semiColonIndex = inputs.indexOf(';');
					if (semiColonIndex == -1) {
						targetNode
						.removeAttribute(TARGET_INPUTS_ATTRIBUTE_NAME);
						targetNode
						.removeAttribute(TARGET_OUTPUTS_ATTRIBUTE_NAME);
					} else {
						int nextIndex = inputs.indexOf(';', semiColonIndex + 1);
						if (nextIndex == -1) {
							targetNode.setAttribute(
									TARGET_INPUTS_ATTRIBUTE_NAME, inputs
									.substring(0, semiColonIndex));
						} else {
							targetNode.setAttribute(
									TARGET_INPUTS_ATTRIBUTE_NAME, inputs
									.substring(0, semiColonIndex)
									+ inputs.substring(nextIndex));
						}
					}
					//write();
					break;
				}
			}
		}
	}




	private String getRelPath(String container) {
		String cn = container;
		// Do we have a absolute or relative path?

		String f = ResourcesPlugin.getWorkspace().getRoot().getLocation()
		.toOSString();
		String bfl = f + getBuildFile().getProject().getFullPath().toOSString();
		if (bfl.equals(cn)) {
			return ".";
		}
		// Is this location part of the container?
		if (bfl.length() < container.length()){
			String cb = container.substring(0, bfl.length());
			// bfl.length()+1: bfl does not contain the seperator we have to
			// stripe!
			if (cb.equals(bfl)) {
				cn = container.substring(bfl.length() + 1, container.length());
			}
		}
		// Seems not to work (at least xbuild replaces ${src} with "", independent
		// from the settings!
// Seems not to work?
//		if ( cn != null && getSrcDir() != null &&  cn.startsWith(getSrcDir())) {
//			return SRC_VARIABLE  + cn.substring(getSrcDir().length());
//		}
		return cn;
	}

//	private void addOrModifyCSCTarget(String targetName, String folderPath,
//			String filename, String targetType, boolean optimizeCode,
//			boolean debugCode, String warnLevel, String references,
//			String defines) {
//		Element targetNode = (Element) getNodebyTag(TARGET_NODE_NAME,
//				TARGET_NAME_ATTRIBUTE_NAME, targetName);
//
//		if (targetNode == null) {
//			targetNode = document.createElement(TARGET_NODE_NAME);
//			Element projectNode = (Element) SearchForNode(PROJECT_NODE_NAME);
//			projectNode.appendChild(targetNode);
//			targetNode.setAttribute(TARGET_NAME_ATTRIBUTE_NAME, targetName);
//		}
//
//		Element cscNode = (Element) LoopSearch(targetNode, CSC_NODE_NAME);
//		if (cscNode == null) {
//			cscNode = document.createElement(CSC_NODE_NAME);
//			targetNode.appendChild(cscNode);
//		}
//
//		String file = getRelPath(folderPath) + File.separatorChar + filename;
//		String inputs = targetNode.getAttribute(TARGET_INPUTS_ATTRIBUTE_NAME);
//		String outputs = targetNode.getAttribute(TARGET_OUTPUTS_ATTRIBUTE_NAME);
//		if (outputs.equals("")) { //$NON-NLS-1$
//			if (inputs.equals("")) { //$NON-NLS-1$
//				targetNode.setAttribute(TARGET_INPUTS_ATTRIBUTE_NAME, file);
//			} else {
//				targetNode.setAttribute(TARGET_INPUTS_ATTRIBUTE_NAME, inputs
//						+ ';' + file);
//			}
//			int index = filename.lastIndexOf('.');
//			if (index == -1) {
//				outputs = BIN_VARIABLE + File.separatorChar + filename + '.'
//				+ targetType;
//				targetNode.setAttribute(TARGET_OUTPUTS_ATTRIBUTE_NAME, outputs);
//			} else {
//				outputs = BIN_VARIABLE + File.separatorChar
//				+ filename.substring(0, index) + '.' + targetType;
//				targetNode.setAttribute(TARGET_OUTPUTS_ATTRIBUTE_NAME, outputs);
//			}
//			cscNode.setAttribute(CSC_OUTPUTASSEMBLY_ATTRIBUTE_NAME, outputs);
//		} else {
//			if (inputs.equals("")) { //$NON-NLS-1$
//				targetNode.setAttribute(TARGET_INPUTS_ATTRIBUTE_NAME, file);
//			} else {
//				targetNode.setAttribute(TARGET_INPUTS_ATTRIBUTE_NAME, inputs
//						+ ";" + file);
//			}
//		}
//
//		if (debugCode) {
//			cscNode.setAttribute(CSC_DEBUGTYPE_ATTRIBUTE_NAME,
//					CSC_DEBUGTYPE_FULL_ATTRIBUTE_VALUE);
//		} else {
//			cscNode.removeAttribute(CSC_DEBUGTYPE_ATTRIBUTE_NAME);
//		}
//		cscNode.setAttribute(CSC_DEFINECONSTANTS_ATTRIBUTE_NAME, defines);
//		cscNode.setAttribute(CSC_OPTIMIZE_ATTRIBUTE_NAME, Boolean
//				.toString(optimizeCode));
//
//		String cscReferences = cscNode
//		.getAttribute(CSC_REFERENCES_ATTRIBUTE_NAME);
//		if (references == null || references.equals("")) { //$NON-NLS-1$
//			cscNode.setAttribute(CSC_REFERENCES_ATTRIBUTE_NAME, ""); //$NON-NLS-1$
//		} else {
//			if (cscReferences.equals("")) { //$NON-NLS-1$
//				cscNode.setAttribute(CSC_REFERENCES_ATTRIBUTE_NAME, references);
//			} else {
//				String[] targetReferences = cscReferences.split(";");
//				String[] referencesToAdd = references.split(";");
//				StringBuffer buffer = new StringBuffer();
//				synchronized (buffer) {
//					for (int i = 0; i < referencesToAdd.length; i++) {
//						if (!referencesToAdd[i].equals("")) {
//							boolean match = false;
//							for (int j = 0; j < targetReferences.length; j++) {
//								if (referencesToAdd[i]
//								                    .equals(targetReferences[j])) {
//									match = true;
//									break;
//								}
//							}
//
//							if (!match) {
//								buffer.append(referencesToAdd[i]).append(';');
//							}
//						}
//					}
//
//					if (buffer.length() != 0) {
//						buffer.append(cscReferences);
//						cscNode.setAttribute(CSC_REFERENCES_ATTRIBUTE_NAME,
//								buffer.toString());
//					}
//				}
//			}
//		}
//
//		String sources = cscNode.getAttribute(CSC_SOURCES_ATTRIBUTE_NAME);
//		if (sources.equals("")) { //$NON-NLS-1$
//			cscNode.setAttribute(CSC_SOURCES_ATTRIBUTE_NAME, file);
//		} else if (sources.indexOf(file) == -1) {
//			sources = cscNode.getAttribute(CSC_SOURCES_ATTRIBUTE_NAME) + ";"
//			+ file;
//			cscNode.setAttribute(CSC_SOURCES_ATTRIBUTE_NAME, sources);
//		}
//
//		cscNode.setAttribute(CSC_TARGETTYPE_ATTRIBUTE_NAME, targetType);
//		cscNode.setAttribute(CSC_WARNINGLEVEL_ATTRIBUTE_NAME, warnLevel);
//
//		//write();
//	}

//	public void addOrModifyLanguageTarget(String targetName, String folderPath,
//			String filename, String targetType, boolean optimizeCode,
//			boolean debugCode, String warnLevel, String references,
//			String defines, String supportedLanguage) {
//		if (supportedLanguage.equals(CSHARP_LANGUAGE)) {
//			addOrModifyCSCTarget(targetName, folderPath, filename, targetType,
//					optimizeCode, debugCode, warnLevel, references, defines);
//		}
//	}

//	public void addTarget(String targetName) {
//		Element projectNode = (Element) SearchForNode(PROJECT_NODE_NAME);
//		Element targetNode = (Element) LoopSearch(projectNode, targetName);
//
//		// if a target node of the given name wasn't found, we'll create one
//		if (targetNode == null) {
//			targetNode = document.createElement(TARGET_NODE_NAME);
//			targetNode.setAttribute(TARGET_NAME_ATTRIBUTE_NAME, targetName);
//			projectNode.appendChild(targetNode);
//			//write();
//		}
//	}

//	private void addToExistingCSCTarget(String targetName, String relpath,
//			String fileName) {
//		Element targetNode = (Element) getNodebyTag(TARGET_NODE_NAME,
//				TARGET_NAME_ATTRIBUTE_NAME, targetName);
//		if (targetNode != null) {
//			Element cscNode = (Element) LoopSearch(targetNode, CSC_NODE_NAME);
//			if (cscNode == null) {
//				return;
//			}
//
//			String file = getRelPath(relpath) + File.separatorChar + fileName;
//			String inputs = targetNode
//			.getAttribute(TARGET_INPUTS_ATTRIBUTE_NAME);
//			if (inputs.equals("")) { //$NON-NLS-1$
//				targetNode.setAttribute(TARGET_INPUTS_ATTRIBUTE_NAME, file);
//			} else {
//				targetNode.setAttribute(TARGET_INPUTS_ATTRIBUTE_NAME, inputs
//						+ ";" + file);
//			}
//
//			String sources = cscNode.getAttribute(CSC_SOURCES_ATTRIBUTE_NAME);
//			if (sources.equals("")) { //$NON-NLS-1$
//				cscNode.setAttribute(CSC_SOURCES_ATTRIBUTE_NAME, file);
//			} else if (sources.indexOf(file) == -1) {
//				sources = cscNode.getAttribute(CSC_SOURCES_ATTRIBUTE_NAME)
//				+ ";" + file;
//				cscNode.setAttribute(CSC_SOURCES_ATTRIBUTE_NAME, sources);
//			}
//
//			//write();
//		}
//	}

//	public void addToExistingLanguageTarget(String targetName, String relpath,
//			String fileName, String supportedLanguage) {
//		if (supportedLanguage.equals(CSHARP_LANGUAGE)) {
//			addToExistingCSCTarget(targetName, relpath, fileName);
//		}
//	}

	
	// No longer necessary
//	public String getAllTargetsForLanguageFileAsString(String affectedFiles) {
//		// TODO Auto-generated method stub
//		// Important: Else no inc build works"
//		//
//		return "All";
//	}

	// No longer necessary
//	public String[] getAllTargetsOfLanguage(String language) {
//		if (IBuildMechanismDescriptor.CSHARP_LANGUAGE.equals(language)) {
//			Node[] nodes = getAllNodesOfTag(TARGET_NODE_NAME);
//			ArrayList resNodeNames = new ArrayList();
//			for (int i = 0; i < nodes.length; i++) {
//				NodeList childNodes = nodes[i].getChildNodes();
//				for (int j = 0; j < childNodes.getLength(); j++) {
//					Node node = childNodes.item(j);
//					if (node.getNodeName().equals(CSC_NODE_NAME)) {
//						resNodeNames.add(((Element) nodes[i])
//								.getAttribute(TARGET_NAME_ATTRIBUTE_NAME));
//					}
//				}
//			}
//			return (String[]) resNodeNames.toArray(new String[resNodeNames
//			                                                  .size()]);
//		} else {
//			return new String[0];
//		}
//	}

	// No longer necessary
//	public String[] getAllTargetsOfLanguageFile(String filename) {
//		// TODO Auto-generated method stub
//		return new String[0];
//	}

	public IFile getBuildFile() {
		return file;
	}

	public IFile[] getSourcesOfLanguageTarget(String target) {
		Element targetNode = (Element) getNodebyTag(TARGET_NODE_NAME,
				TARGET_NAME_ATTRIBUTE_NAME, target);
		if (targetNode != null) {
			NodeList children = targetNode.getChildNodes();
			IProject project = getBuildFile().getProject();
			List files = new ArrayList();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element
						&& child.getNodeName().equals(CSC_NODE_NAME)) {
					Element element = (Element) child;
					String src =   element.getAttribute(
							CSC_SOURCES_ATTRIBUTE_NAME);
					String[] sources = src.split(";");
					for (int j = 0; j < sources.length; j++) {
						if (sources[j].startsWith(SRC_VARIABLE)) {
							IPath path = new Path(getSrcDir());
							files.add(project.getFile(path.append(sources[j]
							                                              .substring(SRC_VARIABLE.length()))));
						} else {
							if (sources[j] != ""){
								files.add(project.getFile(new Path(sources[j])));
							}
						}
					}
				}
			}
			return (IFile[]) files.toArray(new IFile[files.size()]);
		}
		return new IFile[0];
	}

	public String[] getReferences(String target) {
		Element targetNode = (Element) getNodebyTag(TARGET_NODE_NAME,
				TARGET_NAME_ATTRIBUTE_NAME, target);
		if (targetNode != null) {
			NodeList children = targetNode.getChildNodes();
			List targetReferences = new ArrayList();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element
						&& child.getNodeName().equals(CSC_NODE_NAME)) {
					Element element = (Element) child;
					String[] references = element.getAttribute(
							CSC_REFERENCES_ATTRIBUTE_NAME).split(";");
					for (int j = 0; j < references.length; j++) {
						if (!references[j].equals("")) { //$NON-NLS-1$
							targetReferences.add(references[j]);
						}
					}
				}
			}
			return (String[]) targetReferences
			.toArray(new String[targetReferences.size()]);
		}
		return new String[0];
	}

	public String getSrcDir() {
		Element projectNode = (Element) SearchForNode(PROJECT_NODE_NAME);
		Element propertyGroupNode = (Element) LoopSearch(projectNode,
				PROPERTY_GROUP_NODE_NAME);

		if (propertyGroupNode == null) {
			return null;
		}

		NodeList children = propertyGroupNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeName().equalsIgnoreCase("src")) {
				Text text = (Text) node.getChildNodes().item(0);
				return text.getData().trim();
			}
		}
		return null;
	}

	public String getBinDir() {
		Element projectNode = (Element) SearchForNode(PROJECT_NODE_NAME);
		Element propertyGroupNode = (Element) LoopSearch(projectNode,
				PROPERTY_GROUP_NODE_NAME);

		if (propertyGroupNode == null) {
			return null;
		}

		NodeList children = propertyGroupNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeName().equalsIgnoreCase("bin")) {
				Text text = (Text) node.getChildNodes().item(0);
				return text.getData().trim();
			}
		}
		return null;
	}

//	public String getSrcVar() {
//		return SRC_VARIABLE;
//	}

//	public String[] getSupportedFrameworks() {
//
//		return new String[] { FRAMEWORK_NA };
//	}

	public String[] getSupportedLanguages() {
		return new String[] { IBuildMechanismDescriptor.CSHARP_LANGUAGE };
	}

//	public String getTargetFrameworkRelease() {
//
//		return FRAMEWORK_NA;
//	}

//	public String getTargetFrameworkType() {
//		// xbuild is not really usable on Windows at the moment, so if the user
//		// is on Windows, we will assume that the target is Microsoft's
//		// implementation
//		if (Platform.OS_WIN32.equals(Platform.getOS())) {
//			return FRAMEWORK_MS;
//		}
//		return FRAMEWORK_MONO;
//	}

//	public void setTargetFramework(String targetFramework) {
//		// TODO Auto-generated method stub
//
//	}

	
// Not longer necessary to implement
//	public boolean supportsLanguage(String language) {
//		return CSHARP_LANGUAGE.equals(language);
//	}

//	public void addLanguageTarget(String targetName, String targetType,
//			String supportedLanguage) {
//		if (supportedLanguage.equals(CSHARP_LANGUAGE)) {
//			addOrModifyCSCTarget(targetName,  targetType);
//		}
//
//	}
//
//	private void addOrModifyCSCTarget(String targetName, String targetType) {
//		Element targetNode = (Element) getNodebyTag(TARGET_NODE_NAME,
//				TARGET_NAME_ATTRIBUTE_NAME, targetName);
//
//		if (targetNode == null) {
//			targetNode = document.createElement(TARGET_NODE_NAME);
//			Element projectNode = (Element) SearchForNode(PROJECT_NODE_NAME);
//			projectNode.appendChild(targetNode);
//			targetNode.setAttribute(TARGET_NAME_ATTRIBUTE_NAME, targetName);
//		}
//
//		Element cscNode = (Element) LoopSearch(targetNode, CSC_NODE_NAME);
//		if (cscNode == null) {
//			cscNode = document.createElement(CSC_NODE_NAME);
//			targetNode.appendChild(cscNode);
//		}
//		
//		String outputs = BIN_VARIABLE + File.separatorChar + targetName + '.' + targetType;
//		targetNode.setAttribute(TARGET_OUTPUTS_ATTRIBUTE_NAME, outputs);
//
//		cscNode.setAttribute(CSC_TARGETTYPE_ATTRIBUTE_NAME, targetType);
//		cscNode.setAttribute(CSC_OUTPUTASSEMBLY_ATTRIBUTE_NAME, outputs);
        // Get the all target if available
//		Element allNode = (Element) getNodebyTag(TARGET_NODE_NAME,
//				TARGET_NAME_ATTRIBUTE_NAME, "All");
//		if (allNode!=null){
//			// Make it depend from the new target
//			String alldepends = allNode.getAttribute(TARGET_DEPENDSONTARGETS_ATTRIBUTE_NAME);
//			String[] depends = alldepends.split(";"); //$NON-NLS-1$
//			boolean alreadyThere=false;
//			for (int i =0; i<depends.length;i++){
//				if (depends[i].equals(targetName)){
//					alreadyThere=true;
//				}
//			}
//			if (! alreadyThere){
//				if (alldepends.equals("")){
//					allNode.setAttribute(TARGET_DEPENDSONTARGETS_ATTRIBUTE_NAME, targetName);
//				} else {
//					allNode.setAttribute(TARGET_DEPENDSONTARGETS_ATTRIBUTE_NAME, alldepends + ";"+ targetName);
//				}
//			}
//		}

		//write();
//	}
	
	 public String getDefaultTargetName(){
	    	return DefaultTaskName;
	    }

	 protected Element getDefaultTarget() {
			Element projectNode = (Element) SearchForNode(NODENAMEPROJECT);
			Attr n = projectNode.getAttributeNode(DefaultTaskName);
			String defTargN = n.getValue();
			return (Element) getNodebyTag(TAGTARGET, ATTRNAMENAME, defTargN);
		}

	 
	 // TODO Untested code: Is this right?
	 public String [] getTargetDependencies(String targetName){
	    	Node target = getNodebyTag(TAGTARGET, ATTRNAMENAME, targetName);
	    	if (target != null && Element.class.isAssignableFrom(target.getClass())){
	    		String d = ((Element)target).getAttribute(TARGET_DEPENDSONTARGETS_ATTRIBUTE_NAME);
	    		if (d != null) {
	    			String[] deps = d.split(";");
	    			return deps;
	    		} 
	    	}
	    	return new String[0];
	    }
	 
	 public void setTargetDependencies(Target targetX){
	    	String targetName=targetX.getName();
	    	String[] dependencies = targetX.getDependencies();
	    	Node target = getNodebyTag(TAGTARGET, ATTRNAMENAME, targetName);
	    	if (target != null && Element.class.isAssignableFrom(target.getClass())){
	    		Attr d = ((Element)target).getAttributeNode(TARGET_DEPENDSONTARGETS_ATTRIBUTE_NAME);
	    		String deps="";
	    		for (int i =0;i< dependencies.length;i++){
	    			deps +=dependencies[i];
	    			if (i<dependencies.length-1) deps +=";";
	    		}
	    		if (d == null) {
	    			((Element)target).setAttribute(TARGET_DEPENDSONTARGETS_ATTRIBUTE_NAME, deps);	
	    		} else {
	    			d.setValue(deps);
	    		}
	    	}
	    }
	    

	private Element getCscNodeOfTarget(String targetName){
		Element targetNode = (Element) getNodebyTag(TARGET_NODE_NAME,
				TARGET_NAME_ATTRIBUTE_NAME, targetName);
		if (targetNode != null){
			Node cscNode = LoopSearch(targetNode, CSC_NODE_NAME);
			if (cscNode != null && Element.class.isAssignableFrom(cscNode.getClass())){
				return (Element) cscNode;
			}
		}
		return null;
	}







	public void setWarningLevel(SourceTarget target) {
		String targetName=target.getName();
		int warningLevel=target.getWarningLevel();
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null){
			cscNode.setAttribute(CSC_WARNINGLEVEL_ATTRIBUTE_NAME, Integer.toString(warningLevel));
			//write();
		}
	}

	public void setBinDir(String dir) {
		addPropertyInProject("bin",dir);
	}



	public void setSrcDir(String dir) {
		addPropertyInProject("src",dir);
	}

	//public void setReferences(String targetName, String[] referenceNames) {
	public void setReferences(SourceTarget target){
		String targetName= target.getName();
		String[] referenceNames = target.getReferences();
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null){
			String cscReferences = cscNode
			.getAttribute(CSC_REFERENCES_ATTRIBUTE_NAME);
			if (referenceNames == null || referenceNames.length==0) { //$NON-NLS-1$
				cscNode.setAttribute(CSC_REFERENCES_ATTRIBUTE_NAME, ""); //$NON-NLS-1$
			} else {
				if (cscReferences.equals("")) { //$NON-NLS-1$
					String references = "";
					for (int i = 0; i < referenceNames.length;i++){
						references+=referenceNames[i];
						if (i < referenceNames.length-1){
							references += ";";
						}
					}
					cscNode.setAttribute(CSC_REFERENCES_ATTRIBUTE_NAME, references);
				} else {
					String[] targetReferences = cscReferences.split(";");
					StringBuffer buffer = new StringBuffer();
					synchronized (buffer) {
						for (int i = 0; i < referenceNames.length; i++) {
							if (!referenceNames[i].equals("")) {
								boolean match = false;
								for (int j = 0; j < targetReferences.length; j++) {
									if (referenceNames[i]
									                   .equals(targetReferences[j])) {
										match = true;
										break;
									}
								}

								if (!match) {
									buffer.append(referenceNames[i]).append(';');
								}
							}
						}

						if (buffer.length() != 0) {
							buffer.append(cscReferences);
							cscNode.setAttribute(CSC_REFERENCES_ATTRIBUTE_NAME,
									buffer.toString());
						}
					}
				}
			}
		}
	}

	public String[] getDefinitions(String targetName) {
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null){
			return cscNode.getAttribute(CSC_DEFINECONSTANTS_ATTRIBUTE_NAME).split(";");
		}
		return new String[0];
	}

	public void setDefinitions(SourceTarget target) {
		String targetName=target.getName();
		String[] Definitions = target.getDefinitions();
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null){
			String definitions="";
			for (int i = 0; i < Definitions.length;i++){
				definitions+=Definitions[i];
				if (i < Definitions.length-1){
					definitions += ";";
				}
			}
			cscNode.setAttribute(CSC_DEFINECONSTANTS_ATTRIBUTE_NAME, definitions);
			//write();
		}
		//write();
	}


	public void setDebuggingOutput(SourceTarget target){
	    String targetName=target.getName();
	    boolean shouldCreateDebugOutput = target.isDebuggingOutput();
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null){
			if (shouldCreateDebugOutput) {
				cscNode.setAttribute(CSC_DEBUGTYPE_ATTRIBUTE_NAME,
						CSC_DEBUGTYPE_FULL_ATTRIBUTE_VALUE);
			} else {
				cscNode.removeAttribute(CSC_DEBUGTYPE_ATTRIBUTE_NAME);
			}
			//write();
		}
		//write();
	}

	protected boolean getDebuggingOutput(String targetName) {
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null){
			String debuggingOutput= cscNode.getAttribute(CSC_DEBUGTYPE_ATTRIBUTE_NAME);
			if (debuggingOutput.equalsIgnoreCase(CSC_DEBUGTYPE_FULL_ATTRIBUTE_VALUE)) {
				return true;
			}
		}
		return false;
	}


	public void setOptimization(SourceTarget target) {
		String targetName=target.getName();
		boolean shouldOptimize=target.isOptimization();
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null){
			cscNode.setAttribute(CSC_OPTIMIZE_ATTRIBUTE_NAME, Boolean
					.toString(shouldOptimize));
			//write();
		}
		//write();
	}

	protected boolean getOptimization(String targetName) {
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null){
			return Boolean.valueOf(cscNode.getAttribute(CSC_OPTIMIZE_ATTRIBUTE_NAME)).booleanValue();
		}
		return false;
	}

	public int getWarningLevel(String targetName) {
		Element cscNode = getCscNodeOfTarget(targetName);
		if (cscNode != null){
			try {
			   return Integer.parseInt(cscNode.getAttribute(CSC_WARNINGLEVEL_ATTRIBUTE_NAME));
		
			} catch (NumberFormatException e){};
		}
		return 0;
	}



	public void setBuildFile(IFile file){
		super.setBuildFile(file);
		setProject();

	}

	public String getLanguageOfLanguageTarget(String targetName) {
		return IBuildMechanismDescriptor.CSHARP_LANGUAGE;
	}

	public String getTypeOfLanguageTarget(String targetName) {
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null)
			return cscNode.getAttribute(CSC_TARGETTYPE_ATTRIBUTE_NAME);
		return "exe";
	}

//	public boolean hasLanguageTarget(String targetName) {
//		Element cscNode=getCscNodeOfTarget(targetName);
//		if (cscNode!=null)
//			return true;
//		return false;
//	}

	public void setSourcesToLanguageTarget(SourceTarget target) {
		String targetName=target.getName();
		IFile[] newSrc=target.getSources();
		
		
		Element targetNode = (Element) getNodebyTag(TARGET_NODE_NAME,
				TARGET_NAME_ATTRIBUTE_NAME, targetName);
		if (targetNode != null) {
			Element cscNode = (Element) LoopSearch(targetNode, CSC_NODE_NAME);
			if (cscNode == null) {
				return;
			}
			String res="";
			for (int i =0; i < newSrc.length;i++){
				String relpath=newSrc[i].getParent().getName();
				String fileName=newSrc[i].getName();
				String file = getRelPath(relpath) + File.separatorChar + fileName;
				if (res.equals("")){
					res = file;
				} else {
					res +=";"+file;
				}
			}
			//targetNode.setAttribute(TARGET_INPUTS_ATTRIBUTE_NAME, res);
			cscNode.setAttribute(CSC_SOURCES_ATTRIBUTE_NAME, res);
		}
		
	}
	
	private static File getXBuildExecutable(File directory) {
		File candidate = new File(directory, "xbuild.bat");
		if (candidate.exists()) {
			return candidate;
		}
		
		candidate = new File(directory, "xbuild");
		if (candidate.exists()) {
			return candidate;
		}
		
		directory = new File(directory, "bin");
		candidate = new File(directory, "xbuild.bat");
		if (candidate.exists()) {
			return candidate;
		}
		
		candidate = new File(directory, "xbuild");
		if (candidate.exists()) {
			return candidate;
		}
		return null;
	}

	private static String getMSBuildCommand() {
		IFrameworkInstall install = FrameworkFactory.getDefaultFrameworkInstall();
		if (install != null) {
			if (install.getType().equals(IFrameworkInstall.MICROSOFT_DOT_NET_FRAMEWORK)) {
				File directory = install.getInstallLocation();
				File msbuildBinary = new File(directory, "MSBuild.exe");
				if (msbuildBinary.exists()) {
					return msbuildBinary.getAbsolutePath();
				}
			} else {
				File candidate = getXBuildExecutable(install.getInstallLocation());
				if (candidate != null) {
					return candidate.getAbsolutePath();
				}
			}
		}
		return Platform.getOS().equals(Platform.OS_WIN32) ? "MSBuild" : "xbuild";
	}

	// TODO Improve!
	public ArrayList<String> getFullBuildCommand() {
		ArrayList<String> args = new ArrayList<String>();
		args.add(getMSBuildCommand());
		args.add(getBuildFile().getName());
		return args;
	}

	// TODO Improve!
	public ArrayList<String> getTargetBuildCommand(String[] targets) {
		if (targets.length == 0) {
			return getFullBuildCommand();
		}
		ArrayList<String> args = new ArrayList<String>();
		ArrayList<String> alltargets = new ArrayList<String>();
		for (int i = 0; i < targets.length; i++) {
			String s = targets[i];
			if (i < targets.length - 1) {
				s += ";";
			}
			alltargets.add(s);
		}
		args.add(getMSBuildCommand());
		args.add(getBuildFile().getName());
		args.add("/target:");
		args.addAll(alltargets);
		return args;
	}
	
	public String suggestFileName() {
		return IBuildFileManipulator.BUILDFILENAMEPROJECT + ".proj"; //$NON-NLS-1$
	}

	public void setLanguageTargetType(SourceTarget target){
		String targetName=target.getName();
		String targetType=target.getType();
		Element cscNode=getCscNodeOfTarget(targetName);
		if (cscNode!=null)
		   cscNode.setAttribute(CSC_TARGETTYPE_ATTRIBUTE_NAME,targetType);
		
	}
	
	public void renameTarget(Target targetX){
    	String orgName=targetX.getName();
    	String newName=targetX.getNewName();
    	Node target = getNodebyTag(TAGTARGET, ATTRNAMENAME, orgName);
    	if (target instanceof Element){
    		Element eTarget = (Element) target;
    		eTarget.setAttribute(ATTRNAMENAME, newName);
    	}
    }


	public void setTargetFramework(String targetFramework, String Release) {
	    		//setPropertyInProject("TargetFrameworkVersion",Release);
		Node prjnode = SearchForNode("Project");
		((Element) prjnode).setAttribute("ToolsVersion",Release);
  	}
		



	public String getTargetFrameworkType() {
		return "net";
	}


	public String getTargetFrameworkRelease() {
		Node prjnode = SearchForNode("Project");
		return ((Element) prjnode).getAttribute("ToolsVersion");
		
	}


	public String[] getSupportedFrameworks() {
		
		String[] res = {"net"};
		return res;
	}


	public String[] getSupportedReleases(String string) {
		// TODO Auto-generated method stub
		String[] res= {"2.0","3.5","4.0"};
		return res;
	}
	
}
