import java.util.ArrayList;

public class Tables extends UIObjects{
	
	boolean available = true;
	int numberOfSpots;
	ArrayList<Spots> availableSpots=new ArrayList<Spots>();

	public Tables(int ID, int x, int y, int x2, int y2) {
		super(ID, x, y, x2, y2);
		this.numberOfSpots=4;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
		// draw a square, circle, or table img in this spot
		
	}

}
