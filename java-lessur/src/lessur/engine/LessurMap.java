package lessur.engine;

import java.io.InputStream;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMapPlus;

public class LessurMap extends TiledMapPlus {
	
	public LessurMap(InputStream in) throws SlickException {
		super(in);
	}
	
	public LessurMap(String ref) throws SlickException {
		super(ref);
	}
	
}
