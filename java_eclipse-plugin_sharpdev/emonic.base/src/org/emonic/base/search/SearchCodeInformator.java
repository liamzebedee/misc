package org.emonic.base.search;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.DotNetElement;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IParent;
import org.emonic.base.filemanipulators.FileDocument;
import org.emonic.base.infostructure.CSharpCodeParser;
import org.emonic.base.infostructure.CodeInformator;

public class SearchCodeInformator extends CodeInformator{


	public SearchCodeInformator() {
		super();
	}


	

	private static final String EMPTYSTRING = ""; //$NON-NLS-1$
	private static final String CESCAPETDOT = "\\."; //$NON-NLS-1$
	


	public DotNetElement[] searchForDeclarations(String elementToSearch, int posInSearch) {
		
		// We need something to search- We return imidiatly if we haven't
		
		if (elementToSearch==null || elementToSearch.equals(""))
			return new CodeElement[0];
		// We have a string of thr type xxxx.yy.zz
		// We are interested to find the element where the cursor stands
		ArrayList res = new ArrayList();

		String elementFromStart = elementToSearch;
		if (posInSearch >= 0 && posInSearch < elementToSearch.length()) {
			String beforeSearch = elementToSearch.substring(0, posInSearch);
			int st = beforeSearch.lastIndexOf('.') + 1;
			if (st < 0) {
				st = 0;
			}
			int en = elementToSearch.indexOf('.', posInSearch);
			if (en < st) {
				en = elementToSearch.length();
			}
			elementFromStart = elementToSearch.substring(0, en);
			elementToSearch = elementToSearch.substring(st, en);
		}
		String signature = EMPTYSTRING;
		if (elementToSearch.indexOf('(') != -1) {
			signature = elementToSearch.substring(elementToSearch.indexOf('('));
			signature = signature.replaceAll("[^(),]", EMPTYSTRING);
			elementToSearch = elementToSearch.substring(0, elementToSearch
					.indexOf('('));
			elementFromStart = elementFromStart.substring(0, elementFromStart
					.indexOf('('));
		}

		
		List res1 = new ArrayList();
        // Is it a namespace?
		IDotNetElement ns = searchForNamespaceName(elementFromStart);
		if (ns != null){
			res.add(ns);
		}
		// Is it a class name?
		IDotNetElement cls = searchForClassOrInterfaceName(elementFromStart);
		if (cls != null) {
			if (!EMPTYSTRING.equals(signature)) {
				// Searching for a constructor
				recaddDynamicElements(cls, res1);
				res1 = matchingResultCodeElementList(res1, elementFromStart,
						getValidNamespaces());
				res.addAll(res1);
			} else {
				res.add(cls);
			}
		}
		// Build a list with all own possible elements
		res1.clear();
//		recaddOwnElements(callingRoot, res1, documentOffset);
		recaddOwnElements(callingRoot, res1, posInSearch);
		res1 = matchingResultCodeElementList(res1, elementFromStart,
				getValidNamespaces());
		res.addAll(res1);
		// Find decls of elements which are part of a object - something like
		// a.type()
		// Search the class to which this var belongs
		String[] prts = elementFromStart.split(CESCAPETDOT);
		if (prts.length >= 2) {
			String varname = prts[prts.length - 2];
			String functionname = prts[prts.length - 1];
			if (functionname.lastIndexOf('(') != -1) {
				functionname = functionname.substring(0, functionname
						.lastIndexOf('(') - 1);
			}
			//IDotNetElement var1 = searchForVarName(varname, documentOffset);
			IDotNetElement var1 = searchForVarName(varname, posInSearch);
			if (var1 == null) {
				var1 = searchForFieldName(varname, false);
			}
			if (var1 != null) {
				IDotNetElement searchedType = searchForClassOrInterfaceName(((CodeElement) var1)
						.getTypeSignature());
				List posElements = new ArrayList();
				recaddDynamicElements(searchedType, posElements);
				res1 = matchingResultCodeElementList(posElements, functionname,
						getValidNamespaces());
				res.addAll(res1);
			} else {
				// Static completions
				var1 = searchForClassOrInterfaceName(varname);
				if (var1 != null) {
					// Seems to be a classname, search for its static members
					List posElements = new ArrayList();
					String context = EMPTYSTRING;
					if (var1.getParent() != null) {
						IDotNetElement pa = var1.getParent();
						if (pa.getElementType() == IDotNetElement.NAMESPACE) {
							context = pa.getElementName();
						}
					}
					recaddStaticElements(var1, posElements, context, false,
							false);
					res1 = matchingResultCodeElementList(posElements,
							functionname, getValidNamespaces()); // fixme:
																	// move to
																	// posElementsDisplay
					res.addAll(res1);
				}
			}

		}

		// Convert res to a array
		DotNetElement[] results = new DotNetElement[res.size()];
		results = (DotNetElement[]) res.toArray(results);
		return results;
	}
	
	
	public IDotNetElement[] searchForInterfaceMethodDeclarations(
			String interfaceName, String elementToSearch) {
		IDotNetElement iface = searchForClassOrInterfaceName(interfaceName);
		if (iface == null) {
			return new IDotNetElement[0];
		} else {
			ArrayList res = new ArrayList();
			IDotNetElement[] children = ((IParent) iface).getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getElementName().equals(elementToSearch)) {
					res.add(children[i]);
				}
			}
			return (IDotNetElement[]) res
					.toArray(new IDotNetElement[res.size()]);
		}
	}
	
	/**
	 * Search all the references of a list of CodeElements We return it as a
	 * list of CodeElements again of the same type of the incoming codeElements,
	 * but position in the doc where they are found
	 * 
	 * @param res
	 *            The incomming code elements
	 * @return
	 */
	public DotNetElement[] searchForReferences(DotNetElement[] incoming) {
		ArrayList resList = new ArrayList();
		for (int i = 0; i < incoming.length; i++) {
			DotNetElement actIncomming = incoming[i];

			// Get the documents which belong to this target
			// TODO Improvement: Let the user choose where he seeks (all files
			// of the project,......)
			IFile[] fls = getTargetFiles();
			IEditorInput input = this.callingEditor.getEditorInput();
			IFile editorFile = null;
			if (input instanceof IFileEditorInput) {
				editorFile = ((IFileEditorInput) input).getFile();
			}
			if (fls == null) {
				if (input instanceof IFileEditorInput) {
					fls = new IFile[] { editorFile };
				} else {
					fls = new IFile[0];
				}
			}
			// Iterate over the files with the refs
			for (int j = 0; j < fls.length; j++) {
				// Vars are only searched in the own doc,
				// all other types in the rest
				boolean toScan = true;
				if (CodeElement.class.isAssignableFrom(actIncomming.getClass()) ){
					CodeElement act=(CodeElement)actIncomming;
					if (act.getCodeType() == IDotNetElement.VARIABLE
						&& !fls[j].equals(editorFile)) {
						toScan = false;
					}
					if (toScan) {
						FileDocument fd = new FileDocument(fls[j], true);
						CSharpCodeParser parser = new CSharpCodeParser(fd, fls[j]
							.getFullPath().toOSString());
						CodeElement[] referencedElements = parser
							.parseDocumentForReferences(act);
						for (int k = 0; k < referencedElements.length; k++) {
							resList.add(referencedElements[k]);
						}
					}
				}

			}

		}
		if (resList.size() == 0) {
			// Add at least the declarations itself
			for (int i = 0; i < incoming.length; i++) {
				resList.add(incoming[i]);
			}
		}
		DotNetElement[] res = new DotNetElement[resList.size()];
		res = (DotNetElement[]) resList.toArray(res);
		return res;
	}
}
	

	
