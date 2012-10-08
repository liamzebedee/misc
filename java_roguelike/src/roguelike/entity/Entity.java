package roguelike.entity;

import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import roguelike.entity.event.StateEvent;

public class Entity extends it.marteEngine.entity.Entity {
	public int health = 0;
	
	// State Data
	protected ArrayList<StateEvent> stateEvents = new ArrayList<StateEvent>();
	
	// 	Burn
	public float burnDamage = 5f;
	
	public Entity(float x, float y) {
		super(x, y);
	}
	
	public Entity(float x, float y, Image image){
		super(x, y, image);
	}
	
	public void addEvent(StateEvent e) {
		this.stateEvents.add(e);
	}
	
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		super.update(container, delta);
		
		// Processes all state events
		Iterator<StateEvent> iterator = stateEvents.iterator();
		while(iterator.hasNext()){
			if(iterator.next().process(this)) iterator.remove();
		}
		
	}

}
