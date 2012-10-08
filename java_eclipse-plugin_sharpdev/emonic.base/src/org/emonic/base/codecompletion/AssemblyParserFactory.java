/**
 * 
 */
package org.emonic.base.codecompletion;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.emonic.base.EMonoPlugin;
import org.emonic.base.preferences.DefaultPrefsSetter;


/**
 * @author bb
 *
 */
public class AssemblyParserFactory {

	public static String[][] getLabelsAndValues() {
		IAssemblyParser[] asex=getAssemblyParsers();
		String[][] result = new String[asex.length][2];
		for (int i =0; i < asex.length;i++){
            result[i][0]=asex[i].getPrefLabel();
            result[i][1]=asex[i].getPrefLabel();
		}
		return result;
	}

	private static IAssemblyParser[] getAssemblyParsers() {
		ArrayList aresults = new ArrayList();
		try{
			IExtensionRegistry er = Platform.getExtensionRegistry(); 
			IExtensionPoint ep = er.getExtensionPoint("org.emonic.base","assemblyparser");
			IExtension[] extensions=ep.getExtensions();
		
			for (int i =0; i<extensions.length;i++){
				IConfigurationElement[] ConfigElements = extensions[i].getConfigurationElements();
				for (int j=0; j<ConfigElements.length;j++){
					IConfigurationElement actual = ConfigElements[j];
					Object o = actual.createExecutableExtension("class");
					if (o instanceof IAssemblyParser ){
					  aresults.add(o);
					}
				}

			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		IAssemblyParser[] results = new IAssemblyParser[aresults.size()+1];
	    results[0]=new NullAssemblyParser();
	    for (int i =0; i < aresults.size();i++){
	    	results[i+1]=(IAssemblyParser)aresults.get(i);
	    }
	    
	    return results;
		
	}
	
	public static IAssemblyParser createAssemblyParser(){ 
		org.eclipse.core.runtime.Preferences prefs = EMonoPlugin.getDefault().getPluginPreferences();
		String wishedMethod = prefs.getString(DefaultPrefsSetter.BINARYSEARCHMETHOD);
		IAssemblyParser[] allPossible = getAssemblyParsers();
		for (int i = 0; i < allPossible.length; i++){
			if (allPossible[i].getPrefLabel().equals(wishedMethod)){
				IAssemblyParser result=allPossible[i];
				return result;
			}
		}
		return new NullAssemblyParser();
	}

	public static IAssemblyParser getPrefferedExtractor() {
		int hit = -1;
		int importance = -1;
		IAssemblyParser[] allPossible = getAssemblyParsers();
		for (int i = 0; i < allPossible.length; i++){
			if (allPossible[i].getImportance() > importance){
				importance=allPossible[i].getImportance();
				hit = i;
			}
		}
		if (hit != -1){
		   return allPossible[hit];	
		}
		return new NullAssemblyParser();
	}
	

}
