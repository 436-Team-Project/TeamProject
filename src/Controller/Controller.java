package Controller;

import java.util.ArrayList;

import Model.*;
import View.View;

/**
 * Get commands from the View and controls the Model
 */
public class Controller {
	
	private final Model model;
	
	public Controller(Model model) {
		this.model = model;
	}
	
	/**
	 * This adds to the object to the model
	 *
	 * @param type   string "wall", "chair", or "table"
	 * @param x      vertical position
	 * @param y      horizontal position
	 * @param width  the new object's radius
	 * @param height the new object's radius
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
		model.removeLastObject();
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
	public void displayModel(){
		model.display();
	}
	
	
	public void save() {
		model.saveState();
	}
	
	public void load() {
		model.loadState();
	}
	
	public ArrayList<UIObjects> getObjects(){
		return model.getObjects();
	}
	
	void updateAvailable(int ID){
		model.updateAvailability(ID);
	}
	
	void removeObject(int ID) {
		model.removeObject(ID);
	}
	
	/*
	 * removes all the items in the list.
	 */
	void removeAll() {
		for (int i = 0; i < model.getObjects().size(); i++) {
			model.removeObject(0);
		}
	}
	
	/**
	 * 
	 * @param x starting x location
	 * @param y starting y location
	 * @param x2 bottom right corner of area selected
	 * @param y2 bottom right corner of area selected
	 * 
	 * takes an area that the user selects and removes all the elements in that selected area.
	 */
	void removeSelected(int x, int y, int x2, int y2) {
		int buffer = 0;
		
		//cycle through the IDs and if the object falls in the area delete the object.
		for (int i = 0; i < model.getObjects().size(); i++) {
			if (model.getObject(i-buffer).getX() >= x 
					&& model.getObject(i-buffer).getX() <= x2 
					&& model.getObject(i-buffer).getY() >= y 
					&& model.getObject(i-buffer).getY() <= y2) {
				
				model.removeObject(i-buffer);
				buffer++;
			}
		}
		
	}
}

