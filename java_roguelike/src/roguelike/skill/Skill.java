package roguelike.skill;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import roguelike.entity.Entity;


public abstract class Skill {
	/** ms of usage for this skill. calculated from accumualtion of update delta's */
	public int usage = 0;
	/** Holder of this skill */
	public Entity holder;
	
	public Skill(Entity e){
		this.holder = e;
	}
	
	public abstract void render(GameContainer container, Graphics g);
	public abstract void update(GameContainer container, int delta, boolean inUse);
	
	/** Called when key is released */
	public abstract void use();
	
	/** Called after skill is used. Should reset all variables related to skill's state */
	public abstract void resetState();
}
