package procedural;

public abstract class CellRule {
	protected CellTree tree;
	
	public CellRule() {}
	
	public void setTree(CellTree tree) {this.tree = tree;}
	
	public abstract boolean getCell(int row, int cell);
}
