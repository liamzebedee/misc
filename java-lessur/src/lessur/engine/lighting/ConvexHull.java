package lessur.engine.lighting;

import java.util.List;

import lessur.util.GeomUtil;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

public class ConvexHull {
	/** A simple list is maintained of all the points that make up the edges of the hull. */
	/* This is calculated from a collection of points and the gift-wrapping algorithm is 
	 * used to discard unneeded points. The gift-wrapping method is useful since the output
	 *  geometry typically has a low number of edges. You may want to look into the 
	 *  QuickHull method as an alternative. */
	public List<Vector2f> points;
	/** Centre position */
	public Vector2f position;
	public float depth;
    public float depthOffset;
    public Color color;
    
    private Boolean lastNodeFrontfacing;

    public ConvexHull() {
    }

    public ConvexHull(Vector2f pos, List<Vector2f> lPoints, float depth, Color c) {
        this.points = new LoopingList<Vector2f>();
        this.position = pos;
        this.depth = depth;
        this.color = c;
        for(Vector2f v: lPoints) {
            this.points.add(new Vector2f(v.x + pos.x, v.y + pos.y));
        }
    }

    public void drawShadowGeometry(Light light) {
        //# Calculate all the front facing sides
        Integer first = null, last = null;
        lastNodeFrontfacing = null;
        for (int x=-1; x<points.size(); x++) {
            Vector2f current_point = points.get(x);
            Vector2f prev_point = points.get(x-1);

            Vector2f nv = new Vector2f(current_point.y - prev_point.y, current_point.x - prev_point.x);
            Vector2f lv = new Vector2f(current_point.x - light.getPosition().x, current_point.y - light.getPosition().y);

            //# Check if the face is front-facing
            if ((nv.x * -1.0f * lv.x) + (nv.y * lv.y) > 0) {
                if (lastNodeFrontfacing!=null && !lastNodeFrontfacing) last = points.indexOf(prev_point);
                lastNodeFrontfacing = true;
            }
            else {
                if (lastNodeFrontfacing!=null && lastNodeFrontfacing) first = points.indexOf(prev_point);
                lastNodeFrontfacing = false;
            }
        }

        if (first == null || last == null) {
            //# The light source is inside the object
            return;
        }

        //# Create shadow fins
        Object[] startFs = create_shadowfins(light, first, 1);
        List<ShadowFin> startFins = (List<ShadowFin>) startFs[0];
        first = (Integer) startFs[1]; Vector2f first_vector = (Vector2f) startFs[2];
        Object[] endFs = create_shadowfins(light, last, -1);
        List<ShadowFin> endFins = (List<ShadowFin>) endFs[0];
        last = (Integer) endFs[1]; Vector2f last_vector = (Vector2f) endFs[2];

        //# Render shadow fins
        for (ShadowFin fin: startFins) {
        	fin.render();
        }
        for (ShadowFin fin: endFins) {
        	fin.render();
        }

        //# Get a list of all the back edges
        List<Vector2f> backpoints = new LoopingList<Vector2f>();
        for (int x=first; x<first+points.size(); x++) {
            backpoints.add(0, points.get((x)%(points.size())));
            if (x%(points.size()) == last) break;
        }

        //# Figure out the length of the back edges. We'll use this later for
        //# weighted average between the shadow fins to find our umbra vectors.
        List<Float> back_length = new LoopingList<Float>();
        back_length.add(0.0f);
        float sum_back_length = 0;

        for (int x=1; x<backpoints.size(); x++) {
            float l = from_points(backpoints.get(x - 1), backpoints.get(x)).length();
            back_length.add(0, Float.valueOf(l));
            sum_back_length += l;
        }

        //# Draw the shadow geometry using a triangle strip
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        float a = 0;

        for (int x=0; x<backpoints.size(); x++) {
            Vector2f point = backpoints.get(x);
            GL11.glVertex3f(point.x, point.y, depth);
            //# Draw our umbra using weighted average vectors
            if (x != backpoints.size() - 2) {
                GL11.glVertex3f(point.x + (first_vector.x * (a / sum_back_length)) + (last_vector.x * (1 - (a / sum_back_length))),
                        point.y + (first_vector.y * (a / sum_back_length)) + (last_vector.y * (1 - (a / sum_back_length))), depth);
            }
            else {
                GL11.glVertex3f(point.x + first_vector.x, point.y + first_vector.y, depth);
            }
            a += back_length.get(x);
        }
        GL11.glEnd();
    }


