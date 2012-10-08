/**
 *  Copyright 2008 
 *  
 *  Base Version: 
 *  	Sam Berlin,  sberlin AT gmail DOT com
 *  	Roger Kapsi, roger AT kapsi DOT de 
 *  
 *  http://code.google.com/p/google-collections/issues/detail?id=5
 *  
 *  Enhancements to fit the data structure to emonic:
 *  	Dominik Ertl
 *  
 *    
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package org.emonic.base.codecompletion.datastructure;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Provides an unmodifiable empty iterator. <code>EmptyIterator</code> always
 * returns that there aren't any more items and throws a 
 * {@link NoSuchElementException} when attempting to move to the next item.
 * 
 <pre>
    try{
        EmptyIterator ei = new EmptyIterator();     
        ei.next();      
    } catch (Exception e) {
        System.out.println("Expected to get NoSuchElementException exception: " + e.toString());
    }

    Output:
        Expected to get NoSuchElementException exception: java.util.NoSuchElementException
 </pre>
 */
public class EmptyIterator extends UnmodifiableIterator {
    /** A constant EmptyIterator. */
    public final static Iterator EMPTY_ITERATOR = new EmptyIterator();

    //@SuppressWarnings("unchecked")
    public static  Iterator emptyIterator() {
        return EMPTY_ITERATOR;
    }

    // inherits javadoc comment
    public boolean hasNext() {
        return false;
    }

    // inherits javadoc comment
    public Object next() {
        throw new NoSuchElementException();
    }
}