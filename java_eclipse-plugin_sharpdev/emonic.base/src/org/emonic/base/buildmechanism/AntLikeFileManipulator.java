package org.emonic.base.buildmechanism;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The class AntLikeFileManipulator encapsulates the common methods between the ant- and the nant-filemanipulator
 * Propably most of them are usefull for other similar buildmechanisms, too (msbuild, csant)
 * So, this classis a good starting point for creating new xml-based building mechanisms. 
 * @author bb
 *
 */
public abstract class AntLikeFileManipulator extends XMLFileManipulator implements IBuildSourceTargetManipulator{
	protected static final String ATTRNAMENAME = "name"; //$NON-NLS-1$

	protected static final String NODENAMEPROJECT = "project"; //$NON-NLS-1$

	protected static final String NODENAMEPROPERTY = "property"; //$NON-NLS-1$

	protected static final String TAGTARGET = "target"; //$NON-NLS-1$

	protected static final String TAGSRC = "src"; //$NON-NLS-1$

	protected static final String EMPTYSTRING = ""; //$NON-NLS-1$

	protected static final String EXTENSIONDLL = ".dll"; //$NON-NLS-1$

	protected static final String EXTENSIONEXE = ".exe"; //$NON-NLS-1$

	protected static final String TARGETTYPEWINEXE = "winexe"; //$NON-NLS-1$

	protected static final String TARGETTYPEEXE = "exe"; //$NON-NLS-1$

	protected static final String BUILDVARNAME = "${build}"; //$NON-NLS-1$

	protected static final String TAGCSC = "csc"; //$NON-NLS-1$

	protected static final String ATTROPTIMIZE = "optimize"; //$NON-NLS-1$

	protected static final String ATTRDEBUG = "debug"; //$NON-NLS-1$

	protected static final String ATTRVALFALSE = "false"; //$NON-NLS-1$

	protected static final String ATTRVALTRUE = "true"; //$NON-NLS-1$

	protected static final String ATTRVALBIN = "bin"; //$NON-NLS-1$

	protected static final String ATTRDEFINE = "define"; //$NON-NLS-1$

	protected static final String ONESPACE = " "; //$NON-NLS-1$

	protected static final String MSGWARNINGNOTARGETFORFILE = "Warning: Found no target for "; //$NON-NLS-1$

	protected static final String VARSRC = "${src}"; //$NON-NLS-1$

	protected static final String ATTRREFERENCES = "references"; //$NON-NLS-1$

	protected static final String ATTRNAMEDEFAULT = "default"; //$NON-NLS-1$

	private static final String ATTNAMEDEPENDS = "depends"; //$NON-NLS-1$

	private static final String ATTRNAMEBASEDIR = "basedir"; //$NON-NLS-1$

	private static final String DEFAULTBASEDRLOCATION = "."; //$NON-NLS-1$

	protected static final String ATTRNAMELOCATION = "location"; //$NON-NLS-1$

	protected static final String ATTRTARGETTYPE = "targetType"; //$NON-NLS-1$
	
	protected static final String ATTRWARNLEVEL = "warnLevel"; //$NON-NLS-1$
	
	private static final String FILENAMESSEPERATOR = ","; //$NON-NLS-1$
	
	protected static final String ATTRDESTFILE = "destFile";
	
	protected  static final String ATTRINCLUDES = "includes"; //$NON-NLS-1$
	
	protected static final String ATTRDIR = "dir"; //$NON-NLS-1$

	private static final String VARBIN = "${build}";
	
	protected static final String BUILDVARPLAINNAME = "build";
	protected final static String DefaultTaskName = "all"; //$NON-NLS-1$
	/**
	 * 
	 * @return the default target node
	 */

	protected Element getDefaultTarget() {
		Element projectNode = (Element) SearchForNode(NODENAMEPROJECT);
		Attr n = projectNode.getAttributeNode(ATTRNAMEDEFAULT);
		String defTargN = n.getValue();
		return (Element) getNodebyTag(TAGTARGET, ATTRNAMENAME, defTargN);
	}

	protected void addDepends(Element target, String nodeName) {
		Attr d = target.getAttributeNode(ATTNAMEDEPENDS);
		if (d != null) {
			d.setValue(d.getValue() + ',' + nodeName);
		} else {
			target.setAttribute(ATTNAMEDEPENDS, nodeName);
		}
	}

	
	
