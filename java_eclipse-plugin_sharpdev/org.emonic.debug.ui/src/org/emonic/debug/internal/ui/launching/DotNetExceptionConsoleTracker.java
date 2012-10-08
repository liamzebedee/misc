/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.emonic.debug.internal.ui.launching;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class DotNetExceptionConsoleTracker implements
		IPatternMatchListenerDelegate {

	private static final Pattern MS_REGEX = Pattern
			.compile("\\s*at ((\\w\\.)*\\w.*\\(.*\\)) in (.*):line ([0-9]+)"); //$NON-NLS-1$

	private static final Pattern MONO_REGEX = Pattern
			.compile("\\s*at ((\\w\\.)*\\w.*\\(.*\\)) .* in (.*):([0-9]+)\\s*"); //$NON-NLS-1$

	private void openEditor(IFile file, IEditorInput input, int line) {
		try {
			IEditorPart part = IDE.openEditor(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage(), file);
			if (part instanceof AbstractTextEditor) {
				AbstractTextEditor editor = (AbstractTextEditor) part;
				IDocument document = editor.getDocumentProvider().getDocument(
						input);
				if (document != null) {
					IRegion region = document.getLineInformation(line - 1);
					((AbstractTextEditor) part).selectAndReveal(region
							.getOffset(), region.getLength());
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void connect(TextConsole console) {
	}

	public void disconnect() {
	}

	public void matchFound(PatternMatchEvent event) {
		TextConsole console = (TextConsole) event.getSource();
		IDocument document = console.getDocument();
		try {
			IRegion region = document.getLineInformationOfOffset(event
					.getOffset());
			int lineOffset = region.getOffset();
			String line = document.get(region.getOffset(), region.getLength());
			Matcher matcher = MS_REGEX.matcher(line);
			if (!matcher.matches()) {
				matcher = MONO_REGEX.matcher(line);
				if (!matcher.matches()) {
					return;
				}
			}
			
			String method = matcher.group(1);
			String filePath = matcher.group(3);
			int methodIndex = line.indexOf(method);
			int fileIndex = line.indexOf(filePath);

			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			String path = root.getRawLocation().toOSString();
			if (filePath.toLowerCase().startsWith(path.toLowerCase())) {
				final IFile file = root.getFile(new Path(filePath
						.substring(path.length())));
				if (file.exists()) {
					final int lineNumber = Integer.parseInt(matcher
							.group(4));
					final IEditorInput input = new FileEditorInput(file);
					console.addHyperlink(new IHyperlink() {
						public void linkActivated() {
							openEditor(file, input, lineNumber);
						}

						public void linkEntered() {
						}

						public void linkExited() {
						}

					}, lineOffset + methodIndex, method.length());

					console.addHyperlink(new IHyperlink() {
						public void linkActivated() {
							openEditor(file, input, lineNumber);
						}

						public void linkEntered() {
						}

						public void linkExited() {
						}
					}, lineOffset + fileIndex, filePath.length());
				}
			}
		} catch (BadLocationException e) {
			// ignored, we'll just not add any hyperlinks
		}
	}
}
