package Model;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class WallTest {
	
	@Test
	void update() {
		UIObjects wall = new Wall(1, 0, 0, 50, 200);
		assertEquals(0, wall.getX());
		assertEquals(0, wall.getY());
		assertEquals(50, wall.getX2());
		assertEquals(200, wall.getY2());
		
		wall.update(500, 800, 1200, 1600);
		assertEquals(500, wall.getX());
		assertEquals(800, wall.getY());
		assertEquals(1200, wall.getX2());
		assertEquals(1600, wall.getY2());
	}
	
	@Test
	void getX() {
		UIObjects wall = new Wall(1, 10, 12, 110, 112);
		assertEquals(10, wall.getX());
		assertEquals(110, wall.getX2());
	}
	
	@Test
	void getY() {
		UIObjects wall = new Wall(1, 10, 12, 110, 112);
		assertEquals(12, wall.getY());
		assertEquals(112, wall.getY2());
	}
	
	@Test
	void getWidth() {
		UIObjects wall = new Wall(1, 0, 0, 50, 200);
		assertEquals(50, wall.getWidth());
	}
	
	@Test
	void getHeight() {
		UIObjects wall = new Wall(1, 0, 0, 50, 200);
		assertEquals(200, wall.getHeight());
	}
}