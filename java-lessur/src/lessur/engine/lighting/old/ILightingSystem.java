package lessur.engine.lighting.old;

import java.util.ArrayList;


import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;


public abstract interface ILightingSystem { 
	/** List of all the current lights */
	ArrayList<CircleLight> lights = new ArrayList<CircleLight>();
	boolean colouredLights = true;
	
	public abstract void update();
	public abstract void render(Graphics g);
	public abstract Image light(int x,int y,Image i);

}

