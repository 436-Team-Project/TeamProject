package Model;

/**
 * Main class for all objects
 *
 */
public abstract class UIObjects {
	//initial starting points of the object we can assume 0,0 or wherever we want them to exist
	double x, x2;
	double y, y2;
	int ID;
	
	/**
	 * Constructor of UI objects
	 *
	 * @param ID the ID of the object to keep track of
	 * @param x initial vertical position
	 * @param y initial horizontal position
	 * @param x2 initial vertical position
	 * @param y2 initial horizontal position
	 */
	public UIObjects(int ID, double x, double y, double x2, double y2) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
		this.ID = ID;
	}
	
	
	/**
	 * abstract classes that will need to be used. Things can be added or removed based on needs
	 */
	public abstract void draw();
	
	/**
	 * when the shape is moved or altered the x and y values will be altered for storage
	 *
	 * @param x vertical position
	 * @param y horizontal position
	 * @param x2 vertical position
	 * @param y2 horizontal position
	 */
	public void update(double x, double y, double x2, double y2) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	/**
	 * Gets width
	 *
	 * @return width
	 */
	public double getWidth() {
		return Math.abs(x2 - x);
	}
	
	/**
	 * Gets height
	 *
	 * @return height
	 */
	public double getHeight() {
		return Math.abs(y2 - y);
	}
}
