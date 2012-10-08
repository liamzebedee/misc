/*
 * Created on 04.09.2006
 * emonic.base org.emonic.base.editors CSharpEditorActionContributor.java
 */
package org.emonic.base.editors;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.emonic.base.actions.DefaultCSharpSourceDelegate;

public class CSharpEditorActionContributor extends TextEditorActionContributor {

	/**
	 * Associates global action handlers contributed via actionSets in the
	 * plug-in's manifest with their PerlEditorAction counterparts in the
	 * currently active PerlEditor.
	 */

	// ~ Instance fields
	// private ToggleMarkOccurrencesAction toggleMarkOccurrencesAction;
	// ~ Constructors
	public CSharpEditorActionContributor() {
		// toggleMarkOccurrencesAction = new ToggleMarkOccurrencesAction();
	}

	// ~ Methods

	public void dispose() {
		super.dispose();
		// toggleMarkOccurrencesAction. dispose();
	}

	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		if (!(part instanceof CSharpEditor)) {
			return;
		}

		CSharpEditor editor = (CSharpEditor) part;

		// Bind actions contributed by the active editor
		String[] csharpEditorActionIds = DefaultCSharpSourceDelegate.getEditorActions();
		for (int i = 0; i < csharpEditorActionIds.length; i++) {
			getActionBars().setGlobalActionHandler(csharpEditorActionIds[i],
					getAction(editor, csharpEditorActionIds[i]));
		}

		getActionBars().updateActionBars();
	}

	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);

		// bind global actions that effect all open editor instances
		// bars.setGlobalActionHandler(PerlEditorActionIds.TOGGLE_MARK_OCCURRENCES,
		// toggleMarkOccurrencesAction);
	}

	
}


