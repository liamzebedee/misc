package procedural;

import java.util.Random;

import org.newdawn.slick.util.Log;

public class CellTree {
	public boolean[][] data;
	public int rowCount;
	public int rowCenter;
	public int rowLength;
	
	public CellTree(int rows, int seed) {
		this.rowCount = rows;
		rowLength = (rowCount - 1) + rowCount;
		rowCenter = (rowLength / 2);
		Log.debug("ROWLEN:"+rowLength+" _ ROWCOUNT:"+rowCount);
		data = new boolean[rowCount+1][rowLength+1];
		
		// Init first row
		// TODO add seed
		data[0][rowCenter] = true;
		Random r = new Random(seed);
		System.out.print("INIT ROW: ");
		for(int i = 0; i < rowLength-1; i++){
			//data[0][i] = ((seed % i+1) == 0) ? true : false;
			data[0][i] = r.nextBoolean();
			if(data[0][i]) System.out.print("█");
			else System.out.print(" ");
		}
		System.out.print("\n");
		
	}
	
	public boolean getCell(int row, int cell) {
		/* We need to accompany for 3 cases
		 * - Cell value is normal
		 * - Cell value is negative (needs wrap around)
		 * - Cell value is over bounds (needs wrap around)
		 */
		int index = cell;
		if(cell < 0) {
			// Negative (STOP! wrap around time) 
			index = rowLength + cell;
		}
		else if(cell > rowLength) {
			// Over bounds (STOP! wrap around time)
			index = (cell - rowLength) - 1;
		}
		return data[row][index];
	}
	
	public void setCell(int row, int cell, boolean set){
		this.data[row][cell] = set;
	}
	
	public boolean[] getRow(int row) {
		return data[row];
	}
	
	public void setRow(int row, boolean[] set){
		this.data[row] = set;
	}
	
}
