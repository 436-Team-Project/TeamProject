package View;

import Controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;

/**
 * Displays what the user sees
 * <p>
 * CuCurrent colors of the panels inside the main border pane are temporary.
 *
 */
public class View extends Application implements Observer {
	
	Controller controller; // Controller of MVC
	
	// The dimensions of the entire application
	final int APP_HEIGHT = 800;
	final int APP_WIDTH = 1200;
	
	// Dimensions for the panels inside the border pane
	final int LEFT_WIDTH = 250;
	final int TOP_HEIGHT = 50;
	final int BOT_HEIGHT = 50;
	final int CENTER_WIDTH = (APP_WIDTH - LEFT_WIDTH);
	final int CENTER_HEIGHT = (APP_HEIGHT - (TOP_HEIGHT+BOT_HEIGHT));
	
	BorderPane root; // Main pane
	
	// Default dimensions for objects created from buttons
	final double WALL_WIDTH = 25;
	final double WALL_HEIGHT = 25;
	final double CHAIR_WIDTH = 25;
	final double CHAIR_HEIGHT = 25;
	
	/**
	 * Initialize
	 */
	@Override
	public void init() {
		controller = new Controller();
	}
	
	/**
	 * Call this once
	 *
	 * @param  primaryStage Stage
	 * @throws Exception ex
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		root = new BorderPane();
		
		root.setLeft(initLeftPanel());
		root.setCenter(initCenterPanel());
		root.setTop(initTopPanel());
		root.setBottom(initBottomPanel());
		
		Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
		primaryStage.setTitle("Covid Calc");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Update the view
	 *
	 * @param observable Observable
	 * @param object Object
	 */
	@Override
	public void update(Observable observable, Object object) {
	
	}
	
	/**
	 * Initializes the left panel in the root border pane
	 * <p>
	 * Places three buttons for now. More will be added later
	 *
	 * @return Pane
	 */
	private Pane initLeftPanel(){
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(110,161,141,1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(LEFT_WIDTH);
		
		VBox vbox = new VBox();
		VBox buttonBox = new VBox();
		
		Label leftPanelHeader = new Label("User Options");
		
		Button placeWall = new Button("Place Wall");
		Button placeChair = new Button("Place Chair");
		Button placeObject = new Button("Place Object"); // Place holder button
		
		Rectangle wallBounds = initObjectBounds(WALL_WIDTH, WALL_HEIGHT);
		Rectangle chairBounds = initObjectBounds(CHAIR_WIDTH, CHAIR_HEIGHT);
		Rectangle objectBounds = initObjectBounds(WALL_WIDTH, WALL_HEIGHT); // Temporarily using default wall dimensions
		
		// --- Event handling "Place Wall" button ---
		placeWall.setOnMousePressed(event -> {
			updateBound(event, wallBounds);
			root.getChildren().add(wallBounds);
		});
			
		placeWall.setOnMouseDragged(event2 -> {
			updateBound(event2, wallBounds);
		});
			
		placeWall.setOnMouseReleased(event3 -> {
			if (!isInCenter(event3.getSceneX(), event3.getSceneY())) {
				System.out.println("Outside of central panel");
			} else {
				System.out.println("Inside of central panel");
				// TODO: Notify controller that user wants to place wall at (mouseX, mouseY) position with WALL_WIDTH and WALL_HEIGHT.
				// controller.addWall(mouseX, mouseY, WALL_WIDTH, WALL_HEIGHT);
			}
			root.getChildren().remove(wallBounds);
		});
			
		// --- Event handling "Place Chair" button ---
		placeChair.setOnMousePressed(event -> {
			updateBound(event, chairBounds);
			root.getChildren().add(chairBounds);
		});
			
		placeChair.setOnMouseDragged(event2 -> {
			updateBound(event2, chairBounds);
		});
			
		placeChair.setOnMouseReleased(event3 -> {
			if (!isInCenter(event3.getSceneX(), event3.getSceneY())) {
				System.out.println("Outside of central panel");
			} else {
				System.out.println("Inside of central panel");
				// TODO: Notify controller that user wants to place chair at (mouseX, mouseY) position with CHAIR_WIDTH and CHAIR_HEIGHT.
				// controller.addChair(mouseX, mouseY, CHAIR_WIDTH, CHAIR_HEIGHT);
			}
			root.getChildren().remove(chairBounds);
		});
		
		// --- Event handling "Place Object" button ---
		placeObject.setOnMousePressed(event -> {
			updateBound(event, objectBounds);
			root.getChildren().add(objectBounds);
		});
			
		placeObject.setOnMouseDragged(event2 -> {
			updateBound(event2, objectBounds);
		});
		
		placeObject.setOnMouseReleased(event3 -> {
			if (!isInCenter(event3.getSceneX(), event3.getSceneY())) {
				System.out.println("Outside of central panel");
			} else {
				System.out.println("Inside of central panel");
				// TODO: Notify controller that user wants to place object at (mouseX, mouseY) position with the default width and height.
				// controller.addObject(mouseX, mouseY, width, height);
			}
			root.getChildren().remove(objectBounds);
		});
		
		buttonBox.getChildren().addAll(placeWall, placeChair, placeObject);
		vbox.getChildren().addAll(leftPanelHeader, buttonBox);
		result.getChildren().add(vbox);
		return result;
	}
	
	/**
	 * Initializes the center panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initCenterPanel(){
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(CENTER_WIDTH);
		result.setPrefHeight(CENTER_HEIGHT);
		return result;
	}
	
	
	/**
	 * Initializes the top panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initTopPanel(){
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(196,153,143,1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefHeight(TOP_HEIGHT);
		
		return result;
	}
	
	/**
	 * Initializes the bottom panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initBottomPanel(){
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(196,153,143,1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefHeight(BOT_HEIGHT);
		
		return result;
	}
	
	/**
	 * Initializes a dashed rectangle representing the bounds of the object being placed.
	 * @param width
	 * @param height
	 * @return
	 */
	private Rectangle initObjectBounds(double width, double height) {
		Rectangle r = new Rectangle();
		r.setWidth(width);
		r.setHeight(height);
		r.setStroke(Color.BLACK);
		r.setStrokeWidth(1);
		r.getStrokeDashArray().addAll(5.0);
		r.setFill(Color.TRANSPARENT);
		
		return r;
	}
	
	/**
	 * Returns true if mouse is in the center panel defined at initialization.
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	private boolean isInCenter(double mouseX, double mouseY) {
		return !(mouseX < LEFT_WIDTH || mouseX > APP_WIDTH || 
				mouseY < TOP_HEIGHT || mouseY > (TOP_HEIGHT + CENTER_HEIGHT));
	}
	
	/**
	 * Updates position of object to mouse's position.
	 * @param event
	 * @param objectBounds
	 */
	private void updateBound(MouseEvent event, Node objectBounds) {
		objectBounds.setTranslateX(event.getSceneX());
		objectBounds.setTranslateY(event.getSceneY());
	}
}
