package roguelike.entity.event;

import roguelike.entity.Entity;

public class BurnEvent extends StateEvent {
	public int burnTTL = -1;
	public int burnIntensity = 1;
	
	public BurnEvent(int ttl, int intensity) {
		this.burnTTL = ttl;
		this.burnIntensity = intensity;
	}
	
	@Override
	public boolean process(Entity entity) {
		entity.health -= entity.burnDamage;
		this.burnTTL--;
		if(this.burnTTL < 0) return true;
		
		return false;
	}
	
}
