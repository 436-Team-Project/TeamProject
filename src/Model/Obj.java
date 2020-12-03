package Model;

/**
 * The object class
 */
public class Obj extends UIObjects {
	
	/**
	 * The place holder object class
	 *
	 * @param ID the ID of the object to keep track of and the initial coordinates of the shapes
	 * @param x  double
	 * @param y  double
	 * @param x2 double
	 * @param y2 double
	 */
	public Obj(int ID, double x, double y, double x2, double y2) {
		super(ID, x, y, x2, y2);
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