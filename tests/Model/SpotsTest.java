package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpotsTest {
	
	@Test
	void update() {
		UIObjects spot = new Spots(1, 0, 0, 50, 200);
		assertEquals(0, spot.getX());
		assertEquals(0, spot.getY());
		assertEquals(50, spot.getX2());
		assertEquals(200, spot.getY2());
		
		spot.update(500, 800, 1200, 1600);
		assertEquals(500, spot.getX());
		assertEquals(800, spot.getY());
		assertEquals(1200, spot.getX2());
		assertEquals(1600, spot.getY2());
	}
	
	@Test
	void getX() {
		UIObjects spot = new Spots(1, 10, 12, 110, 112);
		assertEquals(10, spot.getX());
		assertEquals(110, spot.getX2());
	}
	
	@Test
	void getY() {
		UIObjects spot = new Spots(1, 10, 12, 110, 112);
		assertEquals(12, spot.getY());
		assertEquals(112, spot.getY2());
	}
	
	@Test
	void getWidth() {
		UIObjects spot = new Spots(1, 0, 0, 50, 200);
		assertEquals(50, spot.getWidth());
	}
	
	@Test
	void getHeight() {
		UIObjects spot = new Spots(1, 0, 0, 50, 200);
		assertEquals(200, spot.getHeight());
	}
	
}