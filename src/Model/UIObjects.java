package Model;

import java.io.Serializable;
import java.lang.Cloneable;

/*
 * main class for all objects
 * param: the ID of the object to keep track of and the initial coordinates of the shapes
 */
public abstract class UIObjects implements Serializable, Cloneable {
	
	//initial starting points of the object we can assume 0,0 or wherever we want them to exist
	private static final long serialVersionUID = 1L;
	boolean isHighlighted;
	boolean isSafe;
	double x, x2;
	double y, y2;
	int ID;
	
	/**
	 * UIObjects constructor
	 *
	 * @param ID integer
	 * @param x  double
	 * @param y  double
	 * @param x2 double
	 * @param y2 double
	 */
	public UIObjects(int ID, double x, double y, double x2, double y2) {
		isHighlighted = false;
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
		this.ID = ID;
	}
	
	/**
	 * when the shape is moved or altered the x and y values will be altered for storage
	 *
	 * @param x  double
	 * @param y  double
	 * @param x2 double
	 * @param y2 double
	 */
	public void update(double x, double y, double x2, double y2) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/**
	 * Sets the value of isHighlighted to the given value
	 *
	 * @param highlight boolean Whether or not this UIObject is highlighted
	 */
	public void setHighlighted(boolean highlight) {
		isHighlighted = highlight;
	}
	
	public void setSafety(boolean safety) {
		isSafe = safety;
	}
	
	public boolean isSafe(){
		return isSafe;
	}
	
	public boolean isHighlighted(){
		return isHighlighted;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getX2() {
		return x2;
	}
	
	public double getY2() {
		return y2;
	}
	
	public double getWidth() {
		return Math.abs(x2 - x);
	}
	
	public double getHeight() {
		return Math.abs(y2 - y);
	}
	
	public void setID(int id) {
		this.ID = id;
	}
	
	public int getId() {
		return ID;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public String toString() {
		String highlightStr = "F";
		if(isHighlighted) {
			highlightStr = "T";
		}
		
		return String.format("<%d>(%s)  [%.2f, %.2f, %.2f, %.2f]", ID, highlightStr, x, y, x2, y2);
	}
}
