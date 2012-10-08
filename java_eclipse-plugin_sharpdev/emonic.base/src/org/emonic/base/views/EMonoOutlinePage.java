/**
 * Created on Oct 3, 2005
 * emonic org.emonic.base.views EMonoOutlinePage.java
 * 
 * @author bb
 * Contributors:
 * 		Bernhard Brem
 * 		Harald Krapfenbauer - Remember outline settings
 */
package org.emonic.base.views;


import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.actions.OutlineFieldsFilterAction;
import org.emonic.base.actions.OutlineHideSignatureTypesAction;
import org.emonic.base.actions.OutlineLocalTypesFilterAction;
import org.emonic.base.actions.OutlineNonPublicMembersFilterAction;
import org.emonic.base.actions.OutlineSortingAction;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.CodeElementComparer;
import org.emonic.base.editors.CSharpEditor;
import org.emonic.base.infostructure.SimpleTaskInterface;

public class EMonoOutlinePage extends ContentOutlinePage implements SimpleTaskInterface {
	
	protected ISourceViewer input;
	protected int lastHashCode = 0;
    protected CodeElement root = null;
	private CSharpEditor fTextEditor;
	private boolean blockJump;
	// actions
	private OutlineFieldsFilterAction _outlineFieldsFilterAction;
	private OutlineSortingAction _outlineSortingAction;
	private OutlineLocalTypesFilterAction _outlineLocalTypesFilterAction;
	private OutlineNonPublicMembersFilterAction _outlineNonPublicMembersFilterAction;
	private OutlineHideSignatureTypesAction _outlineHideSignatureTypesAction;
	// default settings (at start-up)
	public static boolean fieldsFilterEnabled = false;
	public static boolean sortingEnabled = true;
	public static boolean localTypesFilterEnabled = false;
	public static boolean nonPublicMembersFilterEnabled = false;
	public static boolean hideSignatureTypesEnabled = true;
	
	private TreeViewer _viewer;
	private SourceElementLabelProvider LabelProvider;
	private static ImageDescriptor _sortingDescriptor; 
	private static ImageDescriptor _fieldsFilterDescriptor;
	private static ImageDescriptor _localTypesFilterDescriptor; 
	private static ImageDescriptor _nonPublicMembersFilterDescriptor;
	private static ImageDescriptor _typesinsignatureFilterDescriptor;
	
    
	/**
	 * Constructor
	 * @param textEditor TextEditor instance
	 * @param input ISourceViewer instance
	 */
    public EMonoOutlinePage(CSharpEditor textEditor, ISourceViewer input)	{
    	super();
		this.input = input;		
		this.fTextEditor = textEditor;
		// register this Job to be run in the UI thread
		fTextEditor.getExamineJob().registerForCodeElement(this);
		blockJump = false;	
		LabelProvider = new SourceElementLabelProvider();
		LabelProvider.setShowSigVars(true);
		_sortingDescriptor = EMonoPlugin.imageDescriptorFromPlugin( "icons/alphab_sort_co.gif");
		_fieldsFilterDescriptor = EMonoPlugin.imageDescriptorFromPlugin("icons/fields_co.gif");
		_localTypesFilterDescriptor = EMonoPlugin.imageDescriptorFromPlugin("icons/localtypes_co.gif");
		_nonPublicMembersFilterDescriptor = EMonoPlugin.imageDescriptorFromPlugin("icons/publicmethod.gif");
		_typesinsignatureFilterDescriptor=EMonoPlugin.imageDescriptorFromPlugin("icons/hidesigtypes.gif");
		fTextEditor.forceRunExamineJob();
    }

    
    /**
     * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#dispose()
     */
    public void dispose() {
		fTextEditor.getExamineJob().free(this);
		super.dispose();
	}
	
    
    /** 
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#selectionChanged(SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {

		super.selectionChanged(event);
		ISelection selection= event.getSelection();
		if (selection.isEmpty())
			fTextEditor.resetHighlightRange();
		else {
			// Check whether the selection really changed
			if (!blockJump) {
				CodeElement segment = (CodeElement)((IStructuredSelection)selection).getFirstElement();
				int start = segment.getNameOffset();
				int length = segment.getLength();
				int nameLength = segment.getNameLength();
				try {
					if (nameLength == 0) {
						fTextEditor.setHighlightRange(start, length, true);	
					} else {
						fTextEditor.selectAndReveal(start, nameLength);
					}
				} catch (IllegalArgumentException x) {
					fTextEditor.resetHighlightRange();
				}
			}
		}
	}
    
    
	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(Composite)
	 */
    public void createControl(Composite parent) {
    	// subclasses must extend this method of the superclass
		super.createControl(parent);
		_viewer = getTreeViewer();
		_viewer.setContentProvider(new SourceElementContentProvider());
		_viewer.setLabelProvider(LabelProvider);
		IElementComparer comparer = new CodeElementComparer();
		_viewer.setComparer(comparer);
		
		createActions();
		createToolbarButtons(_viewer);
	}
    
