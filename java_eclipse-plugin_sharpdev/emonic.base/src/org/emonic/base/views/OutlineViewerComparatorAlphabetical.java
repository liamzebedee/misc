/**
 * Virtual Machines for Embedded Multimedia - VIMEM
 *
 * Copyright (c) 2007 University of Technology Vienna, ICT
 * (http://www.ict.tuwien.ac.at)
 * All rights reserved.
 *
 * This file is made available under the terms of the 
 * Eclipse Public License v1.0 which is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Dominik Ertl - Implementation
 */

package org.emonic.base.views;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.IDotNetElement;

public class OutlineViewerComparatorAlphabetical extends ViewerComparator {

	private static int ROOT_TYPE_COMPARATOR = 0;
	private static int USING_CONTAINER_TYPE_COMPARATOR = 1;
	private static int USING_TYPE_COMPARATOR= 2;
	private static int NAMESPACE_TYPE_COMPARATOR = 3;
	private static int INTERFACE_TYPE_COMPARATOR = 4;
	private static int CLASS_TYPE_COMPARATOR = 5;
	private static int STRUCT_TYPE_COMPARATOR = 6;
	private static int ENUM_TYPE_COMPARATOR = 7;
	private static int FIELD_TYPE_COMPARATOR = 8;
	private static int CONSTRUCTOR_TYPE_COMPARATOR = 9;
	private static int METHOD_TYPE_COMPARATOR = 10;
	private static int PROPERTY_TYPE_COMPARATOR = 11;
	private static int VARIABLE_TYPE_COMPARATOR = 12;
		
	
    /**
     * Returns the category of the given element. The category is a
     * number used to allocate elements to bins; the bins are arranged
     * in ascending numeric order. The elements within a bin are arranged
     * via a second level sort criterion.
     *
     * @param element the element
     * @return the category
     */
    public int category(Object element) {
    	
    	int codeType= ((CodeElement) element).getCodeType();
    	  
    	switch(codeType){
    		case IDotNetElement.ROOT:
    			return ROOT_TYPE_COMPARATOR;
    		
    		case IDotNetElement.USING_CONTAINER:
    			return USING_CONTAINER_TYPE_COMPARATOR;
    		
    		case IDotNetElement.USING:
    			return USING_TYPE_COMPARATOR;
    		
    		case IDotNetElement.NAMESPACE:
    			return NAMESPACE_TYPE_COMPARATOR;
    		
    		case IDotNetElement.INTERFACE:
    			return INTERFACE_TYPE_COMPARATOR;
    		
    		case IDotNetElement.CLASS:
    			return CLASS_TYPE_COMPARATOR;

    		case IDotNetElement.STRUCT:
    			return STRUCT_TYPE_COMPARATOR;

    		case IDotNetElement.ENUM:
    			return ENUM_TYPE_COMPARATOR;
    		
    		case IDotNetElement.FIELD:
    			return FIELD_TYPE_COMPARATOR;
    		
    		case IDotNetElement.CONSTRUCTOR:
    		case IDotNetElement.DESTRUCTOR:
    			return CONSTRUCTOR_TYPE_COMPARATOR;
    		
    		case IDotNetElement.METHOD:
    			return METHOD_TYPE_COMPARATOR;
    		
    		case IDotNetElement.PROPERTY:
    			return PROPERTY_TYPE_COMPARATOR;
    		
    		case IDotNetElement.VARIABLE:
    			return VARIABLE_TYPE_COMPARATOR;
    		
    		default:
    			return 0;
    		
    	}
    }
    
    /**
     * Returns a negative, zero, or positive number depending on whether
     * the first element is less than, equal to, or greater than
     * the second element.
     * <p>
     * The default implementation of this method is based on
     * comparing the elements' categories as computed by the <code>category</code>
     * framework method. Elements within the same category are further 
     * subjected to a case insensitive compare of their label strings, either
     * as computed by the content viewer's label provider, or their 
     * <code>toString</code> values in other cases. 
     * </p>
     * 
     * @param viewer the viewer
     * @param e1 the first element
     * @param e2 the second element
     * @return a negative number if the first element is less  than the 
     *  second element; the value <code>0</code> if the first element is
     *  equal to the second element; and a positive number if the first
     *  element is greater than the second element
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        int cat1 = category(e1);
        int cat2 = category(e2);

        if (cat1 != cat2) {
			return cat1 - cat2;
		}
    	
        String name1;
        String name2;

        if (viewer == null || !(viewer instanceof ContentViewer)) {
            name1 = e1.toString();
            name2 = e2.toString();
        } else {
            IBaseLabelProvider prov = ((ContentViewer) viewer)
                    .getLabelProvider();
            if (prov instanceof ILabelProvider) {
                ILabelProvider lprov = (ILabelProvider) prov;
                name1 = lprov.getText(e1);
                name2 = lprov.getText(e2);
            } else {
                name1 = e1.toString();
                name2 = e2.toString();
            }
        }
        if (name1 == null) {
			name1 = "";//$NON-NLS-1$
		}
        if (name2 == null) {
			name2 = "";//$NON-NLS-1$
		}

        // use the comparator to compare the strings
        return getComparator().compare(name1.toLowerCase(), name2.toLowerCase());
    }

}
