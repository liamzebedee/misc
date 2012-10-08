package lessur.util;

import java.io.File;


public abstract class DStoreFile {
	File f = null;
	
	public File getFileHandle(){
		return this.f;
	}
	
	public abstract void save();
	public abstract void load();
}
