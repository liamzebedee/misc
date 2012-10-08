package lessur.engine.enitity;

import java.util.ArrayList;

public class Party extends EntityGroup {
	Entity leader;
	
	public Party(Entity leader, ArrayList<Entity> members){
		this.leader = leader;
		this.entityList.addAll(members);
	}
}
