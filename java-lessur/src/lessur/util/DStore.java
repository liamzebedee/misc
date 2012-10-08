package lessur.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DStore {
	HashMap<String, DStorePropertiesFile> propertyFiles = new HashMap<String, DStorePropertiesFile>();
	static String mainDirectory;
	public enum DataFile { property }
	
	public DStore(String path){
		mainDirectory = path;
	}
	
	public void doFile(String name, DataFile filetype){
		switch(filetype){
		case property:
			DStorePropertiesFile file = new DStorePropertiesFile(mainDirectory+File.separator+name);
			propertyFiles.put(name, file);
			break;
		}
	}
	
	public DStorePropertiesFile getFile(String name){
		return propertyFiles.get(name);
	}
	
	public void save(){
		for (Map.Entry<String,DStorePropertiesFile> entry : propertyFiles.entrySet()) {
		    DStorePropertiesFile file = entry.getValue();
		    file.save();
		}
	}
	
	
	
}
