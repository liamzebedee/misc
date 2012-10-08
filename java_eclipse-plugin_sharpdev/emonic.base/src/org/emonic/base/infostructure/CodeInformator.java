/************************************************************************* 
 * Copyright (c) 2005, 2008 emonic.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Created on Nov 4, 2005
 * emonic org.emonic.base.infostructure CodeInformator.java
 *************************************************************************/
package org.emonic.base.infostructure;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.SourceTarget;
import org.emonic.base.buildmechanism.Target;
import org.emonic.base.codecompletion.AssemblyExtractor;
import org.emonic.base.codecompletion.IAssemblyExtractor;
import org.emonic.base.codehierarchy.BinaryType;
import org.emonic.base.codehierarchy.CodeElement;
import org.emonic.base.codehierarchy.DotNetElement;
import org.emonic.base.codehierarchy.Flags;
import org.emonic.base.codehierarchy.IDotNetElement;
import org.emonic.base.codehierarchy.IField;
import org.emonic.base.codehierarchy.IMember;
import org.emonic.base.codehierarchy.IMethod;
import org.emonic.base.codehierarchy.IParent;
import org.emonic.base.codehierarchy.IProperty;
import org.emonic.base.editors.CSharpEditor;
import org.emonic.base.filemanipulators.ProjectPreferencesManipulator;

/**
 * @author bb, ertld This class expects parts of code in a IDocument handled in
 *         a TextEditor and has methods to tell for what they are used and how
 *         they can be completed It informs about the codeelements referenced in
 *         the document. Can handle as well csharp-code as compiled Assemblies
 */
public class CodeInformator {

	private static final String CROOT = "Root"; //$NON-NLS-1$

	private static final String CESCAPETDOT = "\\."; //$NON-NLS-1$

	private static final String EMPTYSTRING = ""; //$NON-NLS-1$


	private static final String NOBASECLASS = "__n_o_base__c_l_a_s_s__";

	protected CSharpEditor callingEditor;

	protected CodeElement callingRoot;

	protected String baseClassString = NOBASECLASS;

	protected HashSet actualBaseClassSet = null;

	protected HashSet completeBaseClassSet = null;

	private ArrayList externalSrcList;

	protected boolean thisIsAllowed = false;

	protected boolean strgSpaceIsAllowed = false;

	protected boolean includeVariablesOfMethod = false;

	protected boolean useDllInformator = false;

	protected boolean useSpecificDllInformator = false;

	private boolean SearchForDllsToo = false;

	private IDotNetElement[] rootElements = null;


