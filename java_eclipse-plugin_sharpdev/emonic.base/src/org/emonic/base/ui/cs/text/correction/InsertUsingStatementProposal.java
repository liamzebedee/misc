/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.emonic.base.ui.cs.text.correction;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.infostructure.CSharpCodeParser;
import org.emonic.base.views.EMonoImages;

public class InsertUsingStatementProposal implements ICompletionProposal {

	private String namespaceName;
	private String displayString;

	public InsertUsingStatementProposal(String namespaceName) {
		this.namespaceName = namespaceName;
		displayString = NLS.bind(
				Messages.InsertUsingStatementProposal_DisplayString,
				namespaceName);
	}

	public void apply(IDocument document) {
		ISourceUnit srcUnit = new CSharpCodeParser(document, null).parseDocument();
		List usingContainers = ((CodeElement)srcUnit).getUsingContainerList();
		if (usingContainers.isEmpty()) {
			try {
				String replacement = NLS
						.bind(
								Messages.InsertUsingStatementProposal_ReplacementString,
								namespaceName, document
										.getLineDelimiter(document
												.getLineOfOffset(0)));
				document.replace(0, 0, replacement);
			} catch (BadLocationException e) {
				// ignored
			}
		} else {
			CodeElement container = (CodeElement) usingContainers.get(0);
			int offset = container.getOffset();
			try {
				String replacement = NLS
						.bind(
								Messages.InsertUsingStatementProposal_ReplacementString,
								namespaceName, document
										.getLineDelimiter(document
												.getLineOfOffset(offset)));
				document
						.replace(offset + container.getLength(), 0, replacement);
			} catch (BadLocationException e) {
				// ignored
			}
		}
	}

	public String getAdditionalProposalInfo() {
		return null;
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getDisplayString() {
		return displayString;
	}

	public Image getImage() {
		return EMonoImages.ICON_USING.createImage();
	}

	public Point getSelection(IDocument document) {
		return null;
	}

}
