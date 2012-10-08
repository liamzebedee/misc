package roguelike.tests;

import roguelike.util.LoopList;

public class LoopListTest {

	public static void main(String[] args) {
		LoopList<String> list = new LoopList<String>();
		list.newIndex(0, 1);
		list.add("gun");	// 0
		list.add("sword");	// 1
		list.add("magic");	// 2
		/*
		// Current for i0
		System.out.println(list.getCurrent(0)); // gun
		// goes back for i0: now at magic
		list.goPrevious(0);
		System.out.println(list.getCurrent(0)); // magic
		*/
		
		// Current for i1
		System.out.println(list.getCurrent(1)); // gun
		// goes next for i1: now at sword
		list.goNext(1); // sword
		System.out.println(list.getCurrent(1)); // sword
		
		System.out.println(list.getNext(1)); // magic
		
	}

}
