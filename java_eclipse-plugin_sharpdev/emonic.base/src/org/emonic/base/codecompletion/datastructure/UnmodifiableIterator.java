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

/** 
 * A convenience class to aid in developing iterators that cannot be modified.
 */
public abstract class UnmodifiableIterator implements Iterator {
    /** Throws UnsupportedOperationException */
    public final void remove() {
		throw new UnsupportedOperationException();
    }
}