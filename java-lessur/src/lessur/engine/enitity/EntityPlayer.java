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

public class EntityPlayer extends LivingEntity {
	public String name;
	public int state;
	private Image i = null; // TODO
	public enum MovementDirection { left, right, forward, back } // This can be determined from the angle
	
	public EntityPlayer(Vec2 position){
		try {
			i = new Image("res/player.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
		this.position = position;
	}
	
	@Override
	public void init(GameManager gameManager) {
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;        
        bodyDef.position.set(position);
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
		Vec2 bodyVelocity = this.body.getLinearVelocity();
		// Reduce speed until below max
		float len = bodyVelocity.length();
		float max = 250.0f;
		if(len > max) {
			bodyVelocity.x=(bodyVelocity.x/len)*max;
			bodyVelocity.y=(bodyVelocity.y/len)*max;
			this.body.setLinearVelocity(bodyVelocity);
		}
		updatePositionScreen((GameManagerClient) gameManager);
	}

	@Override
	public void render(GameManager gameManager, Graphics g) {
		g.pushTransform();
		g.rotate(this.position.x, this.position.y, angle);
		i.draw(this.position.x - i.getWidth() / 2, this.position.y - i.getHeight() / 2);
		g.popTransform();
	}
	
	public void move(MovementDirection d, int speed){
		Vec2 velocity = new Vec2();
		switch(d){
		case left:
			velocity.x += -speed;
			break;
		case right:
			velocity.x += speed;
			break;
		case forward:
			velocity.y += speed;
			break;
		case back:
			velocity.y += -speed;
			break;
		}
		this.body.applyLinearImpulse(velocity, body.getLocalCenter());
	}
	
	

}
