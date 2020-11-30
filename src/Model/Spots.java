package Model;

/**
 * Spot class
 */
public class Spots extends UIObjects {
	
	boolean available = true;
	boolean occupied = false;
	
	public Spots(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y, x2, y2);
	}
	
	public void draw() {
		//draw a circle or chair in this spot.
		
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	
	/*
	 * switches whether the spot is available to be used or not
	 */
	public void makeAvailable() {
		available = true;
	}
	
	public void takeAvailable() {
		available = false;
	}
	
	/*
	 * switches whether somebody is in the spot or not
	 */
	void updateOccupancy() {
		occupied = !occupied;
	}
	
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
