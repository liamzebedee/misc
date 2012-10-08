/*********************************************************************
 *  Copyright (c) 2001, 2008 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Mar 24, 2005
 * 
 ************************************************************************/

package org.emonic.base.buildmechanism;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * 
 * @author bb
 *
 */
public class XMLFileManipulator implements IBuildFileManipulator,IBuildDocumentSyncronisator  {
	private static final String APOS = "&apos;"; //$NON-NLS-1$
	private static final String QUOT = "&quot;"; //$NON-NLS-1$
	private static final String AMP = "&amp;"; //$NON-NLS-1$
	private static final String GT = "&gt;"; //$NON-NLS-1$
	private static final String LT = "&lt;"; //$NON-NLS-1$
	private static final String ERRIGNORENODE = "Ignoring node: "; //$NON-NLS-1$
	private static final String COMEND = "-->\n"; //$NON-NLS-1$
	private static final String COMSTART = "<!--"; //$NON-NLS-1$
	private static final String CDATAEND = "]]"; //$NON-NLS-1$
	private static final String CDATASTART = "![CDATA["; //$NON-NLS-1$
	private static final String DIRECTEND = "?>\n"; //$NON-NLS-1$
	private static final String DIRECTSTART = "<?"; //$NON-NLS-1$
	private static final String SINGLEELEMENTEND = "/>\n"; //$NON-NLS-1$
	private static final String ENDELEMENTSTART = "</"; //$NON-NLS-1$
	private static final String ELEMENTSTART = "<"; //$NON-NLS-1$
	private static final String ELEMENTEND = ">\n"; //$NON-NLS-1$
	private static final String DOCTYPEELEMENT = "<!DOCTYPE "; //$NON-NLS-1$
	private static final String XMLHEADER = "<?xml version='1.0'?>\n"; //$NON-NLS-1$
	private static final String EMPTYSTRING = ""; //$NON-NLS-1$
	private static final String EMONICHEADER = "<?xml version=\"1.0\"?>\n<!-- Createt by emonic -->\n"; //$NON-NLS-1$
	protected Document document;
	private final int indentDefaultWith = 2 ;
	protected IFile file;
	
