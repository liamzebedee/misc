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

/*
 * Created on 07.03.2007
 * emonic.base org.emonic.base.views OutlineViewerSorter.java
 */
package org.emonic.base.views;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

//At first the compare-Method is used from the ViewerComparator and not re-implemented
//only the category-Object is used
public class OutlineViewerComparator extends ViewerComparator{

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

    	// sort in same order as in source code, so all have same category
    	return 0;
    }
    
    /**
     * Returns a negative, zero, or positive number depending on whether
     * the first element is less than, equal to, or greater than
     * the second element.
     * <p>
     * The default implementation of this method is based on
     * comparing the elements' categories as computed by the <code>category</code>
     * framework method. 
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
    	// same order as in source code
        return 0;
    }    

}
