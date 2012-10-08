package org.emonic.base.buildmechanism.buildEditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextEditor;

public class BuildTextEditorPage extends TextEditor {

	private ColorManager colorManager;

	public BuildTextEditorPage() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new BuildTextEditorConfiguration(colorManager));
		setDocumentProvider(new BuildTextEditorDocumentProvider ());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
	public IDocument getDocument(){
		return this.getSourceViewer().getDocument();
	}
	
	public void setUndoPoint() {
		// This method is protected.....
		this.markInNavigationHistory();
		
	}

}
