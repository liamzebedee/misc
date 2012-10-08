/*
 * Created on 28.06.2007
 * emonic.base org.emonic.base.search AbstractSearchQuery.java
 */
package org.emonic.base.search;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.emonic.base.codehierarchy.DotNetElement;
import org.emonic.base.editors.CSharpEditor;
//import org.emonic.base.infostructure.CodeInformator;



public class CodeSearchQuery implements ISearchQuery {
	
	
	private CodeQueryResult _currentResult;
	private java.lang.String _relation;
	private java.lang.String _expressionLabel;
	private String _toSearch;
	private CSharpEditor _callingEditor;
	//private IDocument doc;
	private int _startsearch;
	//private int _posInSearch;
	private static ArrayList _markerList;
	
	public static final  String RELATIONDECLARATION = "Declaration";
	public static final String RELATIONREFERENCES = "References";
	
	//public CodeSearchQuery( String toSearch, int startsearch, int posinsearch, String relation, String expressionLabel, CSharpEditor editor  )
	public CodeSearchQuery( String toSearch, int startsearch,  String relation, String expressionLabel, CSharpEditor editor  )
	{
		_relation=relation;
		_toSearch = toSearch;
		_expressionLabel=expressionLabel;
		_callingEditor = editor;
		//doc = _callingEditor.getViewer().getDocument();
		_startsearch = startsearch;
//		_posInSearch = posinsearch;
        if (_markerList==null){
        	_markerList=new ArrayList();
        }
	}
	
	
	public static void addMarkerToList(IMarker marker){
		_markerList.add(marker);
	}
	
	 
	
	public static IMarker[] getMarkerList(){
		IMarker[] mk = new IMarker[_markerList.size()];
		mk =  (IMarker[]) _markerList.toArray(mk);
		return mk;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus run(IProgressMonitor monitor)
			throws OperationCanceledException {
		
		monitor.beginTask( "Run query", IProgressMonitor.UNKNOWN);
		IStatus run_status= new Status(0, "org.emonic.base", IStatus.OK, "Results",null); 
		try{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			
			
			workspace.deleteMarkers(getMarkerList());
			_markerList.clear();
		} catch (CoreException e){
			e.printStackTrace();
		}
		try
		{
			monitor.subTask( "Get enumeration");
			monitor.worked(1);
			// Do the real search
//			Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
//	    	  boolean useinformator = prefs
//				.getBoolean(DefaultPrefsSetter.USEEMONICINFORMATORCOMPLETION);
			if (RELATIONDECLARATION.equals( _relation )){
				
			   SearchCodeInformator info = new SearchCodeInformator();
			   info.setEditor(_callingEditor);
			   info.setSearchDlls(true);
			   //Point p = _callingEditor.getViewer().getSelectedRange();
//			   CodeElement[] res = info.searchForDeclarations(_toSearch, _startsearch,_posInSearch);
			   DotNetElement[] res = info.searchForDeclarations(_toSearch, _startsearch);
	    	   
	    	   ((CodeQueryResult)getSearchResult()).addResultItems(res);
				monitor.worked( 1);
			}
			if (RELATIONREFERENCES.equals( _relation )){
			   SearchCodeInformator info = new SearchCodeInformator();
			   info.setEditor(_callingEditor);
			   //Point p = _callingEditor.getViewer().getSelectedRange();
//	    	   CodeElement[] res1 = info.searchForDeclarations(_toSearch, _startsearch,_posInSearch);
	    	   DotNetElement[] res1 = info.searchForDeclarations(_toSearch, _startsearch);
	    	   DotNetElement[] res= info.searchForReferences(res1);
	    	   ((CodeQueryResult)getSearchResult()).addResultItems(res);
				monitor.worked( 1);
			}
		}
		catch ( OperationCanceledException oce)
		{
			throw oce;
		}
		// TODO The following catch prevents opening errors if anything went wrong. So, if You suspect that You
		// don't see the results You want, comment it out and take a look to the rised errors.
//		catch ( Exception e)
//		{
//			run_status = new Status(0, "org.emonic.base", IStatus.CANCEL, "Exception was triggered",null);
//		}
		return run_status;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#getLabel()
	 */
	public java.lang.String getLabel() {
		return _expressionLabel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#canRerun()
	 */
	public boolean canRerun() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
	 */
	public boolean canRunInBackground() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
	 */
	public synchronized ISearchResult getSearchResult() {
		if ( _currentResult==null){
		   IFile editorFile=null;
		   if (_callingEditor != null){
			   IEditorInput input = _callingEditor.getEditorInput();
			   if (input instanceof IFileEditorInput) {
				   editorFile = ((IFileEditorInput) input).getFile();
			   }
		   }
			_currentResult=new CodeQueryResult( this, editorFile );
		}
		return _currentResult;
	}

   
	
}
