package Model;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
	private final int SIZE = 20;
	
	@Test
	void createObject() {
		Model model = new Model();
		assertEquals(0, model.getObjects().size());
		model.createObject("wall", 0, 0);
		assertEquals(1, model.getObjects().size());
	}
	
	@Test
	void updateObject() {
		Model model = new Model();
		model.createObject("wall", 0, 0);
		UIObjects wall = model.getObject(0,0);
		
		assertEquals(0, wall.getX());
		assertEquals(0, wall.getY());
		assertEquals(20, wall.getX2());
		assertEquals(20, wall.getY2());
		
		model.updateObject(100, 200, 800, 900, 0);
		
		assertEquals(100, wall.getX());
		assertEquals(200, wall.getY());
		assertEquals(800, wall.getX2());
		assertEquals(900, wall.getY2());
	}
	
	@Test
	void addObject() {
		Model model = new Model();
		UIObjects wall = new Wall(1, 0, 0, 50, 200);
		
		assertEquals(0, model.getObjects().size());
		model.addObject(wall);
		assertEquals(1, model.getObjects().size());
	}
	
	@Test
	void removeLastObject() {
		Model model = new Model();
		assertEquals(0, model.getObjects().size());
		
		model.createObject("wall", 0, 0);
		assertEquals(1, model.getObjects().size());
		
		model.removeLastObject();
		assertEquals(0, model.getObjects().size());
	}
	
	
	@Test
	void getObject() {
		Model model = new Model();
		model.createObject("wall", 0, 0);
		model.createObject("wall", 50, 50);
		model.createObject("wall", 100, 100);
		
		UIObjects wall = model.getObject(50,50);
		assertEquals(50, wall.getX());
		assertEquals(50, wall.getY());
	}

}