package Controller;

import Model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Get commands from the View and controls the Model
 */
public class Controller {
	
	private final Model model;
	private String currentFilePath;
	
	public Controller(Model model) {
		this.model = model;
	}
	
	/**
	 * This adds to the object to the model
	 *
	 * @param type string "wall", "chair", or "table"
	 * @param x    vertical position
	 * @param y    horizontal position
	 * @param x2   the new object's radius
	 * @param y2   the new object's radius
	 */
	public void createNewObject(String type, double x, double y, double x2, double y2) {
		UIObjects newObj = null;
		int ID = model.nextID();
		switch(type) {
			case "wall":
				newObj = new Wall(ID, x, y, x2, y2);
				break;
			case "chair":
				newObj = new Spots(ID, x, y, x2, y2);
				break;
			case "object":
				newObj = new Tables(ID, x, y, x2, y2);
				break;
			default:
				System.out.println("invalid object type");
				return;
		}
		model.addObject(newObj);
	}
	
	/**
	 * "Undo"s the last action done by the user
	 */
	public void undo() {
		try {
			ArrayList<UIObjects> objs = model.undoStack.pop();
			model.redoStack.push(model.cloneItemList());
			model.updateItemList(objs);
		} catch(EmptyStackException ese) {
			System.out.println("Undo stack is empty");
			return;
		}
	}

	/**
	 * "redo"s the last action undone by the user
	 */
	public void redo() {
		try {
			ArrayList<UIObjects> objs = model.redoStack.pop();
			model.undoStack.push(model.cloneItemList());
			model.updateItemList(objs);
		} catch(EmptyStackException ese) {
			System.out.println("Redo stack is empty");
			return;
		}
	}
	
	/**
	 * Deselect all the objects that are currently highlighted
	 */
	public void deselectAll(UIObjects key) {
//		System.out.println("Controller.deselectAll");
		boolean highlightedPresent = false;
		for(UIObjects object : model.getObjects()) {
//			System.out.println("object.getId() = " + object.getId());
				object.setHighlighted(false);
				highlightedPresent = true;
		}
		if(highlightedPresent) {
			displayModel();
		}
	}
	
	/**
	 * @param x1 first vertical position
	 * @param y1 first horizontal position
	 * @param x2 second vertical position
	 * @param y2 second horizontal position
	 * @param ID int ID is the position it holds in the arraylist
	 */
	public void updateCurrentObject(double x1, double y1, double x2, double y2, int ID) {
		model.updateObject(x1, y1, x2, y2, ID);
	}
	
	/**
	 * Gets the object at given location
	 *
	 * @param x vertical position
	 * @param y horizontal position
	 * @return UIObject
	 */
	public UIObjects getObject(double x, double y) {
		return model.getObject(x, y);
	}
	
	/**
	 * Used when the view wants to draw the model
	 */
	public void displayModel() {
		model.display();
	}
	
	/**
	 * @param file the file in which the program will be saved to.
	 */
	public void save(File file) {
		if(file != null) {
			System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
			currentFilePath = file.getPath();
			model.saveState(currentFilePath);
		} else {
			System.out.println("File empty");
		}
	}
	
	/**
	 * Load file at the given file
	 *
	 * @param file File
	 */
	public void load(File file) {
		if(file != null) {
			System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
			currentFilePath = file.getPath();
			model.loadState(currentFilePath);
			model.display();
		} else {
			System.out.println("File empty");
		}
	}
	
	/**
	 * @param ID the spot that you want to update.
	 *           <p>
	 *           when you call this it will either take-away or give occupancy to the seat. It also updates the
	 *           surrounding seats that would be considered risky.
	 */
	public void updateAvailable(int ID) {
		model.updateAvailability(ID);
	}
	
	/**
	 * Tell the model to remove the object with the matching given ID
	 *
	 * @param ID int
	 */
	public void removeObject(int ID) {
		model.removeObject(ID);
	}
	
	/*
	 * removes all the items in the list.
	 */
	public void removeAll() {
		for(int i = 0; i < model.getObjects().size(); i++) {
			model.removeObject(0);
		}
	}
	
	/**
	 * Prints the models items
	 *
	 * @return String
	 */
	public String printItems() {
		return model.printItems();
	}
	
