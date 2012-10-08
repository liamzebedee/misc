/*****************************************************************************
 * Created on Aug 20, 2005
 * emonic org.emonic.base.FileManipulators CodeElement.java
 *
 *****************************************************************************
 * Copyright (c)  2007 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
*****************************************************************************/
package org.emonic.base.codehierarchy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;

/**
 * This class represents a piece of .net-code. A .net-code-file (it does not matter wether compiled byte code or represented as language) 
 * can be represented as a tree of code elements. Naturly the parser which parse the code to this structure must be language-dependent. 
 * The code tree can be used to be displayed in outline view, used for commandline completion,....
 * @author bb
 *
 */
public class CodeElement extends DotNetElement implements IParent, ISourceUnit ,IDotNetElement{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String NOTAVAILABLE = "NA"; //$NON-NLS-1$
//	private static final String CCOMMASPACE = ", "; //$NON-NLS-1$
//	private static final String CLEFTROUNDBR = "("; //$NON-NLS-1$
//	private static final String CCOMMA = ","; //$NON-NLS-1$
//	private static final String CROUNDBRACKETS = "()"; //$NON-NLS-1$
	private static final String EMPTYSTRING = ""; //$NON-NLS-1$
	//protected SourceElement parent;
	/**
	 * The type of the code element itself, for example namespace, variable or class
	 */
	
	private int codeType = 999; //CodeTypes are Namespace, Class, Property, Method....
	/**
	 * The name of the element
	 */
	private String elementName = EMPTYSTRING; 
	/**
	 * The object type of the element, for example String of the var is a string var
	 */
	private String typeSignature = EMPTYSTRING;
	/**
	 * The children of that code element
	 */
	protected ArrayList children;
	/**
	 * A counter of the open {. You need this info for code formations.....
	 */
	private int curlCounter;
	/**
	 * If the code element is a class, from which classes inherits it, which interfaces are implemented?
	 */
	private String derived=EMPTYSTRING; // name of class derived from
	/**
	 * The source file
	 */
	private String source; // source code file
	/**
	 * In case of methods and constructors: The signature
	 */
	protected String signature = EMPTYSTRING;
	/**
	 * The access type (private, protected, ....)
	 */
	private String accessType = EMPTYSTRING;
	// modifiers
	public boolean staticMod = false;
	public boolean constMod = false;
	public boolean abstractMod = false;
	public boolean virtualMod = false;
	public boolean overrideMod = false;
	public boolean eventMod = false;
	public boolean readonlyMod = false;
	public boolean sealedMod = false;
	public boolean unsafeMod = false;
	public boolean volatileMod = false;
	public boolean newMod = false;
	// position related items
	/**
	 * The start point in the source file in the format x:y, e.g. lineoffset:line
	 */
	private Point startPoint;
	/**
	 * The end point in the source file in the format x:y, e.g. lineoffset:line
	 */
	private Point endPoint;
	/**
	 * The start point in the source file in the format x:y, e.g. lineoffset:line
	 */
	protected int offset = 0;
	/**
	 * The length of the element in the source file (from begin to end)
	 */
	protected int length = 0;
	/**
	 * The offset of the name. If -1: undefined If 0: Same as the offset of the element. If != 0: The start of the element name
	 * For example: offset = 0, text = class abc => nameoffset = (start of class =0) + 6 = 6
	 */
	protected int nameoffset = -1;
	/**
     * The valid Offset is the begin of the region, in which the element can have children 
     * For ecample after the curl of namespace abc{
     */
	private int validOffset=-1;
	/**
     * The valid length is length between the begin of the 
     * region, in which the element can have children or is valid 
     * to the end 
     * For example the length from the curl of namespace abc{ to the closing curl
     */
	private int validLength=-1;
	
	private String[] parameterTypes = null;
	
	private String[] parameterNames = null;
	private String _body = EMPTYSTRING;
	
	
	/**
	 * Constructor
	 * @param type The type of the element (class? constructor? namespace?)
	 * @param name The name of the element 
	 * @param offset The offset in the code files
	 * @param length The length
	 */
	public CodeElement(IDotNetElement parent, int type, String name, int offset, int length) {
		super(parent, null);
		this.codeType = type;
		this.elementName=name;
		this.offset = offset;
		this.length = length;
		this.children=new ArrayList();
	}
	
