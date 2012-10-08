/*
 * Created on 18.06.2007
 * emonic.base org.emonic.base.editors CSharpSearchDeclarationActionImplementation.java
 */
package org.emonic.base.actions;


import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.graphics.Point;
import org.emonic.base.editors.CSharpEditor;
import org.emonic.base.search.CodeSearchQuery;

public class CSharpSearchReferencesAction {
      private CSharpEditor editor;
      public CSharpSearchReferencesAction(CSharpEditor Editor){
    	  editor=Editor;
      }
      
      public void run(){
    	  // Getting the type to search
    	  Point p = editor.getViewer().getSelectedRange();
    	  String toSearch=editor.getWordAtCursorpos();
    	  NewSearchUI.activateSearchResultView();
    	  NewSearchUI.runQueryInBackground(new CodeSearchQuery(toSearch,p.x,CodeSearchQuery.RELATIONREFERENCES,"References",editor));

      }
}