	/**
	 * The CodeInformator class is used to inform about the state of the code in
	 * a documents. So it must know these.
	 * 
	 * @param textEditor
	 *            The tecteditor from which it is called
	 */
	public void setEditor(CSharpEditor editor) {
		callingEditor = editor;
		// Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		IDocument document = editor.getViewer().getDocument();
		IEditorInput input = callingEditor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IFile callingFile = ((IFileEditorInput) input).getFile();
			callingRoot = (CodeElement) new CSharpCodeParser(document,
					callingFile.getFullPath().toOSString()).parseDocument();
		} else {
			callingRoot = (CodeElement) new CSharpCodeParser(document, "")
					.parseDocument();
		}
	}

	public void setSearchDlls(boolean searchForDlls) {
		SearchForDllsToo = searchForDlls;
	}

	/**
	 * Does the word to complete match to the name of the code element name? We
	 * have to consider the valid name spaces!
	 * 
	 * @param wordToComplete
	 * @param validNameSpaces
	 * @param actElement
	 * @return the fitting part of the wordToComplete or null if not fitting
	 */
	private String checkFittingName(String wordToComplete,
			String[] validNameSpaces, String actElement, boolean matchTotal) {
		String result = null;
		if (actElement != null) {
			// They fit if they begin totally equal
			if ((actElement.startsWith(wordToComplete) && !matchTotal)
					|| actElement.equals(wordToComplete)) {
				result = actElement;
			} else {
				boolean eliminate = true;
				// Is the class completion a part of a valid one?
				// For example Cons of System.Console?
				// Lets build all allowed combinations!
				ArrayList allowedCombs = new ArrayList();
				for (int j = 0; j < validNameSpaces.length; j++) {
					String[] part = validNameSpaces[j].split(CESCAPETDOT);
					for (int k = 0; k < part.length; k++) {
						String r = EMPTYSTRING;
						for (int l = 0; l <= k; l++) {
							r += part[l] + '.';
						}

						r += wordToComplete;
						allowedCombs.add(r);
					}
				}
				// Which of this combinations fits?
				for (int j = 0; j < allowedCombs.size(); j++) {
					String actTest = (String) allowedCombs.get(j);
					if ((actElement.startsWith(actTest) && !matchTotal)
							|| actElement.equals(wordToComplete)) {
						// That is a possible completion
						eliminate = false;
						// We have to cut the part before the completion
						int cut = actTest.length() - wordToComplete.length();
						result = actElement.substring(cut);
					}
				}
				// As a sign we have to eliminate this possibility set it
				// null!
				if (eliminate) {
					result = null;
				}
			}
		}
		return result;
	}


	private ArrayList addRootsToRelevant(ArrayList resold, String[] paths,
			Class type) {
		for (int i = 0; i < paths.length; i++) {
			File fl = new File(paths[i]);
			try {
				if (fl.exists()) {
					CodeInfoHash cih = CodeHashesFactory.getCodeHash(fl
							.getAbsolutePath());
					long timestamp = fl.lastModified();
					if (cih.hasToBeUpdated(timestamp)) {
						IAbstractParser parser; // new DllInfo(fl);
						// parserDefinition=type;
						// Class[] argClass = new Class[] {fl.getClass()};
						// Object[] args=new Object[]{fl};
						Constructor parserConstructor = type
								.getConstructor(new Class[0]); // ( (argClass);
						parser = (IAbstractParser) parserConstructor
								.newInstance(new Object[0]);
						// parser.init(fl, this);
						parser.init(fl);
						//IEditorInput input = this.callingEditor
						//		.getEditorInput();
						//IFile editorFile = (IFile) input
						//		.getAdapter(IFile.class);
						//IProject proj = editorFile.getProject();
						cih.setHash(parser.getRoot(), timestamp);
					}
					resold.add(cih.returnHash());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return resold;
	}


	public IDotNetElement[] findTypesWithName(String name) {
		IDotNetElement[] roots = getAllRelevantRoots();
		Collection c = new ArrayList();
		for (int i = 0; i < roots.length; i++) {
			findTypesWithName(c, roots[i], name);
		}

		return (IDotNetElement[]) c.toArray(new IDotNetElement[c.size()]);
	}

	private static void findTypesWithName(Collection c, IDotNetElement element,
			String name) {
		switch (element.getElementType()) {
		case IDotNetElement.CLASS:
		case IDotNetElement.ENUM:
		case IDotNetElement.INTERFACE:
		case IDotNetElement.STRUCT:
		case IDotNetElement.TYPE:
			if (element.getElementName().equals(name)) {
				c.add(element);
			}
			break;
		}

		if (element instanceof IParent) {
			IDotNetElement[] elements = ((IParent) element).getChildren();
			for (int i = 0; i < elements.length; i++) {
				findTypesWithName(c, elements[i], name);
			}
		}
	}

	/**
	 * Get a list of all relevant code root to search elements in changed on
	 * 21/04/08 only srcs, NO dlls or assemblies!
	 * 
	 * @return the relevant list
	 */
	private IDotNetElement[] extractAllRelevantRoots() {
		ArrayList res = new ArrayList();

		// get project specific src
		IFile[] fls = getTargetFiles();
		if (fls == null) {
			IEditorInput input = this.callingEditor.getEditorInput();
			IFile file = (IFile) input.getAdapter(IFile.class);
			if (file == null) {
				fls = new IFile[0];
			} else {
				fls = new IFile[] { file };
			}
		}
		String[] flnames = new String[fls.length];
		for (int i = 0; i < fls.length; i++) {
			flnames[i] = fls[i].getRawLocation().toOSString();
		}
		res = addRootsToRelevant(res, flnames, CSharpCodeParser.class);

		// get external src
		IProject proj = null;
		ArrayList srclst = new ArrayList();
		if (callingEditor != null){
		IEditorInput input = this.callingEditor.getEditorInput();
		IFile editorFile = (IFile) input.getAdapter(IFile.class);
		proj=editorFile.getProject();
		srclst = new ProjectPreferencesManipulator(proj)
				.getCodeCompletionSrc();
		}
		ArrayList finalsrclst = new ArrayList();
		Iterator iter = srclst.iterator();
		while (iter.hasNext()) {
			ArrayList srcInDir = getSrcOfDir((String) iter.next());
			finalsrclst.addAll(srcInDir);
		}

		String[] srcDirs = new String[finalsrclst.size()];
		srcDirs = (String[]) finalsrclst.toArray(srcDirs);
		res = addRootsToRelevant(res, srcDirs, CSharpCodeParser.class);

		// FIXME emonicinformator is deprecated
		// get dlls by emonicinformator

		if (SearchForDllsToo) {

			// TODO: Add the results of dll searching
			// res = addRootsToRelevant(res, relevantDlls,
			// EmonicInformatorDllParser.class);
			// We iterate with deep searching over the dlls - deep extraction!

			IAssemblyExtractor assExGac = new AssemblyExtractor();
			assExGac.setProject(proj);
			HashSet assemblyDirs = assExGac.getDirs();
			IAssemblyExtractor assEx = new AssemblyExtractor();
			assEx.setProject(proj);
			assemblyDirs.addAll(assEx.getDirs());
			HashSet assemblies = assEx.addFiles(assemblyDirs);
			Iterator iter1 = assemblies.iterator();
			ArrayList finaldlllst = new ArrayList();
			while (iter1.hasNext()) {
				finaldlllst.add(((File)iter1.next()).getAbsolutePath());
			}
			String[] dllDirs = new String[finaldlllst.size()];
			dllDirs = (String[]) finaldlllst.toArray(dllDirs);
			res = addRootsToRelevant(res, dllDirs, DllParser.class);

		}

		
		return (IDotNetElement[]) res.toArray(new IDotNetElement[res.size()]);
	}

	private IDotNetElement[] getAllRelevantRoots() {
		if (rootElements == null) {
			rootElements = extractAllRelevantRoots();
		}
		return rootElements;
	}


	/**
	 * 
	 * @param path
	 * @return
	 */
	private ArrayList getSrcOfDir(final String path) {

		externalSrcList = new ArrayList();
		File dir = new File(path);
		visitAllFiles(dir);

		return externalSrcList;
	}

	/**
	 * 
	 * @param dir
	 */
	private void visitAllFiles(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				visitAllFiles(new File(dir, children[i]));
			}
		} else {
			// allow only cs-files
			if (dir.getName().endsWith(".cs") || dir.getName().endsWith(".CS"))
				externalSrcList.add(dir.getAbsolutePath());
		}
	}



	/**
	 * Get the target code files associatedd with the actual code file.
	 * 
	 * @return List of IFiles with code relevant to examine
	 */
	protected IFile[] getTargetFiles() {
		// Get the callingRoot element of the namespaces defined in the target
		// of the file
		// To be done!
		IFile editorFile = null;
		IEditorInput input = this.callingEditor.getEditorInput();
		IFile[] res = null;
		if (input instanceof IFileEditorInput) {
			IFileEditorInput ei = (IFileEditorInput) input;
			editorFile = ei.getFile();
			BuildMechanismManipulator bfm = BuildDescriptionFactory
					.getBuildMechanismManipulator(editorFile.getProject()); // new
			if (bfm.isSourceTargetManipulator() && editorFile != null
					&& editorFile.getName() != null) {
				String[] targets = bfm.getAllTargetNamesOfFile(editorFile);
				for (int j = 0; j < targets.length; j++) {
					String target = targets[j];
					if (target != null && !EMPTYSTRING.equals(target)) {

						// System.out.println("Target: " +
						// target.getAttribute("name"));
						// Ensure that the own code file is the first
						Target tgt = bfm.getTarget(target);
						IFile[] res1 = new IFile[0];
						if (tgt != null
								&& SourceTarget.class.isAssignableFrom(tgt
										.getClass()))
							res1 = ((SourceTarget) tgt).getSources();

						int Own = -1;
						for (int i = 0; i < res1.length; i++) {
							if (res1[i].equals(editorFile)) {
								Own = i;
							}
						}
						if (Own != -1) {
							// Found own editor file
							res = new IFile[res1.length];
							res[0] = res1[Own];
							for (int i = 1; (i <= Own); i++) {
								res[i] = res1[i - 1];
							}
							for (int i = Own + 1; i < res.length; i++) {
								res[i] = res1[i];
							}
						}
					}
				}
			}
		}
		if (res == null && editorFile != null) {
			res = new IFile[1];
			res[0] = editorFile;
		}
		if (res == null) {
			res = new IFile[0];
		}
		return res;
	}

	//
	// /**
	// * @return
	// */
	protected String[] getValidNamespaces() {
		List res1List = new ArrayList();
		List resList = new ArrayList();
		res1List = callingRoot.getUsingList();
		res1List.addAll(callingRoot.getNameSpaceList());
		for (int i = 0; i < res1List.size(); i++) {
			CodeElement actEle = (CodeElement) res1List.get(i);
			String name = actEle.getElementName();
			resList.add(name);
		}
		String[] res = new String[resList.size()];
		res = (String[]) resList.toArray(res);
		return res;
	}

	/**
	 * Get a list of code elements with names fitting to wortToComplete out of a
	 * list of codeElements
	 * 
	 * @param codeElementInList
	 *            The orig. list to be scanned for matching elements
	 * @param wordToComplete
	 *            the word to be compared
	 * @param validNameSpaces
	 *            The list of valid namespaces
	 * @return
	 */
	protected List matchingResultCodeElementList(List codeElementInList,
			String wordToComplete, String[] validNameSpaces) {
		if (wordToComplete.equals("")) { //$NON-NLS-1$
			return new ArrayList(codeElementInList);
		}

		List out = new ArrayList();
		for (int i = 0; i < codeElementInList.size(); i++) {
			String actElement = ((DotNetElement) codeElementInList.get(i))
					.getElementName();
			if (checkFittingName(wordToComplete, validNameSpaces, actElement,
					true) != null) {
				out.add(codeElementInList.get(i));
			}
		}

		return out;
	}

	protected void recaddDynamicElements(IDotNetElement element, List res) {
		// String theContext = context;
		int codeType = element.getElementType();
		IDotNetElement parent = element.getParent();
		boolean hasToAdd = ((codeType == IDotNetElement.CLASS) && ((parent
				.getElementType() != IDotNetElement.NAMESPACE)
				&& (parent.getElementType() != IDotNetElement.ROOT) && (parent
				.getElementType() != IDotNetElement.CLASS)));
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.FIELD);
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.PROPERTY);
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.METHOD);
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.VARIABLE);
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.CONSTRUCTOR);
		if (!hasToAdd)
			hasToAdd = ((codeType == IDotNetElement.ENUM) && ((parent
					.getElementType() != IDotNetElement.NAMESPACE) && ((parent
					.getElementType() != IDotNetElement.ROOT))));
		if (!hasToAdd)
			hasToAdd = ((codeType == IDotNetElement.INTERFACE) && ((parent
					.getElementType() != IDotNetElement.NAMESPACE) && ((parent
					.getElementType() != IDotNetElement.ROOT))));

		if (hasToAdd) {
			// Add in the correct syntax to the result
			res.add(element);
		}

		if (element instanceof IParent) {
			IDotNetElement[] children = ((IParent) element).getChildren();
			for (int i = 0; i < children.length; i++) {
				if (!children[i].getElementName().equals(EMPTYSTRING)) {
					recaddDynamicElements(children[i], res);
				}
			}
		}
	}

	/**
	 * recursive add the elements of the actual document
	 * 
	 * @param callingRoot
	 * @param res1
	 * @param documentOffset
	 */
	protected void recaddOwnElements(CodeElement element, List res1,
			int documentOffset) {
		int codeType = element.getElementType();
		CodeElement parent = (CodeElement) element.getParent();
		boolean hasToAdd = ((codeType == IDotNetElement.CLASS) && ((parent
				.getElementType() != IDotNetElement.NAMESPACE)
				&& (parent.getElementType() != IDotNetElement.ROOT) && (parent
				.getElementType() != IDotNetElement.CLASS)));
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.FIELD);
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.PROPERTY);
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.METHOD);
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.VARIABLE);
		if (!hasToAdd)
			hasToAdd = (codeType == IDotNetElement.CONSTRUCTOR);
		if (!hasToAdd)
			hasToAdd = (((codeType == IDotNetElement.ENUM) && ((parent
					.getElementType() != IDotNetElement.NAMESPACE) && ((parent
					.getElementType() != IDotNetElement.ROOT)))));
		if (!hasToAdd)
			hasToAdd = ((codeType == IDotNetElement.INTERFACE) && ((parent
					.getElementType() != IDotNetElement.NAMESPACE) && ((parent
					.getElementType() != IDotNetElement.ROOT))));

		// An element is only valid in the context of his parent (at least in
		// that function:
		// It is for the own c#-file and each c#-file should be a class.....
		if (parent != null && CROOT.equals(parent.getElementName())) {
			hasToAdd = (hasToAdd && parent.getOffset() <= documentOffset && parent
					.getOffset()
					+ parent.getLength() >= documentOffset);
		}
		if (hasToAdd) {
			res1.add(element);
		}

		IDotNetElement[] children = element.getChildren();
		for (int i = 0; i < children.length; i++) {
			recaddOwnElements((CodeElement) children[i], res1, documentOffset);
		}

	}



	/**
	 * Add the static elements of a class. Either as string representations or
	 * as code elements.
	 * 
	 * @param element
	 * @param res
	 *            with CodeCompletionElements
	 * @param _context
	 * @param asString
	 */
	protected void recaddStaticElements(IDotNetElement element, List res,
			String context, boolean asCompletionElement, boolean display) {
		String _context = context;
		int codeType = element.getElementType();
		boolean hasToAdd = false;
		if (codeType == IDotNetElement.NAMESPACE) {
			_context = element.getElementName();
		}

		switch (codeType) {
		case IDotNetElement.CLASS:
		case IDotNetElement.ENUM:
		case IDotNetElement.INTERFACE:
		case IDotNetElement.TYPE:
			if (!EMPTYSTRING.equals(_context)) {
				_context += '.';
			}
			_context += element.getElementName();
			break;
		}

		switch (codeType) {
		case IDotNetElement.FIELD:
		case IDotNetElement.METHOD:
		case IDotNetElement.PROPERTY:
			hasToAdd = true;
			break;
		}

		// An element is only valid in the context of his parent (at least in
		// that function: It is for the own c#-file and each c#-file should be a
		// class.....
		if (element instanceof CodeElement) {
			hasToAdd = hasToAdd && ((CodeElement) element).staticMod;
		} else if (element instanceof IMethod || element instanceof IProperty) {
			hasToAdd = Flags.isMethodStatic(((IMember) element).getFlags());
		} else if (element instanceof IField) {
			hasToAdd = Flags.isFieldStatic(((IField) element).getFlags());
		} else if (element instanceof BinaryType) {
			//TODO
			// There seems to be a bug in the assembly exractor which
			// prevents the recognition of static elements
			//hasToAdd = Flags.isFieldStatic(((BinaryType) element).getFlags());
			hasToAdd=true;
		}

		if (hasToAdd) {
			if (!asCompletionElement) {
				res.add(element);
			} else {

				String toAdd = element.getElementName();
				switch (codeType) {
				case IDotNetElement.METHOD:
					if (element instanceof IMethod) {
						toAdd += ((IMethod) element).getSignature();
						break;
					}
				case IDotNetElement.PROPERTY:
					if (element instanceof IProperty) {
						toAdd += ((IProperty) element).getSignature();
						break;
					}
				case IDotNetElement.CLASS:
				case IDotNetElement.INTERFACE:
				case IDotNetElement.ENUM:
				case IDotNetElement.FIELD:
				case IDotNetElement.TYPE:
					if (element instanceof CodeElement) {
						toAdd += ((CodeElement) element).getSignature();
					}
					break;
				}

				// Add in the correct syntax to the result
				if (!EMPTYSTRING.equals(_context)) {
					toAdd = _context + '.' + toAdd;
				}
				if (!display) {
					res.add(new CodeCompletionElement(element, toAdd));
				} else if (element instanceof CodeElement) {
					String type = ((CodeElement) element).getTypeSignature();
					if (type == null) {
						res.add(new CodeCompletionElement(element, toAdd));
					} else {
						res.add(new CodeCompletionElement(element, toAdd
								+ " : " + type));
					}
				} else if (element instanceof IMethod) {
					res.add(new CodeCompletionElement(element, toAdd + " : "
							+ ((IMethod) element).getReturnType()));
				} else if (element instanceof IField) {
					res.add(new CodeCompletionElement(element, toAdd + " : "
							+ ((IField) element).getTypeSignature()));
				} else {
					res.add(new CodeCompletionElement(element, toAdd));
				}
			}
		}

		if (element instanceof IParent) {
			IDotNetElement[] children = ((IParent) element).getChildren();
			for (int i = 0; i < children.length; i++) {
				recaddStaticElements(children[i], res, _context,
						asCompletionElement, display);
			}
		}
	}

	private IDotNetElement recSearchForTypeByName(IDotNetElement element,
			String name, int type) {
		switch (type) {
		case IDotNetElement.CLASS:
		case IDotNetElement.ENUM:
		case IDotNetElement.INTERFACE:
		case IDotNetElement.TYPE:
			int elementType = element.getElementType();
			switch (elementType) {
			case IDotNetElement.CLASS:
			case IDotNetElement.ENUM:
			case IDotNetElement.INTERFACE:
			case IDotNetElement.TYPE:
				// TODO This ia a work around: It seems that the class names are
				// sometimes with the namespace in the element name, sometimes
				// without. Accept both. To do: Consolidate naming shema
				String shortname = ""; //$NON-NLS-1$
				int index = name.lastIndexOf('.');
				if (index != -1 && index < name.length() - 1) {
					shortname = name.substring(index + 1, name.length());
				}
				if (element.getElementName().equals(name)
						|| element.getElementName().equals(shortname)) {
					return element;
				}
			}
		default:
			if (element.getElementType() == type) {
				// TODO This ia a work around: It seems that the class names are
				// sometimes with the namespace in the element name, sometimes
				// without. Accept both. To do: Consolidate naming shema
				String shortname = ""; //$NON-NLS-1$
				int index = name.lastIndexOf('.');
				if (index != -1 && index < name.length() - 1) {
					shortname = name.substring(index + 1, name.length());
				}
				if (element.getElementName().equals(name)
						|| element.getElementName().equals(shortname)) {
					return element;
				}
			}

			if (element instanceof IParent) {
				IDotNetElement[] children = ((IParent) element).getChildren();
				for (int i = 0; i < children.length; i++) {
					IDotNetElement res = recSearchForTypeByName(children[i],
							name, type);
					if (res != null) {
						return res;
					}
				}
			}
			return null;
		}
	}

	private CodeElement recSearchForTypeByName(CodeElement element,
			String varname, int type, int offset) {
		if (element.getElementType() == type) {
			if (element.getElementName().equals(varname)
					&& element.getOffset() <= offset
					&& element.getOffset() + element.getLength() >= offset) {
				return element;
			}
		}

		IDotNetElement[] children = element.getChildren();
		for (int i = 0; i < children.length; i++) {
			CodeElement chld = (CodeElement) children[i];
			CodeElement res = recSearchForTypeByName(chld, varname, type,
					offset);
			if (res != null) {
				return res;
			}
		}
		return null;
	}

	/**
	 * This code searches for a class or an interface with the given name in
	 * each relevant code root
	 */
	protected IDotNetElement searchForClassOrInterfaceName(String classname) {
		IDotNetElement[] allRoots = getAllRelevantRoots();
		IDotNetElement result = null;
		for (int i = 0; (i < allRoots.length && result == null); i++) {
			result = recSearchForTypeByName(allRoots[i], classname,
					IDotNetElement.CLASS);
		}
		if (result == null) {
			for (int i = 0; (i < allRoots.length && result == null); i++) {
				result = recSearchForTypeByName(allRoots[i], classname,
						IDotNetElement.INTERFACE);
			}
		}
		return result;
	}

	/**
	 * This code searches for a namespace with the given name in
	 * each relevant code root
	 */
	protected IDotNetElement searchForNamespaceName(String classname) {
		IDotNetElement[] allRoots = getAllRelevantRoots();
		IDotNetElement result = null;
		for (int i = 0; (i < allRoots.length && result == null); i++) {
			result = recSearchForTypeByName(allRoots[i], classname,
					IDotNetElement.NAMESPACE);
		}
		return result;
	}



	/**
	 * This code searchs for a field with the given name in each relevant code
	 * root
	 * 
	 * @param searchallRoots
	 */
	protected IDotNetElement searchForFieldName(String fieldname,
			boolean searchallRoots) {
		IDotNetElement result = null;
		if (searchallRoots) {
			IDotNetElement[] allRoots = getAllRelevantRoots();
			for (int i = 0; (i < allRoots.length && result == null); i++) {
				result = recSearchForTypeByName(allRoots[i], fieldname,
						IDotNetElement.FIELD);
			}
		} else {
			result = recSearchForTypeByName(callingRoot, fieldname,
					IDotNetElement.FIELD);
		}
		return result;
	}

	/**
	 * This code searchs for a var with the given name in each relevant code
	 * root
	 */
	protected CodeElement searchForVarName(String varname, int offset) {
		return recSearchForTypeByName(this.callingRoot, varname,
				IDotNetElement.VARIABLE, offset);
	}

	/**
	 * This function is used to eliminate double entries in proposal lists
	 * 
	 * @param res
	 * @return The arraylist res without null-elements, sorted and without
	 *         double values
	 */
	protected ArrayList SortAndEliminateDouble(List res) {
		ArrayList newRes = new ArrayList();
		for (int i = 0; i < res.size(); i++) {
			if (res.get(i) != null) {
				String oldS = (String) res.get(i);
				boolean isInserted = false;
				for (int j = 0; (j < newRes.size() && !isInserted); j++) {
					int comp = ((String) newRes.get(j)).compareTo(oldS);
					if (comp == 0) {
						isInserted = true; // Already in newres
					}
					if (comp > 0) {
						newRes.add(j, oldS);
						isInserted = true;
					}
				}
				if (!isInserted) {
					newRes.add(oldS);
				}

			}
		}
		return newRes;
	}

	// uses CodeCompletionElements instead of strings
	protected ArrayList SortAndEliminateDoubleEnhanced(List res) {
		ArrayList newRes = new ArrayList();
		for (int i = 0; i < res.size(); i++) {
			if (((CodeCompletionElement) res.get(i)).getDisplayString() != null) {
				String oldS = ((CodeCompletionElement) res.get(i))
						.getDisplayString();
				boolean isInserted = false;
				for (int j = 0; (j < newRes.size() && !isInserted); j++) {
					// int comp = ((String) newRes.get(j)).compareTo(oldS);
					int comp = ((CodeCompletionElement) newRes.get(j))
							.getDisplayString().compareTo(oldS);
					if (comp == 0) {
						isInserted = true; // Already in newres
					}
					if (comp > 0) {
						// newRes.add(j, oldS);
						newRes.add(j, res.get(i));
						isInserted = true;
					}
				}
				if (!isInserted) {
					// newRes.add(oldS);
					newRes.add(res.get(i));
				}

			}
		}
		return newRes;
	}



}
