package org.emonic.base.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IParent;



public class SourceElementContentProvider implements ITreeContentProvider {
//	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	
	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {}

	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	
	

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IParent) {
			IDotNetElement[] children = ((IParent) parentElement).getChildren();
			List res = new ArrayList(); 
			for (int i = 0; i < children.length; i++) {
				// Which elements do we want t display? All but comments and strings
				switch (children[i].getElementType()) {
				case IDotNetElement.COMMENT:
				case IDotNetElement.STRING:
					continue;
				}
				res.add(children[i]);
			}
			
			return res.toArray();
		}
		return new Object[0];
	}
	
	

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		if(element instanceof CodeElement) {
			return ((CodeElement)element).getParent();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		if ( getChildren(element)!= null)
			return getChildren(element).length > 0;
		return false;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	

}
