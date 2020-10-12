package Model;

import java.util.ArrayList;

/**
 * Table class
 */
public class Tables extends UIObjects {
	
	boolean available = true;
	int numberOfSpots;
	ArrayList<Spots> availableSpots = new ArrayList<Spots>();
	
	/**
	 * Table constructor
	 *
	 * @param ID table ID
	 * @param x vertical position
	 * @param y horizontal position
	 * @param x2 vertical position
	 * @param y2 horizontal position
	 */
	public Tables(int ID, int x, int y, int x2, int y2) {
		super(ID, x, y, x2, y2);
		this.numberOfSpots = 4;

	}
	
	/**
	 * Draw a square, circle, or table img in this spot
	 */
	@Override
	public void draw() {
	
	}
}
