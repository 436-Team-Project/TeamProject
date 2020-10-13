package Controller;

import Model.*;
import View.View;

/**
 * Get commands from the View and controls the Model
 */
public class Controller {
	
	private Model model;
	
	public Controller(Model model) {
		this.model = model;
	}


	/*
	 * param: "wall, seat, or table" string. this adds to the object to the model
	 */
	public void createNewObject(String type, double x, double y, double width, double height) {
		UIObjects newObj = null;
		double x2 = x + width;
		double y2 = y + height;
		int ID = model.nextID();
		switch(type)
		{
			case "wall":
				newObj = new Wall(ID, x, y, x2, y2);
				break;
			case "chair":
				newObj = new Spots(ID,x,y,x2,y2);
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


	/*
	 * param: the x and y coords and the ID the ID is the position it holds in the arraylist.
	 */
	public void updateCurrentObject(int x, int y, int ID) {
		model.updateObject(x, y, ID);
	}
	
	public UIObjects getObject(int x, int y) {
		return model.getObject(x,y);
	}
}

