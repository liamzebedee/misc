package lessur.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class DStorePropertiesFile extends DStoreFile {
	static Properties prop = new Properties();

	public DStorePropertiesFile(String name) {
		f = new File(name);
			if(!f.exists()){
			 	try {
		        	f.createNewFile();
		        	FileOutputStream out = new FileOutputStream(f);
		        	out.flush(); 
		            out.close();
		        } catch (IOException ex) { 
		        	ex.printStackTrace();
		        }
		} 
		this.load();
	}
	
	public void save()
	{
        try {
        	FileOutputStream out = new FileOutputStream(f); //creates a new output steam needed to write to the file
			//You need this line! It stores what you just put into the file and adds a comment.
        	prop.store(out, "Config file for a Lessur server");
	        out.flush(); 
	        out.close(); //Closes the output stream as it is not needed anymore.
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public Properties getPropertyHandle(){
		return this.prop;
	}
	
	public void put(String key,String value){
		prop.put(key, value);
	}
	
	public String get(String key){
		return (String) prop.get(key);
	}

	@Override
	public void load() {
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
			prop.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
