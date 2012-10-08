/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.builders;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

class CSharpPatternMatchListener implements IPatternMatchListener {

	private IProject project;

	void setProject(IProject project) {
		this.project = project;
	}

	public int getCompilerFlags() {
		return Pattern.CASE_INSENSITIVE;
	}

	public String getLineQualifier() {
		return null;
	}

	public String getPattern() {
		return "(.*\\[csc\\]\\s+(\\S.*)\\((\\d+)(,\\d+)?\\):*\\s*error\\s.*)|((\\S.*)\\((\\d+)(,\\d+)?\\):*\\s*warning\\s(.*))"; //$NON-NLS-1$
	}

	public void connect(TextConsole console) {
	}

	public void disconnect() {
	}

	public void matchFound(PatternMatchEvent e) {
		if (project == null) {
			return;
		}
		try {
			TextConsole console = ((TextConsole) e.getSource());
			IDocument document = console.getDocument();

			String match = document.get(e.getOffset(), e.getLength());
			String content = match.trim();
			content = content.substring(5, content.lastIndexOf(':'));
			content = content.substring(0, content.lastIndexOf(':'));
			int index = content.lastIndexOf('(') + 1;
			int lastIndex = content.lastIndexOf(')') - 1;
			String[] split = content.substring(index, lastIndex).split(","); //$NON-NLS-1$
			final int line = Integer.parseInt(split[0]) - 1;

			content = content.substring(0, content.lastIndexOf('('));
			content = content.trim();
			String orgPath = project.getLocation().toOSString();
			String file = content;
			if (content.toLowerCase().startsWith(orgPath.toLowerCase())) {
				file = content.substring(orgPath.length());
			}
			final IFile theFile = project.getFile(file);
			if (!theFile.exists()) {
				return;
			}
			console.addHyperlink(new IHyperlink() {
				public void linkActivated() {
					try {
						IEditorPart part = IDE.openEditor(PlatformUI
								.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage(), theFile);
						if (part instanceof ITextEditor) {
							ITextEditor editor = (ITextEditor) part;
							IDocument document = editor.getDocumentProvider()
									.getDocument(part.getEditorInput());
							if (document != null) {
								int offset = document.getLineOffset(line);
								int length = document.getLineLength(line);
								editor.setHighlightRange(offset, length, true);
							}
						}
					} catch (BadLocationException e) {
						// ignored
					} catch (PartInitException e) {
						// ignored
					}
				}

				public void linkEntered() {
					// nothing to do
				}

				public void linkExited() {
					// nothing to do
				}

			}, document.get().lastIndexOf(match) + match.indexOf(content),
					content.length());
		} catch (BadLocationException e1) {
			// ignored
		}
	}

}
