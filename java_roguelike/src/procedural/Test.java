package procedural;

import java.util.Random;

import javax.swing.JFrame;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.jfugue.Rhythm;

public class Test extends JFrame {

	public Test(){
		CellRule30 rule = new CellRule30();
		CellAutomata ca = new CellAutomata(100, rule, (int) ("I LIKE TRai".hashCode()+System.currentTimeMillis()));
		ca.generate();
		ca.print();
		System.out.print("\n\n\n");

		printArray(ca.cellTree.data);
		System.out.println();

		boolean[][] score = ca.score(5);
		printArray(score);

		Rhythm rhythm = new Rhythm();
		rhythm.setLayer(1, "O..oO...O..oOO..");
		rhythm.setLayer(2, "..*...*...*...*.");
		rhythm.setLayer(3, "^^^^^^^^^^^^^^^^");
		rhythm.setLayer(4, "...............!");
		rhythm.addSubstitution('O', "[BASS_DRUM]i");
		rhythm.addSubstitution('o', "Rs [BASS_DRUM]s");
		rhythm.addSubstitution('*', "[ACOUSTIC_SNARE]i");
		rhythm.addSubstitution('^', "[PEDAL_HI_HAT]s Rs");
		rhythm.addSubstitution('!', "[CRASH_CYMBAL_1]s Rs");
		rhythm.addSubstitution('.', "Ri"); 
		Pattern pattern = rhythm.getPattern();
		pattern.repeat(4);
		Player player = new Player();
		player.play(pattern);

		/*for(int i = 0; i < score.length; i++) {
			for(int j = 0; j < score[0].length; j++) {
				if(score[i][j]) System.out.print("1");
				else System.out.print("0");
			}
			System.out.print("\n");
		}*/
		
		while(true) {
			// Get next state (column)
			// Play
		}

	}

	private static void printArray(boolean[][] arr)
	{
		for(int row=0 ; row<arr.length ; row++)
		{
			for(int col=0 ; col<arr[0].length ; col++)
			{
				System.out.print(arr[row][col] ? '█' : ' ');
			}
			System.out.println();
		}
		System.out.println();
	}

	public String moar(){
		Random r = new Random();
		if(r.nextBoolean()) return " A";
		else return " B";
	}

	public static void main(String[] args) {
		Test test = new Test();
	}

}
