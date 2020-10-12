package Model;


public class Spots extends UIObjects{
	
	boolean available= true;
	boolean occupied=false;
	
	public Spots(int ID, int x, int y, int x2, int y2) {
		super(ID, x, y, x2, y2);
		// TODO Auto-generated constructor stub
	}
	
	
	public void draw() {
		//draw a circle or chair in this spot.
		
	}
	 /*
	  * switches whether the spot is available to be used or not
	  */
	void updateAvailability() {
		available=!available;
	}
	
	/*
	 * switches whether somebody is in the spot or not
	 */
	void updateOccupancy() {
		occupied=!occupied;
	}
	
}
