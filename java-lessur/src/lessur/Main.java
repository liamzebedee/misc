package lessur;

import lessur.engine.LightingTest3;
import lessur.engine.LightingTest4;
import lessur.engine.MenuState;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Main extends StateBasedGame {

	public Main() {
		super("Lessur");
	}
	
	public void initStatesList(GameContainer container) {
		addState(new MenuState());
		addState(new LightingTest3());
		addState(new LightingTest4());
		this.enterState(1);
	}
	
	public static void main(String[] argv) {
		try {
			AppGameContainer app = new AppGameContainer(new Main()); 
            app.setDisplayMode(854, 480, false); 
            app.setSmoothDeltas(true);
            app.setVSync(true);
            app.setTargetFrameRate(60); 
            app.setShowFPS(true); 
            app.setTitle("Lessur Engine Test - (C) Liam Edwards-Playne");
            app.start(); 
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
