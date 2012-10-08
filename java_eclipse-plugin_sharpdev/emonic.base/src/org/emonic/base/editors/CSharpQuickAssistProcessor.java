/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IMethod;
import org.emonic.base.infostructure.CodeInformator;
import org.emonic.base.search.SearchCodeInformator;
import org.emonic.base.ui.cs.text.correction.CastCompletionProposal;
import org.emonic.base.ui.cs.text.correction.InsertUsingStatementProposal;
import org.emonic.base.ui.cs.text.correction.InterfaceMissingClassCompletionProposal;
import org.emonic.base.ui.cs.text.correction.RemoveUnusedVariableProposal;

final class CSharpQuickAssistProcessor implements IQuickAssistProcessor {
    
	private static final Pattern PATTERN_INSERT_USING_STATEMENT = Pattern.compile(".*(`|')(.*)'.*");
	private static final String REGEXPFINDCASTTYPE = "to\\s+['`\"]+([\\d\\w\\s_\\.]+)['`\"]+";
	private static final String REGEXPFINDMETHODNAME = "['`\"]+([\\(\\)<>\\d\\w\\s_\\.]+)['`\"].*member\\s+['`\"]+([\\(\\)<>\\d\\w\\s_\\.]+)['`\"]";
	
	//CS0266: Cannot implicitly convert type `double' to `int'. An explicit conversion exists (are you missing a cast?)	bla11/src	new_file.cs	line 9	1185835777872	3746

	
	//private static final String REGEXPFINDCASTTYPE = "(nt)";
	private ISourceViewer sourceViewer;
	private CSharpEditor editor;

	CSharpQuickAssistProcessor(ISourceViewer sourceViewer, CSharpEditor textEditor) {
		this.sourceViewer = sourceViewer;
		this.editor=textEditor;
		
	}

	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		//return true;
		return false;
	}

	public boolean canFix(Annotation annotation) {
		//return true;
		return false;
	}

	public ICompletionProposal[] computeQuickAssistProposals(
			IQuickAssistInvocationContext invocationContext) {
		int offset = invocationContext.getOffset();
		Collection c = new ArrayList();
		Iterator it = sourceViewer.getAnnotationModel().getAnnotationIterator();
		while (it.hasNext()) {
			Annotation a = (Annotation) it.next();
			if (a instanceof MarkerAnnotation) {
				MarkerAnnotation annotation = (MarkerAnnotation) a;
				IMarker marker = annotation.getMarker();
				int markerOffset = marker.getAttribute(IMarker.CHAR_START, -1);
				int markerEnd = marker.getAttribute(IMarker.CHAR_END, -1);
				if (markerOffset <= offset && offset <= markerEnd) {
					String message = marker.getAttribute(IMarker.MESSAGE, null);
					if (message != null) {
						if (message.startsWith("CS0168")) { //$NON-NLS-1$
							int markerLength = markerEnd - markerOffset;
							try {
								String name = sourceViewer.getDocument().get(markerOffset, markerLength);
								c.add(new RemoveUnusedVariableProposal(markerOffset, markerLength, name));
							} catch (BadLocationException e) {
								// this shouldn't happen
							}
						} else if (message.startsWith("CS0246")) { //$NON-NLS-1$
							c.addAll(createInsertUsingStatementProposals(message));
						} else if (message.startsWith("CS0266") ) { //$NON-NLS-1$
							// missing cast error
							ICompletionProposal proposal = createCastCompletionProposal(message,
									markerOffset, markerEnd);
							if (proposal != null) {
								c.add(proposal);
							}
						} else if (message.startsWith("CS0535")) { //$NON-NLS-1$
							c.addAll(Arrays.asList(createInterfaceMissingClassCompletionProposal(message,
									markerOffset, markerEnd)));
						}
					}
				}
			}
		}
		return c.isEmpty() ? null : (ICompletionProposal[]) c.toArray(new ICompletionProposal[c.size()]);
	}
	
	private Collection createInsertUsingStatementProposals(String message) {
		Matcher m = PATTERN_INSERT_USING_STATEMENT.matcher(message);
		if (!m.matches()) {
			return Collections.EMPTY_LIST;
		}
		CodeInformator informator = new CodeInformator();
		informator.setEditor(editor);
		IDotNetElement[] types = informator.findTypesWithName(m.group(2));
		List proposals = new ArrayList(types.length);
		for (int i = 0; i < types.length; i++) {
			IDotNetElement namespace = types[i].getAncestor(IDotNetElement.NAMESPACE);
			if (namespace != null) {
				proposals.add(new InsertUsingStatementProposal(namespace.getElementName()));	
			}
		}
		return proposals;
	}

	private ICompletionProposal[] createInterfaceMissingClassCompletionProposal(String message, int markerOffset, int markerEnd) {
		String regExp = REGEXPFINDMETHODNAME;
		Pattern sep = Pattern.compile(regExp);
		Matcher mat = sep.matcher(message);
		
		if (mat.find()&& mat.group(1)!=null&& mat.group(2)!=null){
			String classn = mat.group(1);
			if (classn.lastIndexOf('.') !=-1){
				classn=classn.substring(classn.lastIndexOf('.')+1,classn.length());
			}
			String name = mat.group(2);
			String methodpart = "";
			String ifacepart="";
			try{
			  methodpart = name.substring(0,name.indexOf('('));
			  ifacepart = methodpart.substring(0, methodpart.lastIndexOf('.'));
			  ifacepart=  ifacepart.substring(ifacepart.lastIndexOf('.')+1,ifacepart.length());
  			  methodpart = methodpart.substring(methodpart.lastIndexOf('.')+1,methodpart.length());
  			} catch(final Exception e){
  				//Nothing to do
  			}
  			SearchCodeInformator info = new SearchCodeInformator();
  			info.setEditor(editor);
  			info.setSearchDlls(false);
  			IDotNetElement[] posMethods=info.searchForInterfaceMethodDeclarations(ifacepart,methodpart);
  			if (posMethods.length > 0){
  	  			ICompletionProposal[] result = new ICompletionProposal[posMethods.length];
  				for (int i = 0; i < result.length;i++){
  					if (posMethods[i] instanceof CodeElement) {
  	  					String type = ((CodeElement) posMethods[i]).getTypeSignature();
  	  					result[i]=new InterfaceMissingClassCompletionProposal(name,type,classn, markerOffset, markerEnd - markerOffset);	
  					} else {
  	  					String type = ((IMethod) posMethods[i]).getReturnType();
  	  					result[i]=new InterfaceMissingClassCompletionProposal(name,type,classn, markerOffset, markerEnd - markerOffset);  						
  					}
  				}
  	  			return result;
  			}
		}
		return new ICompletionProposal[0];
	}

	public String getErrorMessage() {
		return null;
	}

	private ICompletionProposal createCastCompletionProposal(String message,
			int markerOffset, int markerEnd) {
		// we retrieve the type that's been suggested by the compiler's error
		// message, the format basically goes like "...'found'...'expected'..."
		String regExp = REGEXPFINDCASTTYPE;
		Pattern sep = Pattern.compile(regExp);
		Matcher mat = sep.matcher(message);
		if (mat.find()&&mat.group(1)!=null){
			String type = mat.group(1);
			return new CastCompletionProposal(type, markerOffset,
					markerEnd - markerOffset);
		}		
		return null;
	}

}
