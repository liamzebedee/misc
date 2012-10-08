package sharpdev.util;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;

public class UIHelper {
	/**
	 * Return the active editor.
	 * 
	 * @param event
	 *            The execution event that contains the application context
	 * @return the active editor, or <code>null</code>.
	 */
	public static IEditorPart getActiveEditor(ExecutionEvent event) {
		Object o = getVariable(event, ISources.ACTIVE_EDITOR_NAME);
		if (o instanceof IEditorPart) {
			return (IEditorPart) o;
		}
		return null;
	}
	
	/**
	 * Extract the variable.
	 * 
	 * @param event
	 *            The execution event that contains the application context
	 * @param name
	 *            The variable name to extract.
	 * @return The object from the application context, or <code>null</code>
	 *         if it could not be found.
	 */
	public static Object getVariable(ExecutionEvent event, String name) {
		if (event.getApplicationContext() instanceof IEvaluationContext) {
			return ((IEvaluationContext) event.getApplicationContext())
					.getVariable(name);
		}
		return null;
	}
}
