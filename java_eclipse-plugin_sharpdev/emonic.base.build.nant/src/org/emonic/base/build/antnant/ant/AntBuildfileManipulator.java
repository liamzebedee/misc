///**************************************************************************
// * Copyright (c) 2005, 2008 emonic.org.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// * Created on Mar 24, 2005
// *
// ****************************************************************************/
//package org.emonic.base.build.antnant.ant;
//
//import java.io.File;
//import java.util.ArrayList;
//
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IProject;
//import org.eclipse.core.runtime.IPath;
//import org.emonic.base.buildmechanism.AntLikeFileManipulator;
//import org.emonic.base.buildmechanism.IBuildFileLayoutManipulator;
//import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
//import org.emonic.base.buildmechanism.IBuildProjectManipulator;
//import org.emonic.base.buildmechanism.IBuildSourceTargetManipulator;
//import org.emonic.base.buildmechanism.IInternalBuilder;
//import org.emonic.base.buildmechanism.IInternalBuilderFactory;
//import org.emonic.base.buildmechanism.SourceTarget;
//import org.w3c.dom.Element;
//import org.w3c.dom.NamedNodeMap;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
///**
// * @author bb
// * Window - Preferences - Java - Code Style - Code Templates
// */
//public class AntBuildfileManipulator extends AntLikeFileManipulator implements IBuildMechanismDescriptor,IBuildSourceTargetManipulator,
//         IBuildFileLayoutManipulator,IBuildProjectManipulator,IInternalBuilderFactory  {
//	
//	private static final String ATTRREFERENCE = "reference"; //$NON-NLS-1$
//	private static final String ATTRFILE = "file"; //$NON-NLS-1$
//	
//	private static final String ATTRWARNLEVEL = "warnLevel"; //$NON-NLS-1$
//	
//
//	private static final String FILENAMESSEPERATOR = ","; //$NON-NLS-1$
//	protected static final String TAGTARGET = "Target"; //$NON-NLS-1$
//	
//	//private static final String TARGETNAMEALL = "all"; //$NON-NLS-1$
//	
//	
//	
//	// These are also used at least by the nant-bfm.
//	
//	
////	public AntBuildfileManipulator(IFile fn, IProgressMonitor moni){
////		super(fn,moni);
////	}
////	
//	
//	
//	//public AntBuildfileManipulator(IFile fn){
//	//	super(fn);
//	//}
//	
//	
//	public AntBuildfileManipulator(){
//		super();
//		// Do we have a buld file?
//		
//	}
//	
//	
//	
//	/**
//	 * Try to evaluate the build file to a arbitrary resource, throwexception if
//	 * not found
//	 */
////	public AntBuildfileManipulator(IFile fl){
////		super();
//////		IFile fn = null;
//////	    // Get the project of the resource
//////		IFile fl = null;
//////		if (res!=null){
//////			IProject proj = res.getProject();
//////			if (proj != null) fl = proj.getFile("build.xml");
//////		}
////		if (fl != null && fl.exists()){
////			file = fl;
////			parseDocument();	
////		} 
////		
////	}
//	
////	public AntBuildfileManipulator(IProject res, IProgressMonitor monitor) {
////		// TODO Auto-generated constructor stub
////	}
//
//
//
//	
//
//	
//	
//	
//
//
//	
//
//	/**
//	
//
// 
//	
//	
////	/**
////	 * @param fileNames 
////	 * @return The target names
////	 */
////	public String[] getAllTargetsForLanguageFileAsString(String fileNames) {
////		//System.out.println("Searching targets for: " + fileNames);
////		String[] res =  getAllTargetsOfLanguageFile(fileNames); 
//////		String result = EMPTYSTRING;
//////		for (int i = 0; i < res.length; i++){
//////			result += res[i];
//////			if (i < res.length-1) {
//////				result += ' ';
//////			}
//////		}
////		//System.out.println("Found Targets: " + res);
////		return res;
////	}
//	
//
// 
//
//	
//	protected IFile[] getSourcesOfLanguageTarget(String targetAsString){
//		ArrayList res = new ArrayList();
//		IProject proj = this.file.getProject();
//		Node targetNode = getNodebyTag(TAGTARGET,ATTRNAMENAME,targetAsString);
//		if (targetNode != null){
//			NodeList nl =  ((Element)targetNode).getElementsByTagName(TAGSRC);
//			for (int i = 0; i <nl.getLength();i++){
//				Element e = (Element) nl.item(i);
//				String path = e.getAttribute(ATTRDIR);
//				if (VARSRC.equals( path )) {
//					path = getSrcDir();
//				}
//				path = path + IPath.SEPARATOR ;
//				String[] allIncs=  e.getAttribute(ATTRINCLUDES).split(FILENAMESSEPERATOR);
//				for (int j=0; j<allIncs.length;j++){
//					String cpath = path + allIncs[j];
//					IFile fl = proj.getFile(cpath);
//					res.add(fl);
//				}
//
//			}
//			IFile[] result = new IFile[res.size()];
//			result=(IFile[]) res.toArray(result);
//			return result;
//		}
//    	return new IFile[0];
//    }
//
//
//
//	/**
//	 * Calculates the relative path of a IFile to the build file from the absolute (beginning of workspace)
//	 * @param container
//	 * @return
//	 */
//	
//
//
//
//	/**
//	 * @param target
//	 * @return
//	 */
//	public String[] getReferences(String targetName) {
//		Element cscNode=null;
//		Element target = (Element) getNodebyTag(TAGTARGET,ATTRNAMENAME,targetName);
//		if (target != null && target.getChildNodes() != null) {
//			for (int i = 0; i < target.getChildNodes().getLength(); i++) {
//				Node cscinode = target.getChildNodes().item(i);
//				if (TAGCSC.equals( cscinode.getNodeName() )) {
//					cscNode = (Element) cscinode;
//				}
//			}
//		}
//		if (cscNode!=null){
//			String refs = cscNode.getAttribute(ATTRREFERENCES);
//			if (!EMPTYSTRING.equals( refs )) {
//				return refs.split(FILENAMESSEPERATOR);
//			}
//			//return new String [0];
//		}
//		return new String[0];
//	}
//
//
//
//	
//
////	public String[] getAllTargetsOfLanguageFile(String fileNames) {
////		StringTokenizer st = new StringTokenizer(fileNames,ONESPACE);
////		ArrayList res=new ArrayList();
////		while(st.hasMoreTokens()){
////			String fn = st.nextToken();
////			try{
////				Element srcT = (Element) getNodebyTag(TAGSRC,ATTRINCLUDES,fn);
////				// We have the hirarchy: Target->csc->src 
////				Node targetNode = srcT.getParentNode().getParentNode();
////				//System.out.println ("Parent Node of " + srcT.getNodeName() + " is " + parent.getNodeName());
////				
////				res.add(findTargetNodeName(targetNode));
////			} catch (Exception e){
////				//System.out.println("Warning: Found no target for " + fn + "!");
////				//e.printStackTrace();
////			}
////		}
////		String[] result = new String[res.size()];
////		result=(String[]) res.toArray(result);
////		return result;
////	}
//
//
//	
//
//
//
//	protected Element addCSCTag(SourceTarget target) {
//		String targetName = target.getName();
//		String targetType= target.getType();
//
//		if (target.getArtefact() != null){
//			IFile arte = target.getArtefact();
//			String cn = getRelPath(arte);
//			if (cn.startsWith(getSrcDir())){
//				// Replace the begin of cv eith srcvar
//				cn = getSrcVar() + "/" +  cn.substring(getSrcDir().length());
//			}
//			if (cn.startsWith(getBinDir())){
//				// Replace the begin of cv eith srcvar
//				cn = BUILDVARNAME + File.separator +  cn.substring(getSrcDir().length());
//			}
//			//System.out.println("Add target....");
//			//addTarget(targetName);
//			Node targetNode = getNodebyTag(TAGTARGET,ATTRNAMENAME,targetName);
//			//System.out.println("Add csc node....");
//			Element cscNode = appendChildWithAttrib((Element)targetNode, TAGCSC, ATTRDESTFILE, cn);
//			cscNode.setAttribute(ATTRTARGETTYPE, targetType);
//			return cscNode;
//		}
//		return null;
//
//	}
//
//   
//	
//
//	
//
//
//
//	public void setWarningLevel(SourceTarget target) {
//		String targetName=target.getName();
//		int warnLevel=target.getWarningLevel();
//		Element cscElement=getCscElementOfTarget(targetName);
//        if (cscElement != null){
//			cscElement.setAttribute(ATTRWARNLEVEL,Integer.toString( warnLevel));
//		}
//	}
//
//
//
//
//
//	//public void setReferences(String targetName, String[] referenceNames) {
//	public void setReferences(SourceTarget target){
//		String targetName=target.getName();
//		String[] referenceNames=target.getReferences();
//		try{
//			Element cscElement=getCscElementOfTarget(targetName);
//			if (cscElement != null){
//				// Alten Nodes loeschen
//				boolean hasMoreChilds = true;
//				while (hasMoreChilds){
//					NodeList oldNodes=cscElement.getChildNodes();
//					hasMoreChilds= false;
//					for (int i = 0; i < oldNodes.getLength() && ! hasMoreChilds;i++ ){
//						Node actual = oldNodes.item(i);
//						if (actual.getNodeName().endsWith(ATTRREFERENCE)){
//							cscElement.removeChild(actual);
//							hasMoreChilds= true;
//						}
//					}
//				}
//				
//				// Neuen Nodes anhängen
//				for (int i=0; i < referenceNames.length; i++){
//					appendChildWithAttrib(cscElement,ATTRREFERENCE,ATTRFILE,referenceNames[i]);
//				}
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		//write();
//		
//	}
//
//
//
//	public String[] getDefinitions(String targetName) {
//		ArrayList defs = new ArrayList();
//		try{
//			Element cscElement=getCscElementOfTarget(targetName);
//			if (cscElement != null){
//			NodeList cNodes=cscElement.getChildNodes();
//		    for (int i = 0; i < cNodes.getLength();i++ ){
//						Node actual = cNodes.item(i);
//						if (actual.getNodeName().equals(ATTRDEFINE) && Element.class.isAssignableFrom(actual.getClass())){
//							String def=((Element) actual).getAttribute(ATTRNAMENAME);
//							if (! def.equals("")){
//								defs.add(def);
//							}
//						}
//					}
//				}
//			
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		String[] res=new String[defs.size()];
//		res=(String[]) defs.toArray(res);
//		return res;
//	}
//
//
//
//	public void setDefinitions(SourceTarget target) {
//		String targetName=target.getName();
//		String[] Definitions = target.getDefinitions();
//		try{
//			Element cscElement=getCscElementOfTarget(targetName);
//			if (cscElement != null){
//				// Delete old nodes
//				boolean hasMoreChilds = true;
//				while (hasMoreChilds){
//					NodeList oldNodes=cscElement.getChildNodes();
//					hasMoreChilds= false;
//					for (int i = 0; i < oldNodes.getLength() && ! hasMoreChilds;i++ ){
//						Node actual = oldNodes.item(i);
//						if (actual.getNodeName().equals(ATTRDEFINE)){
//							cscElement.removeChild(actual);
//							hasMoreChilds= true;
//						}
//					}
//				}
//				
//				// Add new nodes
//				for (int i=0; i < Definitions.length; i++){
//					if (!Definitions[i].equals("")){
//						appendChildWithAttrib(cscElement,ATTRDEFINE,ATTRNAMENAME,Definitions[i]);
//				
//					}
//				}
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		//write();
//		
//	}
//
//
//
//	
//
//	
//
//	public int getWarningLevel(String targetName) {
//		Element cscElement=getCscElementOfTarget(targetName);
//        if (cscElement != null){
//			return Integer.parseInt(cscElement.getAttribute(ATTRWARNLEVEL));
//		}
//        return 0;
//		
//	}
//
//	public String getBinDir(){
//		Node node =  getNodebyTag(NODENAMEPROPERTY,ATTRNAMENAME,BUILDVARPLAINNAME);
//		String result = EMPTYSTRING;
//		if (node != null){
//			NamedNodeMap attributes = node.getAttributes(); 
//			for (int i = 0; i < attributes.getLength(); i++) { // Loop through Attributes
//				Node a = attributes.item(i);
//				if (ATTRNAMELOCATION.equals( a.getNodeName() )) {
//					result = a.getNodeValue();
//				}
//			}
//		}
//		return result;
//	}
//	
//
//	
//
//	public String getTypeOfLanguageTarget(String targetName) {
//		Element cscElement=getCscElementOfTarget(targetName);
//        if (cscElement != null){
//        	String targetType=cscElement.getAttribute(ATTRTARGETTYPE);
//        	if (targetType != null) return targetType;
//		}
//        return TARGETTYPEEXE;
//	}
//
//
//
//	
//
//
//	public void setSourcesToLanguageTarget(SourceTarget target) {
//		String targetName=target.getName();
//		IFile[] newSrc=target.getSources();
//		Node targetNode = getNodebyTag(TAGTARGET,ATTRNAMENAME,targetName);
//		//Element cscNode = (Element) loopSearchForTag(targetNode, TAGCSC,"destFile", binName);
//		Node cscNode = null;
//		for (int i = 0; i<targetNode.getChildNodes().getLength();i++){
//			Node cscinode = targetNode.getChildNodes().item(i);
//			if (TAGCSC.equals( cscinode.getNodeName() )) {
//				cscNode=cscinode;
//			} 
//		}
//		// Delete all old sources
//		NodeList childs = cscNode.getChildNodes();
//		for (int i = 0; i < childs.getLength();i++){
//			Node actNode = childs.item(i);
//			if (Element.class.isAssignableFrom(actNode.getClass())){
//				if (actNode.getNodeName().equals(TAGSRC)){
//					cscNode.removeChild(actNode);
//				}
//			}
//		}
//		// Add the new files
//		for (int fn=0;fn<newSrc.length;fn++){
//            IFile codeFile=newSrc[fn];
//			String fileName =codeFile.getFullPath().lastSegment();
//			Element srcNode = document.createElement(TAGSRC);
//			String cn = getRelPath(codeFile);
//			//if (getSrcDir().equals(cn)) {
//			if (cn.startsWith(getSrcDir())){
//				// Replace the begin of cv eith srcvar
//				cn = getSrcVar() + "/" +  cn.substring(getSrcDir().length());
//			}
//			if (cn.lastIndexOf(fileName) != -1){
//				cn=cn.substring(0,cn.lastIndexOf(fileName));
//			}
//			srcNode.setAttribute(ATTRDIR,cn);
//			srcNode.setAttribute(ATTRINCLUDES,fileName);		
//			cscNode.appendChild(srcNode);
//		}
//		//write();
//		
//	}
//
//
//
//
//
//	public IInternalBuilder getInternalBuilder() {
//		return new EmonicAntBuilder(getBuildFile());
//	}
//
//
//
//	//public void setLanguageTargetType(String targetName, String targetType) {
//	public void setLanguageTargetType(SourceTarget target){
//		String targetName=target.getName();
//		String targetType=target.getType();
//		Element cscElement=getCscElementOfTarget(targetName);
//	    if (cscElement != null){
//	      cscElement.setAttribute(ATTRTARGETTYPE, targetType);	
//		}
//	       
//
//	}
//
//
//	
//	
//}
