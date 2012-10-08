/*
 * Created on Aug 20, 2005
 * emonic org.emonic.base.editors EMonoCSharpFolder.java
 * 
 * Contributors:
 *  Bernhard Brem - Initial implementation
 *  Harald Krapfenbauer (TU Vienna, ICT) - Clean-up, improved update method
 */
package org.emonic.base.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.infostructure.SimpleTaskInterface;

/**
 * Creates the folding annotations for the C# editor.
 */
public class EMonoCSharpFolder implements SimpleTaskInterface {

	private CSharpEditor fTextEditor;
	private IDocument document;

	/**
	 * Constructor
	 * @param editor Editor instance
	 * @param document 
	 */
	public EMonoCSharpFolder(CSharpEditor editor, IDocument document) {
		//super("EmonoFoldingThread");
		this.fTextEditor = editor;
		this.document=document;
	}

	
	/**
	 * Updates annotations for folding regions in the editor incrementally
	 * @param root Instance of the file manipulator
	 */
	public void updateFoldingAnnotations(CodeElement root) {
		IAnnotationModel model = (IAnnotationModel) fTextEditor.getAdapter(
				ProjectionAnnotationModel.class);
		
		if (model == null) {
			return;
		}
		
		// Get lists of code elements
		//CodeElement root = root.getRoot();
		List namespaceList = root.getNameSpaceList();
		List classList = root.getClassList();
		List methodlist = root.getMethodList();
		List propertylist = root.getPropertyList();
		List usingList = root.getUsingContainerList();
		List interfaceList = root.getInterfaceList();
		List commentList = root.getCommentList(2,
				fTextEditor.getDocumentProvider().getDocument(fTextEditor.getEditorInput()));
		List constructorList = root.getConstructorList();
		List destructorList = root.getDestructorList();
		List structList = root.getStructList();
		List enumList = root.getEnumList();
		List completeList = new ArrayList();
		completeList.addAll(namespaceList);
		completeList.addAll(classList);
		completeList.addAll(methodlist);
		completeList.addAll(propertylist);
		completeList.addAll(usingList);
		completeList.addAll(interfaceList);
		completeList.addAll(commentList);
		completeList.addAll(structList);
		completeList.addAll(enumList);
		completeList.addAll(constructorList);
		completeList.addAll(destructorList);
		
		// traverse complete list
		HashMap actualPositions = new HashMap();
		for (int i = 0; i < completeList.size(); i++) {
			Object o = completeList.get(i);
			if (!(o instanceof CodeElement)) {
				continue;
			}
			CodeElement element = (CodeElement) o;
			int start = element.getOffset();
			// Set the length 2 positions after the closing curl
			// so that folded region folds to only 1 line
			int length = element.getLength();
			if (document.getLength() >= start+length && start+length>0 ){
				try {
					int line = document.getLineOfOffset(start + length);
					if (line == document.getNumberOfLines() - 1) {
						Position position = new Position(start, document.getLength() - start);
						actualPositions.put(position,new Integer(position.hashCode()));
					} else {
						IRegion region = document.getLineInformation(line);
						Position position = new Position(start, region.getOffset() + region.getLength() - (start - 1));
						actualPositions.put(position,new Integer(position.hashCode()));	
					}
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// get old positions from model
		HashMap oldPositions = new HashMap();
		HashMap oldAnnotations = new HashMap();
		for (Iterator i = model.getAnnotationIterator(); i.hasNext();) {
			ProjectionAnnotation annotation = (ProjectionAnnotation)i.next();
			oldPositions.put(model.getPosition(annotation),new Integer(model.getPosition(annotation).hashCode()));
			oldAnnotations.put(model.getPosition(annotation), annotation);
		}
		
		// delete annotations not valid any more
		for (Iterator i = oldPositions.keySet().iterator(); i.hasNext();)
		{
			Object next = i.next();
			if (!actualPositions.containsValue(oldPositions.get(next)))
				model.removeAnnotation((ProjectionAnnotation)oldAnnotations.get(next));
		}
		
		// add new annotations
		for (Iterator i = actualPositions.keySet().iterator(); i.hasNext();)
		{
			Object next = i.next();
			if (!oldPositions.containsValue(actualPositions.get(next)))
				model.addAnnotation(new ProjectionAnnotation(), (Position)next);
		}
		
		// fold #region areas
		LinkedList regionAnnotations = new LinkedList();
		for (Iterator it = model.getAnnotationIterator(); it.hasNext();) {
			Annotation annotation = (Annotation) it.next();
			if (annotation instanceof CSharpRegionAnnotation) {
				regionAnnotations.add(annotation);
			}
		}
		
		for (Iterator it = regionAnnotations.iterator(); it
				.hasNext();) {
			model.removeAnnotation((Annotation) it.next());			
		}

		int lines = document.getNumberOfLines();
		Stack positions = new Stack();
		for (int i = 0; i < lines; i++) {
			try {
				IRegion region = document.getLineInformation(i);
				String line = document.get(region.getOffset(), region.getLength()).trim();
				if (line.startsWith("#region")) { //$NON-NLS-1$
					positions.push(region);
				} else if (line.startsWith("#endregion")) { //$NON-NLS-1$
					IRegion startRegion = (IRegion) positions.pop();
					if (startRegion != null) {
						int offset = startRegion.getOffset() + startRegion.getLength();
						Position p = new Position(offset, region.getOffset() + region.getLength() - (offset));
						String delim = document.getLineDelimiter(i);
						if (delim != null) {
							// add the length of the #endregion's line delimiter if applicable
							p.length += delim.length();
						}
						model.addAnnotation(new CSharpRegionAnnotation(), p);	
					} 
				}
			} catch (BadLocationException e) {
				// ignored, we just won't fold this region
			}
		}
	}
	

	/**
	 * @see org.emonic.base.filemanipulators.SimpleTaskInterface#runTask(java.lang.Object)
	 */
	public void runTask(Object info) {
		CodeElement root = (CodeElement) info;
		updateFoldingAnnotations(root);
	}
	
	/**
	 * A tag class for defining region directives for folding.
	 */
	private class CSharpRegionAnnotation extends ProjectionAnnotation {
		
	}
}
