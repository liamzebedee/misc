package procedural.stream;

import java.util.Random;

import javax.swing.JFrame;

import org.jfugue.Pattern;
import org.jfugue.Player;
import org.jfugue.Rhythm;
import org.jfugue.StreamingPlayer;
import org.newdawn.slick.util.Log;

import procedural.CellAutomata;
import procedural.CellRule30;

public class Test extends JFrame {

	public Test(){
		CellRule30 rule = new CellRule30();
		CellAutomata ca = new CellAutomata(200, rule, 342);
		ca.generate();

		boolean[][] score = ca.score(10);
		StreamingPlayer player = new StreamingPlayer();

		printArray(score);
		// Get next state (column)
		Rhythm rhythm = new Rhythm();

		// Init instruments
		/*for(int layer = 0; layer < score.length; layer++) {
			rhythm.addSubstitution(stringChar, musicString)
		}*/

		/*rhythm.addSubstitution('2', "A[BASSOON]");
		rhythm.addSubstitution('3', "B[DISTORTION_GUITAR]");
		rhythm.addSubstitution('1', "C[CHIFF]");*/
		rhythm.addSubstitution('0', "[CHIFF]");
		rhythm.addSubstitution('1', "A");
		rhythm.addSubstitution('2', "B");
		rhythm.addSubstitution('3', "C");
		rhythm.addSubstitution('4', "D");
		Log.info("PLAYING for "+score[0].length);

		for(int col = 0; col < score[0].length; col++) {
			for(int layer = 0; layer < score.length; layer++) {
				rhythm.clearLayer(layer);
				if(score[layer][col]) rhythm.setLayer(layer, String.valueOf(layer));
			}

			Pattern pattern = rhythm.getPattern();
			// Play
			player.streamAndWait(pattern);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.info("DONE PLAYING");
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

	public static void main(String[] args) {
		Test test = new Test();
	}

}
