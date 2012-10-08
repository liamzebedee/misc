package roguelike.entity;


import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import roguelike.Globals;
import roguelike.skill.FlameSkill;
import roguelike.skill.Skill;
import roguelike.util.LoopList;
import it.marteEngine.ResourceManager;

public class LocalPlayer extends Entity {
	// Movements
	float move = 1;
	float step = move * 1.8f;
	
	// Skills and Mechanics
	LoopList<Skill> skillSet = new LoopList<Skill>();

	public LocalPlayer(float x, float y) {
		super(x, y);
		
		// Loads graphics data
		SpriteSheet spriteSheet = ResourceManager.getSpriteSheet("playerSpritesheet");
		setGraphic(spriteSheet);
		duration = 175;
		addAnimation("testSpell", true, 0, 0, 1, 2);

		// Collision
		addType(Globals.TYPE_GOOD);
		setHitBox(0 - (spriteSheet.getSprite(0, 0).getWidth() / 2), 0 - (spriteSheet.getSprite(0, 0).getHeight() / 2), 64, 64);

		// Parameters
		this.centered = true;
		this.skillSet.newIndex(0, 1);
		
		// Movement controls
		define("PLAYER_MOVE_FORWARD", Input.KEY_W);
		define("PLAYER_MOVE_BACK", Input.KEY_S);
		define("PLAYER_MOVE_LEFT", Input.KEY_A);
		define("PLAYER_MOVE_RIGHT", Input.KEY_D);
		define("PLAYER_SKILL1", Input.MOUSE_LEFT_BUTTON);
		define("PLAYER_SKILL2", Input.MOUSE_RIGHT_BUTTON);
		define("PLAYER_INVENTORY", Input.KEY_E);

		// Skills
		this.skillSet.add(new FlameSkill(this));
	}

	private void move(int dx, int dy) {
		x += step * dx;
		y += step * dy;
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		float mouseX = input.getMouseX();
		float mouseY = input.getMouseY();

		angle = (int) calculateAngle(x, y, mouseX, mouseY);

		// Movements
		if (check("PLAYER_MOVE_FORWARD") && (collide("SOLID", x, y-2) == null)) {
			move(0, -1);
		}
		if (check("PLAYER_MOVE_BACK") && (collide("SOLID", x, y+2) == null)) {
			move(0, 1);
		}
		if (check("PLAYER_MOVE_LEFT") && (collide("SOLID", x-2, y) == null)) {
			move(-1, 0);
		}
		if (check("PLAYER_MOVE_RIGHT") && (collide("SOLID", x+2, y) == null)) {
			move(1, 0);
		}
		Skill skill1 = this.skillSet.getCurrent(0);
		Skill skill2 = this.skillSet.getCurrent(1);
		
		if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) skill1.update(container, delta, true);
		else skill1.update(container, delta, false);
		
		// Prevents double updates of same skill
		if(!skill2.equals(skill1)){
			if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
				skill2.update(container, delta, true);
			}
			else skill2.update(container, delta, false);
		}
		
		if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) 
		
		if(pressed("PLAYER_SKILL2")){ // If right mouse is released from down state
			this.skillSet.getCurrent(1).use();
		}
		super.update(container, delta);
	}
	
	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		super.render(container, g);
		g.pushTransform();
		this.skillSet.getCurrent(0).render(container, g);
		g.popTransform();
	}

}
