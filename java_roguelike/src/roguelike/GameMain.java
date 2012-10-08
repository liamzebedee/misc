package roguelike;
import it.marteEngine.ME;
import it.marteEngine.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.StateBasedGame;

public class GameMain extends StateBasedGame {

	public GameMain(String title) {
		super(title);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		loadResources();
		//addState(new MenuState(0));
		addState(new TestWorld(1, container));
	}

	public void loadResources() {
		try {
			Globals.instantiate();
			ResourceManager.loadResources("resources/resourceIndex.xml");
			UnicodeFont font = new UnicodeFont("resources/fonts/PIXEARG_.TTF", 36, false, false);
			Globals.put("fonts/pixel", font);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] argv) {
		try {
			ME.keyToggleDebug = Input.KEY_F1;
			AppGameContainer container = new AppGameContainer(new GameMain(
					"the adventures of rog[ue]"));
			container.setDisplayMode(800, 600, false);
			container.setTargetFrameRate(60);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}
