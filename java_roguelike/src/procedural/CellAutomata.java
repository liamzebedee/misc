package procedural;

import org.newdawn.slick.util.Log;

public class CellAutomata {
	public CellTree cellTree;
	public CellRule rule;

	public CellAutomata(int rows, CellRule rule, int seed){
		this.cellTree = new CellTree(rows, seed);
		rule.setTree(cellTree);
		this.rule = rule;
	}

	public CellTree generate(){
		// We skip row 0, as this is the starting row, and for reduced clutterage it is initialised seperately
		for(int i = 1; i < cellTree.rowCount; i++){
			// For each row
			for(int j = 0; j < cellTree.rowLength; j++){
				// For each cell
				cellTree.setCell(i, j, rule.getCell(i, j)); // Sets the cell according to the rule
			}
			// Log.debug("Generated Row: "+(i+1));
		}
		this.print();
		return cellTree;
	}

	public void print() {
		for(int i = 0; i < cellTree.rowCount; i++) {
			for(int j = 0; j < cellTree.rowLength; j++) {
				if(cellTree.getCell(i, j)) System.out.print("█");
				else System.out.print(" ");
			}
			System.out.print("\n");
		}
	}

	public boolean[][] score(int rows) {
		boolean[][] data = new boolean[cellTree.rowLength][cellTree.rowCount];
		
		// Copy and transform data across
		for(int x = 0; x < cellTree.rowLength; x++) {
			for(int y = 0; y < cellTree.rowCount; y++){
				// data[x][y] = cellTree.data[y][x+1];
				data[x][y] = cellTree.data[y][x+1];
			}
		}
		
		if(rows > data.length) {
			Log.error("Rows is larger than array length, defaulting to array length instead");
			rows = data.length;
		}
		boolean[][] data2 = new boolean[rows][data[0].length];
		
		// Cut down row count
		for(int x = 0; x < rows; x++) {
			for(int y = 0; y < data[0].length; y++) {
				data2[x][y] = data[x][y];
			}
		}
		
		/*
		 * rotate code
		for(int i=0; i<data[0].length; i++) {
	    	for(int j=data.length-1; j>=0; j--){
	        	data2[i][j] = data[j][i];
	     	}
	    }
	    */
		return data2;
	}
	
	/**
	 * 
	 * @param source
	 * @param offset Index of line to start at
	 * @param width
	 * @return
	 */
	public boolean[][] score2(boolean[][] source, int offset, int width) {
		final boolean[][] target = new boolean[width][source.length];
		for(int targetRow = 0; targetRow < width; targetRow++) {
			for(int targetCol = 0; targetCol < source.length; targetCol++) {
				target[targetRow][targetCol] = source[targetCol][offset + width-1 - targetRow];
			}
		}
		return target;
	}
}