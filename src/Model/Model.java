package Model;

import java.util.ArrayList;
import java.util.Observable;

/**
 * The state of the application
 */
public class Model extends Observable {
	public ArrayList<UIObjects> itemList = new ArrayList<UIObjects>();
	
	private final int SIZE = 20;
	
	/**
	 * Model constructor
	 */
	public Model() {
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Adds an object
	 *
	 * @param newObj new UIObject
	 */
	public void addObject(UIObjects newObj) {
		itemList.add(newObj);
		setChanged();
		notifyObservers();
		System.out.format("new object added. %d items exist\n", itemList.size());
	}
	
	/**
	 * Creates object
	 *
	 * @param type String the type of object to create
	 * @param x vertical position
	 * @param y horizontal position
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
	 * Updates object
	 *
	 * @param x vertical position
	 * @param y horizontal position
	 * @param ID id of the object
	 */
	public void updateObject(int x, int y, int ID) {
		setChanged();
		notifyObservers();
		System.out.println("updated");
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
	 * Returns the list of the objects
	 *
	 * @return array list of UIObjects
	 */
	public ArrayList<UIObjects> getObjects() {
		System.out.println("returning items");
		return itemList;
	}
}