    /**
     * Update the tree, starting from the given code element
     * @param r The code element where to start from
     */
	public void update(CodeElement r) {
	    // Avoid refresh of the editor
		blockJump = true;
        getTreeViewer().refresh(r, true);
        blockJump = false;
	}

	/**
	 * Method is scheduled periodically to update the Tree Viewer and hence
	 * the Outline view.
	 * @param info A CodeElement instance
	 */
	public void runTask(Object info) {
		if (root == null) {
			root = (CodeElement) info;
			// If we open very fresh, we might not have a root
			if (root != null) {
				TreeViewer viewer = getTreeViewer();
				if (viewer != null) {
					viewer.setInput(root);
					viewer.setComparator(new OutlineViewerComparator());
					IElementComparer comparer = new CodeElementComparer();
					viewer.setComparer(comparer);
					viewer.expandAll();
				}
			}
		} else {
			//mani.adaptRoot(callingRoot);
			root = (CodeElement) info;
			update(root);
		}
	}
	
	private void createActions() {				
		_outlineSortingAction = new OutlineSortingAction(this, "outline sort");
		_outlineSortingAction.setImageDescriptor(_sortingDescriptor); 
		_outlineSortingAction.setToolTipText("Sort by alphabetical order");
		_outlineSortingAction.setChecked(sortingEnabled);
		_outlineSortingAction.run();
		
		_outlineFieldsFilterAction = new OutlineFieldsFilterAction(this, "outline fields filter");
		_outlineFieldsFilterAction.setImageDescriptor(_fieldsFilterDescriptor);
		_outlineFieldsFilterAction.setToolTipText("Hide fields");
		_outlineFieldsFilterAction.setChecked(fieldsFilterEnabled);
		_outlineFieldsFilterAction.run();
		
		_outlineLocalTypesFilterAction = new OutlineLocalTypesFilterAction(this, "outline local types filter");
		_outlineLocalTypesFilterAction.setImageDescriptor(_localTypesFilterDescriptor);
		_outlineLocalTypesFilterAction.setToolTipText("Hide local types");
		_outlineLocalTypesFilterAction.setChecked(localTypesFilterEnabled);
		_outlineLocalTypesFilterAction.run();
		
		_outlineNonPublicMembersFilterAction = new OutlineNonPublicMembersFilterAction(this, "outline non public members filter");
		_outlineNonPublicMembersFilterAction.setImageDescriptor(_nonPublicMembersFilterDescriptor);
		_outlineNonPublicMembersFilterAction.setToolTipText("Hide non-public members");
		_outlineNonPublicMembersFilterAction.setChecked(nonPublicMembersFilterEnabled);
		_outlineNonPublicMembersFilterAction.run();
		
		_outlineHideSignatureTypesAction = new org.emonic.base.actions.OutlineHideSignatureTypesAction(this, "outline types in signature filter");
		_outlineHideSignatureTypesAction.setImageDescriptor(_typesinsignatureFilterDescriptor);
		_outlineHideSignatureTypesAction.setToolTipText("Hide parameter names in signature");
		_outlineHideSignatureTypesAction.setChecked(hideSignatureTypesEnabled);
		_outlineHideSignatureTypesAction.run();
	}
	
	private void createToolbarButtons(TreeViewer viewer) {
		IToolBarManager toolBarManager = this.getSite().getActionBars().getToolBarManager();
		toolBarManager.add(_outlineSortingAction);
		toolBarManager.add(_outlineFieldsFilterAction);
		toolBarManager.add(_outlineNonPublicMembersFilterAction);
		toolBarManager.add(_outlineLocalTypesFilterAction);	
		toolBarManager.add(_outlineHideSignatureTypesAction);	
	}	
	
	public void addTreeViewerFilter(ViewerFilter filter){
		getTreeViewer().addFilter(filter);
		update(root);
	}
	
	public void removeTreeViewerFilter(ViewerFilter filter){
		getTreeViewer().removeFilter(filter);
		update(root);
	}
	
	public void setTreeViewerComparator(ViewerComparator comparator){
		getTreeViewer().setComparator(comparator);
		update(root);		
	}

	public void setSigMode(boolean filter) {
		LabelProvider.setShowSigVars(filter);
		update(root);	
	}
}
