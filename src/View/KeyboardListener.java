package View;

import Controller.Controller;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * This class is used to perform keyboard shortcuts
 *
 * <ul>
 *     <li>undo: CTRL Z</li>
 *     <li>redo: CTRL Y</li>
 *     <li>zoom in: CTRL +</li>
 *     <li>zoom out: CTRL -</li>
 *     <li>zoom reset: CTRL 0</li>
 *     <li>delete: Delete</li>
 * </ul>
 *
 */
public class KeyboardListener {
	
	private final Scene scene;
	private final Pane drawPane; // Drawing Canvas
	private Controller controller;
	
	private boolean SHIFT_PRESSED;
	private boolean CONTROL_PRESSED;
	private boolean ALT_PRESSED;
	
	/**
	 * Keyboard listener constructor
	 *
	 * @param scene scene
	 * @param controller controller
	 * @param drawPane drawPane
	 */
	public KeyboardListener(Scene scene, Controller controller, Pane drawPane) {
		this.scene = scene;
		this.controller = controller;
		this.drawPane = drawPane;
		scene.setOnKeyPressed(this::keyPressed);
		scene.setOnKeyReleased(this::keyReleased);
	}
	
	/**
	 * Determine if the given key code is currently pressed
	 *
	 * @param keyCode keycode
	 * @return boolean
	 */
	public boolean isKeyPressed(KeyCode keyCode) {
		if(keyCode == KeyCode.SHIFT) {
			return SHIFT_PRESSED;
		} else if(keyCode == KeyCode.CONTROL) {
			return CONTROL_PRESSED;
		} else if(keyCode == KeyCode.ALT) {
			return ALT_PRESSED;
		} else {
			System.out.println("Key Unsupported - isKeyPressed()");
			return false;
		}
	}
	
	/**
	 * Sets controller
	 *
	 * @param controller controller
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	/**
	 * This is called each time the user presses a key while the window has the input focus.
	 *
	 * @param keyEvent key event
	 */
	private void keyPressed(KeyEvent keyEvent) {
		KeyCode key = keyEvent.getCode();
		System.out.println("Key Pressed: " + key);
		
		if(key == KeyCode.Z) {
			if(CONTROL_PRESSED) {
				System.out.println("\tUndo");
				controller.undo();
			}
		} else if(key == KeyCode.Y) {
			if(CONTROL_PRESSED) {
				System.out.println("\tRedo");
				controller.redo();
			}
		} else if(key == KeyCode.S) {
			if(CONTROL_PRESSED) {
				System.out.println("\tSave");
				controller.save(View.CURR_FILE);
			}
		} else if(key == KeyCode.DELETE) {
			System.out.println("\tDelete");
			controller.removeHighlighted();
		} else if(key == KeyCode.EQUALS) {
			if(CONTROL_PRESSED) {
				System.out.println("\tZoom In");
				drawPane.setScaleX(drawPane.getScaleX() * 1.1);
				drawPane.setScaleY(drawPane.getScaleY() * 1.1);
			}
		} else if(key == KeyCode.MINUS) {
			if(CONTROL_PRESSED) {
				System.out.println("\tZoom Out");
				drawPane.setScaleX(drawPane.getScaleX() / 1.1);
				drawPane.setScaleY(drawPane.getScaleY() / 1.1);
			}
		} else if(key == KeyCode.DIGIT0) {
			if(CONTROL_PRESSED) {
				System.out.println("\tReset Zoom");
				drawPane.setScaleX(1);
				drawPane.setScaleY(1);
				drawPane.setTranslateX((View.CENTER_WIDTH / 4.0) / 2);
				drawPane.setTranslateY((View.CENTER_HEIGHT / 4.0) / 2);
			}
		} else if(key == KeyCode.SHIFT) {
			SHIFT_PRESSED = true;
		} else if(key == KeyCode.CONTROL) {
			CONTROL_PRESSED = true;
		} else if(key == KeyCode.ALT) {
			ALT_PRESSED = true;
		}
	}
	
	/**
	 * his is called each time the user releases a key while the window has the input focus.
	 *
	 * @param keyEvent key event
	 */
	private void keyReleased(KeyEvent keyEvent) {
		KeyCode key = keyEvent.getCode();
		System.out.println("Key Released: " + key);
		if(key == KeyCode.SHIFT) {
			SHIFT_PRESSED = false;
		} else if(key == KeyCode.CONTROL) {
			CONTROL_PRESSED = false;
		} else if(key == KeyCode.ALT) {
			ALT_PRESSED = false;
		}
	}
}
