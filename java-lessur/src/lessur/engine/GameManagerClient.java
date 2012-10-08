package lessur.engine;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lessur.engine.net.LessurClient;
import lessur.engine.net.LessurServer;
import lessur.engine.physics.Slick2DDebugDraw;

import org.jbox2d.common.OBBViewportTransform;
import org.newdawn.slick.GameContainer;

public class GameManagerClient extends GameManager {
	public GameContainer gameContainer;
	public LessurClient client;
	public LessurServer server;
	public Camera camera;
	public OBBViewportTransform transform;
	
	private boolean worldRenderDebug = false;
	
	public GameManagerClient(GameContainer gameContainer){
		super();
		this.gameContainer = gameContainer;
		transform = new OBBViewportTransform();
		transform.setYFlip(true);
		transform.setExtents(this.getWidth() / 2, this.getHeight() / 2);
	}
	
	/* Helper Methods */
	public int getWidth(){
		return this.gameContainer.getWidth();
	}
	
	public int getHeight(){
		return this.gameContainer.getHeight();
	}
	
	public void toggleRenderWorldDebug(){
		worldRenderDebug = !worldRenderDebug;
	}
	
	public void renderWorldDebug(){
		this.worldManager.currentPhysicsWorld.drawDebugData();
	}
	
	public void initWorldDebugDraw(){
		Slick2DDebugDraw physicsWorldDebugDraw = new Slick2DDebugDraw(this.gameContainer.getGraphics(), this.gameContainer);
		physicsWorldDebugDraw.setFlags(0x0001);
		this.worldManager.currentPhysicsWorld.setDebugDraw(physicsWorldDebugDraw);
	}
	
	public void setupServer(InetAddress address, int port) throws UnknownHostException{
		LessurServer server = null;
		server = new LessurServer(address, port);
		Thread t = new Thread(server);
		t.start();
		this.server = server;
	}
}
