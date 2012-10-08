package roguelike.skill;

import java.io.IOException;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;

import roguelike.entity.Entity;
import roguelike.util.ComplexParticleEmitterFactory;

public class FlameSkill extends Skill {
	ParticleSystem system;
	ConfigurableEmitter emitter;
	int angle = 0;
	float x, y;

	public FlameSkill(Entity e){
		super(e);
		usage = -1;
		try {
			ComplexParticleEmitterFactory complexParticleEmitter = new ComplexParticleEmitterFactory(this.holder);
			system = ParticleIO.loadConfiguredSystem("resources/particles/flame.xml", complexParticleEmitter);
			system.getEmitter(0).wrapUp();
			emitter = (ConfigurableEmitter) system.getEmitter(0);
			emitter.angularOffset.setValue(angle);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void render(GameContainer container, Graphics g) {
		system.render(x, y);
	}

	@Override
	public void update(GameContainer container, int delta, boolean inUse) {
		if(inUse) {
			if(usage == 0){
				emitter.resume();
			}
			system.update(delta);
			usage += delta;
			
			angle = holder.getAngle();
			this.emitter.angularOffset.setValue(angle);
			x = holder.x;
			y = holder.y;
		}
		else {
			if(usage > 0) {
				system.getEmitter(0).wrapUp();
				usage = 0;
			}
			system.update(delta);
		}
	}

	@Override
	public void use() {
		resetState();
	}

	@Override
	public void resetState() {
		usage = 0;
	}
}
