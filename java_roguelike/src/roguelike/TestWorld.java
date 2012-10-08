package roguelike;
import java.util.Random;

import it.marteEngine.Camera;
import it.marteEngine.World;
import it.marteEngine.actor.TopDownActor;
import it.marteEngine.entity.Entity;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMapPlus;

import roguelike.entity.*;

public class TestWorld extends World {
	public TiledMapPlus map;

	public TestWorld(int id, GameContainer container) {
		super(id, container);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.init(container, game);

		Image zombieImage = new Image("resources/zombie/main.png");
		zombieImage.setFilter(Image.FILTER_NEAREST);
		Globals.put("zombieimage", zombieImage.getScaledCopy(4f));
		Globals.put("localPlayer", new LocalPlayer(50, 50));

		add((LocalPlayer) Globals.get("localPlayer"), GAME);


		setCamera(new Camera(this, (LocalPlayer) Globals.get("localPlayer"), container.getWidth(), container.getHeight()));

	}


	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		super.update(container, game, delta);
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE))
			container.exit();
		if(container.getInput().isKeyPressed(Input.KEY_F2)){
			Random r = new Random();
			Zombie z = new Zombie(r.nextInt(500), r.nextInt(500), (Image) Globals.get("zombieimage"));
			add(z);

		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g)
			throws SlickException {
		super.render(container,stateBasedGame,g);
	}



}
