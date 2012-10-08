/*
 * Created on 30.06.2007
 * emonic.base org.emonic.base.search CodeQueryResult.java
 */
package org.emonic.base.search;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.NewSearchUI;
import org.emonic.base.codehierarchy.BinaryMember;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.DotNetElement;

public class CodeQueryResult implements ISearchResult {
	
	private static final String EMPTYSTRING = ""; //$NON-NLS-1$
	private ISearchQuery _query;
	private ArrayList _searchResultListeners;
	private ArrayList _resultItems;
	private IFile _callingFile;
	
	CodeQueryResult( ISearchQuery query, IFile callingFile)
	{
		_query=query;
		_searchResultListeners=new ArrayList();
		_resultItems=new ArrayList();
		_callingFile=callingFile;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#addListener(org.eclipse.search.ui.ISearchResultListener)
	 */
	public void addListener(ISearchResultListener l) {
		synchronized ( _searchResultListeners)
		{
			_searchResultListeners.add( l);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#removeListener(org.eclipse.search.ui.ISearchResultListener)
	 */
	public void removeListener(ISearchResultListener l) {
		synchronized ( _searchResultListeners)
		{
			_searchResultListeners.remove( l);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getLabel()
	 */
	public String getLabel() {
		return _query.getLabel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getTooltip()
	 */
	public String getTooltip() {
		return getLabel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
	return null; 
		//return Bbq_eclipsePlugin.getImageDescriptor( "icons/weber-small.gif");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getQuery()
	 */
	public ISearchQuery getQuery() {
		return _query;
	}

	void addResultItems(DotNetElement[]  results)
	{
		for (int i = 0; i < results.length; i++){
			DotNetElement res = results[i];	

			
			// Add a marker if we have the code available
			if ( CodeElement.class.isAssignableFrom(res.getClass())){
				CodeElement res1 = (CodeElement) res;
				_resultItems.add(res1);
				if (res1.getSource()!=null && !EMPTYSTRING.equals( res1.getSource() )){
					try{
						IFile fl = res1.getSourceAsResource();
						//IMarker theMarker=fl.createMarker(SearchUI.SEARCH_MARKER);
						
						// To mark a file, it must be in the  workspace. So, if it is defined in the GAC, we will 
						// not be able to mount it, the file will not exist relative to the work space
						
						if (fl.exists()){
							IMarker theMarker=fl.createMarker(NewSearchUI.SEARCH_MARKER);
							//theMarker.delete();

							theMarker.setAttribute(IMarker.MESSAGE, res1.toString());		
							theMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
							theMarker.setAttribute(IMarker.SEVERITY,IMarker.SEVERITY_INFO);
							int absPos = res1.getNameOffset();
							theMarker.setAttribute(IMarker.CHAR_START,absPos);
							theMarker.setAttribute(IMarker.CHAR_END,absPos+res1.getElementName().length());
							CodeSearchQuery.addMarkerToList(theMarker);
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			} else if ( res instanceof BinaryMember){
				BinaryMember res1 = (BinaryMember) res;
				_resultItems.add(res1);
				if (res1.getPath()!=null && !EMPTYSTRING.equals( res1.getPath() )){
					try{
						IFile fl = res1.getPathAsResource();
						//IMarker theMarker=fl.createMarker(SearchUI.SEARCH_MARKER);
						if (fl.exists()){
							IMarker theMarker=fl.createMarker(NewSearchUI.SEARCH_MARKER);
							//theMarker.delete();

							theMarker.setAttribute(IMarker.MESSAGE, res1.toString());		
							theMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
							theMarker.setAttribute(IMarker.SEVERITY,IMarker.SEVERITY_INFO);
							// We do not have this info!
							int absPos = 0;
							theMarker.setAttribute(IMarker.CHAR_START,absPos);
							theMarker.setAttribute(IMarker.CHAR_END,absPos+res1.getElementName().length());
							CodeSearchQuery.addMarkerToList(theMarker);
						} else  if(_callingFile != null && _callingFile.exists())  {
							// Add it to the calling editor
							IMarker theMarker= _callingFile.createMarker(NewSearchUI.SEARCH_MARKER);

							theMarker.setAttribute(IMarker.MESSAGE, "Matches outside Workspace: "+ res1.getPath());		
							theMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
							theMarker.setAttribute(IMarker.SEVERITY,IMarker.SEVERITY_INFO);
							CodeSearchQuery.addMarkerToList(theMarker);
						}
						

					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				
			}		
			
		}
		synchronized ( _searchResultListeners)
		{
			CodeQueryResultEvent evt=new CodeQueryResultEvent( CodeQueryResult.this);
			for ( Iterator iter=_searchResultListeners.iterator(); iter.hasNext();)
			{
				ISearchResultListener listener=(ISearchResultListener)iter.next();
				listener.searchResultChanged( evt);
			}
		}
	}
	
	ArrayList getResultItems()
	{
		return _resultItems;
	}
	
	
}