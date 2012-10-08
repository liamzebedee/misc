package roguelike.util;

import java.util.ArrayList;


import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.util.Log;

import roguelike.Globals;
import roguelike.entity.Entity;
import roguelike.entity.event.BurnEvent;

public class ComplexParticleEmitter extends ConfigurableEmitter {
	public Entity owner;
	
	public ComplexParticleEmitter(String name, Entity owner) {
		super(name);
		this.owner = owner;
	}
	
	public void updateParticle(Particle particle, int delta) {
		super.updateParticle(particle, delta);
		ArrayList<it.marteEngine.entity.Entity> collisions = (ArrayList<it.marteEngine.entity.Entity>) owner.collideInto(
				Globals.TYPE_BAD, owner.x+particle.getX()-50, owner.y+particle.getY()-50);
		if(collisions != null){
			for(it.marteEngine.entity.Entity collider : collisions){
				BurnEvent burnEvent = new BurnEvent(5, 1);
				((Entity) collider).addEvent(burnEvent);
			}
		}
	}
	
}