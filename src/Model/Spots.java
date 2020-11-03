package Model;

/**
 * Spot class
 */
public class Spots extends UIObjects{
	
	boolean available = true;
	boolean occupied = false;
	
	public Spots(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y, x2, y2);
	}
	
	
	public void draw() {
		//draw a circle or chair in this spot.
		
	}
	
	 /*
	  * switches whether the spot is available to be used or not
	  */
	void makeAvailable() {
		available = true;
	}
	
	void takeAvailable() {
		available = false;
	}
	/*
	void updateAvailability() {
		available=!available;
	}
	*/
	
	/*
	 * switches whether somebody is in the spot or not
	 */
	void updateOccupancy() {
		occupied =! occupied;
	}
	
	@Override
	public String toString() {
		return "ID: " + this.ID + "type: chair";
	}
	
}
