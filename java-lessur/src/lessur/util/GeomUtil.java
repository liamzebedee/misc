package lessur.util;

import org.lwjgl.util.vector.Vector2f;


public class GeomUtil {
	/** Return this vector multiplied by a scalar; does not alter this vector. */
    public static Vector2f mul(Vector2f v, float a) {
        return new Vector2f(v.x * a, v.y * a);
    }
}
