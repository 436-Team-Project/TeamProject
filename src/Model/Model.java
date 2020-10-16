package Model;

import java.util.ArrayList;
import java.util.Observable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.*;

/**
 * The state of the application
 *
 * @author Ben Taylor
 */

public class Model extends Observable {
	public ArrayList<UIObjects> itemList = new ArrayList<UIObjects>();
	UIObjects lastObject;
	private final int SIZE = 20;
	private final int BUFFER = 60;
	
	/**
	 * Constructor class
	 */
	public Model() {
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Creates an object of given type and at the given location
	 *
	 * @param type string "wall", "chair", or "table"
	 * @param x    vertical position
	 * @param y    horizontal position
	 */
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
	
	/**
	 * Updates object at given location
	 *
	 * @param x1 first vertical position
	 * @param y1 first horizontal position
	 * @param x2 second vertical position
	 * @param y2 second horizontal position
	 * @param ID int ID is the position it holds in the arraylist
	 */
	public void updateObject(double x1, double y1, double x2, double y2, int ID) {
		UIObjects obj=itemList.get(ID);
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
	 * <p>
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
		UIObjects obj=null;
		for (UIObjects items : itemList) {
			if (items.x<=x && items.y<=y && items.x2>=x && items.y2>=y) {
				obj=items;
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
	 * checks the availability of every spot update which seats can be taken again or
	 * which seats are too close to another customer to be taken.
	 *
	 * @param ID the id of the object
	 */
	public void takeCalculator(int ID) {
		Spots nSpot = (Spots) itemList.get(ID);
		//loop through every item. If that item is a spot then continue
		for(UIObjects items : itemList) {
			if(items instanceof Spots) {
				Spots spot = (Spots) items;
				
				//if it is the same spot then skip calulation on this spot
				if(nSpot.ID == spot.ID) {
					continue;
				}
				
				//distance formula (x2-x1)^2+(y2-y1)^2=z^2
				else {
					double distance = Math.sqrt((nSpot.x - spot.x) + (nSpot.y - spot.y));
					//if one is but not the other. prevents possible case for cluster
					//or group sitting at the same table
					if(!spot.occupied) {
						if(distance <= BUFFER) {
							//we are fine if it was already unavailable and we take again
							spot.takeAvailable();
							itemList.set(spot.ID, spot);
						}
					}
				}
			}
		}
	}
	
	/**
	 * unlike take we need to make give recursive. this is because unlike take all surrounding
	 * spots need to be clear instead of just taking if 1 is within range.
	 *
	 * @param ID
	 * @param element
	 * @return avail true or false of if the give availability permission is allowed
	 */
	public boolean giveCalculator(int ID, int element) {
		Spots nSpot = (Spots) itemList.get(ID);
		//default to true since we are changing if false
		boolean avail = true;
		
		//base case on the last element of the list.
		if(element == itemList.size()) {
			return true;
		}
		
		//recursive case
		if(itemList.get(element) instanceof Spots) {
			Spots spot = (Spots) itemList.get(element);
			//distance formula (x2-x1)^2+(y2-y1)^2=z^2
			double distance = Math.sqrt((nSpot.x - spot.x) + (nSpot.y - spot.y));
			//if one is but not the other. prevents possible case for cluster
			//or group sitting at the same table
			if(!spot.occupied) {
				return (distance <= BUFFER && giveCalculator(ID, element++));
			}
		}
		
		return (true && giveCalculator(ID, element++));
	}
	
	/**
	 * based on the action it changes the occupancy status of the spot and then updates
	 * the spots around it
	 *
	 * @param ID updates the availability of the spot
	 */
	public void updateAvailability(int ID) {
		if(itemList.get(ID) instanceof Spots) {
			Spots spot = (Spots) itemList.get(ID);
			if(!spot.occupied && spot.available) {
				spot.takeAvailable();
				spot.updateOccupancy();
				takeCalculator(ID);
				itemList.set(spot.ID, spot);
			} else if(spot.occupied) {
				spot.updateOccupancy();
				//need to loop through all elements to give back availability
				for(int i = 0; i < itemList.size(); i++) {
					if(itemList.get(i) instanceof Spots) {
						boolean update = giveCalculator(i, 0);
						//update the availability of the item
						if(update) {
							Spots uSpot = (Spots) itemList.get(i);
							uSpot.makeAvailable();
							itemList.set(i, uSpot);
						}
					}
				}
			}
		}
	}
	
	/**
	 * serializes the itemList into a file named layoutData
	 */
	public void saveState() {
		try {
			FileOutputStream fileout = new FileOutputStream("layoutData");
			ObjectOutputStream objectout = new ObjectOutputStream(fileout);
			objectout.writeObject(itemList);
			objectout.close();
			fileout.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		System.out.println("finished storing");
	}
	
	/**
	 * loads a previously saved state.
	 */
	@SuppressWarnings("unchecked")
	public void loadState() {
		try {
			FileInputStream filein = new FileInputStream("layoutData");
			ObjectInputStream objin = new ObjectInputStream(filein);
			
			itemList = (ArrayList<UIObjects>) objin.readObject();
			
			objin.close();
			filein.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException c) {
			System.out.println("class not found.");
			c.printStackTrace();
		}
		
		System.out.println("loaded list");
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
