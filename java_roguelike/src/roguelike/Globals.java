package roguelike;
import java.util.HashMap;

public class Globals {
	public static final String TYPE_BAD = "bad";
	public static final String TYPE_GOOD = "good";
	
	protected static HashMap<Object, Object> vars = new HashMap<Object, Object>();
	private static Globals instance;
	
	public static void instantiate() {
		if(instance == null){
			instance = new Globals();
		}
	}
	
	public static Object get(Object key){
		return vars.get(key);
	}
	
	public static void put(Object key, Object data){
		vars.put(key, data);
	}
}	
