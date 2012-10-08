/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which is available at http://www.opensource.org/licenses/cpl1.0.txt
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.emonic.base.editors;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

class CSharpReconcilingStrategy implements IReconcilingStrategy {

	private Job job;

	CSharpReconcilingStrategy(Job job) {
		this.job = job;
	}

	public void reconcile(IRegion partition) {
		job.cancel();
		job.schedule();
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		job.cancel();
		job.schedule();
	}

	public void setDocument(IDocument document) {
		// nothing to do
	}

}
