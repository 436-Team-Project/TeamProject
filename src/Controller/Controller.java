package Controller;

import Model.Model;
import View.View;

/**
 * Get commands from the View and controls the Model
 */
public class Controller {
	
	private Model model;
	private View view;
	
	public Controller() {
		model = new Model();
		view = new View();
	}
	/*
	 * param: wall, seat, or table string. this adds to the object to the model
	 */
	public void createNewObject(String type) {
		model.createObject(type);
	}
	/*
	 * param: the x and y coords and the ID.
	 */
	public void updateCurrentObject(int x, int y, int ID) {
		model.updateObject(x, y, ID);
	}
}

