package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TablesTest {
	
	@Test
	void update() {
		UIObjects table = new Tables(1, 0, 0, 50, 200);
		assertEquals(0, table.getX());
		assertEquals(0, table.getY());
		assertEquals(50, table.getX2());
		assertEquals(200, table.getY2());
		
		table.update(500, 800, 1200, 1600);
		assertEquals(500, table.getX());
		assertEquals(800, table.getY());
		assertEquals(1200, table.getX2());
		assertEquals(1600, table.getY2());
	}
	
	@Test
	void getX() {
		UIObjects table = new Tables(1, 10, 12, 110, 112);
		assertEquals(10, table.getX());
		assertEquals(110, table.getX2());
	}
	
	@Test
	void getY() {
		UIObjects table = new Tables(1, 10, 12, 110, 112);
		assertEquals(12, table.getY());
		assertEquals(112, table.getY2());
	}
	
	@Test
	void getWidth() {
		UIObjects table = new Tables(1, 0, 0, 50, 200);
		assertEquals(50, table.getWidth());
	}
	
	@Test
	void getHeight() {
		UIObjects table = new Tables(1, 0, 0, 50, 200);
		assertEquals(200, table.getHeight());
	}

}