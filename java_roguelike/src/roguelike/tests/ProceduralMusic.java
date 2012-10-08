package roguelike.tests;

import org.jfugue.*;

public class ProceduralMusic {
	enum Genre { dubstep }
	class PMSet {
		Genre genre;
		int tick = 0;
		int state = 0;
		final int seed = (int) Math.random();
		public PMSet(){}
	}
	
	public void getNext(final int seed, int state, int tick, PMSet set) {
		
		if((tick % 4) == 0){
			// Start new beat rhythm thing
		}
		else if(((tick+1) % 4) == 0) {
			// Add a lead on beat
			// If dubstep, make everything fade silence
		}
		tick++;
	}
	
	public ProceduralMusic(){
		PMSet set = new PMSet();
		
		Player player = new Player();
		Rhythm rhythm = new Rhythm();
		rhythm.setLayer(1, "C A B A");
		Pattern pattern = rhythm.getPattern();
		pattern.repeat(1);
		player.play(pattern);
	}

	public static void main(String[] args){
		ProceduralMusic m = new ProceduralMusic();
	}
}