	public CodeElement(IDotNetElement parent, int codetype) {
		this(parent, codetype, "", 0, 0);
	}
	
	/**
     * Constructor 
     * @param codetype The type of the element (class? constructor? namespace?)
     */	
	public CodeElement(int codetype) {
		this(null, codetype);
	}
	
	/**
	 * @return Returns the signature.
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Sets the signature.
	 * @param signature The signature to set.
	 */
	public void setSignature(String signature) {
		// Sanitize signature: Seems that it is sometimes set with a following class
		signature.trim();
		if ((signature.indexOf(')') != -1) && (signature.indexOf(')') < signature.length()-1)){
			signature=signature.substring(0, signature.indexOf(')')+1);
		}
		this.signature = signature;
	}


	public void setParent(IDotNetElement parent) {
		this.parent = parent;
	}
	
	/**
	 * Get the children
	 * @see the children field
	 * @return the children of this code element
	 */
	public IDotNetElement[] getChildren(){
		return (IDotNetElement[]) children.toArray(new IDotNetElement[children.size()]);
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	/**
	 * Add a new child
	 * @see the children field
	 * @param child
	 */
	public void addChild(IDotNetElement child){
		children.add(child);
	}
	
	/**
	 * Get the name of the element
	 * @return the elementname
	 */
	public String getElementName(){
		return this.elementName;
	}
	
	/**
	 * Set the name of the element
	 * @param value
	 */
	public void setElementName(String value) {
		this.elementName = value;
	}
	
	/**
	 * Set the type of the code
	 * @see description of codeType field
	 * @param codetype
	 */
	public int getCodeType() {
		return codeType;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getNameLength() {
		if (this.codeType == IDotNetElement.USING_CONTAINER)
			return getLength();
		return getElementName().length();
	}
	
	/**
	 * @return Returns the type signature
	 */
	public String getTypeSignature() {
		return typeSignature;
	}

	public void setTypeSignature(String typeSignature) {
		this.typeSignature = typeSignature;
	}
	
	/**
	 * Get the offset in the document
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * Set the offfset in the document
	 * @param offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	/**
	 * Set the length of the code element (from start to the end, for example from the p of private class to the last closing bracket of the class)
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * Set all children of this element
	 * @param children
	 */
	public void setChildren(ArrayList children) {
		this.children = children;
	}
	
	/**
	 * Get all elements of name name and code type type
	 * @param name The name of the searched elements
	 * @param type the code type
	 * @return An array of the matching code elements
	 */
	public CodeElement[] getElementsByNameAndType(String name, int type) {
		ArrayList res = new ArrayList();
		res = getElementsByNameAndType(this,name,type,res);
		CodeElement[] result = new CodeElement[res.size()];
		result= (CodeElement[]) res.toArray(result);
		return result;
	}
	
	private ArrayList getElementsByNameAndType(CodeElement element, String name, int type, ArrayList res) {
		if ((element.codeType == type) &&
		(element.elementName.equals(name))) {
			res.add(element);
		}
		for (int i=0; i< element.children.size();i++){
			CodeElement newEle = (CodeElement) element.children.get(i);
			res = getElementsByNameAndType(newEle,name,type,res);
		}
	    return res;
	}
	
	/**
	 * Set the curlcounter
	 * @param curlcounter
	 */
	public void setCurlCounter(int curlcounter) {
		curlCounter = curlcounter;
	}
	
	public int getCurlCounter() {
		return curlCounter;
	}
	
	/**
	 * Setter for access type
	 * @param accessType
	 */
	public void setAccessType(String accessType) {
		this.accessType=accessType;
	}
	
	/**
	 * Getter for the access type
	 * @return
	 */
	public String getAccessType(){
		return accessType;
	}
	
	/**
	 * Setter for the derived string
	 * @see descrition of derived field
	 * @param derived
	 */
	public void setDerived(String derived) {
		this.derived = derived;
	}
	
	/**
	 * Getter for the derived string
	 * @see descrition of derived field
	 * @return the derived string
	 */
	public String getDerived() {
		if (derived != null) {
			return derived;
		}
		return EMPTYSTRING;
	}
	
	
	private ArrayList recBuildElementList(IDotNetElement element, ArrayList resList, int tcodeType) {
		if (element.getElementType() == tcodeType) {
			resList.add(element);
		}
		
		if (element instanceof IParent) {
			IDotNetElement[] children = ((IParent) element).getChildren();
			for (int i = 0; i < children.length; i++) {
				resList = recBuildElementList(children[i], resList, tcodeType);
			}
		}
		return resList;
	}
	
	/**
	 * Returns a list with all comment code elements in the AST, except those that exist of
	 * fewer lines than <code>minLineCount</code>.
	 * @param minLineCount Minimum line count
	 * @param doc The IDocument the AST was generated for
	 * @return The list with comment elements
	 */
	public List getCommentList(int minLineCount, IDocument doc) {
		List resList = getCommentList();
		int endLine = 0, startLine = 0;
		
		for (int i=0; i<resList.size(); i++) {
			CodeElement actual = (CodeElement) resList.get(i);
			try {
				startLine = doc.getLineOfOffset(actual.getOffset());
				// multi line comments include one character more than necessary (for folding)
				endLine = doc.getLineOfOffset(actual.getOffset() + actual.getLength() - 1);
			} catch (BadLocationException e) {
				continue;
			}
			if (endLine-startLine+1 < minLineCount) {
				resList.remove(i);
				i--; // decrease i because we removed one element!
			}
		}
		return resList;
	}

	
	/**
	 * Get all the comments of the element and its childreen
	 * @return a list of document code elements
	 */
	public List getCommentList() {
	  	ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.COMMENT );
		return resList;
	}
	
	
	/**
	 * Get a list of namespaces of this code element and its children
	 * @return the list of namespaces
	 */
	public List getNameSpaceList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.NAMESPACE );
		return resList;
	}
	
	/**
	 * Get a list of classes of this code element and its children
	 * @return A list of classes
	 */
	public List getClassList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.CLASS );
		return resList;
	}
	
	/**
	 * Get a list of methods
	 * @return the list of methods
	 */
	public List getMethodList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.METHOD );
		return resList;
	}
	
	/**
	 * Get a list of usings
	 * @return The list of usings
	 */
	public List getUsingList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.USING);
		return resList;
	}
	
	public List getUsingContainerList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.USING_CONTAINER);
		return resList;
	}
	
	/**
	 * Get a list of interfaces
	 * @return The list of interfaces
	 */
	public List getInterfaceList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this, resList, IDotNetElement.INTERFACE);
		return resList;
	}

	/**
	 * Get a list of properties
	 * @return The list of properties
	 */
	public List getPropertyList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.PROPERTY );
		return resList;	
	}
	
	/**
	 * Get a list of enums
	 * @return The list of enums
	 */
	public List getEnumList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.ENUM );
		return resList;
	}
	
	/**
	 * Get a list of structs
	 * @return The list of structs
	 */
	public List getStructList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.STRUCT );
		return resList;
	}

	/**
	 * Get a list of strings
	 * @return The list of strings
	 */
	public List getStringList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.STRING );
		return resList;
	}

	/**
	 * Get a list of constructors
	 * @return The list of constructors
	 */
	public List getConstructorList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.CONSTRUCTOR );
		return resList;
	}
	
	public List getDestructorList() {
		ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.DESTRUCTOR);
		return resList;
	}

	/**
	 * Get the source code file
	 * @return a path to the source code file
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * Get the source file as resource in the workspace
	 * Does NOT test wether it exists
	 * @return the source file
	 */
	public IFile getSourceAsResource(){
		 IWorkspace workspace = ResourcesPlugin.getWorkspace();
		 // If the path is in the workspace and absolute, make it relative to the workspace
		 String orgPath = workspace.getRoot().getLocation().toOSString();
		 String file = getSource();
		 String sfile = file;
		 // Grr: Win does not care about capitals!
		 // So since I don't know which system i have I have to allow generally both
		 if (file.toLowerCase().startsWith(orgPath.toLowerCase())) 
				sfile = file.substring(orgPath.length());
		 return workspace.getRoot().getFile(new Path(sfile));
	}
	
	
	/**
	 * Setter of the source code file
	 * @param source The path to the source code file
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * Getter for the endPoint
	 * @return the endPoint
	 */
	public Point getEndPoint() {
		return endPoint;
	}
	
	/**
	 * Setter of the end point
	 * @param endPoint
	 */
	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}
	
	/**
	 * Getter of the start point
	 */
	public Point getStartPoint() {
		return startPoint;
	}
	
	/**
	 * Setter for the startPoint
	 * @return the endPoint
	 */
	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}
	
	/**
	 * Get a text representation of the start point. Format line:offset
	 * @return the text representation of the start point
	 */
	public String getStartPointText(){
		if (startPoint != null ){
			return String.valueOf( startPoint.y) + ':' + String.valueOf(startPoint.x);
		}
		return NOTAVAILABLE;
	}
	
	/**
	 * Get a text representation of the end point. Format line:offset
	 * @return the text representation of the end point
	 */
	public String getEndPointText(){
		if (endPoint != null ){
			return String.valueOf( endPoint.y) + ':' + String.valueOf(endPoint.x);
		}
		return NOTAVAILABLE;
	}
	
	public String toString(){
		return getSource() + ' ' + getStartPointText() + ' ' + getAccessType() + ' ' + getEndPointText()+' ' + getTypeSignature() + ' ' + getElementName() + ' ' + getSignature() ;
	}

	/**
	 * @return the nameoffset
	 */
	public int getNameOffset() {
		if (nameoffset == 0) return offset;
		return nameoffset;
	}

	/**
	 * @param nameoffset the nameoffset to set
	 */
	public void setNameOffset(int nameoffset) {
		this.nameoffset = nameoffset;
	}

    /**
     * Get a normed variant of a signature, defined as a signature with the
     * parameter types but not the parameter names and with excessive whitespace
     * trimmed.
     * @return the normed signature
     */
	public String getNormedSignature() {
		// remove ( and )
		String sig = getSignature();
		String sigUnpacked = sig.substring(sig.indexOf('(')+1, sig.lastIndexOf(')'));
		String[] eles = sigUnpacked.split(","); //$NON-NLS-1$
		if (eles.length > 0) {
			// Remove first (
//			eles[0]=eles[0].replaceAll("^\\s*\\(\\s*","");
			// Remove last )
//			eles[eles.length-1]=eles[eles.length-1].replaceAll("\\s*\\)\\s*$","");
			//Remove types, if there are some
			for (int i = 0; i < eles.length;i++) {
				eles[i] = eles[i].trim().replaceAll("\\s+\\w*$",""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			// Build the result string
			StringBuffer buffer = new StringBuffer();
			synchronized (buffer) {
				buffer.append('(');
				for (int i = 0; i < eles.length; i++) {
					buffer.append(eles[i].trim());
					if (i < eles.length - 1) {
						if (buffer.charAt(buffer.length() - 1) == '[') {
							buffer.append(',');	
						} else {
							buffer.append(", "); //$NON-NLS-1$
						}
					}
				}
				buffer.append(')');
				return buffer.toString();
			}
		}
		return ""; //$NON-NLS-1$
	}

	public String getExtendedSignature() {
		String[] eles = getSignature().split(",");
		if (eles.length>0){
//			 Remove first (
			eles[0]=eles[0].replaceAll("^\\s*\\(\\s*","");
			// Remove last )
			eles[eles.length-1]=eles[eles.length-1].replaceAll("\\s*\\)\\s*$","");
			// Ensure that all values have vars
			for (int i = 0; i < eles.length;i++){
				String ele=eles[i];
				ele=ele.trim();
				if ( (ele.length()>0) && ele.lastIndexOf(' ')==-1){
				      eles[i]=ele + " v"+i;
				}
			}
            //	Build the result string
			String res = "(";
			for (int i = 0; i < eles.length;i++){
				res+=eles[i].trim();
				if (i < eles.length-1){
					res += ",";
				}
			}
			res += ")";
			return res;
		
		}
		return "";
	}
	
    /**
     * The valid Offset is the begin of the region, in which the element can have children
     * or is valid. For a namespace or a class from after the opening { to before }
     * Example:
     * 12345678901234567890
     * class a{ }
     * validOffset = 9
     * The thought is that if You have a IDocument doc and do a 
     * doc.replace(validOffset,0,String) You insrert the string after the { 
     * @return the valid Offset
     */
	public int getValidOffset() {
		return validOffset;
	}

	/**
     * The valid Length is the length of the region, in which the element can have children or is valid.
     *  For a namespace or a class from after { to  } (including the bracket) 
     *  * Example:
     * 12345678901234567890
     * class a{ }
     * validOffset = 9
     * validLength = 1
     * The thought is that if You have a IDocument doc and do a 
     * doc.replace(validOffset+validLength,0,String) You insrert the string before the }
     * @return the valid Length
     * 
     */
	public int getValidLength() {
		return validLength;
	}
	
	/**
     * The valid Offset is the begin of the region, in which the element can have children
     * or is valid. For a namespace or a class from after { to before } 
     */
	public void setValidOffset(int o) {
		validOffset=o;
	}

	/**
     * The valid Length is the length of the region, in which the element can have children or is valid.
     *  For a namespace or a class from after { to before } 
     * @return
     */
	public void setValidLength(int l) {
		validLength=l;
	}
	/**
	 * Find a child of the given element with a given name an a given code type
	 * @param elementName2
	 * @param codeType2
	 * @return
	 */
	public IDotNetElement getChildByNameAndCType(String elementName2, int codeType2) {
		for (int i = 0; i <children.size(); i++){
			IDotNetElement e = recSearchForNCT((CodeElement)children.get(i),elementName2,codeType2);
			if (e != null) {
				return e;
			}
		}
		return null;
	}


	private IDotNetElement recSearchForNCT(IDotNetElement element, String elementName2, int codeType2) {
		if (element.getElementName().equals(elementName2) && element.getElementType() == codeType2){
			return element;
		}
		if (element instanceof IParent) {
			IDotNetElement[] children = ((IParent) element).getChildren();
			for (int i = 0; i < children.length; i++) {
				IDotNetElement e= recSearchForNCT(children[i], elementName2, codeType2);
				if (e != null){
					return e;
				}
			}	
		}
		return null;
	}


	public String getModifierAsString() {
		String res = accessType;
		if (constMod) res += " const";
		if (staticMod) res += " static";
		if (abstractMod) res += " anatract";
		if (virtualMod) res += " virtual";
		if (overrideMod) res += " override";
		if (eventMod) res += " event";
		if (readonlyMod) res +=" readonly";
		if (sealedMod) res +=" sealed";
		if (volatileMod) res +=" volatile";
		if (newMod) res +=" new";
		return res.trim();
	}


	public static CodeElement getCopy(CodeElement orig) {
		CodeElement res = new CodeElement(orig.getParent(), orig.getCodeType());
		res.setAccessType(orig.getAccessType());
		res.setChildren(orig.children);
		res.setDerived(orig.getDerived());
		res.setElementName(orig.getElementName());
		res.setTypeSignature(orig.getTypeSignature());
		res.setEndPoint(orig.getEndPoint());
		res.setLength(orig.getLength());
		res.setNameOffset(orig.getNameOffset());
		res.setOffset(orig.getOffset());
		res.setSignature(orig.getSignature());
		res.setSource(orig.getSource());
		res.setStartPoint(orig.getStartPoint());
		res.setValidLength(orig.getValidLength());
		res.setValidOffset(orig.getValidOffset());
		return res;
	}

	public int getElementType() {
		return getCodeType();
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 * required from ISourceUnit
	 */
	public INamespace[] getNamespaces() {
		List nsElem = this.getNameSpaceList();	
		Iterator iter = nsElem.iterator();
		ArrayList ret = new ArrayList();
		while(iter.hasNext()){
			CodeElement elem = (CodeElement)iter.next();
			SourceNamespace ns = new SourceNamespace(elem.parent, elem.elementName, null /*FIXME IPath*/,
					null/*FIXME IDocumentation*/, elem.getSourceRange(), elem.source);
			//add types to namespace
			IType[] types = elem.getTypes();
			for(int i = 0; i<types.length;i++){
				ns.put(types[i].getElementName(), types[i]);
			}
			
			ret.add(ns);
		}				
		return (INamespace[])ret.toArray(new INamespace[ret.size()]);
		
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	public INamespace getNamespace(String namespaceName) {
		// FIXME Auto-generated method stub
		return null;
	}
	
	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 * required from ISourceUnit
	 * FIXME only sample implementation for getTypes()!
	 */
	public IType[] getTypes() {
		//FIXME 
		//missing (IType[])getInterfaceList().toArray()
		//mising (IType[])getEnumList().toArray()
		//missing (IType[])getStructList().toArray
		List classElem = this.getClassList();
		Iterator iter = classElem.iterator();
		ArrayList ret = new ArrayList();
		while(iter.hasNext()){
			CodeElement elem = (CodeElement)iter.next();
			SourceType type = new SourceType(elem.parent, elem.calcTypeFlags(), null /*no IDoc*/, elem.calcEvents(),
											 elem.calcFields(),elem.calcMethods(),elem.calcProperties(),new IType[0] /*elem.getTypes()*/,
											 elem.codeType, elem.elementName, elem.elementName /*fullname*/,
											 elem.getSuperClass(), elem.getSuperInterfaces(), elem.getSourceRange(),  elem.source);
			ret.add(type);
		}
		return (IType[])ret.toArray(new IType[ret.size()]);
	}
	
	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	public IType getType(String typeName) {
		// FIXME Auto-generated method stub
		return null;
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private String[] getSuperInterfaces() {
		// FIXME Auto-generated method stub
		//parse for super interfaces
		return new String[0];
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private String getSuperClass() {
		return this.getDerived();
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private IField[] calcFields() {
	  	ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.FIELD );
		Iterator iter = resList.iterator();
		ArrayList ret = new ArrayList();
		while(iter.hasNext()){
			CodeElement elem = (CodeElement)iter.next();
			SourceField field = new SourceField(elem.parent, elem.calcFieldFlags(), 
					elem.elementName, null /*noDoc*/, elem.typeSignature,
					elem.getSourceRange(), elem.source);
			ret.add(field);
		}
		return (IField[])ret.toArray(new IField[ret.size()]);
		
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private IProperty[] calcProperties() {
	  	ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.PROPERTY );
		Iterator iter = resList.iterator();
		ArrayList ret = new ArrayList();
		while(iter.hasNext()){
			CodeElement elem = (CodeElement)iter.next();
			SourceProperty property = new SourceProperty(elem.parent, elem.calcMethodFlags(),
						elem.elementName, elem.getPropertyReturn(), elem.signature, 
						null /*no Idoc*/, elem.getSourceRange(), elem.source);
			ret.add(property);
		}
		return (IProperty[])ret.toArray(new IProperty[ret.size()]);
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private String getPropertyReturn() {
		// FIXME Auto-generated method stub
		//parse property return
		String propertyReturn = this.getTypeSignature();
		return propertyReturn;
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 * add methods and constructors
	 */
	private IMethod[] calcMethods() {
		//add methods
	  	ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.METHOD );
		Iterator iter = resList.iterator();
		ArrayList ret = new ArrayList();
		while(iter.hasNext()){
			CodeElement elem = (CodeElement)iter.next();
			SourceMethod method = new SourceMethod(elem.parent, elem.elementName,
						elem.calcMethodFlags(), elem.getMethodReturnType(), elem.signature,
						false,null /*no idoc*/, elem.getParameterTypes(), 
						elem.getParameterNames(), elem.getLocalVars(),
						elem.getSourceRange(), elem.source);
			ret.add(method);
		}
		//add constructors
		resList = new ArrayList();
		resList  = recBuildElementList(this,resList, IDotNetElement.CONSTRUCTOR );
		iter = resList.iterator();
		while(iter.hasNext()){
			CodeElement elem = (CodeElement)iter.next();
			SourceMethod constructor = new SourceMethod(elem.parent, elem.elementName,
						elem.calcMethodFlags(), elem.getMethodReturnType(), elem.signature,
						true,null /*no idoc*/, elem.getParameterTypes(), 
						elem.getParameterNames(), elem.getLocalVars(),
						elem.getSourceRange(), elem.source);
			ret.add(constructor);
		}
		return (IMethod[])ret.toArray(new IMethod[ret.size()]);
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private ILocalVariable[] getLocalVars() {
	  	ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.VARIABLE );
		Iterator iter = resList.iterator();
		ArrayList ret = new ArrayList();
		while(iter.hasNext()){
			CodeElement elem = (CodeElement)iter.next();
			SourceLocalVariable variable = new SourceLocalVariable(elem.parent, elem.calcFieldFlags(), 
					elem.elementName, elem.typeSignature, null /*nodoc*/, 
					elem.getSourceRange(), elem.source);
			ret.add(variable);
		}
		return (ILocalVariable[])ret.toArray(new ILocalVariable[ret.size()]);
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private String[] getParameterNames() {
		calcMethodSignature();
		return this.parameterNames;
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private String[] getParameterTypes() {
		calcMethodSignature();
		return this.parameterTypes;
	}

	/**
	 * parse through signature to get types and names
	 */
	private void calcMethodSignature() {
		String toCalc = signature;
		toCalc = toCalc.trim();
		if(!signature.endsWith("()")){
			toCalc = toCalc.substring(1, toCalc.length()-1); //remove empty elements ( and )
			//get number of params
			// TODO Improve this!
			// We have possible , in generics- so this fails
			toCalc=toCalc.replaceAll("<.*>", "<>");
			String[] typeNamePair = toCalc.split(",");
			this.parameterNames = new String[typeNamePair.length];
			this.parameterTypes = new String[typeNamePair.length];
			for(int i = 0; i< typeNamePair.length; i++){
				//split between type and name
				String[] dividedSigElement = typeNamePair[i].trim().split(" ");
				//DEBUG
				if(dividedSigElement.length < 2){
					System.out.println("Problem calculating signature of method: " + toCalc);
					parameterNames = new String[0];
					parameterTypes = new String[0];
					break;
				}
				//DEBUG
				else{
					parameterTypes[i] = dividedSigElement[0]; //contains type
					parameterNames[i] = dividedSigElement[1]; //contains name
				}
				
			}
		}else{
			parameterNames = new String[0];
			parameterTypes = new String[0];
		}
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private String getMethodReturnType() {
		//parse for method return
		String methodreturn = this.getTypeSignature();
		return methodreturn;
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 * INCOMPLETE EVENT BUILDING
	 */
	private IEvent[] calcEvents() {
	  	ArrayList resList = new ArrayList();
		resList = recBuildElementList(this,resList, IDotNetElement.EVENT );
		Iterator iter = resList.iterator();
		ArrayList ret = new ArrayList();
		while(iter.hasNext()){
			CodeElement elem = (CodeElement)iter.next();
			SourceEvent event = new SourceEvent(elem.parent, elem.calcMethodFlags(),
					null/*nodoc*/, null /*nodelegate*/, elem.elementName,
					elem.signature, new String[0], new String[0],
					elem.getSourceRange(), elem.source);
			ret.add(event);
		}
		return (IEvent[])ret.toArray(new IEvent[ret.size()]);
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private int calcFieldFlags(){
		int flags = 0;
		if(this.accessType != null &&
			(this.accessType.equals("public") || this.accessType.equals("PUBLIC")))
		   flags = flags | 0x0006;
		if(this.staticMod)
			flags = flags | 0x0010;		
		return flags;
	}
	
	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private int calcMethodFlags(){
		int flags = 0;

		if(this.accessType.equals("public") || this.accessType.equals("PUBLIC"))
			   flags = flags | 0x0006;
		if(this.staticMod)
			flags = flags | 0x0010;
		if(this.virtualMod)
			flags = flags | 0x0040;
		if(this.abstractMod)
			flags = flags | 0x0400;		  		   
		
		return flags;
	}
	
	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	private int calcTypeFlags(){
		int flags = 0x00;
		
		if(this.accessType.equals("public") || this.accessType.equals("PUBLIC"))
			   flags = flags | 0x0006;		

		if(this.sealedMod)
			flags = flags | 0x00000100;
		if(this.abstractMod)
			flags= flags | 0x00000080;
		//check class interface struct enum
		switch(codeType){
			case IDotNetElement.CLASS:
				flags = flags | 0000000000;
				break;
			case IDotNetElement.INTERFACE:
				flags = flags | 0x00000020;
				break;
			case IDotNetElement.STRUCT:
				flags = flags | 0x00000008;
				break;
			//FIXME: No flags in TypeAttribute of MBEL for ENUM
			//case IDotNetElement.ENUM:
			//	flags = flags & 
			//	break;
			default:
				break;
		}
			
		return flags;
	}
	
	
	
	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 * required from ISourceUnit
	 */
	public ISourceRange getSourceRange() {
		return new SourceRange(this.startPoint, this.endPoint, this.offset, this.length,
								this.nameoffset, this.validOffset, this.validLength);
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	public String getFileExtension() {
		return ".cs";
	}

	/**
	 * WORKAROUND UNTIL CODEELEMENT IS REMOVED
	 */
	public IPath getPath() {
		// FIXME Auto-generated method stub
		return null;
	}

    public String getBody(){
    	return _body;
    }
    public void setBody(String body){
    	_body = body;
    }
	
}
