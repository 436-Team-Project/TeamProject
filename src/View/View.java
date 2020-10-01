package View;

import Controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
		
		placeWall.setOnMouseClicked(event -> {
			System.out.println("Place Wall button clicked");
			// TODO: Notify controller that user wants to place a wall
		});
		
		placeChair.setOnMouseClicked(event -> {
			System.out.println("Place Chair button clicked");
			// TODO: Notify controller that user wants to place a chair
		});
		
		placeObject.setOnMouseClicked(event -> {
			System.out.println("Place Object button clicked");
			// TODO: Notify controller that user wants to place a object
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
}
