package Model;

import java.util.ArrayList;

/**
 * Wall class
 */
public class Wall extends UIObjects {
	
	ArrayList<Wall> connections = new ArrayList<Wall>();
	
	/**
	 * Wall constructor
	 *
	 * @param ID Wall ID
	 * @param x vertical position
	 * @param y horizontal position
	 * @param x2 vertical position
	 * @param y2 horizontal position
	 */
	public Wall(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y,x2,y2);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Draw wither a line or a filled rectangle in this spot.
	 */
	@Override
	public void draw() {
	
	}
	
	
}
