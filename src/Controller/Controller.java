package Controller;

import Model.*;

import java.io.File;
import java.util.ArrayList;

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
	 * @param type   string "wall", "chair", or "table"
	 * @param x      vertical position
	 * @param y      horizontal position
	 * @param x2  the new object's radius
	 * @param y2 the new object's radius
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
	
	/**
	 * Saves a file at the current file path
	 *
	 * @param file File
	 */
	public void save(File file) {
		if (file != null) {
			System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
			currentFilePath = file.getPath();
			model.saveState(currentFilePath);
		} else {
			System.out.println("File empty");
		}
	}
	
	/**
	 * Loads the file at the given file path
	 *
	 * @param file File
	 */
	public void load(File file) {
		if (file != null) {
			System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
			currentFilePath = file.getPath();
			model.loadState(currentFilePath);
			model.display();
		} else {
			System.out.println("File empty");
		}
		
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
}

