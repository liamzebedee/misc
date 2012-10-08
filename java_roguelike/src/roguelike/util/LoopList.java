package roguelike.util;

import java.util.ArrayList;

public class LoopList<E> extends ArrayList<E> {
	public ArrayList<Integer> indices = new ArrayList<Integer>();
	private static final long serialVersionUID = 7691812685024822091L;
	
	/**
	 * Creates new index's
	 */
	public void newIndex(Integer... index) {
		for(int i : index){
			if(this.indices.lastIndexOf(i) == -1) this.indices.add(i, 0);
		}
	}
	
	/**
	 * Gets the next selection for the index, without changing the index's selection state
	 */
	public E getNext(int index) {
		if(this.indices.get(index) == null) throw new ArrayIndexOutOfBoundsException("Index "+index+" does not exist!");
		if(this.size() == 0) throw new ArrayIndexOutOfBoundsException("Array Empty, cannot get non-existent element!");
		int temp = this.indices.get(index);
		temp++;
		if(temp+1 > this.size()){
			temp -= this.size() - 1;
		}
		return this.get(temp);
	}
	
	/**
	 * Gets the previous selection for the index, without changing the index's selection state
	 */
	public E getPrevious(int index) {
		if(this.indices.get(index) == null) throw new ArrayIndexOutOfBoundsException("Index "+index+" does not exist!");
		if(this.size() == 0) throw new ArrayIndexOutOfBoundsException("Array Empty, cannot get non-existent element!");
		int temp = this.indices.get(index);
		temp--;
		if(temp < 0){
			temp += this.size() + 1;
		}
		return this.get(temp);
	}
	
	/**
	 * Gets the current selection for the index
	 */
	public E getCurrent(int index) {
		if(this.indices.get(index) == null) throw new ArrayIndexOutOfBoundsException("Index "+index+" does not exist!");
		if(this.size() == 0) throw new ArrayIndexOutOfBoundsException("Array Empty, cannot get non-existent element!");
		return this.get(this.indices.get(index));
	}
	
	/**
	 * Moves forward the selection element for that index recursively
	 */
	public void goNext(int index){
		int temp = this.indices.get(index);
		temp++;
		if(temp+1 > this.size()){
			temp -= this.size() - 1;
		}
		this.indices.set(index, temp);
	}
	
	/**
	 * Moves back the selection for that index recursively
	 */
	public void goPrevious(int index) {
		int temp = this.indices.get(index);
		temp--;
		if(temp < 0){
			temp += this.size();
		}
		this.indices.set(index, temp);
	}
	
	public ArrayList<E> getAllCurrent() {
		ArrayList<E> current = new ArrayList<E>();
		for(int index : this.indices){
			E e = this.get(index);
			if(!current.contains(e)) current.add(e);
		}
		return current;
	}
	
	public ArrayList<E> getAllUniqueSelections(){
		ArrayList<Integer> uniques = new ArrayList<Integer>();
		for(int index : this.indices){
			if(!uniques.contains(index)){
				uniques.add(index);
			}
		}
		ArrayList<E> uniqueSelections = new ArrayList<E>();
		for(int i : uniques){
			uniqueSelections.add(this.get(i));
		}
		return uniqueSelections;
	}
	
}
