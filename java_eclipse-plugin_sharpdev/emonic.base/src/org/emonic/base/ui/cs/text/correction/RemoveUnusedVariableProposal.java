/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.base.ui.cs.text.correction;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.ISourceUnit;
import org.emonic.base.infostructure.CSharpCodeParser;

public class RemoveUnusedVariableProposal implements ICompletionProposal {

	private int markerOffset;
	private int markerLength;
	private String variableName;

	private static CodeElement findElement(CodeElement element, int offset,
			int length) {
		if (element.getNameOffset() == offset
				&& element.getNameLength() == length) {
			return element;
		} else {
			IDotNetElement[] children = element.getChildren();
			for (int i = 0; i < children.length; i++) {
				CodeElement child = (CodeElement) children[i];
				CodeElement ce = findElement(child, offset, length);
				if (ce != null) {
					return ce;
				}
			}
			return null;
		}
	}

	public RemoveUnusedVariableProposal(int markerOffset, int markerLength,
			String variableName) {
		this.markerOffset = markerOffset;
		this.markerLength = markerLength;
		this.variableName = variableName;
	}

	public void apply(IDocument document) {
		try {
			ISourceUnit srcUnit = new CSharpCodeParser(document, "").parseDocument(); //$NON-NLS-1$
			srcUnit = findElement((CodeElement)srcUnit, markerOffset, markerLength);
			if (srcUnit != null) {
				int offset = ((CodeElement)srcUnit).getOffset();

				String text = document.get(offset, ((CodeElement)srcUnit).getLength());
				int index = text.indexOf(';') + offset + 1;
				while (Character.isWhitespace(document.getChar(index))) {
					index++;
				}
				document.replace(offset, index - offset, ""); //$NON-NLS-1$
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getAdditionalProposalInfo() {
		return null;
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getDisplayString() {
		return NLS.bind(Messages.RemoveUnusedVariableProposal_DisplayString,
				variableName);
	}

	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_TOOL_DELETE);
	}

	public Point getSelection(IDocument document) {
		return null;
	}

}
