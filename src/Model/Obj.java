package Model;

public class Obj extends UIObjects {
	
	public Obj(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y, x2, y2);
	}
	
	public void draw() {
		// draw wither a line or a filled rectangle in this spot.
		
	}
	
	@Override
	public String toString() {
		if(super.getId() < 10) {
			return "   Obj"+ super.toString();
		} else {
			return "  Obj"+ super.toString();
		}
	}
}