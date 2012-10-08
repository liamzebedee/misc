package lessur.engine.enitity;

import lessur.engine.GameManager;
import lessur.engine.GameManagerClient;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class EntityZombie extends LivingEntity {
	private Image i; // TODO
	
	public EntityZombie(int entityX, int entityY){
		try {
			i = new Image("res/zombie.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(GameManager gameManager) {
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;        
        bodyDef.position.set(new Vec2(0, 0));
        body = gameManager.worldManager.getWorld().createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.m_radius = (float) 10.0;
        FixtureDef f = new FixtureDef();
        f.shape = circle;
        //f.density = 0.1f;
        //f.friction = 5.0f;
        f.restitution = 0.0f;
        f.shape.m_radius = 25;
        body.createFixture(f);
        body.setLinearDamping(3f);
	}
	
	@Override
	public void update(GameManager gameManager, int delta) {
		
	}

	@Override
	public void render(GameManager gameManager, Graphics g) {
		this.updatePositionScreen((GameManagerClient) gameManager);
		i.draw(this.position.x-i.getWidth()/2, this.position.y-i.getHeight()/2);
	}

}
