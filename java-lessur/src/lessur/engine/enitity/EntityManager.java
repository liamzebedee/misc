package lessur.engine.enitity;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.Graphics;

import lessur.engine.GameManager;
import lessur.engine.physics.WorldManager;

/**
 * Manages all game entities
 * @author liamzebedee
 *
 */
public class EntityManager {
	private GameManager gameManager;
	private ArrayList<Entity> entityList = new ArrayList<Entity>();
	private EntityPlayer mainPlayer;
	
	public EntityManager(GameManager game){
		this.gameManager = game;
	}
	
	public void render(Graphics g){
		if(entityList.isEmpty()){return;}
		for(Entity e : this.entityList){
			e.render(gameManager, g);
		}
	}
	
	public void update(int delta){
		if(entityList.isEmpty()){return;}
		for(Entity e : this.entityList){
			e.update(gameManager, delta);
		}
	}
	
	public Entity getEntity(int entityID){
		return entityList.get(entityID);
	}
	
	public void addEntity(Entity entity){
		entity.init(gameManager);
		entityList.add(entity);
	}
	
	public void addMainPlayer(EntityPlayer entity){
		if(mainPlayer != null){
			Logger.getLogger("EntityManager").log(Level.SEVERE, "Main player has already been set!");
			System.exit(0);
		}
		entity.init(gameManager);
		entityList.add(entity);
		mainPlayer = entity;
	}
	
	public EntityPlayer getMainPlayer(){
		if(mainPlayer == null){
			Logger.getLogger("EntityManager").warning("Main player not set, returning null!");
		}
		return this.mainPlayer;
	}
	
}
