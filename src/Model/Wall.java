package Model;

import java.util.ArrayList;

public class Wall extends UIObjects{
	
	ArrayList<Wall> connections=new ArrayList<Wall>();
	
	public Wall(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y,x2,y2);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void draw() {
		// draw wither a line or a filled rectangle in this spot.
		
	}
	
	
}
