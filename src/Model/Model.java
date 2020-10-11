package Model;

import java.util.ArrayList;
import java.util.Observable;

/**
 * The state of the application
 */
public class Model extends Observable {
	ArrayList<UIObjects> itemList = new ArrayList<UIObjects>();
	
	/**
	 * Constructor
	 */
	public Model() {
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Template method (ignore)
	 */
	public void addObject(UIObjects newObj) {
		itemList.add(newObj);
		setChanged();
		notifyObservers();
		System.out.format("new object added. %d items exist", itemList.size());
	}
	
	public void updateObject(int x, int y, int ID) {
		System.out.println("updated");
	}

	public ArrayList<UIObjects> getObjects() {
		return itemList;
	}
	
}
