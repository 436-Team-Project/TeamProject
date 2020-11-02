package Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class modelTestCases {

	@Test
	void addElement() {
		Model testModel = new Model();
		
		testModel.createObject("chair", 100, 100);
		testModel.createObject("table", 200, 200);
		testModel.createObject("wall", 300, 300);
		
		int x = testModel.getObjects().size();
		
		ArrayList<UIObjects> arr= testModel.getObjects();
		
		for (UIObjects item : arr) {
			System.out.println(item);
		}
		assertEquals(3, x);
	}
	
	@Test
	void testSave() {
		Model testModel = new Model();
		
		testModel.createObject("chair", 100, 100);
		testModel.createObject("table", 200, 200);
		testModel.createObject("wall", 300, 300);
		
		//save the item
		testModel.saveState();
		
		//create new model and see if it loads in.
		Model testModel2 = new Model();
		testModel2.loadState();
		
		int x = testModel2.getObjects().size();
		assertEquals(x, 3);
	}

}
