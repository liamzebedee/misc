package lessur.engine;

import org.jbox2d.common.Vec2;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Graphics;

import lessur.engine.enitity.Entity;

public class Camera {
	public Vec2 position;
	private GameManagerClient gameManager;
	
	public Camera(GameManager game, int x, float y){
		this.position = new Vec2(x, y);
		this.gameManager = (GameManagerClient) game;
	}
	
	public void center(Entity e, Graphics g){
		int centerX = gameManager.getWidth()/2; 
        int centerY = gameManager.getHeight()/2;
        g.translate(centerX, centerY);
        g.rotate(0, 0, 0); 
        g.translate((int) -position.x,(int) -position.y); 
        this.position = e.updatePositionScreen(gameManager);
	}
	
	public Vec2 getMouseWorld(Entity e, Vec2 mousePos){
		Vec2 entityPosition = e.updatePositionScreen(gameManager);
    	int cameraX = (int) (entityPosition.x - (gameManager.getWidth()/2));
    	int cameraY = (int) (entityPosition.y - (gameManager.getHeight()/2));
    	return new Vec2(cameraX+mousePos.x,cameraY+mousePos.y);
	}
	
	/*public float getAngle(Entity e1, Entity e2){
		float rotation = (float) Math.toDegrees(Math.atan2 (e1.getPositionScreen().x - e2.getPositionScreen().x+16,
				e1.getPositionScreen().y - e2.getPositionScreen().y+16) * -1); 
		return rotation;
	}
	
	public float getAngle(Entity e1, Vec2 pos){
		float rotation = (float) Math.toDegrees(Math.atan2 (e1.getPositionScreen().x - pos.x+16,
				e1.getPositionScreen().y - pos.y+16) * -1); 
		return rotation;
	}*/
}
