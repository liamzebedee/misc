/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Harald Krapfenbauer - adapted for new Emonic Code Completion
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
import org.emonic.base.documentation.IDocumentation;
import org.emonic.base.documentation.IDocumentationParser;
import org.emonic.base.framework.FrameworkFactory;
import org.emonic.base.framework.IFrameworkInstall;

final public class NamespaceCompletionProposal extends
		AbstractCSharpCompletionProposal implements ICompletionProposal,
		ICompletionProposalExtension3, ICompletionProposalExtension5 {

	/** The name of the namespace. */
	private String namespaceName;

	/** The full path of the assembly. */
	private String assemblyPath;

	/** The replacement string. */
	private String string;

	/** The replacement offset. */
	private int offset;

	/** The replacement length. */
	private int length;

	/** The cursor position after this proposal has been applied. */
	private int position;

	
	public NamespaceCompletionProposal(String namespaceName, String assemblyPath,
			String replacementString, int replacementOffset,
			int replacementLength, int cursorPosition, Image image,
			String displayString) {
		
		super(image, displayString);
		this.namespaceName = namespaceName;
		this.assemblyPath = assemblyPath;
		string = replacementString;
		offset = replacementOffset;
		length = replacementLength;
		position = cursorPosition;
	}

	/*
	 * @see ICompletionProposal#apply(IDocument)
	 */
	public void apply(IDocument document) {
		try {
			document.replace(offset, length, string);
		} catch (BadLocationException x) {
			// ignore
		}
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(offset + position, 0);
	}

	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {

		IDocumentationParser[] parsers = EMonoPlugin.getDefault().getParsers();
		IFrameworkInstall install = FrameworkFactory.getDefaultFrameworkInstall();
		if (install == null) {
			monitor.done();
			return null;
		}
		
		String documentationFolder = install.getDocumentationLocation();
		if (documentationFolder.equals("")) { //$NON-NLS-1$
			monitor.done();
			return null;
		}
		
		monitor.beginTask("Scanning for documentation...",parsers.length);

		for (int i = 0; i < parsers.length; i++) {
			IDocumentation documentation =
				parsers[i].findNamespaceDocumentation(documentationFolder,
						assemblyPath, namespaceName, new SubProgressMonitor(monitor, 1));
			if (documentation != null) {
				monitor.done();
				return documentation;
			}
		}
		monitor.done();
		return null;
	}
}
