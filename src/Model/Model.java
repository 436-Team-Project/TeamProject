package Model;

import java.util.Observable;

/**
 * The state of the application
 */
public class Model extends Observable {
	
	/**
	 * Constructor
	 */
	public Model() {
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Template method (ignore)
	 */
	public void doSomething(){
		setChanged();
		notifyObservers();
	}
	
}
