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
		switch(type)
		{
			case "wall":
				newObj = new Wall(model.getNewId(), x, y, x2, y2);
				break;
			case "chair":
				//newObj = new Chair();
				break;
			case "object":
				newObj = new Obj(model.getNewId(), x, y, x2, y2);
				break;
			default:
				System.out.println("invalid object type");
				return;
		}
		model.addObject(newObj);
	}


	/*
	 * param: the x and y coords and the ID.
	 */
	public void updateObject(int id, double x1, double y1, double x2, double y2) {
		model.updateObject(id, x1, y1, x2, y2);
	}

	
	public UIObjects getObject(int x, int y) {
		return model.getObject(x,y);
	}
}

