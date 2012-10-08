/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Harald Krapfenbauer - adapted for the new Emonic code completion
 *******************************************************************************/
package org.emonic.base.codecompletion.session;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.documentation.IDocumentationParser;
import org.emonic.base.documentation.ITypeDocumentation;
import org.emonic.base.framework.FrameworkFactory;
import org.emonic.base.framework.IFrameworkInstall;

final public class TypeCompletionProposal extends AbstractCSharpCompletionProposal
		implements ICompletionProposal, ICompletionProposalExtension3,
		ICompletionProposalExtension5 {

	private String namespaceName;
	private String typeName;
	private String assemblyPath;

	/** The replacement string. */
	private String string;

	/** The replacement offset. */
	private int offset;

	/** The replacement length. */
	private int length;

	/** The cursor position after this proposal has been applied. */
	private int position;

	
	public TypeCompletionProposal(String namespaceName, String typeName,
			String assemblyPath, String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString) {
		
		super(image, displayString);
		this.namespaceName = namespaceName;
		this.typeName = typeName;
		this.assemblyPath = assemblyPath;
		string = replacementString;
		offset = replacementOffset;
		length = replacementLength;
		position = cursorPosition;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {
		try {
			document.replace(offset, length, string);
		} catch (BadLocationException x) {
			// ignore
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(offset + position, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension5#getAdditionalProposalInfo(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		
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
		
		monitor.beginTask("Scanning for documentation...",parsers.length);

		for (int i = 0; i < parsers.length; i++) {
			ITypeDocumentation documentation =
				parsers[i].findTypeDocumentation(documentationFolder,
						assemblyPath, namespaceName, typeName, new SubProgressMonitor(monitor, 1));
			if (documentation != null) {
				monitor.done();
				return documentation;
			}
		}
		monitor.done();
		return null;
	}
}
