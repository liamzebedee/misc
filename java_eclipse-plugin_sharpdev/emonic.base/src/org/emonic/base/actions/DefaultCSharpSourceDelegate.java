/* * Created on 31.08.2006
 * emonic.base org.emonic.base.actions DefaultCSharpSourceDelegate.java
 */
package org.emonic.base.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.emonic.base.editors.CSharpEditor;
import org.emonic.base.editors.CSharpEditorMessages;
import org.emonic.base.editors.CSharpSourceViewer;
import org.emonic.base.filemanipulators.CSharpFileManipulator;

public class DefaultCSharpSourceDelegate extends ActionDelegate implements
		IEditorActionDelegate {

	private CSharpEditor editor;

	public DefaultCSharpSourceDelegate() {

	}

	// Definition of the command ID-s
	public static final String TOGGLE_COMMENT_COMMAND = "org.emonic.editors.commands.toggleComment";
	public static final String TOGGLE_COMMENT_ACTION = "org.emonic.editors.actions.ToggleCommentAction";
	
	public static final String CORRECT_INDENTATION_COMMAND = "org.emonic.editors.commands.correctIndentation";
	public static final String CORRECT_INDENTATION_ACTION = "org.emonic.editors.actions.correctIndentationAction";
	public static final String SEARCH_DECLARATION_COMMAND="org.emonic.editors.commands.searchDeclarations";
	public static final String SEARCH_DECLARATION_ACTION="org.emonic.editors.actions.SearchDeclarationsAction";
	public static final String SEARCH_REFERENCES_COMMAND="org.emonic.editors.commands.searchReferences";
	public static final String SEARCH_REFERENCES_ACTION = "org.emonic.editors.actions.SearchReferencesAction";
	public static final String OPEN_DECLARATION_COMMAND="org.emonic.editors.commands.openDeclaration";
	public static final String OPEN_DECLARATION_ACTION="org.emonic.editors.commands.OpenDeclarationAction";	
	public static final String REMOVE_FROM_BUILD_COMMAND= "org.emonic.base.actions.CSharpRemoveFromBuildCommand";
	public static final String REMOVE_FROM_BUILD_ACTION= "org.emonic.base.actions.CSharpRemoveFromBuildAction";
	public static final String ADD_TO_BUILD_COMMAND = "org.emonic.base.actions.CSharpAddToBuildCommand";
	public static final String ADD_TO_BUILD_ACTION = "org.emonic.base.actions.CSharpAddToBuildAction";
	public static final String QUICK_OUTLINE_COMMAND = "org.emonic.csharp.ui.edit.text.csharp.show.outline";
	public static final String QUICK_OUTLINE_ACTION = "org.emonic.editors.actions.quickOutlineAction";
	public static final String CONTENT_ASSIST_ACTION="org.emonic.base.CSharpEditor.ContentAssist";
		
	public final void run(IAction action) {
		Point p = editor.getViewer().getSelectedRange();
		if (action.getActionDefinitionId().equals(TOGGLE_COMMENT_COMMAND)) {
			IDocument doc = editor.getViewer().getDocument();
			CSharpFileManipulator csharpmani = new CSharpFileManipulator(doc);
			int[] section = csharpmani.toggleComment(p.x, p.y,false);
			editor.selectAndReveal(section[0], section[1]);
		}
		if (action.getActionDefinitionId().equals(CORRECT_INDENTATION_COMMAND)) {
			IDocument doc = editor.getViewer().getDocument();
			CSharpFileManipulator csharpmani = new CSharpFileManipulator(doc);
			csharpmani.correctIndentation(p.x, p.y,false);
			editor.selectAndReveal(p.x,p.y);
		}
		
		if  (action.getActionDefinitionId().equals(SEARCH_DECLARATION_COMMAND)) {
			new CSharpSearchDeclarationAction(editor).run();
			//System.out.println("Searching declarations for " + p.x + " " + p.y);
		}
		if  (action.getActionDefinitionId().equals(OPEN_DECLARATION_COMMAND)) {
			new CSharpOpenDeclarationAction(editor).run();
			//System.out.println("Searching declarations for " + p.x + " " + p.y);
		}
		if  (action.getActionDefinitionId().equals(SEARCH_REFERENCES_COMMAND)) {
			new CSharpSearchReferencesAction(editor).run();
			//System.out.println("Searching declarations for " + p.x + " " + p.y);
		}
		if  (action.getActionDefinitionId().equals(REMOVE_FROM_BUILD_COMMAND)) {
			CSharpRemoveFromBuildAction act = new CSharpRemoveFromBuildAction();
			act.setActiveEditor(action,editor);
			act.run(action);
			//System.out.println("Searching declarations for " + p.x + " " + p.y);
		}
		if  (action.getActionDefinitionId().equals(ADD_TO_BUILD_COMMAND)) {
			CSharpAddToBuildAction act = new CSharpAddToBuildAction();
			act.setActiveEditor(action,editor);
			act.run(action);
			//System.out.println("Searching declarations for " + p.x + " " + p.y);
		}
		if  (action.getActionDefinitionId().equals(QUICK_OUTLINE_COMMAND)) {
			TextOperationAction act = new TextOperationAction(
					CSharpEditorMessages.getResourceBundle(),
					"ShowOutline.", editor, CSharpSourceViewer.SHOW_OUTLINE, true);
			
			act.run();
			//System.out.println("Searching declarations for " + p.x + " " + p.y);
		}
		if  (action.getActionDefinitionId().equals( ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS)){
			ContentAssistAction ca = new ContentAssistAction(CSharpEditorMessages.getResourceBundle(), "ContentAssistProposal,",editor);
			ca.run();
		}
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof CSharpEditor) {
			editor = (CSharpEditor) targetEditor;
		}
	}
//	 This will be used later for dispose in the editor
	public static final String[] getEditorActions() {
		return new String[] { 
				DefaultCSharpSourceDelegate.TOGGLE_COMMENT_ACTION,
				DefaultCSharpSourceDelegate.CORRECT_INDENTATION_ACTION,
				DefaultCSharpSourceDelegate.SEARCH_DECLARATION_ACTION,
				DefaultCSharpSourceDelegate.OPEN_DECLARATION_ACTION,
				DefaultCSharpSourceDelegate.SEARCH_REFERENCES_ACTION,
				DefaultCSharpSourceDelegate.QUICK_OUTLINE_ACTION
				};
	}

}
