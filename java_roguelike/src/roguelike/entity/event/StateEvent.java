package roguelike.entity.event;

import roguelike.entity.Entity;

public abstract class StateEvent {
	public StateEvent(){}
	
	/**
	 * Processes the event, modifying entity state if necessary
	 * 
	 * @param e The entity of which this event belongs to
	 * @return True if the event is to be desroyed, else false
	 */
	public abstract boolean process(Entity e);
}
