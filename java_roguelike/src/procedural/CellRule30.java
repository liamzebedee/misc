package procedural;

public class CellRule30 extends CellRule {
	
	@Override
	public boolean getCell(int row, int cell) {
		boolean c1 = false, c2 = false, c3 = false;
		c1 = this.tree.getCell(row - 1, cell - 1);	// Left neighbour
		c2 = this.tree.getCell(row - 1, cell    );	// Middle neighbour
		c3 = this.tree.getCell(row - 1, cell + 1);	// Right neighbour

		if(c1 && c2 && c3) {	// 111 
			return false;
		}
		if(c1 && c2 && !c3) {	// 110
			return true;
		}
		if(c1 && !c2 && c3) {	// 101
			return true;
		}
		if(c1 && !c2 && !c3) {	// 100
			return false;
		}
		if(!c1 && c2 && c3) {	// 011
			return true;
		}
		if(!c1 && c2 && !c3) {	// 010
			return true;
		}
		if(!c1 && !c2 && c3) {	// 001
			return true;
		}
		if(!c1 && !c2 && !c3) {	// 000
			return false;
		}
		throw new ArrayIndexOutOfBoundsException("");
	}

}
