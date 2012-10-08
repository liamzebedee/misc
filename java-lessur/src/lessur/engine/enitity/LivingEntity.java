package lessur.engine.enitity;

public abstract class LivingEntity extends Entity {
	/* Properties */
	public int health = 100;
	
	/* Helper Methods */
	public void kill(){
		this.health = 0;
	}
	
	public void heal(int heal){
		health += heal;
	}
	
}
