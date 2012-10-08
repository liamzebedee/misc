package lessur.engine.physics;

import org.jbox2d.dynamics.World;
import org.newdawn.slick.SlickException;

public class WorldManager {
	
	private static final int maxTimestep = 350;
	private static final float fixedTimestep = 1.0f / 1000f;
	// Minimum remaining time to avoid box2d instability caused by very small delta times
	// if remaining time to simulate is smaller than this, the rest of time will be added to the last step,
	// instead of performing one more single step with only the small delta time.
	private static final float minimumTimestep = fixedTimestep / 2;
	private static final int velocityIterations = 1;
	private static final int positionIterations = 1;
	// maximum number of steps per tick to avoid spiral of death
	private static final int maxSteps = 25;

	public World currentPhysicsWorld;
	
	private World worldStates[] = new World[maxTimestep];
	private int worldStateIndex = 0;

	public WorldManager(World physicsWorld){
		this.currentPhysicsWorld = physicsWorld;
		worldStates[worldStateIndex] = currentPhysicsWorld;
	}

	private void step(float dt) {
		float frameTime = dt;
		int stepsPerformed = 0;
		while ( (frameTime > 0.0) && (stepsPerformed < maxSteps) ){
			float deltaTime = Math.min( frameTime, fixedTimestep );
			frameTime -= deltaTime;
			if (frameTime < minimumTimestep) {
				deltaTime += frameTime;
				frameTime = 0.0f;
			}
			currentPhysicsWorld.step(deltaTime,velocityIterations,positionIterations);
			stepsPerformed++;
			afterStep(); // process collisions and result from callbacks called by the step
		}
		currentPhysicsWorld.clearForces();
	}

	public void afterStep() {
		// process collisions and result from callbacks called by the step
		saveWorldState();
	}

	public World getWorld(){
		return this.currentPhysicsWorld;
	}

	public void update(float delta){
		step(delta);
	}
	
	/* PROTOTYPING */

	/*
	This is how World rollbacks will work
	Every simulation, a clone of the world is created and becomes the next element in an array. 
	This obviously consumes alot of memory, saving all the world states, so after x milliseconds,
	the world will be overwritten. Since there is a fixed timestep, the max number of elements in the
	array, will be the denominator of the timestep
	 */
	public void saveWorldState(){
		worldStates[worldStateIndex] = currentPhysicsWorld; // Saving the world (Pun)
		this.worldStateIndex = (this.worldStateIndex + 1) % maxTimestep;
	}

	/**
	 * Gets a previous World State from up to maxTimestep milliseconds ago
	 * @param worldAge The 
	 * @return
	 * @throws SlickException 
	 */
	public World getPreviousWorldState(int worldAge) throws SlickException{
		if(worldAge > maxTimestep){
			throw new SlickException("");
		}
		World w = worldStates[(this.worldStateIndex - worldAge) % maxTimestep];
		return w;
	}
	/* END PROTOTYPING */
}
