package Controller;

import Model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.EmptyStackException;

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
	public UIObjects getObject(int x, int y) {
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
	
	public ArrayList<UIObjects> getObjects() {
		return model.getObjects();
	}
	
	/**
	 * @param ID the spot that you want to update.
	 * 
	 * when you call this it will either take-away or give occupancy to the seat. It also updates the
	 * surrounding seats that would be considered risky.
	 */
	public void updateAvailable(int ID) {
		model.updateAvailability(ID);
	}
	
//-----------------------------------------------------------------------------------	
	/**
	 * this is the replacement for updateAvailable
	 * @param ID
	 * @return risk if a user clicks a spot that is considered risky it will return true.
	 */
	boolean updateSpot(int ID) {
		boolean risk =  false;
		risk =  model.giveGetSeat(ID);
		return risk;
	}
//-----------------------------------------------------------------------------------	
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
	 * @param x  starting x location
	 * @param y  starting y location
	 * @param x2 bottom right corner of area selected
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
				System.out.println("removed object with ID: "+ (i-buffer));
				buffer++;
			}
			model.display();
		}
	}
	
	
	/**
	 * This just returns the spot. ***IT DOES NOT SET THE SPOT OR UPDATE SURROUNDING SPOTS*** 
	 * if that is desired then it can be updated to do so
	 * 
	 * @return the spot object that is considered most safe. 
	 */
	public Spots getBestSpot(){
		int id = model.bestSpot(0);
		Spots spot = (Spots) model.getObject(id); //get the spot
		return spot;
	}


	/**
	 * updates a given object's dimensions with the given new 
	 * dimensions.
	 *
	 * @param obj       is the object to be updated
	 * @param newWidth  is the new width to be assigned
	 * @param newHeight is the new height to be assigned
	 */
	public void resize(UIObjects obj, double newWidth, double newHeight) {
		double dw, dh;			// delta width, delta height
		double x1, y1, x2, y2;	// new endpoints

		dw = newWidth - obj.getWidth();		// change in width
		dh = newHeight - obj.getHeight();	// change in height

		// This preserves the center of the object
		// (i.e. the addition/subtraction of area is distributed to all sides)
		if(obj.getX() < obj.getX2()) {
			x1 = obj.getX() - (dw/2);
			x2 = obj.getX2() + (dw/2);
		} else {
			x1 = obj.getX() + (dw/2);
			x2 = obj.getX2() - (dw/2);
		}

		if(obj.getY() < obj.getY2()) {
			y1 = obj.getY() - (dh/2);
			y2 = obj.getY2() + (dh/2);
		} else {
			y1 = obj.getY() + (dh/2);
			y2 = obj.getY2() - (dh/2);
		}
		updateCurrentObject(x1, y1, x2, y2, obj.getId());
	}
}

