package Model;

public class Spots extends UIObjects {
	
	boolean available = true;
	boolean occupied = false;
	
	/**
	 * Spot constructor
	 *
	 * @param ID Spot ID
	 * @param x vertical position
	 * @param y horizontal position
	 * @param x2 vertical position
	 * @param y2 horizontal position
	 */
	public Spots(int ID, int x, int y, int x2, int y2) {
		super(ID, x, y, x2, y2);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Draw a circle or chair in this spot.
	 */
	public void draw() {

	}
	
	/**
	 * Switches whether the spot is available to be used or not
	 */
	void updateAvailability() {
		available = !available;
	}
	
	/**
	 * Switches whether somebody is in the spot or not
	 */
	void updateOccupancy() {
		occupied = !occupied;
	}
}
