package lessur.engine.lighting;

import lessur.util.GeomUtil;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

public class Light {
	public Vector2f position;
	public float depth, size, radius, intensity;
	public Color color; // RGB
	
	 public Light(Vector2f position, float radius, float depth, Color color) {
	        this.position = position;
	        this.radius = radius;
	        this.depth = depth;
	        this.color = color;
	        this.size=10.0f;
	    }

	    public Vector2f outerVector(Vector2f edge, int step) {
	        Vector2f cv = new Vector2f(position.x - edge.x, position.y - edge.y);
	        boolean useNegative = false;
	        if(position.x<edge.x) useNegative=true;

	        Vector2f perpVec = new Vector2f(position.x - edge.x, position.y - edge.y);
	        perpVec.normalise();
	        
	        if (step == 1) {
	            if(useNegative) {
	                perpVec = GeomUtil.mul(perpVec,-size);
	                perpVec = rotateVec(perpVec, (float) (Math.PI*2.0f/4.0f));
	            }
	            else {
	                perpVec = GeomUtil.mul(perpVec, size);
	                perpVec = rotateVec(perpVec, (float) (-Math.PI*2.0f/4.0f));
	            }
	        }
	        else {
	            if(useNegative) {
	            	 perpVec = GeomUtil.mul(perpVec, -size);
	                perpVec = rotateVec(perpVec, (float) (-Math.PI*2.0f/4.0f));
	            }
	            else {
	                perpVec = rotateVec(perpVec, (float) (Math.PI*2.0f/4.0f));
	                perpVec = GeomUtil.mul(perpVec, size);
	            }
	        }
	        cv = new Vector2f((position.x + perpVec.x) - edge.x, (position.y + perpVec.y) - edge.y);
	        cv = GeomUtil.mul(cv, -1.0f);

	        cv.normalise();
	        return GeomUtil.mul(cv, radius*10.0f);
	    }

	    public Vector2f innerVector(Vector2f edge, int step) {
	        Vector2f cv = new Vector2f(position.x - edge.x, position.y - edge.y);
	        boolean useNegative = false;
	        if(position.x<edge.x) useNegative=true;

	        Vector2f perpVec = new Vector2f(position.x - edge.x, position.y - edge.y);
	        perpVec.normalise();

	        if (step == 1) {
	            if(useNegative) {
	                perpVec = GeomUtil.mul(perpVec,-size);
	                perpVec = rotateVec(perpVec, (float) (-Math.PI*2.0f/4.0f));
	            }
	            else {
	                perpVec = rotateVec(perpVec, (float) (Math.PI*2.0f/4.0f));
	                perpVec = GeomUtil.mul(perpVec, size);
	            }
	        }
	        else {
	            if(useNegative) {
	                perpVec = GeomUtil.mul(perpVec, -size);
	                perpVec = rotateVec(perpVec, (float) (Math.PI*2.0f/4.0f));
	            }
	            else {
	                perpVec = GeomUtil.mul(perpVec, size);
	                perpVec = rotateVec(perpVec, (float) (-Math.PI*2.0f/4.0f));
	            }
	        }
	        cv = new Vector2f((position.x + perpVec.x) - edge.x, (position.y + perpVec.y) - edge.y);
	        cv = GeomUtil.mul(cv, -1.0f);

	        cv.normalise();
	        return GeomUtil.mul(cv, radius*10.0f);
	    }

	    public void renderSource() {
	        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

	        //# Color
	        GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
	        GL11.glVertex3f(position.x, position.y, depth);

	        float angle = 0;
	        while (angle <= Math.PI * 2) {
	            GL11.glVertex3f((float)(size * Math.cos(angle) + position.x), (float) (size * Math.sin(angle) + position.y), depth);
	            angle += Math.PI * 2.0f / 12.0f;
	        }
	        GL11.glVertex3f(position.x + size, position.y, depth);

	        GL11.glEnd();
	    }

	    public void render(float intensity) {
	        //Begin Drawing
	        LightShader shader = LightShader.getLightShader();
	        shader.enable();
	        shader.setState(color.brighter(intensity));

	        GL11.glPushMatrix();
	        GL11.glTranslatef(position.x, position.y, 0);
	        GL11.glScalef(radius, radius, 0);
	        GL11.glBegin(GL11.GL_QUADS);
	        GL11.glVertex3f(-1.0f, -1.0f, depth);
	        GL11.glVertex3f(1.0f, -1.0f, depth);
	        GL11.glVertex3f(1.0f, 1.0f, depth);
	        GL11.glVertex3f(-1.0f, 1.0f, depth);
	        GL11.glEnd();
	        GL11.glPopMatrix();

	        shader.disable();
	    }

	    private Vector2f rotateVec(Vector2f vec, float rad) {
	        //"""Rotate vector with radians."""
	        float length = vec.length();
	        if (vec.y != 0) rad += Math.asin(vec.y/length);
	        else if (vec.x != 0) rad += Math.acos(vec.x/length);
	        else {
	            //# Null Vector
	            return new Vector2f();
	        }
	        return new Vector2f((float)(length * Math.cos(rad)),(float)(length * Math.sin(rad)));
	    }

	    public Vector2f getPosition() {
	        return position;
	    }

	    public void setPosition(Vector2f position) {
	        this.position = position;
	    }

	    public float getRadius() {
	        return radius;
	    }
}