	/**
	 * Set the project name
	 * 
	 * @param projName
	 */
	public void setProject(String projName) {
		// Lets look whether the project is allready found
		Element projectnode = (Element) SearchForNode(NODENAMEPROJECT);
		if (projectnode == null) {
			// No project defined, define new
			projectnode = document.createElement(NODENAMEPROJECT);
			document.appendChild(projectnode);
		}
		projectnode.setAttribute(ATTRNAMENAME, projName);
		projectnode.setAttribute(ATTRNAMEDEFAULT, DefaultTaskName);
		projectnode.setAttribute(ATTRNAMEBASEDIR, DEFAULTBASEDRLOCATION);
		// write();
	}
	
	public String getProject(){
		Element projectnode = (Element) SearchForNode(NODENAMEPROJECT);
		if (projectnode != null) {
			return projectnode.getAttribute(ATTRNAMENAME);
		}
		return "";
	}

	protected String getRelPath(IFile file) {
		String container = file.getFullPath().toOSString();
		String cn = container;
		try {
			// String f =
			// ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
			String bfl = getBuildFile().getFullPath().removeLastSegments(1)
					.toOSString();
			// Is this location part of the container?
			String cb = container.substring(0, bfl.length());

			if (bfl.equals(cn)) {
				// is the file in the build dir?
				cn = DEFAULTBASEDRLOCATION;
			} else {
				// bfl.length()+1: bfl does not contain the seperator we have to
				// stripe!
				if (cb.equals(bfl)) {
					cn = container.substring(bfl.length() + 1, container
							.length());
				}
			}
		} catch (Exception e) {
		}

		if (cn.equals(getSrcDir())) {
			cn = VARSRC;
		}
		return cn;
	}

	public void setBinDir(String dir) {
		setPropertyInProject("build", dir);
		// write();
	}

	public void setSrcDir(String dir) {
		setPropertyInProject("src", dir);
		// write();
	}

