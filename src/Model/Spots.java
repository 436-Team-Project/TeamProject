package Model;

/**
 * Spot class
 */
public class Spots extends UIObjects {
	
	boolean available = true;
	boolean occupied = false;
	
	/**
	 * The object responsible for acting as chairs or a person standing in the floor plan
	 *
	 * @param ID the ID of the object to keep track of and the initial coordinates of the shapes
	 * @param x  double
	 * @param y  double
	 * @param x2 double
	 * @param y2 double
	 */
	public Spots(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y, x2, y2);
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	
	/**
	 * Switches spot availability to true
	 */
	public void makeAvailable() {
		available = true;
	}
	
	/**
	 * Switches spot availability to false
	 */
	public void takeAvailable() {
		available = false;
	}
	
	/**
	 * Switches whether somebody is in the spot or not
	 */
	void updateOccupancy() {
		occupied = !occupied;
	}
	
	/**
	 * Sets the value of occupied to the given value
	 *
	 * @param occupancy boolean
	 */
	public void setOccupancy(boolean occupancy) {
		this.occupied = occupancy;
	}
	
	@Override
	public String toString() {
		if(super.getId() < 10) {
			return "  Spot"+ super.toString();
		} else {
			return " Spot"+ super.toString();
		}
	}
}
