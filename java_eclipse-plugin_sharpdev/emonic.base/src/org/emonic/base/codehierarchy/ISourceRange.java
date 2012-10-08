/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.codehierarchy;

import org.eclipse.swt.graphics.Point;

/**
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ISourceRange {
	
	public int getOffset();
	
	public int getLength();
	
	public Point getStartPoint();
	
	public Point getEndPoint();
	
	public int getNameOffset();
	
	public int getValidOffset();
	
	public int getValidLength();
}