	public int countSpotType(String type) {
		int count = 0;
		for(UIObjects object : model.getObjects()) {
			if(object instanceof Spots) {
				Spots spot = (Spots) object;
				if(type.equals("occupied") && spot.isOccupied()){
					count++;
				} else if(type.equals("unavailable") && !spot.isAvailable()) {
					count++;
				}else if(type.equals("free") && spot.isAvailable() && !spot.isOccupied()) {
					count++;
				}else if(type.equals("total") ) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Undoes the state changes from hosting. If the user wants to return to the construct view.
	 */
	public void resetFromHosting(){
		for(UIObjects object : model.getObjects()) {
			if(object instanceof Spots) {
				Spots current = (Spots)object;
				current.setSafety(false);
				current.setOccupancy(false);
				current.makeAvailable();
			}
		}
	}
	
	/**
	 * Remove all the objects in the model that are currently highlighted
	 */
	public void removeHighlighted() {
		ArrayList<UIObjects> newList = new ArrayList<>(); // list to remove
		for(UIObjects object : model.getObjects())
			if(!object.isHighlighted())
				newList.add(object);
		model.itemList = newList;
		model.updateIndices();
		model.display();
	}

	/**
	 * gets a list of the model's highlighted objects
	 *
	 */
	public ArrayList<UIObjects> getHighlightedObjects() {
		ArrayList<UIObjects> result = new ArrayList<>();

		// Find highlighted objects
		for(UIObjects o : model.getObjects())
			if(o.isHighlighted())
				result.add(o);

		return result;
	}
	
	/**
	 * @param x  starting x location
	 * @param y  starting y location
	 * @param x2 bottom left corner of area selected
	 * @param y2 bottom right corner of area selected
	 *           <p>
	 *           takes an area that the user selects and removes all the elements in that selected area.
	 */
	public void removeSelected(double x, double y, double x2, double y2) {
		int buffer = 0;
		int size = model.getObjects().size();
		//cycle through the IDs and if the object falls in the area delete the object.
		for(int i = 0; i < size; i++) {
			System.out.println("i = " + i);
			if(model.getObject(i - buffer).getX() >= x
					&& model.getObject(i - buffer).getX() <= x2
					&& model.getObject(i - buffer).getY() >= y
					&& model.getObject(i - buffer).getY() <= y2) {
				
				model.removeObject(i - buffer);
				System.out.println("removed object with ID: " + (i - buffer));
				buffer++;
			}
			model.display();
		}
	}
	
	/**
	 * Highlight the UIObjects that are within the given rectangle.
	 *
	 * @param x  starting x location
	 * @param y  starting y location
	 * @param x2 bottom left corner of area selected
	 * @param y2 bottom right corner of area selected
	 */
	public void highlightSelected(double x, double y, double x2, double y2) {
		int buffer = 0;
		int size = model.getObjects().size();
		//cycle through the IDs and if the object falls in the area, highlight the object.
		for(int i = 0; i < size; i++) {
			if(model.getObject(i).getX() >= x
					&& model.getObject(i).getX() <= x2
					&& model.getObject(i).getY() >= y
					&& model.getObject(i).getY() <= y2) {
				
				model.getObject(i).setHighlighted(true);
//				System.out.println("highlighted object with ID: " + (i));
				buffer++;
			}
			model.display();
		}
	}
	
	/**
	 * Get all the objects in the model
	 *
	 * @return list of UIObjects
	 */
	public ArrayList<UIObjects> getObjects() {
		return model.getObjects();
	}
	
	/**
	 * This returns a spot and sets that spot as highlighted.
	 *
	 * @return the spot object that is considered most safe.
	 */
	public Spots getBestSpot() {
		int id[] = model.bestSpot(0);
//		if (id == -1) {
//			return null;
//		}
		Spots spot = (Spots) model.getObject(id[0]); //get the spot
		spot.setSafety(true);
		return spot;
	}

	/**
	 * Marks spot at x and y as occupied.
	 * @param x
	 * @param y
	 */
	public void occupySpot(double x, double y) {
		UIObjects uio = getObject(x,y);
		updateAvailable(uio.getId());
	}
	
	/**
	 * updates the dimensions of all objects in the given list
	 * with the given new dimensions.
	 *
	 * @param objs      is a list of objects to be updated
	 * @param newWidth  is the new width to be assigned
	 * @param newHeight is the new height to be assigned
	 */
	public void resizeAll(ArrayList<UIObjects> objs, double newWidth, double newHeight) {
		// Do nothing if the list is empty
		if(objs.isEmpty())
			return;

		model.updateAll(objs, newWidth, newHeight);
  }
	
	/**
	 * this is the replacement for updateAvailable
	 *
	 * @param ID int
	 * @return risk if a user clicks a spot that is considered risky it will return true.
	 */
	boolean updateSpot(int ID) {
		boolean risk = false;
		risk = model.giveGetSeat(ID);
		return risk;
	}
}

