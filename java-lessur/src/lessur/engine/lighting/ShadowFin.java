package lessur.engine.lighting;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Ciano
 */
public class ShadowFin {

    private PenumbraShader shader;
    private Vector2f rootPos, inner, outer;
    private float penumbraIntensity, umbraIntensity, depth;
    private Integer index;

    public ShadowFin(Vector2f rootPosition) {
        this.rootPos = rootPosition;
        this.outer = null;
        this.penumbraIntensity = 1.0f;
        this.inner = null;
        this.umbraIntensity = 0.0f;
        this.depth = 0.0f;
    }

    public float angle() {
    	Vector2f uv = new Vector2f(inner.x, inner.y);
    	Vector2f pv = new Vector2f(outer.x, outer.y);
        uv.normalise();
        pv.normalise();
        return (float) Math.acos(Vector2f.dot(uv, pv));
    }

    public void render() {
        shader = PenumbraShader.getPenumbraShader();
        shader.enable();
        shader.setState(rootPos, angle(), inner, umbraIntensity, penumbraIntensity);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex3f(rootPos.x, rootPos.y, depth);
        GL11.glVertex3f(rootPos.x + outer.x, rootPos.y + outer.y, depth);
        GL11.glVertex3f(rootPos.x + inner.x, rootPos.y + inner.y, depth);
        GL11.glEnd();
        shader.disable();
    }

    public void setInner(Vector2f inner) { this.inner = inner; }
    public void setOuter(Vector2f outer) { this.outer = outer; }
    public void setPenumbraIntensity(float penumbraIntensity) { this.penumbraIntensity = penumbraIntensity; }
    public void setUmbraIntensity(float umbraIntensity) { this.umbraIntensity = umbraIntensity; }
    public void setIndex(int index) { this.index = index; }
    public int getIndex() { return index; }
    public Vector2f getInner() { return inner; }
    public Vector2f getOuter() { return outer; }


    
}
