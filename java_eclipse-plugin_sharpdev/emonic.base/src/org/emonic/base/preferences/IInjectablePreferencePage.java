/**
 * 
 */
package org.emonic.base.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author bb
 *
 */
public interface IInjectablePreferencePage {
	/**
	 * Inject one single field editor. It should be enough to call here the (protected) method addField(Editor)
	 * @param editor
	 * @param parent 
	 */
   public void injectOneFieldEditor(FieldEditor editor);
}
