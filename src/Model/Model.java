package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * The state of the application
 *
 * @author Ben Taylor
 */

public class Model extends Observable {
	private final int SIZE = 20;
	private final int BUFFER = 60;
	public ArrayList<UIObjects> itemList;
	public UIObjects lastObject;
	
	/**
	 * Constructor class
	 */
	public Model() {
		itemList = new ArrayList<>();
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
	public void createObject(String type, int x, int y) {
		UIObjects obj;
		//create the object that is asked for.
		if(type.equals("wall")) {
			obj = new Wall(itemList.size(), x, y, x + SIZE, y + SIZE);
		} else if(type.equals("chair")) {
			obj = new Spots(itemList.size(), x, y, x + SIZE, y + SIZE);
		} else {
			obj = new Tables(itemList.size(), x, y, x + SIZE, y + SIZE);
		}
		lastObject = obj;
		itemList.add(obj);
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
	public UIObjects getObject(double x, double y) {
		UIObjects obj = null;
		for(UIObjects item : itemList) {
			if(item.x <= x && item.y <= y && item.x2 >= x && item.y2 >= y) {
				obj = item;
				break;
			}
		}
		return obj;
	}
	
	/**
	 * Returns a single object in the list based on if the item is on the clicked area. If not it
	 * returns null.
	 *
	 * @param ID identification
	 * @return UIObject
	 */
	public UIObjects getObject(int ID) {
		return itemList.get(ID);
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
	 * Used when the view wants to draw the model again
	 */
	public void display() {
		setChanged();
		notifyObservers();
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
				
				//if it is the same spot then skip calculation on this spot
				if(nSpot.ID == spot.ID) {
					continue;
				}
				
				// distance formula (x2 - x1)^2 + (y2 - y1)^2 = z^2
				else {
					double distance = Math.sqrt((nSpot.x - spot.x) + (nSpot.y - spot.y));
					//if one is but not the other. prevents possible case for cluster
					//or group sitting at the same table
					if(!spot.occupied) {
						if(distance <= BUFFER) {
							//we are fine if it was already unavailable and we take again
							spot.takeAvailable();
							itemList.set(spot.ID, spot);
							
							setChanged();
							notifyObservers();
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
			
			//if spot is available take it.
			if(!spot.occupied && spot.available) {
				spot.takeAvailable();
				spot.updateOccupancy();
				takeCalculator(ID);
				itemList.set(spot.ID, spot);
			}
			
			//if spot is taken remove occupancy
			else if(spot.occupied) {
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
		setChanged();
		notifyObservers();
	}
	
	/**
	 * based on the action it changes the occupancy status of the spot and then updates
	 * the spots around it
	 *
	 * @param ID updates the availability of the spot
	 */
	public boolean giveGetSeat(int ID) {
		
		boolean risk = false;
		if(itemList.get(ID) instanceof Spots) {
			Spots spot = (Spots) itemList.get(ID);
			
			//if spot is available take it.
			if(!spot.occupied) {
				spot.takeAvailable();
				spot.updateOccupancy();
				takeCalculator(ID);
				itemList.set(spot.ID, spot);
				
				//if the spot is considered risky return risk = true
				if(!spot.available) {
					risk = true;
				}
			}
			
			//if spot is taken remove occupancy
			else if(spot.occupied) {
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
		setChanged();
		notifyObservers();
		return risk;
	}
	
	/**
	 * serializes the itemList into a file named layoutData
	 */
	public void saveState(String filePath) {
		try {
			FileOutputStream fileout = new FileOutputStream(filePath);
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
	public void loadState(String filePath) {
		try {
			FileInputStream filein = new FileInputStream(filePath);
			ObjectInputStream objin = new ObjectInputStream(filein);
			
			itemList = (ArrayList<UIObjects>) objin.readObject();
			
			System.out.println("Loading...");
			for(UIObjects uiObject : itemList) {
				System.out.println(uiObject.toString());
			}
			
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
	
	//shift the ID's and then remove the object from itemList
	public void removeObject(int ID) {
		
		int size = itemList.size();
		for(int i = ID; i < size - 1; i++) {
			itemList.get(i + 1).ID = itemList.get(i).ID;
		}
		itemList.remove(ID);
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Searches through all of the items if it is a spot that isn't occupied or in the hazard range then
	 * search check the impact of that spot on the surrounding spots.
	 * Return the spot with the least amount of impact on surrounding spots.
	 *
	 * @param i int
	 * @return int
	 */
	public int bestSpot(int i) {
		int cur;
		//base case
		if(i == itemList.size()) {
			return -1;
		}
		//rest is recursive case
		ArrayList<UIObjects> checker = new ArrayList<>(itemList);
		cur = numSpotsNear(checker, i);
		int next = bestSpot(i + 1);
		
		//return either the next or the current based on what is larger
		return (cur >= next) ? i : i + 1;
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
	
	/**
	 * helper function for bestSpot returns the number of spots within the range of the passed in spot.
	 *
	 * @param checker the list of objects
	 * @return spots the number of spots
	 */
	int numSpotsNear(ArrayList<UIObjects> checker, int ID) {
		int spots = 0;
		
		//not a spot or spot is occupied or unsafe object return
		if(!(checker.get(ID) instanceof Spots)) {
			return -1;
		}
		Spots temp = (Spots) checker.get(ID);
		if(!temp.available || temp.occupied)
			return -1; //checks if it is a valid spot to sit somebody at
		
		for(int i = 0; i < checker.size(); i++) {
			
			//make sure it is comparing spots to spots
			if(!(checker.get(ID) instanceof Spots)) {
				if(i == ID)
					; //skip this iteration
				else {
					//distance equation
					if(Math.sqrt(Math.pow(checker.get(i).x - checker.get(ID).x, 2.0)
							+ Math.pow(checker.get(i).y - checker.get(ID).y, 2.0))
							<= BUFFER) {
						spots++;
					}
				}
			}
		}
		return spots;
	}
	
	/**
	 * Updates the indices of the items. This is helpful when items are deleted and their old id is
	 * greater than the current size of the item list
	 */
	public void updateIndices(){
		for(int i = 0; i < itemList.size(); i++) {
			itemList.get(i).setID(i);
		}
	}
	
	/**
	 * Displays the item lists
	 *
	 * @return String
	 */
	public String printItems(){
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < itemList.size(); i++) {
			String line = String.format("[%02d]: %s\n", i, itemList.get(i));
			result.append(line);
		}
		result.append("\n");
		return result.toString();
	}
}
