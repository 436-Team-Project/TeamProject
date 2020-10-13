package Model;

import java.util.ArrayList;
import java.util.Observable;

/**
 * The state of the application
 */

public class Model extends Observable {
	public ArrayList<UIObjects> itemList = new ArrayList<UIObjects>();
	private int SIZE=20;
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
		System.out.format("new object added. %d items exist\n", itemList.size());
}

 
	public void createObject(String type, int x, int y){
		//create the object that is asked for.
		if (type.equals("wall")) {
			Wall obj = new Wall(x,y,x+SIZE,y+SIZE,itemList.size());
			itemList.add(obj);
		}
		
		else if(type.equals("chair")) {
			Spots obj = new Spots(x,y,x+SIZE,y+SIZE,itemList.size());
			itemList.add(obj);
		}
		
		else {
			Tables obj = new Tables(x,y,x+SIZE,y+SIZE,itemList.size());
			itemList.add(obj);
		}
		setChanged();
		notifyObservers();
		System.out.format("new object added. %d items exist\n", itemList.size());
	}
	
	public void updateObject(double x, double y, double x2, double y2, int ID) {
		UIObjects obj=itemList.get(ID);
		obj.update(x, y, x2, y2);
		setChanged();
		notifyObservers();
		System.out.println("updated");
	}
 
 
	/*
	 * returns the list of the objects
	 */
	public ArrayList<UIObjects> getObjects(){
		System.out.println("returning items");
		return itemList;
	}
	/*
	 * returns a single object in the list based on if the item is on the clicked area. if not it returns null.
	 */
	public UIObjects getObject(int x, int y) {
		UIObjects obj=null;
		for (UIObjects items : itemList) {
			if (items.x<=x && items.y<=y && items.x2>=x && items.y2>=y) {
				obj=items;
				break;
			}
		}
		return obj;
		
	}
	
	public int nextID() {
		return itemList.size();
	}
	
}
