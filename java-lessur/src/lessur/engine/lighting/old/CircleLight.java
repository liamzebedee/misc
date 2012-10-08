package lessur.engine.lighting.old;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

/**
 * A single light in the example. It's capable of determining how much effect
 * it will have in any given point on the tile map. Note that all coordinates
 * are given in tile coordinates rather than pixel coordinates.
 * 
 * @author kevin
 */
public class CircleLight extends Circle {
	private Color col;
	public boolean colouredLights = true;
	 
    public CircleLight(Color col, float centerPointX, float centerPointY, float radius) {
        super(centerPointX, centerPointY, radius * 32);
        this.col = col;
    }
    
    public float[] getEffectAtCorner(Vector2f corner, boolean colouredLights) {
        float dx = (corner.x - getCenterX());
        float dy = (corner.y - getCenterY());
        float distance2 = (dx * dx) + (dy * dy);
        float r = radius;
        float effect = 1f - (distance2 / (r * r));
 
        if (effect < 0) {
            effect = 0;
        }
 
        if (colouredLights) {
            return new float[]{col.r * effect * col.a, col.g * effect * col.a, col.b * effect * col.a};
        } else {
            return new float[]{effect * col.a, effect * col.a, effect * col.a};
        }
    }
}