	/** 
	 * 
	 * The Constructor 
	 * 
	 */
	public XMLFileManipulator(){
		super();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder parser = dbf.newDocumentBuilder();
			parser = dbf.newDocumentBuilder();
			document = parser.newDocument();
			//write();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}	
	}
//	
//	/**
//	 * 
//	 * @param fn
//	 */
//	public XMLFileManipulator(IFile fn) {
//		//super(fn);
//		document = null;
//	}
//	/**
//	 * 
//	 * @param fn
//	 * @param moni
//	 */
//	public XMLFileManipulator(IFile fn, IProgressMonitor moni) {
//		file = fn;
//		if (!file.exists() || !file.isAccessible() || !seemsLikeXML(file)){
//			if (!file.exists()){
//				ByteArrayInputStream s = new  ByteArrayInputStream(EMONICHEADER.getBytes());
//				// Create the file to use
//				try {
//					file.create(s, true, new NullProgressMonitor());
//				} catch (CoreException e) {
//					e.printStackTrace();
//				}
//			}
//		   // Create a content xerxes can handle
//			try {
//				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//				dbf.setValidating(false);
//				DocumentBuilder parser = dbf.newDocumentBuilder();
//				parser = dbf.newDocumentBuilder();
//				document = parser.newDocument();
//				//write();
//			} catch (ParserConfigurationException e1) {
//				e1.printStackTrace();
//			}	
//		} else {
//			parseDocument();
//		}
//	}

	
	
	
	public String getIndent(int level){
		String result = EMPTYSTRING;
		for (int i = 0; i < level; i++){
			for (int j = 0; j < indentDefaultWith; j++){
				result += ' '; 
			}
		}
		return result;
	}
	/**
	 * Parse the document
	 */
	private void parseDocument() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		try {
			DocumentBuilder parser = dbf.newDocumentBuilder();
			//System.out.println("File-Content bei Parsen:" + file.getContents().toString() +file.toString());
			if (file.exists() && file.isAccessible() && seemsLikeXML(file)){
			//if (file.exists() && file.isAccessible() ){
				InputStream s = file.getContents();
				//parser.setErrorHandler(eh );
				document = parser.parse(s);
				if (s!=null) {
					s.close();
				} else {
					//lets create a new doc!
					try {
						parser = dbf.newDocumentBuilder();
						document = parser.newDocument();
					} catch (ParserConfigurationException e1) {
						e1.printStackTrace();
					}	
				}
			}
		} catch (ParserConfigurationException e){
			e.printStackTrace();
		} catch (SAXException e) {
			// If we have a SAXExcepton, this means,
			// the document is sued (no XML)
			// So lets create a fresh one!
			try {
				DocumentBuilder parser = dbf.newDocumentBuilder();
				document = parser.newDocument();
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	
	}
	
	private boolean seemsLikeXML(IFile file) {
		String cont="";
		try {
			InputStream inputStream  = file.getContents();
			int byteRead;
			byte[] buffer = new byte [4096];
			StringBuffer content = new StringBuffer();
			synchronized (content) {
				while ((byteRead = inputStream.read(buffer)) != -1) {
					// Copy to the content
					char[] c = new char[byteRead];
					for (int i = 0; i<byteRead; i++) {
						c[i] = (char) buffer[i];
					}
					content.append(c);
				}
				cont=content.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			cont="";
		}
		boolean result=false;
		if (cont.indexOf('<') >= 0  && cont.lastIndexOf('>')>0){
			// We have at least one tag -> true
			result=true;
		}
		
		
		return result;
	}

	/**
	 * Write the document
	 * @param doc: The xml-document to write
	 *
	 */
	private void write(){
		StringBuffer buffer = new StringBuffer();
		synchronized (buffer) {
			loopWrite(document, 0, buffer);
		}
		ByteArrayInputStream st = new  ByteArrayInputStream(buffer.toString().getBytes());
		try {
			file.setContents(st, true, false, new NullProgressMonitor());
			//System.out.println("XML to be written:: " + s);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write all notes recursivly
	 * @param n
	 * @param level 
	 */
	private StringBuffer loopWrite(Node n, int level, StringBuffer _writer) {
		// The output depends on the type of the node
		switch (n.getNodeType()) {
		case Node.DOCUMENT_NODE: { 
			// If its a Document create the document
			Document doc = (Document) n;
			//System.out.println("New document callingRoot node found");
			_writer.append(XMLHEADER); // Output header
			Node child = doc.getFirstChild(); // Get the first node
			while (child != null) { // Output the childs
				//System.out.println("Dokument has a child with name: " +child.getLocalName() + " and a type: " + child.getNodeType());
				_writer = loopWrite(child, level, _writer);
				child = child.getNextSibling(); 
			}
			break;
		}
		case Node.DOCUMENT_TYPE_NODE: { // It is a <!DOCTYPE> tag
			DocumentType dt = (DocumentType) n;
			_writer.append(DOCTYPEELEMENT).append(dt.getName()).append(ELEMENTEND);
			break;
		}
		case Node.ELEMENT_NODE: { // A normal Element
			Element e = (Element) n;
			_writer.append(getIndent(level)).append(ELEMENTSTART).append(e.getTagName()); 
			NamedNodeMap attributes = e.getAttributes(); 
			for (int i = 0; i < attributes.getLength(); i++) { // Loop through Attributes
				Node a = attributes.item(i);
				String val = a.getNodeValue();
				if (val != null){
					val=fixReservedChars(val);
				} else {
					val="";
				}
				_writer.append(' ').append(a.getNodeName()).append("='").append(val).append('\'');
			}
			 
			int newLevel = level+1;
			// Noe the children
			Node child = e.getFirstChild();
			boolean hasChildren = false;
			if (child != null) {
				hasChildren=true;
			}
			if (hasChildren){
				_writer.append(ELEMENTEND);
				while (child != null) { // Loop over the childs and write them
					if (child instanceof Text) {
						// append text elements directly
						Text textNode = (Text) child;
						String text = null;
						if (textNode.getData()!= null){
							text=textNode.getData().trim(); 
						}
						if ((text != null) && text.length() > 0) {
							//We do not indent Text Nodes! If we do this the text changes!
							//writer+=getIndent(level) + fixReservedChars(text) + "\n"; // print text
							while (Character.isWhitespace(_writer.charAt(_writer.length() - 1))) {
								_writer.deleteCharAt(_writer.length() - 1);
							}
							_writer.append(fixReservedChars(text));
						}
					} else {
						_writer = loopWrite(child, newLevel,_writer);	
					} 
					child = child.getNextSibling();
				}
				// only if the last node was not a text element
				if (!(e.getLastChild() instanceof Text)) {
					_writer.append(getIndent(level));
				}
				_writer.append(ENDELEMENTSTART).append(e.getTagName()).append(ELEMENTEND);
			} else {
				_writer.append(SINGLEELEMENTEND);
			}
			break;
		}
		case Node.TEXT_NODE: { // Plain text node
			Text textNode = (Text) n;
			String text = null;
			if (textNode.getData()!= null){
				text=textNode.getData().trim(); 
			}
			if ((text != null) && text.length() > 0) {
				//We do not indent Text Nodes! If we do this the text changes!
				//writer+=getIndent(level) + fixReservedChars(text) + "\n"; // print text
				_writer.append(fixReservedChars(text)).append('\n'); // print text
			}
			break;
		}
		case Node.PROCESSING_INSTRUCTION_NODE: { // Handle PI nodes
			ProcessingInstruction pi = (ProcessingInstruction) n;
			_writer.append(getIndent(level)).append(DIRECTSTART).append(pi.getTarget()).append(' ').append(pi.getData()).append(DIRECTEND);
			break;
		}
		case Node.ENTITY_REFERENCE_NODE: { // Handle entities
			_writer.append(getIndent(level)).append('&').append(n.getNodeName()).append(";\n");
			break;
		}
		case Node.CDATA_SECTION_NODE: { // Output CDATA sections
			CDATASection cdata = (CDATASection) n;
			// Careful! Don't put a CDATA section in the program itself!
			_writer.append(getIndent(level)).append(ELEMENTSTART).append(CDATASTART).append(cdata.getData()).append(CDATAEND).append(ELEMENTEND);
			break;
		}
		case Node.COMMENT_NODE: { // Comments
			Comment comment = (Comment) n;
			_writer.append(getIndent(level)).append(COMSTART).append(comment.getData()).append(COMEND);
			break;
		}
		default: 
			System.err.println(ERRIGNORENODE + n.getClass().getName());
			break;
		}
		return _writer;
	}
	
	protected Node SearchForNode(String name){
		return LoopSearch(document,name);
	}
	
	
	/**
	 * @param document2
	 * @param name
	 * @return
	 */
	protected Node LoopSearch(Node node, String name) {
		if (node == null || name == null) {
			return null;
		}
		if (node.getNodeName().equalsIgnoreCase(name)){
			return node;
		}
		Node child = node.getFirstChild();
		while (child != null){
			Node result = LoopSearch(child,name);
			if (result != null){
				return result;
			}
			child=child.getNextSibling();
		}
		return null;
	}

	// This method replaces reserved characters with entities.
	String fixReservedChars(String s) {
		StringBuffer sb = new StringBuffer();
		int l = s.length();
		for (int i = 0; i < l; i++) {
			char c = s.charAt(i);
			switch (c) {
			default:
				sb.append(c);
				break;
			case '<':
				sb.append(LT);
				break;
			case '>':
				sb.append(GT);
				break;
			case '&':
				sb.append(AMP);
				break;
			case '"':
				sb.append(QUOT);
				break;
			case '\'':
				sb.append(APOS);
				break;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Search a node with the name nodename, a special attribute and the val the attribute must have
	 * @param nodename: the name to search for
	 * @param tagneme: the name of the attribute to search for
	 * @param nameTag: The value the attribute must have
	 * @return
	 */
	protected Node getNodebyTag(String nodename,String tagName,  String nameTag) {
		Node resultnode = loopSearchForTag(document,nodename,tagName,nameTag);
		return resultnode;
	}
	
	/**
	 * @param document2
	 * @param nodeName
	 * @return
	 */
	protected Node loopSearchForTag(Node node, String nodeName,String tagName, String nametag) {
		if (node.getNodeName().equalsIgnoreCase(nodeName)){
			NamedNodeMap attributes = node.getAttributes(); 
			for (int i = 0; i < attributes.getLength(); i++) { // Loop through Attributes
				Node a = attributes.item(i);
				//System.out.println("Node " + a.getNodeName() + " Value " + a.getNodeValue());
				String val = a.getNodeValue();
				if (val == null){
					val="";
				}
				if (a.getNodeName().equalsIgnoreCase(tagName) && val.equalsIgnoreCase(nametag)) {
					return node;
				}
			}
		}
		Node child = node.getFirstChild();
		while (child != null){
			Node result = loopSearchForTag(child,nodeName,tagName,nametag);
			if (result != null){
				return result;
			}
			child=child.getNextSibling();
		}
		return null;
	}
	
	/**
	 * Search all the nodes with a special tag
	 * @param tagName
	 * @return
	 */
	protected Node[] getAllNodesOfTag(String tagName){
		
		NodeList n = document.getElementsByTagName(tagName);
		int l = n.getLength();
		Node[] res = new Node[l];
		for (int i = 0; i< l ; i++) {
			res[i]=n.item(i);
		}
		return res;
	}
	
//	/**
//	 * @param document2
//	 * @param nodeName
//	 * @param tagName
//	 * @param resNodes
//	 */
//	private void loopSearchForAllTags(Node node, String nodeName, String tagName, ArrayList resNodes) {
//		
//		if (node.getNodeName().equals(nodeName)){
//			NamedNodeMap attributes = node.getAttributes(); 
//			for (int i = 0; i < attributes.getLength(); i++) { // Loop through Attributes
//				Node a = attributes.item(i);
//				if (a.getNodeName().equals(tagName)) resNodes.add(node);
//			}
//		}
//		Node child = node.getFirstChild();
//		while (child != null){
//			loopSearchForAllTags(child,nodeName,tagName,resNodes);
//			child=child.getNextSibling();
//		}
//	}

	/**
	 * @param parentnode
	 * @param nodename
	 * @param tagname
	 * @param tagvalue
	 */
	protected Element appendChildWithAttrib(Element node, String nodeName, String tagName, String tagValue) {
		if (node != null){
			Element newnode = (Element) loopSearchForTag(node, nodeName,tagName, tagValue);
			if (newnode == null){
				newnode = document.createElement(nodeName); 
				newnode.setAttribute(tagName,tagValue);
				node.appendChild(newnode);
			}
			return newnode;
				
		}
		return null;
	}
	
	
	
	public void setBuildFile(IFile file){
		this.file=file;
		if (!file.exists() || !file.isAccessible() || !seemsLikeXML(file)){
			if (!file.exists()){
				ByteArrayInputStream s = new  ByteArrayInputStream(EMONICHEADER.getBytes());
				// Create the file to use if not already there
				try {
					file.create(s, true, new NullProgressMonitor());
				} catch (CoreException e) {
					// Sometimes the file seems to exist and (!file.exists()) seems to be true - whyever! 
					//e.printStackTrace();
				}
			}
		   // Create a content xerxes can handle
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setValidating(false);
				DocumentBuilder parser = dbf.newDocumentBuilder();
				parser = dbf.newDocumentBuilder();
				document = parser.newDocument();
				//write();
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			}	
		} else {
			parseDocument();
		}
	}
	
	public void save() {
		write();
	}
	
	public IFile getBuildFile(){
		return file;
	}




	public String suggestFileName() {
		return "build.xml";
	}
	
	public org.eclipse.jface.text.IDocument getAsDocument() {
		 org.eclipse.jface.text.Document doc = new org.eclipse.jface.text.Document();
		StringBuffer buffer = new StringBuffer();
		synchronized (buffer) {
			loopWrite(document, 0, buffer);
		}
		doc.set(buffer.toString()); 
		return doc;
	}
	
	public void initFromDocument(org.eclipse.jface.text.IDocument doc) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		try {
			DocumentBuilder parser = dbf.newDocumentBuilder();
			
			ByteArrayInputStream s = new ByteArrayInputStream(doc.get()
					.getBytes()); // doc.get()
			document = parser.parse(s);
			s.close();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// If we have a SAXExcepton, this means,
			// the document is sued (no XML)
			// So lets create a fresh one!
			try {
				DocumentBuilder parser = dbf.newDocumentBuilder();
				document = parser.newDocument();
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}