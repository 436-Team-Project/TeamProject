package Controller;

import Model.*;
import View.View;

/**
 * Get commands from the View and controls the Model
 */
public class Controller {
	
	private Model model;
	
	/**
	 * Controller constructor
	 *
	 * @param model current state of the model
	 */
	public Controller(Model model) {
		this.model = model;
	}
	
	/**
	 * Param: "wall, seat, or table" string. this adds to the object to the model
	 *
	 * @param type "wall, seat, or table" string
	 * @param x vertical position
	 * @param y horizontal position
	 * @param width the new object's width
	 * @param height the new object's height
	 */
	public void createNewObject(String type, double x, double y, double width, double height) {
		UIObjects newObj = null;
		double x2 = x + width;
		double y2 = y + height;
		switch(type)
		{
			case "wall":
				newObj = new Wall(0, x, y, x2, y2);
				break;
			case "chair":
				//newObj = new Chair();
				break;
			case "object":
//				newObj = new Obj(1, x, y, x2, y2);
				break;
			default:
				System.out.println("invalid object type");
				return;
		}
		model.addObject(newObj);
	}
	
	/**
	 * Updates the UI object's coordinates with the given arguments
	 *
	 * @param x vertical position
	 * @param y horizontal position
	 * @param ID UI object's ID
	 */
	public void updateCurrentObject(int x, int y, int ID) {
		model.updateObject(x, y, ID);
	}
	
	/**
	 * Gets the UI object at the given coordinate
	 *
	 * @param x vertical position
	 * @param y horizontal position
	 * @return UI object
	 */
	public UIObjects getObject(int x, int y) {
		return model.getObject(x,y);
	}
}

