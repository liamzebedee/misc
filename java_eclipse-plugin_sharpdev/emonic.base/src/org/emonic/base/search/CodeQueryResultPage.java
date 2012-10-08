/*
 * Created on 30.06.2007
 * emonic.base org.emonic.base.search CodeQueryResultPage.java
 */
package org.emonic.base.search;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.Page;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.codehierarchy.BinaryMember;
import org.emonic.base.codehierarchy.BinaryMethod;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.views.SourceElementLabelProvider;


public class CodeQueryResultPage extends Page implements ISearchResultPage {

	private static final String NAMELABEL = "Name                        "; //$NON-NLS-1$
	private static final String TYPELABEL = "Type                        "; //$NON-NLS-1$
	private static final String LINELABEL = "Line:Pos"; //$NON-NLS-1$
	private static final String FILELABEL = "File                                                           "; //$NON-NLS-1$
	private static final String HNAME = "Name"; //$NON-NLS-1$
	private static final String HTYPE = "Type"; //$NON-NLS-1$
	private static final String HLINE = "Line"; //$NON-NLS-1$
	private static final String HFILE = "File"; //$NON-NLS-1$
	private static final String MENUPOPUPRESULT = "#PopupResult"; //$NON-NLS-1$
	private static final String CODEQUERYLABEL = "CodeQuery Result"; //$NON-NLS-1$
	private static final String DEFAULTID = "org.emonic.base.search.CodeQueryResultPage"; //$NON-NLS-1$
	private String _id=DEFAULTID;
	private TableViewer _viewer;
	private ISelection _selection;
	private IAction _selectAction;
	private TableColumn columFile;
	private TableColumn columLine;
	private TableColumn columType;
	private TableColumn columName;
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultPage#getID()
	 */
	public String getID() {
		return _id;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultPage#getLabel()
	 */
	public String getLabel() {
		return CODEQUERYLABEL;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultPage#getUIState()
	 */
	public Object getUIState() {
		return _id;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultPage#restoreState(org.eclipse.ui.IMemento)
	 */
	public void restoreState(IMemento memento) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultPage#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultPage#setID(java.lang.String)
	 */
	public void setID(String id) {
		_id=id;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultPage#setInput(org.eclipse.search.ui.ISearchResult, java.lang.Object)
	 */
	public void setInput(ISearchResult search, Object uiState) {
		_viewer.setInput( search);

	}


	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultPage#setViewPart(org.eclipse.search.ui.ISearchResultViewPart)
	 */
	public void setViewPart(ISearchResultViewPart part) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		_viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		_viewer.setContentProvider(new ViewContentProvider());
		_viewer.setLabelProvider(new ViewLabelProvider());
		_viewer.setSorter(new ViewerSorter());
		_viewer.addSelectionChangedListener( new ISelectionChangedListener() {
			public void selectionChanged( SelectionChangedEvent evt)
			{
				_selection=evt.getSelection();
			}
		});
		_selectAction=new SelectAction();
		MenuManager manager=new MenuManager( MENUPOPUPRESULT);
		manager.add( _selectAction);
		Table theTable = _viewer.getTable();
		theTable.setMenu( manager.createContextMenu( _viewer.getTable()));
		theTable.setHeaderVisible(true);

		//_viewer.getTable(). 
		//_viewer.setItemCount(4);
		String[] heads = {HFILE,HLINE,HTYPE,HNAME};
		columFile = new TableColumn(theTable,SWT.LEFT);
		columFile.setText(FILELABEL);
		columFile.setResizable(true);
		columFile.setWidth(600);
		columFile.pack();

		columLine = new TableColumn(theTable,SWT.LEFT);
		columLine.setText(LINELABEL);
		columLine.setResizable(true);
		columLine.setWidth(100);
		columLine.pack();

		columType = new TableColumn(theTable,SWT.LEFT);
		columType.setText(TYPELABEL);
		columType.setResizable(true);
		columType.setWidth(300);
		columType.pack();

		columName = new TableColumn(theTable,SWT.LEFT);
		columName.setText(NAMELABEL);
		columName.setResizable(true);
		columName.setWidth(300);

		columName.pack();
		_viewer.setColumnProperties(heads);
		theTable.layout(true);

		_viewer.addDoubleClickListener( new IDoubleClickListener() {
			public void doubleClick( DoubleClickEvent evt) {
				_selectAction.run();
			}
		});
		_viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	public Control getControl() {
		return _viewer.getControl();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	public void setFocus() {
		_viewer.getControl().setFocus();
	}

	class SelectAction extends Action
	{
		private static final String JUMPTO = "Jump to"; //$NON-NLS-1$
		SelectAction()
		{
			super( JUMPTO);
		}
		public void run() {			
			if ( _selection!=null && ! _selection.isEmpty() && _selection instanceof IStructuredSelection)
			{
				Object selected=((IStructuredSelection)_selection).getFirstElement();
				if ( selected instanceof CodeElement)
				{
					try {
						CodeElement element=(CodeElement)selected;

						// Get the right marker
						IMarker theMarker = null;
						IMarker[] actMarkers = CodeSearchQuery.getMarkerList();
						for (int i = 0 ; i < actMarkers.length; i++){
							if ((element.toString()).equals(actMarkers[i].getAttribute(IMarker.MESSAGE))){
								theMarker=actMarkers[i];
							}
						}
						if (theMarker != null) {
							IDE.openEditor(EMonoPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(),theMarker);
						}


					} catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
		
	
	
		class ViewContentProvider implements IStructuredContentProvider, ISearchResultListener {
			ArrayList _pendingAdds=new ArrayList();

			public synchronized void inputChanged(Viewer v, Object oldInput, Object newInput) {
				if ( oldInput!=null) {
					((CodeQueryResult)oldInput).removeListener( this);
				}
				if ( newInput!=null) {
					((CodeQueryResult)newInput).addListener( this);
				}
				_pendingAdds.clear();
			}
			public void dispose() {
			}
			
			public synchronized void searchResultChanged( SearchResultEvent evt)
			{
				ArrayList l=((CodeQueryResult)evt.getSearchResult()).getResultItems();
				_pendingAdds.add( l.get( l.size()-1));
				if ( _pendingAdds.size()==1)
				{
					getSite().getShell().getDisplay().asyncExec( new Runnable() {
						public void run() {
							synchronized( ViewContentProvider.this) {
								if ( _pendingAdds.size()>0)
								{
									//_viewer.add( _pendingAdds.toArray());
									_pendingAdds.clear();
									_viewer.refresh();
								}
							}
						}
					});
				}
			}
			public Object[] getElements(Object parent) {
				CodeQueryResult result=(CodeQueryResult)parent;
				return result.getResultItems().toArray();
			}
		}

		class ViewLabelProvider extends SourceElementLabelProvider implements ITableLabelProvider {
			private static final String EMPTYSTRING = ""; //$NON-NLS-1$

			public String getColumnText(Object obj, int index) {
				if (obj instanceof CodeElement){
					CodeElement ele = (CodeElement)obj;
					if (index == 0){
						String res = ele.getSource();
						try{
							//IPath path = new Path(res);
						    IWorkspace workspace = ResourcesPlugin.getWorkspace();
						    IFile fl = ele.getSourceAsResource();
						    if (fl.exists()){
						    	res=workspace.getRoot().getLocation().toOSString() + fl.getFullPath();
						    }
						} catch (Exception e) {
							// Nothing type filter textto do, It means no source code avail
						    
						}
						return res;
					}
					if (index==1) {
						return ele.getStartPointText() ;
					}
					if (index==2) {
						return ele.getTypeSignature();
					}
					if (index==3){
						List parents = new ArrayList();
						IDotNetElement pa = ele.getParent();
						while (pa != null && ! (pa.getElementType() == IDotNetElement.ROOT) ){
							parents.add(pa);
							pa = pa.getParent();
						}
						String allParentNames = EMPTYSTRING;
						for (int i = parents.size()-1; i>=0; i-- ){
							allParentNames +=((CodeElement)parents.get(i)).getElementName() + '.';
						}
						return allParentNames + ele.getElementName() +ele.getSignature();
					}
				} else if (obj instanceof BinaryMember){
					BinaryMember bt = (BinaryMember) obj; 
					if (index == 0){
						String res = bt.getPath().toOSString();
						try{
							//IPath path = new Path(res);
						    IWorkspace workspace = ResourcesPlugin.getWorkspace();
						    IFile fl = bt.getPathAsResource();
						    if (fl.exists()){
						    	res=workspace.getRoot().getLocation().toOSString() + fl.getFullPath();
						    } else {
						    	res=bt.getPath().toOSString();
						    }
						} catch (Exception e) {
							// Nothing to do, It means no source code avail
						    
						}
						return res;
					}
					if (index==1) {
						return "";
					}
					if (index==2) {
						int type = bt.getElementType();
						if (type == IDotNetElement.ASSEMBLY) return "Assembly";
						if (type == IDotNetElement.CLASS) return "Class";
						if (type == IDotNetElement.NAMESPACE) return "Namespace";
						if (type == IDotNetElement.METHOD) return "Method";
						if (type == IDotNetElement.CONSTRUCTOR) return "Constructor";
						if (type == IDotNetElement.FIELD) return "Field";
						if (type == IDotNetElement.INTERFACE) return "Interface";
					}
					if (index==3){
						List parents = new ArrayList();
						IDotNetElement pa = bt.getParent();
						while (pa != null && ! (pa.getElementType() == IDotNetElement.ROOT) ){
							parents.add(pa);
							pa = pa.getParent();
						}
						String allParentNames = EMPTYSTRING;
						for (int i = parents.size()-1; i>=0; i-- ){
							allParentNames +=((IDotNetElement)parents.get(i)).getElementName() + '.';
						}
						String signature="";
						if (bt instanceof BinaryMethod){
							String[] parnames = ((BinaryMethod)bt).getParameterNames();
							String[] partypes = ((BinaryMethod)bt).getParameterTypes();
							signature="(";
							for (int i =0; i < partypes.length; i++ ){
								signature += partypes[i];
								if (parnames.length > i){
									signature += " "+ parnames[i];
								}
								if (i < partypes.length -1){
									signature += ", ";
								}
							}
							signature +=")";
						}
						return allParentNames + bt.getElementName() + signature;
					}
					
					
				}
				return getText(obj);
			}

			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex==0) {
					return this.getImage(element);
				}
				return null;
			}


		}

}