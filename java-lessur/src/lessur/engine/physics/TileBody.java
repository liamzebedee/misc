package lessur.engine.physics;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.tiled.Tile;
import org.newdawn.slick.tiled.TiledMap;

public class TileBody {
	// TODO move into Bodyfac
	public static void createWall(World physicsWorld,GameContainer gc,TiledMap tiledMap,Tile tile){
		OBBViewportTransform transform = new OBBViewportTransform();
        transform.setYFlip(true);
        transform.setExtents(gc.getWidth()/2, gc.getHeight()/2);
		
		int width = tiledMap.getTileWidth()/2;
    	BodyDef bodyDef = new BodyDef();
    	bodyDef.type = BodyType.STATIC;
    	Vec2 pos = new Vec2();
    	float x = (tile.x*(32))+width;
    	float y = (tile.y*(32))+width;
    	
    	transform.getScreenToWorld(new Vec2(x,y), pos);
    	bodyDef.position.set(pos);
    	PolygonShape dynamicBox = new PolygonShape();
    	dynamicBox.setAsBox(16.0f, 16.0f);
    	FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = dynamicBox;
    	fixtureDef.density = 1.0f;
    	fixtureDef.friction = 0.3f;
    	Body body = physicsWorld.createBody(bodyDef);
    	body.createFixture(fixtureDef);
	}
}
