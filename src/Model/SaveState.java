package Model;

import java.io.*;
import java.util.ArrayList;

/**
 * Provided the ability to save
 */
public class SaveState implements Serializable {
	
	private static final long serialVersionUID = 1L;
	ArrayList<UIObjects> itemList = new ArrayList<UIObjects>();
	
	/**
	 * SaveState constructor
	 *
	 * @param items List of UIObjects from the model
	 */
	public SaveState(ArrayList<UIObjects> items) {
		this.itemList = items;
	}
}
