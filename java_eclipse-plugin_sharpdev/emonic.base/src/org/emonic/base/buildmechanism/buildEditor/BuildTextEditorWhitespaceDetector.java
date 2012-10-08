package org.emonic.base.buildmechanism.buildEditor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class BuildTextEditorWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
