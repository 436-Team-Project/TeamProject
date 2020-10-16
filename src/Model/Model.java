package Model;

import java.util.ArrayList;
import java.util.Observable;

/**
 * The state of the application
 */

public class Model extends Observable {
	public ArrayList<UIObjects> itemList = new ArrayList<UIObjects>();
	UIObjects lastObject;
	
	private final int SIZE = 20;
	
	/**
	 * Constructor
	 */
	public Model() {
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Creates an object of given type and at the given location
	 *
	 * @param type string "wall", "chair", or "table"
	 * @param x      vertical position
	 * @param y      horizontal position
	 */
	public void createObject(String type, int x, int y) {
		//create the object that is asked for.
		if(type.equals("wall")) {
			Wall obj = new Wall(x, y, x + SIZE, y + SIZE, itemList.size());
			itemList.add(obj);
		} else if(type.equals("chair")) {
			Spots obj = new Spots(x, y, x + SIZE, y + SIZE, itemList.size());
			itemList.add(obj);
		} else {
			Tables obj = new Tables(x, y, x + SIZE, y + SIZE, itemList.size());
			itemList.add(obj);
		}
		setChanged();
		notifyObservers();
		System.out.format("new object added. %d items exist\n", itemList.size());
	}
	
	/**
	 * Updates object at given location
	 *
	 * @param x1      first vertical position
	 * @param y1      first horizontal position
	 * @param x2      second vertical position
	 * @param y2      second horizontal position
	 * @param ID int ID is the position it holds in the arraylist
	 */
	public void updateObject(double x1, double y1, double x2, double y2, int ID) {
		UIObjects obj = itemList.get(ID);
		obj.update(x1, y1, x2, y2);
		setChanged();
		notifyObservers();
		System.out.println("updated");
	}
	
	/**
	 * Adds a UIObject
	 *
	 * @param newObj UIObjects new object to add to the item list
	 */
	public void addObject(UIObjects newObj) {
		lastObject = newObj;
		itemList.add(newObj);
		setChanged();
		notifyObservers();
		System.out.format("new object added. %d items exist\n", itemList.size());
	}
	
	/**
	 * Removes the last object
	 *
	 * This method is used to accomplish the "undo" feature
	 */
	public void removeLastObject() {
		itemList.remove(lastObject);
		if(itemList.size() != 0) {
			lastObject = itemList.get(itemList.size() - 1);
		}
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns a single object in the list based on if the item is on the clicked area. If not it
	 * returns null.
	 *
	 * @param x vertical position
	 * @param y horizontal position
	 * @return UIObject
	 */
	public UIObjects getObject(int x, int y) {
		UIObjects obj = null;
		for(UIObjects items : itemList) {
			if(items.x <= x && items.y <= y && items.x2 >= x && items.y2 >= y) {
				obj = items;
				break;
			}
		}
		return obj;
	}
	
	/**
	 * Returns the next object's identification
	 *
	 * @return int
	 */
	public int nextID() {
		return itemList.size();
	}
	
	/**
	 * Returns the list of the objects
	 *
	 * @return array list of UIObjects
	 */
	public ArrayList<UIObjects> getObjects() {
		System.out.println("returning items");
		return itemList;
	}
}
