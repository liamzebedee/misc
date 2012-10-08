package lessur.engine;

import java.util.Arrays;
import java.util.Random;

import lessur.engine.lighting.Light;
import lessur.engine.lighting.Lighting;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class LightingTest4 extends BasicGameState {
	Lighting lighting;
	
    private void addRandomHull() {
        Random gen = new Random();
        Vector2f points[] = {new Vector2f(0, 0), new Vector2f(20, 0), new Vector2f(20, 20), new Vector2f(0, 20)};
        lighting.convexHullList.add(new lessur.engine.lighting.ConvexHull(new Vector2f(gen.nextInt(1024), gen.nextInt(768)), Arrays.asList(points), 0.1f, Color.white));
    }

    private void addRandomLight() {
        Random gen = new Random();
        Color c = new Color(gen.nextFloat(), gen.nextFloat(), gen.nextFloat());
        lighting.lightList.add(new Light(new Vector2f(gen.nextInt(1024), gen.nextInt(768)), 200.0f, 0.0f, c));
    }
	
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		lighting  = Lighting.getInstance(arg0);
		Vector2f points[] = {new Vector2f(0, 0), new Vector2f(20, 0), new Vector2f(20, 20), new Vector2f(0, 20)};
        lighting.convexHullList.add(new lessur.engine.lighting.ConvexHull(new Vector2f(400, 50), Arrays.asList(points), 0.1f, Color.white));
    
		
		addRandomLight();addRandomLight();addRandomLight();addRandomLight();addRandomLight();addRandomLight();
		
		arg0.getGraphics().setBackground(Color.white);
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		lighting.render(arg2, null);
		arg2.drawRect(400, 50, 20, 20);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		Input input = arg0.getInput();
		if(lighting.lightList.size()>0) lighting.lightList.get(lighting.lightList.size()-1).setPosition(new Vector2f(input.getMouseX(), input.getMouseY()));
	}

	@Override
	public int getID() {
		return 2;
	}

}
