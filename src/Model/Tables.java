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
	
	@Override
	public String toString() {
		if(super.getId() < 10) {
			return " Table"+ super.toString();
		} else {
			return "Table"+ super.toString();
		}
	}
}
