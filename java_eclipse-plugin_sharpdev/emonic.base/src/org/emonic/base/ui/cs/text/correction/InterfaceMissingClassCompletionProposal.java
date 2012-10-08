/*
 * Created on 13.11.2007
 * emonic.base org.emonic.base.ui.cs.text.correction InterfaceMissingClassCompletionProposal.java
 */
package org.emonic.base.ui.cs.text.correction;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.filemanipulators.CSharpFileManipulator;

public class InterfaceMissingClassCompletionProposal implements ICompletionProposal,
		ICompletionProposalExtension2 {
    
	private String _name;
	private int _offset;
	private int _length;
	private String _type;
	private String _classn;
	private CodeElement _element=null;
	public InterfaceMissingClassCompletionProposal(String name, String type, String classn, int markerOffset, int i) {
		_name=name;
		_offset=markerOffset;
		_length=i;
		_type=type;
		_classn=classn;
	}
	
	
	public void apply(IDocument document) {
		// ignored as the framework will call apply(ITextViewer, int, int)
		// instead
	}

	public String getAdditionalProposalInfo() {
		return null;
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getDisplayString() {
		return NLS.bind(Messages.InterfaceAddMethodProposal_DisplayString,_type+" "+_name,_classn);
	}

	public Image getImage() {
		// TODO: replace with an image
		return null;
	}

	public Point getSelection(IDocument document) {
		if (_element!=null){
			return new Point(_element.getOffset(),_element.getLength());
		}
		return null;
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask,
			int offset) {
		// Get the name of the member
		  String methodpart = _name.substring(0,_name.indexOf('('));
		  String ifacepart = methodpart.substring(0, methodpart.lastIndexOf('.'));
		  ifacepart=  ifacepart.substring(ifacepart.lastIndexOf('.')+1,ifacepart.length());
		  methodpart = methodpart.substring(methodpart.lastIndexOf('.')+1,methodpart.length());
		  String signaturePart=_name.substring(_name.lastIndexOf('('),_name.length());
		  CSharpFileManipulator mani = new CSharpFileManipulator(viewer.getDocument());
		  CodeElement toInsert = new CodeElement(null, IDotNetElement.METHOD);
		  toInsert.setElementName(methodpart);
		  toInsert.setAccessType("public");
		  toInsert.setTypeSignature(_type);
		  toInsert.setSignature(signaturePart);
		  // Determine where to add the classes
		 _element=mani.AddCodeElementAsLastElementOfClass(toInsert,_classn,false);
	}

	
	
	
	public void selected(ITextViewer viewer, boolean smartToggle) {
		// nothing to do
	}

	public void unselected(ITextViewer viewer) {
		// nothing to do
	}

	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		return this._offset <= offset && offset <= this._offset + _length;
	}
	

}
