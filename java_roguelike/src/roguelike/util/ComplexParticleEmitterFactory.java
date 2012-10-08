package roguelike.util;


import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ConfigurableEmitterFactory;

import roguelike.entity.Entity;

public class ComplexParticleEmitterFactory implements ConfigurableEmitterFactory {
	Entity owner;
	
	public ComplexParticleEmitterFactory(Entity owner) {
		this.owner = owner;
	}
	
	@Override
	public ConfigurableEmitter createEmitter(String name) {
		return new ComplexParticleEmitter(name, owner);
	}

}
