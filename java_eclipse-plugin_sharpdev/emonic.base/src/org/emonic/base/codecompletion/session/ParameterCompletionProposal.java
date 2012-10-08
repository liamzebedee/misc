/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Harald Krapfenbauer - Adapted for new Emonic Code Completion
 *******************************************************************************/
package org.emonic.base.codecompletion.session;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codehierarchy.CodeHierarchyHelper;
//import org.emonic.base.codehierarchy.MBel2AssemblyParser;
import org.emonic.base.documentation.IDocumentation;
import org.emonic.base.documentation.IDocumentationParser;
import org.emonic.base.documentation.ITypeDocumentation;
import org.emonic.base.framework.FrameworkFactory;
import org.emonic.base.framework.IFrameworkInstall;

final public class ParameterCompletionProposal extends
		AbstractCSharpCompletionProposal implements ICompletionProposal,
		ICompletionProposalExtension3, ICompletionProposalExtension5 {

	/** The replacement string. */
	private String string;

	/** The replacement offset. */
	private int offset;

	/** The replacement length. */
	private int length;

	/** The cursor position after this proposal has been applied. */
	private int position;

	private int modifiedOffset = -1;

	private int modifiedLength = -1;

	private ITextViewer viewer;
	private String namespaceName;
	private String typeName;
	private String assemblyPath;
	private String memberName;
	

	public ParameterCompletionProposal(String namespaceName, String typeName, String memberName,
			String assemblyPath, ITextViewer viewer, String replacementString, int replacementOffset,
			int replacementLength, int cursorPosition, Image image, String displayString) {
		
		super(image, displayString);
		this.viewer = viewer;
		string = replacementString;
		offset = replacementOffset;
		length = replacementLength;
		position = cursorPosition;
		this.namespaceName = namespaceName;
		this.typeName = typeName;
		this.assemblyPath = assemblyPath;
		this.memberName = memberName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {
		try {
			document.replace(offset, length, string);

			int start = string.indexOf('(');
			int end = string.lastIndexOf(')');
			if (start < end - 1) {
				LinkedModeModel model = new LinkedModeModel();
				
				String[] params = getParameters(string);
				for (int i=0; i<params.length; i++) {
					int posStart = offset + string.indexOf(params[i]);
					int posLength = params[i].length();
					
					LinkedPositionGroup group = new LinkedPositionGroup();
					group.addPosition(new LinkedPosition(document,
							posStart, posLength,
							LinkedPositionGroup.NO_STOP));
					model.addGroup(group);
					
					if (i==0) {
						// the first parameter will be marked
						modifiedOffset = posStart;
						modifiedLength = posLength;
					}
				}
				model.forceInstall();

				LinkedModeUI ui = new EditorLinkedModeUI(model, viewer);
				ui.setExitPosition(viewer, offset + string.length(), 0,
						LinkedPositionGroup.NO_STOP);
				ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
				ui.enter();
			}
		} catch (BadLocationException x) {
			// ignore
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		if (modifiedOffset != -1) {
			return new Point(modifiedOffset, modifiedLength);
		}
		return new Point(offset + position, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension5#getAdditionalProposalInfo(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {

		if (typeName == null || memberName == null) {
			monitor.done();
			return null;
		}
		
		IDocumentationParser[] parsers = EMonoPlugin.getDefault().getParsers();
		IFrameworkInstall install = FrameworkFactory.getDefaultFrameworkInstall();
		if (install == null) {
			monitor.done();
			return null;
		}
		
		String documentationFolder = install.getDocumentationLocation();
		if (documentationFolder.equals("")) {
			monitor.done();
			return null;
		}
		
		monitor.beginTask("Scanning for documentation...", parsers.length);
		String methodName = removeNamesFromSignature(memberName);
		
		for (int i = 0; i < parsers.length; i++) {
			ITypeDocumentation documentation =
				parsers[i].findTypeDocumentation(documentationFolder, assemblyPath,
						namespaceName, typeName, new SubProgressMonitor(monitor, 1));
			
			if (documentation != null) {
				List memberDocumentation = documentation.getDocumentation();
				
				for (int j = 0; j < memberDocumentation.size(); j++) {
					IDocumentation doc = (IDocumentation) memberDocumentation.get(j);
					
					if (doc.getName().equals(methodName)) {
						monitor.done();
						return doc;
					}
				}
			}
		}
		monitor.done();
		return null;
	}

	
	/**
	 * Removes the parameter names from a method signature.
	 * @param method A string containing the method name followed by the signature, e.g. 
	 * <code>add (int first, int second)</code>
	 * @return The method name followed by the compacted signature, e.g. <code>add(int,int)</code>
	 */
	public static String removeNamesFromSignature(String method) {
		int index1 = method.indexOf('(');
		String methodName = method.substring(0,index1);
//		methodName = methodName.replace(" ", "");
		methodName = methodName.trim();

		String[] sigSplitted = getParameters(method);
		StringBuffer result = new StringBuffer();
		result.append(methodName);
		result.append("(");
		for (int i=0; i<sigSplitted.length; i++) {
			int index = sigSplitted[i].lastIndexOf(" ");
			if (index == -1)
				continue;
			String withoutName = sigSplitted[i].substring(0, index);
			// withoutName = withoutName.replace(" ", ""); //replace(string,string) does not exist! Only replace(char,char)!
			 withoutName = withoutName.replaceAll(" ", "");
			withoutName = CodeHierarchyHelper.convertSignature(withoutName);
			result.append(withoutName);
			result.append(",");
		}
		result.deleteCharAt(result.length()-1);
		result.append(")");
		return result.toString();
	}

	/**
	 * Gets parameters out of a method.
	 * @param method A method followed by its signature, e.g. <code>int add (int first, int second)</code>
	 * @return a String array containing the parameters, e.g. [int first][int second]
	 */
	public static String[] getParameters(String method) {
		int index1 = method.indexOf('(');
		int index2 = method.lastIndexOf(')');
		String sigWithoutParen = method.substring(index1+1,index2);
		String[] sigSplitted = sigWithoutParen.split(",");
		for (int i=0; i<sigSplitted.length; i++) {
			sigSplitted[i] = sigSplitted[i].trim();
		}
		if (sigSplitted.length == 1 && sigSplitted[0].equals(""))
			return new String[0];
		else
			return sigSplitted;
	}

}