    public Object[] create_shadowfins(Light light, int origin, int step) {
        List<ShadowFin> shadowfins = new LoopingList<ShadowFin>();

        //# Go backwards to see if we need any shadow fins
        int i = origin;
        while(true) {
            Vector2f p1 = points.get(i);

            //# Make sure we wrap around
            i -= step;
            if (i < 0) i = points.size() - 1;
            else if (i == points.size()) i = 0;

            Vector2f p0 = points.get(i);

            Vector2f edge = from_points(p1, p0);
            edge.normalise();

            ShadowFin shadowfin = new ShadowFin(p0);
            shadowfin.setIndex(i);

            float angle = vecAngle(edge) - vecAngle(light.outerVector(p0, step));

            if (step == 1) {
                if (angle < 0 || angle > Math.PI * 0.5f) break;
            }
            else if (step == -1) {
            //# Make sure the angle is within the right quadrant
                if (angle > Math.PI) angle -= Math.PI * 2.0f;
                if (angle > 0 || angle < -Math.PI * 0.5f) break;
            }

            shadowfin.setOuter(light.outerVector(p0, step));
            shadowfin.setInner(GeomUtil.mul(edge, light.innerVector(p0, step).length()));

            shadowfins.add(shadowfin);
            //#break
        }

        //# Go forwards and see if we need any shadow fins
        i = origin;
        while(true) {
            ShadowFin shadowfin = new ShadowFin(points.get(i));
            shadowfin.setIndex(i);

            shadowfin.setOuter(light.outerVector(points.get(i), step));
            shadowfin.setInner(light.innerVector(points.get(i), step));

            if(shadowfins.size() > 0) shadowfin.setOuter(shadowfins.get(0).getInner());

            Vector2f p0 = points.get(i);

            //# Make sure we wrap around
            i += step;
            if (i < 0) i = points.size() - 1;
            else if (i == points.size()) i = 0;

            Vector2f p1 = points.get(i);

            Vector2f edge = from_points(p1, p0);
            edge.normalise();

            boolean done = true;
            Vector2f penumbra = new Vector2f(shadowfin.getOuter().x, shadowfin.getOuter().y);
            penumbra.normalise();
            Vector2f umbra = new Vector2f(shadowfin.getInner().x, shadowfin.getInner().y);
            umbra.normalise();
            if (Math.acos(Vector2f.dot(edge, penumbra)) < Math.acos(Vector2f.dot(umbra, penumbra))) {
                shadowfin.setInner(GeomUtil.mul(edge, light.outerVector(p0, step).length()));
                done = false;
            }
            shadowfins.add(0, shadowfin);

            if (done) break;
        }

        //# Get the total angle
        float sum_angles = 0;
        for(int x=0; x<shadowfins.size(); x++) {
            sum_angles += shadowfins.get(x).angle();
        }

        //# Calculate the inner and outer intensity of the shadowfins
        float angle = 0;
        for (int x=0; x<shadowfins.size(); x++) {
            shadowfins.get(x).setUmbraIntensity(angle / sum_angles);
            angle += shadowfins.get(x).angle();
            shadowfins.get(x).setPenumbraIntensity(angle / sum_angles);
        }

        //# We'll use these for our umbra generation
        if(shadowfins.size()>0)
        return new Object[] {shadowfins, shadowfins.get(0).getIndex(), shadowfins.get(0).getInner()};
        else return new Object[] {shadowfins, 1, new Vector2f()};
    }

    public void render() {
        GL11.glColor4f(color.r, color.g, color.b, 1.0f);
        GL11.glBegin(GL11.GL_POLYGON);
        for(Vector2f point: points) {
            GL11.glVertex3f(point.x, point.y, depth);
        }
        GL11.glEnd();
    }

    public Vector2f from_points(Vector2f fromPt, Vector2f toPt) {
        return new Vector2f(fromPt.x - toPt.x, fromPt.y - toPt.y);
    }

    public float vecAngle(Vector2f v) {
        return (float) Math.atan2(v.y, v.x);
    }
}
