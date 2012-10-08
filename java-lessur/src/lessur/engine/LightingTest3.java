package lessur.engine; 

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList; 
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import lessur.engine.enitity.EntityManager;
import lessur.engine.enitity.EntityPlayer;
import lessur.engine.enitity.EntityZombie;
import lessur.engine.lighting.Light;
import lessur.engine.lighting.Lighting;
import lessur.engine.lighting.old.CircleLight;
import lessur.engine.net.LessurClient;
import lessur.engine.physics.Slick2DDebugDraw;
import lessur.engine.physics.TileBody;
import lessur.engine.physics.WorldManager;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color; 
import org.newdawn.slick.GameContainer; 
import org.newdawn.slick.Graphics; 
import org.newdawn.slick.Image; 
import org.newdawn.slick.Input; 
import org.newdawn.slick.SlickException; 
import org.newdawn.slick.geom.Rectangle; 
import org.newdawn.slick.imageout.ImageOut;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.*;

public class LightingTest3 extends BasicGameState {
	GameManagerClient game;
	public static final int stateID = 1;
	StateBasedGame sbg;
	Lighting lighting;

	@Override
	public int getID() {
		return stateID;
	} 

	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		game = new GameManagerClient(container);
		game.map = new LessurMap("res/level-1.tmx");
		game.worldManager = new WorldManager(new World(new Vec2(0,0), true));			
		game.camera = new Camera(game, 0, 0);
		loadDataFromMap(container);
		try {
			game.setupServer(java.net.InetAddress.getLocalHost(), 1234);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		LessurClient client = new LessurClient();
		game.client = client;
		InetSocketAddress a = (InetSocketAddress) this.game.server.getSocket().getLocalSocketAddress();
		game.client.connect(a.getAddress(), a.getPort());
		
		game.entityManager =  new EntityManager(game);
		GroupObject mapObjectSpawn =  game.map.getObjectGroup("entity").getObject("player");
		EntityPlayer player = new EntityPlayer(new Vec2(mapObjectSpawn.x, mapObjectSpawn.y));
		game.entityManager.addMainPlayer(player);
		EntityZombie zombie = new EntityZombie(15*32,3*32);
		game.entityManager.addEntity(zombie);
		
		lighting = Lighting.getInstance(container);
		addRandomHull();addRandomHull();addRandomHull();addRandomHull();addRandomHull();addRandomHull();
		addRandomLight();addRandomLight();addRandomLight();addRandomLight();addRandomLight();addRandomLight();
	}

	private void addRandomHull() {
		Random gen = new Random();
		Vector2f points[] = {new Vector2f(0, 0), new Vector2f(20, 0), new Vector2f(20, 20), new Vector2f(0, 20)};
		lighting.convexHullList.add(new lessur.engine.lighting.ConvexHull(new Vector2f(gen.nextInt(1024), gen.nextInt(768)), Arrays.asList(points), 0.1f, Color.white));
	}

	private void addRandomLight() {
		Random gen = new Random();
		Color c = new Color(gen.nextFloat(), gen.nextFloat(), gen.nextFloat());
		lighting.lightList.add(new Light(new Vector2f(gen.nextInt(1024), gen.nextInt(768)), 200.0f, 0.0f, c));
	}


	@Override
	public void enter(GameContainer container, StateBasedGame sbg) throws SlickException{
		
	}

	public void loadDataFromMap(GameContainer gc){
		//lighting = new LightingSystem(map,0.5f);
		ArrayList<Tile> mapWallTiles = game.map.getAllTilesFromAllLayers("structure-wall"); 
		for(Tile mapWallTile : mapWallTiles){
			TileBody.createWall(game.worldManager.currentPhysicsWorld, gc, game.map, mapWallTile);
			Rectangle ra = new Rectangle(mapWallTile.x *32, mapWallTile.y*32 , 32, 32); 
			//lighting.getObstructions().add(ra); 
		}
		ArrayList<GroupObject> lights = game.map.getObjectGroup("entity").getObjectsOfType("light");
		for(GroupObject light : lights){
			CircleLight lightSrc = new CircleLight(new Color(Integer.parseInt((String) light.props.get("colourR")),
					Integer.parseInt((String) light.props.get("colourG")),
					Integer.parseInt((String) light.props.get("colourB"))), 
					light.x, light.y, Float.parseFloat((String)light.props.get("radius")));
			//lighting.getLights().add(lightSrc);
		}
		//lighting.update();
	}

	public void update(GameContainer container, StateBasedGame sbg, int delta) throws SlickException {
		EntityPlayer player = game.entityManager.getMainPlayer();
		Input input = container.getInput();
		Vec2 mouseWorld = game.camera.getMouseWorld(player, new Vec2(input.getMouseX(),input.getMouseY()));
		game.entityManager.getMainPlayer().rotate(game.entityManager.getMainPlayer().getAngle(mouseWorld));
		int ps = 7+1/2;
		if (input.isKeyDown(Input.KEY_W)) { 
			player.move(EntityPlayer.MovementDirection.forward, ps);
		}
		if (input.isKeyDown(Input.KEY_A)){ 
			player.move(EntityPlayer.MovementDirection.left, ps);
		} 
		if (input.isKeyDown(Input.KEY_D)) { 
			player.move(EntityPlayer.MovementDirection.right, ps);
		} 
		if (input.isKeyDown(Input.KEY_S)) { 
			player.move(EntityPlayer.MovementDirection.back, ps);
		}

		if (input.isKeyPressed(Input.KEY_F1)) {
			Image target = new Image(container.getWidth(), container.getHeight());
			container.getGraphics().copyArea(target, 0, 0);
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String screenshotPath = System.getProperty("user.dir")+System.getProperty("file.separator")+"screenshot-"+sdf.format(cal.getTime())+".png";
			ImageOut.write(target, screenshotPath, false);
			target.destroy();
		}
		if(input.isKeyPressed(Input.KEY_F2)){
			((GameManagerClient) game).toggleRenderWorldDebug();
		}
		if(input.isKeyPressed(Input.KEY_ESCAPE)){
			container.exit();
		}

		//lighting.update();
		game.worldManager.update(delta);
		game.entityManager.update(delta);
	} 

	@Override 
	public void mouseDragged(int oldx, int oldy, int newx, int newy) { 
		mousePressed(0, newx, newy); 
	} 

	@Override 
	public void mousePressed(int button, int x, int y) { 
		Vec2 mouseWorld = game.camera.getMouseWorld(game.entityManager.getMainPlayer(), new Vec2(x,y));
	} 


	public void render(GameContainer container, StateBasedGame sbg, Graphics g) 
			throws SlickException {
		g.pushTransform();
		game.camera.center(game.entityManager.getMainPlayer(), g);
		
		lighting.render(g, game.map);

		game.entityManager.render(g);
		((GameManagerClient) game).renderWorldDebug();
		g.popTransform();

		g.drawString("Lessur - ENGINE Test", 0, 50);

	} 

} 