	public String getSrcDir() {
		Node node = getNodebyTag(NODENAMEPROPERTY, ATTRNAMENAME, TAGSRC);
		String result = EMPTYSTRING;
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) { // Loop through
															// Attributes
			Node a = attributes.item(i);
			if (ATTRNAMELOCATION.equals(a.getNodeName())) {
				result = a.getNodeValue();
			}
		}
		return result;
	}



	/**
	 * Set a property within the project node
	 * 
	 * @param name
	 * @param value
	 */

	protected void setPropertyInProject(String name, String value) {
		Element projectNode = (Element) SearchForNode(NODENAMEPROJECT);
		NodeList children = projectNode.getChildNodes();
		int lastProperty = -1;
		boolean found = false;
		for (int i = 0; i < children.getLength(); i++) {
			Node actual = children.item(i);
			if (Element.class.isAssignableFrom(actual.getClass())) {
				Element actualE = (Element) actual;
				if (actualE.getNodeName().equals(NODENAMEPROPERTY)) {
					lastProperty = i;
					String actName = actualE.getAttribute(ATTRNAMENAME);
					if (actName.equals(name)) {
						actualE.setAttribute(ATTRNAMELOCATION, value);
						found = true;
						break;
					}
				}

			}
		}
		if (!found) {
			// Property not set until now. Set it and append the ne node
			Element proppNode = document.createElement(NODENAMEPROPERTY);
			proppNode.setAttribute(ATTRNAMENAME, name);
			proppNode.setAttribute(ATTRNAMELOCATION, value);

			Node insertBefore = null;
			if (children.getLength() > lastProperty + 1) {
				insertBefore = children.item(lastProperty + 1);
			}
			projectNode.insertBefore(proppNode, insertBefore);
		}
	}

	public String[] getSupportedLanguages() {
		String[] res = { IBuildMechanismDescriptor.CSHARP_LANGUAGE };
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
			if (TAGCSC.equals(cscinode.getNodeName())) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.emonic.base.FileManipulators.IBuildfileManipulator#getSrcVar()
	 */
	protected String getSrcVar() {
		return VARSRC;
	}
	protected String getBinVar() {
		return VARBIN;
	}

	public void setDebuggingOutput(SourceTarget target){
	    String targetName=target.getName();
	    boolean shouldCreateDebugOutput = target.isDebuggingOutput();
		Element cscElement = getCscElementOfTarget(targetName);
		if (cscElement != null) {
			if (shouldCreateDebugOutput) {
				cscElement.setAttribute(ATTRDEBUG, ATTRVALTRUE);
			} else {
				cscElement.setAttribute(ATTRDEBUG, ATTRVALFALSE);
			}
		}
		// write();
	}

	protected boolean getDebuggingOutput(String targetName) {
		Element cscElement = getCscElementOfTarget(targetName);
		if (cscElement != null) {
			return Boolean.valueOf(cscElement.getAttribute(ATTRDEBUG))
					.booleanValue();

		}
		return false;
	}

	public void setOptimization(SourceTarget target) {
		String targetName=target.getName();
		boolean shouldOptimize=target.isOptimization();
		Element cscElement = getCscElementOfTarget(targetName);
		if (cscElement != null) {
			if (shouldOptimize) {
				cscElement.setAttribute(ATTROPTIMIZE, ATTRVALTRUE);
			} else {
				cscElement.setAttribute(ATTROPTIMIZE, ATTRVALFALSE);
			}
			// write();
		}
	}

	protected boolean getOptimization(String targetName) {
		Element cscElement = getCscElementOfTarget(targetName);
		if (cscElement != null) {
			return Boolean.valueOf(cscElement.getAttribute(ATTROPTIMIZE))
					.booleanValue();
		}
		return false;
	}

//	public boolean hasLanguageTarget(String targetname) {
//		Element cscElement = getCscElementOfTarget(targetname);
//		if (cscElement != null) {
//			return true;
//		}
//		return false;
//	}

	protected String getLanguageOfLanguageTarget(String targetName) {
		Element cscElement = getCscElementOfTarget(targetName);
		if (cscElement != null) {
			return IBuildMechanismDescriptor.CSHARP_LANGUAGE;
		}
		return IBuildMechanismDescriptor.LANGUAGE_NA;
	}

	/**
	 * 
	 * @param targetname
	 */
	public void addTarget(Target target) {
        String targetname=target.getName();
		Element projectNode = (Element) SearchForNode(NODENAMEPROJECT);
		// Make sure that the target does not exist
		if (getNodebyTag(TAGTARGET, ATTRNAMENAME, targetname) == null) {
				Element elemNode = document.createElement(TAGTARGET);
				elemNode.setAttribute(ATTRNAMENAME, targetname);
				projectNode.appendChild(elemNode);
		}
		
	}
	
	//TODO: Take away CSC-Tag if not needed!
	public void adaptLanguageTarget(Target target){
		if (target instanceof SourceTarget){
			if (((SourceTarget)target).getLanguage().equals(IBuildMechanismDescriptor.CSHARP_LANGUAGE))
			addCSCTag((SourceTarget)target);
		
	     }
	}
	
	
	protected abstract Element addCSCTag(SourceTarget tgt);
	
	

	protected void modifyCSCTarget(SourceTarget target) {
		// It is expected that the classes implementing this class 
		
	}

	public void deleteTarget(Target targetX) {
		String targetname=targetX.getName();
		Node target = getNodebyTag(TAGTARGET, ATTRNAMENAME, targetname);
		if (target != null){
			target.getParentNode().removeChild(target);
		}
	}

   

	/**
	 * Find the name of a target
	 * 
	 * @param targetNode
	 * @return the node name or a empty sting
	 */
	protected String findTargetNodeName(Node targetNode) {
		NamedNodeMap attributes = targetNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) { // Loop through
															// Attributes
			Node a = attributes.item(i);
			// System.out.println(a.getNodeName()+ " " + a.getNodeValue());
			if (ATTRNAMENAME.equals(a.getNodeName())) {
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

    protected IFile[] getSourcesOfLanguageTarget(String targetName) {
    	ArrayList res = new ArrayList();
		IProject proj = this.file.getProject();
		Node targetNode = getNodebyTag(TAGTARGET,ATTRNAMENAME,targetName);
		if (targetNode != null){
			NodeList nl =  ((Element)targetNode).getElementsByTagName(TAGSRC);
			for (int i = 0; i <nl.getLength();i++){
				Element e = (Element) nl.item(i);
				String path = e.getAttribute(ATTRDIR);
				if (VARSRC.equals( path )) {
					path = getSrcDir();
				}
				path = path + IPath.SEPARATOR ;
				String[] allIncs=  e.getAttribute(ATTRINCLUDES).split(FILENAMESSEPERATOR);
				for (int j=0; j<allIncs.length;j++){
					String cpath = path + allIncs[j];
					IFile fl = proj.getFile(cpath);
					res.add(fl);
				}

			}
			IFile[] result = new IFile[res.size()];
			result=(IFile[]) res.toArray(result);
			return result;
		}
    	return new IFile[0];
	}

	/**
	 * @param target
	 * @return
	 */
	public String[] getReferences(String targetName) {
		Element cscNode=null;
		Element target = (Element) getNodebyTag(TAGTARGET,ATTRNAMENAME,targetName);
		if (target != null && target.getChildNodes() != null) {
			for (int i = 0; i < target.getChildNodes().getLength(); i++) {
				Node cscinode = target.getChildNodes().item(i);
				if (TAGCSC.equals( cscinode.getNodeName() )) {
					cscNode = (Element) cscinode;
				}
			}
		}
		if (cscNode!=null){
			String refs = cscNode.getAttribute(ATTRREFERENCES);
			if (!EMPTYSTRING.equals( refs )) {
				return refs.split(FILENAMESSEPERATOR);
			}
			//return new String [0];
		}
		return new String[0];
	}

    
    
    public String getTypeOfLanguageTarget(String targetName) {
		Element cscElement=getCscElementOfTarget(targetName);
        if (cscElement != null){
        	String targetType=cscElement.getAttribute(ATTRTARGETTYPE);
        	if (targetType != null) return targetType;
		}
        return TARGETTYPEEXE;
	}

    protected int getWarningLevel(String targetName) {
    	
        int result= 0;
		Element cscElement=getCscElementOfTarget(targetName);
        if (cscElement != null){
        	try {
			   result=  Integer.parseInt(cscElement.getAttribute(ATTRWARNLEVEL));
        	} catch (NumberFormatException e){
        		
        	}
		}
        return result;
		
	}
    
	protected String[] getDefinitions(String targetName) {
		try{
			Element cscElement=getCscElementOfTarget(targetName);
			if (cscElement != null){
				String s = cscElement.getAttribute("defines");
				//TODO ensure that definitions containing , are treated correct!
				return s.split(",");
			}			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return new String[0];
	}
	
    protected boolean targetIsSourceTarget(String targetName){
        // At the moment, we support only c#.
    	// So, a source target is a target which has a csc-element as child
    	Element cscElement = getCscElementOfTarget(targetName);
		if (cscElement != null) {
			return true;
		}
		return false;
    
    }
    
    protected String [] getTargetDependencies(String targetName){
    	Node target = getNodebyTag(TAGTARGET, ATTRNAMENAME, targetName);
    	if (target != null && Element.class.isAssignableFrom(target.getClass())){
    		Attr d = ((Element)target).getAttributeNode(ATTNAMEDEPENDS);
    		if (d != null) {
    			String[] deps = d.getValue().split(",");
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
    		Attr d = ((Element)target).getAttributeNode(ATTNAMEDEPENDS);
    		String deps="";
    		for (int i =0;i< dependencies.length;i++){
    			if (! dependencies[i].equals("")){
    				deps +=dependencies[i];
    				if (i<dependencies.length-1) deps +=",";
    			}
    		}
    		if (d == null) {
    			((Element)target).setAttribute(ATTNAMEDEPENDS, deps);	
    		} else {
    			d.setValue(deps);
    		}
    	}
    }
    
    public String getDefaultTargetName(){
    	// TODO: Improve: Scan the buildfile for it
    	return DefaultTaskName;
    }
    
    /**
     * Rename a target.
     */
    public void renameTarget(Target targetX){
    	String orgName=targetX.getName();
    	String newName=targetX.getNewName();
    	Node target = getNodebyTag(TAGTARGET, ATTRNAMENAME, orgName);
    	if (target instanceof Element){
    		Element eTarget = (Element) target;
    		eTarget.setAttribute(ATTRNAMENAME, newName);
    	}
    }
    
}
