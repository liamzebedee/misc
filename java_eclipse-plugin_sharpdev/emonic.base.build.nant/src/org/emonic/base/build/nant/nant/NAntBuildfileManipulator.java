/****************************************************************************
 * Copyright (c) 2005, 2008 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Dec 30, 2005
 * emonic org.emonic.base.FileManipulators NAntBuildfileManipulator.java
 ****************************************************************************/

package org.emonic.base.build.nant.nant;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Preferences;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.buildmechanism.AntLikeFileManipulator;
import org.emonic.base.buildmechanism.IBuildCommandRunner;
import org.emonic.base.buildmechanism.IBuildFileLayoutManipulator;
import org.emonic.base.buildmechanism.IBuildFileManipulator;
import org.emonic.base.buildmechanism.IBuildFrameworkManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.buildmechanism.IBuildProjectManipulator;
import org.emonic.base.buildmechanism.IBuildSourceTargetManipulator;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.preferences.DefaultPrefsSetter;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * @author bb
 *
 */
public class NAntBuildfileManipulator extends AntLikeFileManipulator implements 
IBuildFrameworkManipulator, IBuildMechanismDescriptor,IBuildSourceTargetManipulator,
IBuildFileLayoutManipulator,IBuildProjectManipulator, IBuildCommandRunner{



	private static final String NANT_SETTINGS_CURRENTFRAMEWORK = "nant.settings.currentframework";
	//private static final String NODELOCATION = "value"; //$NON-NLS-1$
	//private static final String MSGREFDLL = "Referenced dll:"; //$NON-NLS-1$
	private static final String REGEXPBINVAR = "\\$\\{bin\\}"; //$NON-NLS-1$
	// TODO Since this is allowed to change by user we have to remove the constant!!
	private static final String REGEXPSRCVAR = "\\$\\{src\\}"; //$NON-NLS-1$
	// TODO Since this is allowed to change by user we have to remove the constant!!
	private static final String NODESOURCES = "sources"; //$NON-NLS-1$
	private static final String NODEREFERENCES = "references"; //$NON-NLS-1$
	// This differs from normal ant build (called there warnlevel)
	private static final String ATTRNAMEWARNINGLEVEL = "warninglevel"; //$NON-NLS-1$
	//private static final String ATTRNANEOUTPUT = "output"; //$NON-NLS-1$
	private static final String ATTRNAMEVALUE = "value"; //$NON-NLS-1$
	private static final String ATTRINCLUDE = "include"; //$NON-NLS-1$

	private static final String ATTRNAMEDEFINES = "define";
	private static final String ATTRTARGETTYPE="target";
	private static final String ATTRDESTFILE="output";

	public NAntBuildfileManipulator() {
		super();
	}

	/**
	 * NAnt wants "value" instead of "location"
	 */
	protected void setPropertyInProject(String name, String location){
		Element projectNode = (Element) SearchForNode(NODENAMEPROJECT);
		NodeList children = projectNode.getChildNodes();
		int lastProperty = -1;
		boolean found = false;
		for (int i =0; i< children.getLength();i++){
			Node actual = children.item(i);
			if (Element.class.isAssignableFrom(actual.getClass()) ){
				Element actualE = (Element) actual;
				if (actualE.getNodeName().equals(NODENAMEPROPERTY)){
					lastProperty=i;
					String actName  = actualE.getAttribute(ATTRNAMENAME);
					if (actName.equals(name) ){
						actualE.setAttribute(ATTRNAMEVALUE, location);
						found = true;
						break;
					}
				}

			}
		}
		if (! found){
			// Property not set until now. Set it and append the ne node
			Element proppNode = document.createElement(NODENAMEPROPERTY);
			proppNode.setAttribute(ATTRNAMENAME,name);
			proppNode.setAttribute(ATTRNAMEVALUE,location);

			Node insertBefore = null;
			if (children.getLength() > lastProperty+1 ){
				insertBefore = children.item(lastProperty+1);
			}
			projectNode.insertBefore(proppNode, insertBefore);
		}

	}

	/**
	 * NAnt wants "value" instead of property
	 */
	public String getSrcDir(){
		Node node =  getNodebyTag(NODENAMEPROPERTY,ATTRNAMENAME,TAGSRC);
		String result = EMPTYSTRING;
		if (node != null){
			NamedNodeMap attributes = node.getAttributes(); 
			for (int i = 0; i < attributes.getLength(); i++) { // Loop through Attributes
				Node a = attributes.item(i);
				if (ATTRNAMEVALUE.equals( a.getNodeName() )) {
					result = a.getNodeValue();
				}
			}
		}
		return result;
	}

	public String getBinDir(){
		Node node =  getNodebyTag(NODENAMEPROPERTY,ATTRNAMENAME,BUILDVARPLAINNAME);
		String result = EMPTYSTRING;
		if (node != null){
			NamedNodeMap attributes = node.getAttributes(); 
			for (int i = 0; i < attributes.getLength(); i++) { // Loop through Attributes
				Node a = attributes.item(i);
				if (ATTRNAMEVALUE.equals( a.getNodeName() )) {
					result = a.getNodeValue();
				}
			}
		}
		return result;
	}



	public IFile[] getSourcesOfLanguageTarget(String targetAsString){
		Node node = getNodebyTag(TAGTARGET,ATTRNAMENAME,targetAsString);
		Element cscNode=null;
		String[] res = new String[0];
		if (node != null){
			for (int i = 0; i<node.getChildNodes().getLength();i++){
				Node cscinode = node.getChildNodes().item(i);
				if (TAGCSC.equals( cscinode.getNodeName() )) {
					cscNode=(Element) cscinode;
				} 
			}
		}
		//ArrayList res = new ArrayList();
		if (cscNode!=null){
			// Has the CSC-Node a child with name "sources"?
			Element referenceNode = searchSourceNode(cscNode);
			if (referenceNode != null){
				res = getFilesNamesOf(referenceNode);
				//for (int i = 0; i< res.length;i++)System.out.println("Codefile:" + res[i]);

			}
		}



		//IFile[] result = new IFile[res.length];
		//result=(IFile[]) res.toArray(result);
		IProject proj = this.file.getProject();
		ArrayList<IFile> lst = new ArrayList<IFile>(); 
		for (int i = 0; i<res.length; i++) {
			try{
				lst.add(proj.getFile(res[i]));
				//result[i]= proj.getFile(res[i]);
			} catch (Exception e){
				// Ignore silently
			}
		}
		IFile[] result =new IFile[0];
		result = (IFile[]) lst.toArray( result);
		return result;
	}

	/**
	 * @param cscNode
	 * @return
	 */
	private Element searchSourceNode(Element cscNode) {
		for (int i = 0; i<cscNode.getChildNodes().getLength();i++){
			Node cscinode = cscNode.getChildNodes().item(i);
			if (NODESOURCES.equals( cscinode.getNodeName() )) {
				return (Element) cscinode;
			} 
		}
		return null;
	}

	private String[] getFilesNamesOf(Element node){
		ArrayList<String> res = new ArrayList<String>();
		//Element node = (Element) getNodebyTag(TAGTARGET,ATTRNAMENAME,targetName);
		NodeList nl =  node.getElementsByTagName(ATTRINCLUDE);
		for (int i = 0; i <nl.getLength();i++){
			Element e = (Element) nl.item(i);
			String path = e.getAttribute(ATTRNAMENAME);
			path=path.replaceAll(REGEXPSRCVAR,getSrcDir());
			path=path.replaceAll(REGEXPBINVAR,getBinDir());

			res.add(path);
		}
		String[] result = new String[res.size()];
		result=(String[]) res.toArray(result);
		return result;
	}

	public String[] getReferences(String targetAsString) {
		Element target = (Element) getNodebyTag(TAGTARGET,ATTRNAMENAME,targetAsString);
		Element cscNode=null;
		if (target != null && target.getChildNodes() != null) {
			for (int i = 0; i < target.getChildNodes().getLength(); i++) {
				Node cscinode = target.getChildNodes().item(i);
				if (TAGCSC.equals( cscinode.getNodeName() )) {
					cscNode = (Element) cscinode;
				}
			}
		}
		//ArrayList res = new ArrayList();
		if (cscNode!=null){
			// Has the CSC-Node a child with name "references"?
			Element referenceNode = searchReferenceNode(cscNode);
			if (referenceNode != null){
				return getFilesNamesOf(referenceNode);
			}
		}
		return new String[0];
	}

	/**
	 * @param cscNode
	 * @return
	 */
	private Element searchReferenceNode(Element cscNode) {
		for (int i = 0; i<cscNode.getChildNodes().getLength();i++){
			Node cscinode = cscNode.getChildNodes().item(i);
			if (NODEREFERENCES.equals( cscinode.getNodeName() )) {
				return (Element) cscinode;
			} 
		}
		return null;
	}



	protected Element addCSCTag(SourceTarget target) {
		Node targetNode = getNodebyTag(TAGTARGET,ATTRNAMENAME,target.getName());
		//TODO: Improve to rel. pathname!
		// We can only append if we have a artefact.
		if (target.getArtefact() != null){
			IFile arte = target.getArtefact();
			String cn = getRelPath(arte);
			if (cn.startsWith(getSrcDir())){
				// Replace the begin of cv eith srcvar
				cn = getSrcVar() + "/" +  cn.substring(getSrcDir().length());
			}
			if (cn.startsWith(getBinDir())){
				// Replace the begin of cv eith srcvar
				cn = BUILDVARNAME + File.separator +  cn.substring(getSrcDir().length());
			}
			Element cscNode = appendChildWithAttrib((Element)targetNode, TAGCSC, ATTRDESTFILE, cn);
			cscNode.setAttribute(ATTRTARGETTYPE, target.getType());
			return cscNode;
		}
		return null;
	}


	public String[] getSupportedFrameworks(){
		return new String[]{IBuildMechanismDescriptor.FRAMEWORK_MONO,IBuildMechanismDescriptor.FRAMEWORK_MS};
	}

	public void setTargetFramework(String targetFramework,String Release) {
		if (targetFramework.equals(IBuildMechanismDescriptor.FRAMEWORK_MONO )){
			// moonlight?
			if (Release.startsWith("moonlight")){
				setPropertyInProject(NANT_SETTINGS_CURRENTFRAMEWORK,Release); 
			} else { 
				setPropertyInProject(NANT_SETTINGS_CURRENTFRAMEWORK,"mono-"+Release);
			}
		}else if  (targetFramework.equals(IBuildMechanismDescriptor.FRAMEWORK_MS)){
			if (Release.startsWith("silverlight") || Release.startsWith("netcf")){
				setPropertyInProject(NANT_SETTINGS_CURRENTFRAMEWORK, Release);
			} else {
				setPropertyInProject(NANT_SETTINGS_CURRENTFRAMEWORK,"net-"+Release);
			}
		}
	}

	private String getFrameorkAndReleaseString(){
		Node node =  getNodebyTag(NODENAMEPROPERTY,ATTRNAMENAME,NANT_SETTINGS_CURRENTFRAMEWORK);
		String result = IBuildMechanismDescriptor.FRAMEWORK_NA;
		if (node != null){
			NamedNodeMap attributes = node.getAttributes(); 
			for (int i = 0; i < attributes.getLength(); i++) { // Loop through Attributes
				Node a = attributes.item(i);
				if (ATTRNAMEVALUE.equals( a.getNodeName() )) {
					result = a.getNodeValue();
				}
			}
		}
		return result;	
	}

	/**
	 * Get the target framework release
	 */
	public String getTargetFrameworkRelease() {
		String s = getFrameorkAndReleaseString();
		if (s.indexOf('-')!=-1 && s.indexOf('-') < s.length()-1){
			return s.substring(s.indexOf('-')+1);
		}
		return FRAMEWORK_NA;
	}


	/**
	 * Return the type of the target framework.
	 * According to the documentation of ant this is on Win csc and on linux mono
	 */
	public String getTargetFrameworkType() {
		String s = getFrameorkAndReleaseString();
		if (s.startsWith("mono")) return FRAMEWORK_MONO;
		if (s.startsWith("net")) return FRAMEWORK_MS;
		return FRAMEWORK_NA;
	}

	//public void setReferences(String targetName, String[] referenceNames) {
	public void setReferences(SourceTarget target){
		try{
			String targetName=target.getName();
			String[] referenceNames = target.getReferences();
			ArrayList<Node> nodesToDelete = new ArrayList<Node>();
			Element cscElement=getCscElementOfTarget(targetName);
			if (cscElement != null){
				NodeList cscchildnodes = cscElement.getChildNodes();
				//				Delete old references when there
				cscchildnodes = cscElement.getChildNodes();
				nodesToDelete = new ArrayList<Node>();
				for (int i = 0; i < cscchildnodes.getLength(); i++){
					Node n = cscchildnodes.item(i);
					if (n.getNodeName().equals(NODEREFERENCES)){
						nodesToDelete.add(n);
					}
				}
				for (int i = 0; i < nodesToDelete.size();i++){
					cscElement.removeChild((Node) nodesToDelete.get(i));
				}
				Element refsNode = document.createElement(NODEREFERENCES);
				int toAppend =0; // Counter how many to apend
				for (int i = 0; i < referenceNames.length;i++){	
					if (!referenceNames[i].equals("")){
						Element defNode = document.createElement(ATTRINCLUDE);
						defNode.setAttribute(ATTRNAMENAME,referenceNames[i]);
						refsNode.appendChild(defNode);
						toAppend++;
					}
				}
				if (toAppend!=0){
					cscElement.appendChild(refsNode);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public String[] getSupportedReleases(String Framework) {

		if (Framework.equals(FRAMEWORK_MONO)){
			return new String[] {"1.0","2.0","3.5", "4.0","moonlight-2.0"};
		}
		if (Framework.equals(FRAMEWORK_MS)){
			return new String[] {"1.0","1.1","2.0","3.5", "4.0","netcf-1.0","silverlight-2.0",};
		}
		return new String[0];
	}

	public String getTypeOfLanguageTarget(String targetName) {
		Element cscElement=getCscElementOfTarget(targetName);
		if (cscElement != null){
			String targetType=cscElement.getAttribute(TAGTARGET);
			if (targetType != null) return targetType;
		}
		return TARGETTYPEEXE;
	}

	public void setLanguageTargetType(SourceTarget target){
		String targetName=target.getName();
		String targetType=target.getType();

		Element cscElement=getCscElementOfTarget(targetName);
		if (cscElement != null){
			cscElement.setAttribute(TAGTARGET, targetType);	
		}
	}

	public void setSourcesToLanguageTarget(SourceTarget target) {
		String targetName=target.getName();
		IFile[] newSrc=target.getSources();
		Node targetNode = getNodebyTag(TAGTARGET,ATTRNAMENAME,targetName);
		//Element cscNode = (Element) loopSearchForTag(targetNode, TAGCSC,"destFile", binName);
		Node cscNode = null;
		for (int i = 0; i<targetNode.getChildNodes().getLength();i++){
			Node cscinode = targetNode.getChildNodes().item(i);
			if (TAGCSC.equals( cscinode.getNodeName() )) {
				cscNode=cscinode;
			} 
		}
		// Delete all old sources. Do it simple by deleting the <sources>-node
		Node cscssNode = null;
		for (int i = 0; i<cscNode.getChildNodes().getLength();i++){
			Node cscinode = cscNode.getChildNodes().item(i);
			if (NODESOURCES.equals( cscinode.getNodeName() )) {
				cscssNode=cscinode;
			} 
		}
		if (cscssNode != null){
			cscNode.removeChild(cscssNode);
		}
		// Create a new, empty one
		cscssNode = document.createElement(NODESOURCES);
		cscNode.appendChild(cscssNode);


		// Add the new files
		for (int fn=0;fn<newSrc.length;fn++){
			IFile codeFile=newSrc[fn];
			//String container = codeFile.getFullPath().removeLastSegments(1).toOSString();
			//String fileName =codeFile.getFullPath().lastSegment();
			String cn = getRelPath(codeFile);
			if (cn.startsWith(getSrcDir())){
				// Replace the begin of cv eith srcvar
				cn = getSrcVar() + "/" +  cn.substring(getSrcDir().length());
			}
			//String name = cn + IPath.SEPARATOR  + fileName;
			//Element srcNode = document.createElement(TAGSRC);


			Element srcNode = document.createElement(ATTRINCLUDE);
			srcNode.setAttribute(ATTRNAMENAME,cn);		
			//cscssNode.appendChild(srcNode);
			//srcNode.setAttribute(ATTRDIR,cn);
			//srcNode.setAttribute(ATTRINCLUDES,fileName);		
			cscssNode.appendChild(srcNode);
		}
	}

	public void setWarningLevel(SourceTarget target) {
		String targetName=target.getName();
		int warnLevel=target.getWarningLevel();
		Element cscElement=getCscElementOfTarget(targetName);
		if (cscElement != null){
			cscElement.setAttribute(ATTRNAMEWARNINGLEVEL,Integer.toString( warnLevel));
		}
	}

	public int getWarningLevel(String targetName) {
		int result= 4;
		Element cscElement=getCscElementOfTarget(targetName);
		if (cscElement != null){
			try {
				result=  Integer.parseInt(cscElement.getAttribute(ATTRNAMEWARNINGLEVEL));
			} catch (NumberFormatException e){

			}
		}
		return result;
	}

	public void setDefinitions(SourceTarget target) {
		String targetName=target.getName();
		String[] Definitions = target.getDefinitions();
		try{
			Element cscElement=getCscElementOfTarget(targetName);
			String result = "";
			for (int i =0; i < Definitions.length;i++){
				result += Definitions[i];
				if (i < Definitions.length-1){
					result += ",";
				}
			}
			if (cscElement != null){
				cscElement.setAttribute(ATTRNAMEDEFINES, result); 
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	protected String[] getDefinitions(String targetName) {
		try{
			Element cscElement=getCscElementOfTarget(targetName);
			if (cscElement != null){
				String s = cscElement.getAttribute(ATTRNAMEDEFINES);
				//TODO ensure that definitions containing , are treated correct!
				return s.split(",");
			}			
		} catch(Exception e){
			e.printStackTrace();
		}

		return new String[0];
	}

	public ArrayList<String> getFullBuildCommand() {
		ArrayList<String> args = new ArrayList<String>();
		Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		String result = prefs.getString(DefaultPrefsSetter.P_NANTCMD);
		String bf = getBuildFile().getName();
		if (result.equals("")) {
			args.add("nant");
		}
		if (!bf.equals("")) {
			args.add(result);
			args.add("-f:"+bf);
		}
		return args;
	}

	public ArrayList<String> getTargetBuildCommand(String[] targets) {
		ArrayList<String> allTgts = new ArrayList<String>();
		for (int i = 0; i < targets.length; i++){
			allTgts.add(targets[i]);
		}
		ArrayList<String> args = getFullBuildCommand();
		args.addAll(allTgts);
		return args;
	}

	public String suggestFileName() {
		return IBuildFileManipulator.BUILDFILENAMEPROJECT + ".build";
	}


}
