package lessur.engine.enitity;

import java.util.logging.Logger;

import lessur.engine.GameManager;
import lessur.engine.GameManagerClient;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.newdawn.slick.Graphics;

/**
 * An entity in a Lessur game
 * @author liamzebedee
 *
 */
public abstract class Entity {
	/* Properties */
	public int id;
	public float angle = 0;
	
	/* Members */
	public Body body;
	public Vec2 position = new Vec2(0, 0);
	
	public abstract void init(GameManager gameManager);
	public abstract void update(GameManager gameManager, int delta);
	public abstract void render(GameManager gameManager, Graphics g);
	
	/* Helper Methods */
	/**
	 * Changes the angle
	 * @param degrees
	 */
	public void rotate(float degrees){
		this.angle = degrees;
	}
	
	/**
	 * Adds to the current rotation
	 * @param degrees
	 */
	public void rotateAdd(float degrees){
		this.angle += degrees;
	}
	
	/**
	 * Gets the angle between this entity, and a point in the world
	 * @param pos The point in the world to find the angle
	 * @return The angle in degrees (0-360)
	 */
	public float getAngle(Vec2 pos){
		float rotation = (float) Math.toDegrees(Math.atan2 (this.body.getPosition().x - pos.x+16,
				this.body.getPosition().x - pos.y+16) * -1); 
		return rotation;
	}

	public Vec2 updatePositionScreen(GameManagerClient gameManager){
		if(body == null){
			Logger.getLogger("LessurError").info("physicsBody wasn't initialised for body, returning blank screen position");
			return new Vec2(0,0);
		}
		// Update screen position
		gameManager.transform.getWorldToScreen(body.getPosition(), position);
		return this.position;
	}
	
	/*
	
	public float getAngle(Vec2 pos){
		float rotation = (float) Math.toDegrees(Math.atan2 (this.getPositionScreen().x - pos.x+16,
				this.getPositionScreen().y - pos.y+16) * -1); 
		return rotation;
	}
	
	public void setPositionFromScreen(int x, int y){
		Vec2 screen = new Vec2(x,y);
		Vec2 world = new Vec2();
		this.manager.getPhysics().getTransform().getScreenToWorld(screen, world);
		this.positionScreen = screen;
	}
	
	public void setPositionFromScreen(Vec2 screenPosition){
		Vec2 world = new Vec2();
		this.manager.getPhysics().getTransform().getScreenToWorld(screenPosition, world);
		this.positionScreen = screenPosition;
	}
	
	public Vec2 getPositionWorld(Vec2 screenPosition){
		Vec2 positionWorld = new Vec2();
		manager.getPhysics().getTransform().getScreenToWorld(positionScreen, positionWorld);
		return positionWorld;
	}*/
	
}
