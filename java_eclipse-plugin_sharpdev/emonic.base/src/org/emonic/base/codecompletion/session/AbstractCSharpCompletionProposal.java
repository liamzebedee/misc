/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.base.codecompletion.session;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

abstract class AbstractCSharpCompletionProposal implements ICompletionProposal,
		ICompletionProposalExtension3, ICompletionProposalExtension5 {
	
	private Image image;
	
	private String displayString;
	
	AbstractCSharpCompletionProposal(Image image, String displayString) {
		this.image = image;
		this.displayString = displayString;
	}

	public String getAdditionalProposalInfo() {
		// getAdditionalProposalInfo(IProgressMonitor) will be called instead
		return null;
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getDisplayString() {
		return displayString;
	}

	public Image getImage() {
		return image;
	}

	public IInformationControlCreator getInformationControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new ProposalInformationControlCreator(parent);
			}
		};
	}

	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return completionOffset;
	}

	public CharSequence getPrefixCompletionText(IDocument document,
			int completionOffset) {
		return null;
	}

}
