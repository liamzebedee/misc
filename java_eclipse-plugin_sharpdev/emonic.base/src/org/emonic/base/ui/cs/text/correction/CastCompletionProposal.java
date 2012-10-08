/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
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
import org.emonic.base.filemanipulators.CSharpFileManipulator;

public class CastCompletionProposal implements ICompletionProposal,
		ICompletionProposalExtension2 {

	private String suggestedType;
	private int offset;
	private int typeLength;

	public CastCompletionProposal(String suggestedType, int offset,
			int typeLength) {
		this.suggestedType = suggestedType;
		this.offset = offset;
		this.typeLength = typeLength;
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
		return NLS.bind(Messages.CastCompletionProposal_DisplayString,
				suggestedType);
	}

	public Image getImage() {
		// TODO: replace with an image
		return null;
	}

	public Point getSelection(IDocument document) {
		return new Point(offset, suggestedType.length() + 3 + typeLength);
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask,
			int offset) {
		CSharpFileManipulator mani = new CSharpFileManipulator(viewer.getDocument());
		mani.insertCast(suggestedType,this.offset,false);
	}

	public void selected(ITextViewer viewer, boolean smartToggle) {
		// nothing to do
	}

	public void unselected(ITextViewer viewer) {
		// nothing to do
	}

	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		return this.offset <= offset && offset <= this.offset + typeLength;
	}
}
