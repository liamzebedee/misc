/*
 * Created on 18.06.2007
 * emonic.base org.emonic.base.editors CSharpSearchDeclarationActionImplementation.java
 */
package org.emonic.base.actions;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ide.IDE;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.DotNetElement;
import org.emonic.base.editors.CSharpEditor;
import org.emonic.base.search.SearchCodeInformator;

public class CSharpOpenDeclarationAction {
      private CSharpEditor editor;
      public CSharpOpenDeclarationAction(CSharpEditor Editor){
    	  editor=Editor;
      }
      
      public void run(){
    	  // Getting the type to search
    	  Point p = editor.getViewer().getSelectedRange();
    	  SearchCodeInformator info = new SearchCodeInformator();
    	  info.setEditor(editor);
    	  info.setSearchDlls(false);
    	  
    	  DotNetElement res = null;
    	      	  String toSearch=editor.getWordAtCursorpos();
    	  //CodeElement[] result = info.searchForDeclarations(toSearch, p.x,p.x);
    	  DotNetElement[] result = info.searchForDeclarations(toSearch, p.x);
    	  if (result.length>0) res=result[0];
    	  if (res != null){
    		  IWorkspace workspace = ResourcesPlugin.getWorkspace();
    		  //IPath path = new Path(res.getSource());
    		  if (CodeElement.class.isAssignableFrom(res.getClass())){
    			  CodeElement res1 = (CodeElement) res;
    			  IFile fl = res1.getSourceAsResource();
    			  try {
    				  // We jump with a simple trick:
    				  // Set a marker
    				  // Jmp to it
    				  // delete it
    				  if (fl.exists() ){
    					  IMarker theMarker=fl.createMarker(NewSearchUI.SEARCH_MARKER);
    					  theMarker.setAttribute(IMarker.MESSAGE, "declaration of " + res.getElementName());		
    					  theMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
    					  theMarker.setAttribute(IMarker.SEVERITY,IMarker.SEVERITY_INFO);
    					  int absPos = res1.getNameOffset();
    					  theMarker.setAttribute(IMarker.CHAR_START,absPos);
    					  theMarker.setAttribute(IMarker.CHAR_END,absPos+res.getElementName().length());

    					  IDE.openEditor(EMonoPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(),theMarker);
    					  IMarker[] toClear = {theMarker};
    					  workspace.deleteMarkers(toClear);
    				  }
    		  
    			  } catch (CoreException e) {
    				  e.printStackTrace();
    			  }
    		  }
    	  }
      }
}
