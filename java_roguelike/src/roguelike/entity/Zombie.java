package roguelike.entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import roguelike.Globals;


public class Zombie extends Entity {

	public Zombie(float x, float y, Image image) {
		super(x, y, image);
		setHitBox(0 - (image.getWidth() / 2), 0 - (image.getHeight() / 2), image.getWidth(), image.getHeight());
		addType(Globals.TYPE_BAD);
			
		// Parameters
		this.centered = true;
		health = 100;
	}
	
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		super.update(container, delta);
		LocalPlayer localPlayer = (LocalPlayer) Globals.get("localPlayer");
		angle = (int) calculateAngle(x, y, localPlayer.x, localPlayer.y);
		
	}
	
	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		super.render(container, g);
		if(health < 0){
			g.drawString("DEAD", x, y);
		}
	}

}
