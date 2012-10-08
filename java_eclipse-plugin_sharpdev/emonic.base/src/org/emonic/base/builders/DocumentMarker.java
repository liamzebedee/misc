/**
 * Contributors:
 *   Bernhard Brem - initial implementation
 *   Harald Krapfenbauer, TU Vienna - moved from EMonoBuilder into class DocumentMarker,
 *                                    created overloaded methods used by on-the-fly parsing
 */
package org.emonic.base.builders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.emonic.base.buildmechanism.BuildDescriptionFactory;
import org.emonic.base.buildmechanism.BuildMechanismManipulator;
import org.emonic.base.buildmechanism.IBuildMechanismDescriptor;
import org.emonic.base.filemanipulators.CSharpFileManipulator;
import org.emonic.base.filemanipulators.FileDocument;

/**
 * This static class parses MCS/GMCS compiler output and sets problem markers accordingly.
 */
public class DocumentMarker {

	private static final String ENDOFERRORMARKER = "[{};\n\\s\t()]"; //$NON-NLS-1$
	
	/**
	 * Refreshes all error/warning markers in the project by analysing the build output.
	 * @param err Output string from the compiler
	 * @param project The current project
	 * @return true if at least one warning or error was found
	 */
	public static boolean markDocument(String err, IProject project) {
		
		try {
			// delete all previous markers
			project.deleteMarkers("org.emonic.base.compilerErrorMarker",false,IResource.DEPTH_INFINITE);
			project.deleteMarkers("org.emonic.base.parserErrorMarker",false,IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		String[] errField = err.split("\n");
		boolean errorWarningFound = false;
		for (int i = 0; i < errField.length; i++) {
			// By this we can add other analysers later
			if (analyzeMonoString(errField[i], project))
				errorWarningFound = true;
		}
		return errorWarningFound;
	}

	
	/**
	 * Refreshes all error/warning markers in the file by analyzing compiler parsing-only output.
	 * @param err Output string from the compiler
	 * @param file File that was analyzed by the compiler. All markers will be set for it.
	 */
	public static void markDocument(String err, IFile file) {
		
		try {
//			 delete previous markers of that file that were set through on-the-fly parsing
			if (file.exists()){
				file.deleteMarkers("org.emonic.base.parserErrorMarker",false,IResource.DEPTH_INFINITE);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		String[] errField = err.split("\n");
		for (int i = 0; i < errField.length; i++) {
			// By this we can add other analysers later
			analyzeMonoString(errField[i], file.getProject(), file.getLocation().toOSString());
		}
	}

	
	/**
	 * Analyses a line of mcs/gmcs compiler output
	 */
	private static void analyzeMonoString(String mes, IProject project, String fileName) {

		// error with file and location
		Pattern errorf = Pattern.compile("(\\S.*)\\((\\d+)(,\\d+)?\\):*\\s*error\\s(.*)");
		Matcher mef = errorf.matcher(mes);
		if (mef.find()) {
			int pos = 0;
			if (mef.groupCount() == 4) {
				// We have a position within the line
				String s = mef.group(3);
				// comma is the first char!
				pos = Integer.parseInt(s.substring(1));
			}
			setOneMarker(fileName, (mef.groupCount() == 4) ? mef.group(4) : mef.group(3),
					Integer.parseInt(mef.group(2)) , pos, true, project, true); 
			return;
		}

		// error without file and location
		// these are errors like "[csc] error CS2008: No files to compile were specified"
		// should we set a marker therefore? (so that it appears in the problems view?)
		Pattern errornf=Pattern.compile("error\\s*(CS.*)");
		Matcher menf = errornf.matcher(mes);
		if (menf.find()) {
			setOneMarker("", menf.group(1), 0, 0, true, project, true); 
			return;
		}

		// warnings with file and location
		Pattern warningf = Pattern.compile("(\\S.*)\\((\\d+)(,\\d+)?\\):*\\s*warning\\s(.*)");
		Matcher mwf = warningf.matcher(mes);
		if (mwf.find()) {
			int pos = 0;
			if (mwf.groupCount() == 4) {
				// We have a position within the line
				String s = mwf.group(3);
				pos = Integer.parseInt(s.substring(1));
			}
			setOneMarker(fileName, (mwf.groupCount() == 4) ? mwf.group(4) : mwf.group(3),
					Integer.parseInt(mwf.group(2)) , pos, false, project, true); 
			return;
		}

		// warnings without file or location		 
		Pattern warningnf=Pattern.compile("warning\\s*(CS.*)");
		Matcher mwnf = warningnf.matcher(mes);
		if (mwnf.find()) {
			setOneMarker("", mwnf.group(1), 0, 0, false, project, true);
			return;
		}
	}
	

	/**
	 * Analyses a line of compiler output
	 * @return true if at least one warning or error was found
	 */
	private static boolean analyzeMonoString(String mes, IProject project) {

		// error with file and location
		Pattern errorf = Pattern.compile(".*\\[csc\\]\\s+(\\S.*)\\((\\d+)(,\\d+)?\\):*\\s*error\\s(.*)");
		Matcher mef = errorf.matcher(mes);
		if (mef.find()) {
			int pos = 0;
			if (mef.groupCount() == 4) {
				// We have a position within the line
				String s = mef.group(3);
				// comma is the first char!
				pos = Integer.parseInt(s.substring(1));
			}
			setOneMarker(mef.group(1), (mef.groupCount() == 4) ? mef.group(4) : mef.group(3), 
					Integer.parseInt(mef.group(2)) , pos, true, project, false); 
			return true;
		}

		// error without file and location
		// these are errors like "[csc] error CS2008: No files to compile were specified"
		// should we set a marker therefore? (so that it appears in the problems view?)
		Pattern errornf=Pattern.compile(".*\\[csc\\]\\s+error\\s*(CS.*)");
		Matcher menf = errornf.matcher(mes);
		if (menf.find()) {
			setOneMarker("", menf.group(1), 0, 0, true, project, false); 
			return true;
		}

		// warnings with file and location
		Pattern warningf = Pattern.compile(".*\\[csc\\]\\s+(\\S.*)\\((\\d+)(,\\d+)?\\):*\\s*warning\\s(.*)");
		Matcher mwf = warningf.matcher(mes);
		if (mwf.find()) {
			int pos = 0;
			if (mwf.groupCount() == 4) {
				// We have a position within the line
				String s = mwf.group(3);
				pos = Integer.parseInt(s.substring(1));
			}
			setOneMarker(mwf.group(1), (mwf.groupCount() == 4) ? mwf.group(4) : mwf.group(3),
					Integer.parseInt(mwf.group(2)) , pos, false, project, false); 
			return true;
		}

		// warnings without file or location		 
		Pattern warningnf=Pattern.compile(".*\\[csc\\]\\s+warning\\s*(CS.*)");
		Matcher mwnf = warningnf.matcher(mes);
		if (mwnf.find()) {
			setOneMarker("", mwnf.group(1), 0, 0, false, project, false);
			return true;
		}
		
		return false;
	}

	
	/**
	 * Sets a new marker on a resource
	 * @param file A string containing the full file name and path
	 * @param err A string containing the error message
	 * @param lineNr Line number in the editor
	 * @param pos Column number in the editor
	 * @param isError true for error, wrong for warning
	 * @param project The current project
	 * @param parsing Whether the marker is set through on-the-fly parsing (sets a special marker
	 * attribute and uses the temporary file given to the parser to calculate marker positions)
	 */
	private static void setOneMarker(String file, String err, int lineNr, int pos, boolean isError,
			IProject project, boolean parsing) {
		
		try {
			// Create the markers
			// Calculate file pos relative to the project
			IResource problemRes = project;
			IFile problemFile = null;
			if (!file.equals("")) {
				String orgPath = project.getLocation().toOSString();
				String sfile = file;
				// Grr: Win does not care about capitals!
				// So since I don't know which system i have I have to allow generally both
				if (file.toLowerCase().startsWith(orgPath.toLowerCase())) 
					sfile = file.substring(orgPath.length());
				problemFile = project.getFile(sfile);
			}
			IMarker theMarker;
			if (problemFile != null && problemFile.exists()) {
				if (parsing) {
					theMarker = problemFile.createMarker("org.emonic.base.parserErrorMarker");
				} else {
					theMarker = problemFile.createMarker("org.emonic.base.compilerErrorMarker");
				}
			} else {
				theMarker = problemRes.createMarker("org.emonic.base.compilerErrorMarker");
			}
				
			theMarker.setAttribute(IMarker.MESSAGE, err);
	        theMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
	        if (isError) {
	        	theMarker.setAttribute(IMarker.SEVERITY,IMarker.SEVERITY_ERROR);
	        } else {
	        	theMarker.setAttribute(IMarker.SEVERITY,IMarker.SEVERITY_WARNING);
	        }
	        if (!file.equals("")) {
	        	theMarker.setAttribute(IMarker.LINE_NUMBER,lineNr);
	        	if (pos != 0) {
	        		IDocument document = new FileDocument(problemFile, true);
	        		int absPos = getMarkerStartPosition(document, lineNr, pos, project);
	        		int endpos = getErrorEndpos(document, absPos,err);
	        		theMarker.setAttribute(IMarker.CHAR_START,absPos);
	        		theMarker.setAttribute(IMarker.CHAR_END,endpos);
	        	}
	        }
		} catch (CoreException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Returns the document offset of the given line number and position in this line.
	 * This method is used to translate line number and position as returned from MCS/GMCS
	 * into the correct offset in our document, also taking care of tabs (MCS/GMCS take one
	 * tab as 8 white spaces).
	 * If we compile against csc we don't want to make that correction
	 * @param lineNr The line number as given from MCS/GMCS (1-based)
	 * @param linePos The position in the line as given from MCS/GMCS (1-based)
	 * @param project 
	 * @return The offset in the document
	 */
	private static int getMarkerStartPosition(IDocument document, int lineNr, int linePos, IProject project) {
		IRegion lineRegion = new Region(0,0);
		
		try {
			// line number for getLineInformation is 0-based
			int docLineNr = lineNr - 1;
			// It can happen that a document in the memory has a other length than the version compiled.
			// So we have to ensure that the message at least is in the document
			if (docLineNr >= 0 && docLineNr < document.getNumberOfLines()) {
				lineRegion = document.getLineInformation(docLineNr);
				String lineString = document.get(lineRegion.getOffset(),
						lineRegion.getLength());
				int countTabs = 0;
				// Do we have to replace the tabs by spaces?
				// We do it when:
				// Called out of the parser job
				// Or: Called out of build and compiled with mono-compiler
				String targetFramework = IBuildMechanismDescriptor.FRAMEWORK_NA;
// It seems that parsing via csc is not possible
// So comment out this!
// Even if: We should first look to the build mechanism and afterwards to the global prefs!
//				if (parsing){
//					String parsingCommand = EMonoPlugin.getDefault().getPreferenceStore().getString(
//							DefaultPrefsSetter.PARSINGCOMMAND);
//					// Parsing via csc?
//					if (parsingCommand.indexOf("csc") != -1){
//						targetFramework=IBuildfileManipulator.FRAMEWORK_MS;
//					}
//					// mcs or gmcs?
//					if (parsingCommand.indexOf("mcs") != -1){
//						targetFramework=IBuildfileManipulator.FRAMEWORK_MONO;
//					}
//				} else {
				
				try {
					BuildMechanismManipulator bfm = BuildDescriptionFactory.getBuildMechanismManipulator(project);
					if (bfm.isFrameworkManipulator()){
						targetFramework = bfm.getTargetFrameworkType();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
//				}
				// We assume mono behaviour if we don't know the framework: It is a mono integration plugin!
				if (targetFramework.equals(IBuildMechanismDescriptor.FRAMEWORK_NA) || targetFramework.equals(IBuildMechanismDescriptor.FRAMEWORK_MONO)){
					int index = 0;
					while (index < linePos && index < lineString.length()
							&& (Character.isWhitespace(lineString.charAt(index)))) {
						if (lineString.charAt(index) == '\t')
							countTabs++;
						index++;
					}
				}
				return lineRegion.getOffset() + (linePos - 1)
						- (CSharpFileManipulator.spacesForTabsMcs - 1)
						* countTabs;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
			
		} catch (IndexOutOfBoundsException e) {
			
			e.printStackTrace();
	
		}
		return lineRegion.getOffset();
	}

	public static int getErrorEndpos(IDocument document, int startpos, String message) {

		// At the moment we don't respect whats in the message, but deliver every time 
		// the next occurance of a ".;} "
		int endpos =startpos;
		//boolean whitespace = false;
		try  {
			FindReplaceDocumentAdapter finder = new FindReplaceDocumentAdapter(
					document);
			IRegion end = finder.find(startpos, ENDOFERRORMARKER, true, true,
					false, true);
			endpos = end.getOffset() + end.getLength()-1;
			
		} catch (Exception e) {
			// Out of bounds
		}
		// We want to have at least one character marked. So, if we didnt find the end or it was 
		// 0 chars away we return absPos+1
		if (document != null && startpos+1 < document.getLength() && endpos <= startpos) {
			endpos =startpos+1;
		}
		if (document == null) {
			endpos =startpos; // Nothing known about the document?
		}
		return endpos;
	}

}
