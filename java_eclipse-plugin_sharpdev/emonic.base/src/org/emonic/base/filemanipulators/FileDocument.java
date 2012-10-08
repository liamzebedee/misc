/**************************************************************************
 * Copyright (c) 2001, 2007 emonic.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Oct 30, 2005
 * emonic org.emonic.base.FileManipulators FileDocument.java
 **************************************************************************/
package org.emonic.base.filemanipulators;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

/**
 * @author bb
 *
 */
public class FileDocument extends Document {

	private IFile file = null;
    private boolean writeSucceeded;
    private boolean ro = true;
    
	/**
	 * Constructor: Construct from IFile
	 */
	public FileDocument(IFile fl, boolean ReadOnly) {
		super();
		ro=ReadOnly;
		file=fl;
		if (fl.exists() && fl.isAccessible()) {
			try {
				setContent(fl.getContents());
			} catch (CoreException e) {
				e.printStackTrace();
				set("");
			}
		} else {
			set("");
		}
	}
	
	/**
	 * Constructor: Construct from String with file name - always read-only!
	 * @param fileName File name
	 */
	public FileDocument(String fileName) {
		try {
			setContent(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			set("");
		}
	}
	
	public FileDocument(String fileName, boolean readonly) {
		try {
			setContent(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			set("");
		}
		ro=readonly;
	}

	private void setContent(InputStream inputStream) {
		try {
			int byteRead;
			byte[] buffer = new byte [4096];
			StringBuffer content = new StringBuffer();
			synchronized (content) {
				while ((byteRead = inputStream.read(buffer)) != -1) {
					// Copy to the content
					char[] c = new char[byteRead];
					for (int i = 0; i<byteRead; i++) {
						c[i] = (char) buffer[i];
					}
					content.append(c);
				}
				set(content.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
			set("");
		}
	}

	
	public void replace(int pos, int length,String text) throws BadLocationException{
		super.replace(pos,length,text);
		writeContent();
		
	}
	

	
	public void replace(int pos, int length,String text,long modificationstamp) throws BadLocationException{
		super.replace(pos,length,text,modificationstamp);
		writeContent();
		
	}

	private void writeContent() {
		if (!ro && file != null) {
			ByteArrayInputStream s = new ByteArrayInputStream(get().getBytes());
			if (file.exists() && file.isAccessible()) {
				try {
					file.setContents(s, true, true, new NullProgressMonitor());
					setWriteSucceeded(true);
				} catch (CoreException e) {
					// Can't handle it better since we are not allowed to
					// throw exception out of derived classes
					setWriteSucceeded(false);
				}
			}else {
				try {
					file.create(s, true, null);
				} catch (CoreException e) {
					// Can't handle it better since we are not allowed to
					// throw exception out of derived classes
					setWriteSucceeded(false);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocument#set(java.lang.String)
	 */
	public void set(String text) {
		super.set(text);
		writeContent();
	}

	public void set(String text,long modificationstamp) {
		super.set(text,modificationstamp);
		writeContent();
	}

	/**
	 * @return the writeSucceeded
	 */
	public boolean isWriteSucceeded() {
		return writeSucceeded;
	}

	/**
	 * @param writeSucceeded the writeSucceeded to set
	 */
	protected void setWriteSucceeded(boolean writeSucceeded) {
		this.writeSucceeded = writeSucceeded;
	}
	
	

}
