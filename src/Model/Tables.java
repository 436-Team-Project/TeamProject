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
	 * @param ID UIObject id
	 * @param x  vertical position
	 * @param y  horizontal position
	 * @param x2 vertical position
	 * @param y2 horizontal position
	 */
	public Tables(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y, x2, y2);
		this.numberOfSpots = 4;
	}
	
	/**
	 * Draw a square, circle, or table img in this spot
	 */
	public void draw() {
	
	}
	
	@Override
	public String toString() {
		String result = String.format("Table<%d>[%.2f, %.2f, %.2f, %.2f]", ID, x, y, x2, y2);
		String connections = "";
		
		return result;
	}
}
