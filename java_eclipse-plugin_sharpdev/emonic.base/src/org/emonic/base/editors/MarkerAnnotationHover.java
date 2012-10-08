package org.emonic.base.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextEditor;

/** 
 * The JavaAnnotationHover provides the hover support for java editors.
 */

class MarkerAnnotationHover implements IAnnotationHover {

	private static final int MAX_INFO_LENGTH = 80;
	private TextEditor fTextEditor;
	
	MarkerAnnotationHover(TextEditor editor) {
		super();
		fTextEditor = editor;
	}

	/**
	 * @see org.eclipse.jface.text.source.IAnnotationHover#getHoverInfo(org.eclipse.jface.text.source.ISourceViewer, int)
	 */
	
	public String getHoverInfo(ISourceViewer viewer, int line) {
		IResource resource = (IResource) fTextEditor.getEditorInput().getAdapter(IResource.class);
		
		List markers = getMarkersForLine(resource, line+1);
		if (markers != null) {
			StringBuffer buffer = new StringBuffer();
			for (int i =  0; i < markers.size(); i++) {
				IMarker marker = (IMarker) markers.get(i);
				String message =
					marker.getAttribute(IMarker.MESSAGE, (String) null);
				if (message != null && message.trim().length() > 0) {
					if (message.length() > MAX_INFO_LENGTH) {
						message = splitMessage(message);
					}
					
					buffer.append(message);
					if(i != markers.size() - 1) {
						buffer.append(System.getProperty("line.separator")); //$NON-NLS-1$
					}
				}
			}
			return buffer.toString();
		}
		return null;
	}

	/**
	 * @param resource
	 * @param i
	 * @return
	 */
	private List getMarkersForLine(IResource resource, int ln) {
		List res = new ArrayList();
		try {
			IMarker[] ml = resource.findMarkers(IMarker.PROBLEM,true,IResource.DEPTH_INFINITE);
			for (int i = 0; i<ml.length;i++){
				if (Integer.parseInt(ml[i].getAttribute(IMarker.LINE_NUMBER).toString()) == ln){
					res.add(ml[i]);
				}
			}
		
		
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return res;
	}

	private String splitMessage(String message) {
		String result = "";
		
		if(message.length() <= MAX_INFO_LENGTH) {
			return message;
		}
		
		String tmpStr = new String(message);
		
		while(tmpStr.length() > MAX_INFO_LENGTH) {
			
			int spacepos = tmpStr.indexOf(' ', MAX_INFO_LENGTH);
			
			if(spacepos != -1) {
				result += tmpStr.substring(0, spacepos) + "\n";
				tmpStr = tmpStr.substring(spacepos);
			}
			else {
				result += tmpStr.substring(0, MAX_INFO_LENGTH) + "\n";
				tmpStr = tmpStr.substring(MAX_INFO_LENGTH);
			}
			
			
		    	
		}
		
		result += tmpStr;
		
		return result;
	}


}
