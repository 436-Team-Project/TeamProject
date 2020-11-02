package Model;

import java.util.ArrayList;

/**
 * Table class
 */
public class Tables extends UIObjects {
	
	boolean available = true;
	int numberOfSpots;
	ArrayList<Spots> availableSpots = new ArrayList<Spots>();
	
	public Tables(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y, x2, y2);
		this.numberOfSpots = 4;
	}
	
	@Override
	public String toString() {
		return "ID: " + this.ID+ "type: table";
	}
}
