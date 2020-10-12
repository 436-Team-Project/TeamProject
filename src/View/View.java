package View;

import Model.*;
import Controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Displays what the user sees
 * <p>
 * CuCurrent colors of the panels inside the main border pane are temporary.
 */
public class View extends Application implements Observer {
	
	// The dimensions of the entire application
	final int APP_HEIGHT = 800;
	final int APP_WIDTH = 1200;
	
	// Dimensions for the panels inside the border pane
	final int LEFT_WIDTH = 250;
	final int TOP_HEIGHT = 50;
	final int BOT_HEIGHT = 50;
	final int CENTER_WIDTH = (APP_WIDTH - LEFT_WIDTH);
	final int CENTER_HEIGHT = (APP_HEIGHT - (TOP_HEIGHT + BOT_HEIGHT));
	
	// Default dimensions for objects created from buttons
	final double WALL_WIDTH = 25;
	final double WALL_HEIGHT = 25;
	final double CHAIR_WIDTH = 25;
	final double CHAIR_HEIGHT = 25;
	boolean inDrawPane = false;
	
	Controller controller; // Controller of MVC
	Model model; // model of MVC
	
	BorderPane root; // Main pane
	Pane drawPane;
	
	/**
	 * Initialize
	 */
	@Override
	public void init() {
		//controller = new Controller();
	}
	
	/**
	 * Call this once
	 *
	 * @param primaryStage Stage
	 */
	@Override
	public void start(Stage primaryStage) {
		
		model = new Model();
		model.addObserver(this);
		
		controller = new Controller(model);
		
		root = new BorderPane();
		
		root.setCenter(initCenterPanel());
		root.setLeft(initLeftPanel());
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
	 * @param object     Object
	 */
	@Override
	public void update(Observable observable, Object object) {
		ArrayList<UIObjects> itemList = model.getObjects();        // items to be placed
		
		// TODO - clear central panel
		
		drawPane.getChildren().clear();
		
		// TODO - redraw all items
		
		for(UIObjects obj : itemList) {
			if(obj instanceof Wall) {
				System.out.println("Drawing wall");
				
				Rectangle wall = initObject(
						obj.getX(),
						obj.getY(),
						obj.getWidth(),
						obj.getHeight());
				
				drawPane.getChildren().add(wall);
			}
			/*else if(obj instanceof Chair) {
				System.out.println("Drawing chair");
			}*/
			else {
				System.out.println("Drawing object");
			}
		}
	}
	
	/**
	 * Initializes the left panel in the root border pane
	 * <p>
	 * Places three buttons for now. More will be added later
	 *
	 * @return Pane
	 */
	private Pane initLeftPanel() {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(110, 161, 141, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(LEFT_WIDTH);
		
		VBox vbox = new VBox();
		VBox buttonBox = new VBox();
		
		Label leftPanelHeader = new Label("User Options");

		
		Button placeWall = new Button("Place Wall");
		Button placeChair = new Button("Place Chair");
		Button placeObject = new Button("Place Object"); // Place holder button
		
		leftPanelHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 20px;" +
				"-fx-padding: 10px 50px 20px 50px;");
		buttonBox.setStyle("-fx-alignment: center;-fx-spacing: 5px");
		placeWall.setStyle("-fx-pref-width: 100px; -fx-pref-height: 40px;");
		placeChair.setStyle("-fx-pref-width: 100px; -fx-pref-height: 40px");
		placeObject.setStyle("-fx-pref-width: 100px; -fx-pref-height: 40px");
		
		
		
		Rectangle wallBounds = initObjectBounds(WALL_WIDTH, WALL_HEIGHT);
		Rectangle chairBounds = initObjectBounds(CHAIR_WIDTH, CHAIR_HEIGHT);
		// Temporarily using default wall dimensions
		Rectangle objectBounds = initObjectBounds(WALL_WIDTH, WALL_HEIGHT);
		
		/*TODO:
			- Change the model via controller (addWall, addChair, addObject)
			- Show that the object has been placed in view
			
			--- Add event-handling to allow the placed object to be changed (widgets on sides of
			      wall to allow change in width, height, and rotation)
			--- Change the model via controller (updateWall, updateChair, updateObject)
		 */
		
		// --- Event handling "Place Wall" button ---
		placeWall.setOnMousePressed(event -> {
			updateBound(event, wallBounds);
			root.getChildren().add(wallBounds);
		});
		
		placeWall.setOnMouseDragged(event2 -> { updateBound(event2, wallBounds); });
		
		placeWall.setOnMouseReleased(event3 -> {
			//if (!isInCenter(event3.getSceneX(), event3.getSceneY())) {
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(
					event3.getSceneX() - LEFT_WIDTH, event3.getSceneY() - TOP_HEIGHT, 1, 1);
			
			if(!inDrawPane) {
				System.out.println("Outside of central panel");
			} else {
				// TODO: Notify controller that user wants to place wall at (mouseX, mouseY)
				//  position with WALL_WIDTH and WALL_HEIGHT.
				
				// TODO: might consider user input for width and height
				Point2D p = drawPane.sceneToLocal(event3.getSceneX(), event3.getSceneY());
				controller.createNewObject("wall", p.getX(), p.getY(), WALL_WIDTH, WALL_HEIGHT);
			}
			root.getChildren().remove(wallBounds);
		});
		
		
		// --- Event handling "Place Chair" button ---
		placeChair.setOnMousePressed(event -> {
			updateBound(event, chairBounds);
			root.getChildren().add(chairBounds);
		});
		
		placeChair.setOnMouseDragged(event2 -> { updateBound(event2, chairBounds); });
		
		placeChair.setOnMouseReleased(event3 -> {
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(
					event3.getSceneX() - LEFT_WIDTH, event3.getSceneY() - TOP_HEIGHT, 1, 1);
			
			if(!inDrawPane) {
				System.out.println("Outside of central panel");
			} else {
				System.out.println("Inside of central panel");
				// TODO: Notify controller that user wants to place chair at (mouseX, mouseY)
				//  position with CHAIR_WIDTH and CHAIR_HEIGHT.
				
				// controller.addChair(mouseX, mouseY, CHAIR_WIDTH, CHAIR_HEIGHT);
			}
			root.getChildren().remove(chairBounds);
		});
		
		// --- Event handling "Place Object" button ---
		placeObject.setOnMousePressed(event -> {
			updateBound(event, objectBounds);
			root.getChildren().add(objectBounds);
		});
		
		placeObject.setOnMouseDragged(event2 -> { updateBound(event2, objectBounds); });
		
		placeObject.setOnMouseReleased(event3 -> {
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(
					event3.getSceneX() - LEFT_WIDTH, event3.getSceneY() - TOP_HEIGHT, 1, 1);
			
			if(!inDrawPane) {
				System.out.println("Outside of central panel");
			} else {
				System.out.println("Inside of central panel");
				// TODO: Notify controller that user wants to place object at (mouseX, mouseY)
				//  position with the default width and height.
				
				// TODO: might consider user input for width and height
				controller.createNewObject("object", event3.getX(), event3.getY(), 10, 10);
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
	private Pane initCenterPanel() {
		Pane result = new Pane();
		Pane child = initCenterInnerPanel(); // Draw panel
		
		result.setBackground(new Background(
				new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(CENTER_WIDTH);
		result.setPrefHeight(CENTER_HEIGHT);
		result.getChildren().add(child);
		
		// Allows right mouse drag to pan the child.
		result.setOnMousePressed((event) -> {
			if(event.isPrimaryButtonDown()) return;
			double mouseX = event.getSceneX();
			double mouseY = event.getSceneY();
			double paneX = child.getTranslateX();
			double paneY = child.getTranslateY();
			
			result.setOnMouseDragged((event2) -> {
				if(event2.isPrimaryButtonDown()) return;
				child.setTranslateX(paneX + (event2.getSceneX() - mouseX));
				child.setTranslateY(paneY + (event2.getSceneY() - mouseY));
			});
		});
		
		// Allows mouse scroll wheel to zoom in and out the child
		result.setOnScroll((event) -> {
			if(event.getDeltaY() < 0) {
				child.setScaleX(child.getScaleX() / 1.1);
				child.setScaleY(child.getScaleY() / 1.1);
			} else {
				child.setScaleX(child.getScaleX() * 1.1);
				child.setScaleY(child.getScaleY() * 1.1);
			}
		});
		
		return result;
	}
	
	/**
	 * Initializes the inner panel for the center panel of the root border pane
	 *
	 * @return pane
	 */
	private Pane initCenterInnerPanel() {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(CENTER_WIDTH / 2.0);
		result.setPrefHeight(CENTER_HEIGHT / 2.0);
		result.setTranslateX((CENTER_WIDTH/2.0)/2);
		result.setTranslateY((CENTER_HEIGHT/2.0)/2);
		drawPane = result;
		return result;
	}
	
	/**
	 * Initializes the top panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initTopPanel() {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(196, 153, 143, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefHeight(TOP_HEIGHT);
		
		HBox hBox = new HBox();
		Label topHeader = new Label("Floor Plan Creator");
		topHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 30px;" +
				"-fx-padding: 0px 0px 0px 100px;");
		
		HBox menuBar = initTopControls();
		hBox.getChildren().addAll(menuBar,topHeader);
		result.getChildren().add(hBox);
		return result;
	}
	
	private HBox initTopControls(){
		HBox result = new HBox();
		HBox undoRedoBox = new HBox();
		HBox zoomBox = new HBox();
		MenuBar menuBar = new MenuBar();
		
		Menu menu = new Menu("File");
		Menu subMenu = new Menu("Submenu");
		MenuItem menuItemNew = new MenuItem("New");
		MenuItem menuItemOpen = new MenuItem("Open");
		MenuItem menuItemSave = new MenuItem("Save");
		MenuItem menuItemSaveAs = new MenuItem("Save As");
		MenuItem menuItemHelp = new MenuItem("Help");
		MenuItem menuItemClose = new MenuItem("Close");
		MenuItem subMenuItem1 = new MenuItem("Submenu Item");
		subMenu.getItems().add(subMenuItem1);
		
		result.setStyle("-fx-spacing: 25px;");
		undoRedoBox.setStyle("-fx-spacing: 2px;");
		zoomBox.setStyle("-fx-spacing: 2px;");

		

		
		menuItemNew.setOnAction(e -> {
			System.out.println("Menu Item \"New\" Selected");
			// TODO: Implement creating a new floor plan layout
		});
		menuItemOpen.setOnAction(e -> {
			System.out.println("Menu Item \"Open\" Selected");
			// TODO: Implement loading a preexisting floor plan layout
		});
		menuItemSave.setOnAction(e -> {
			System.out.println("Menu Item \"Save\" Selected");
			// TODO: Implement the save feature
		});
		menuItemSaveAs.setOnAction(e -> {
			System.out.println("Menu Item \"Save As\" Selected");
			// TODO: Implement the "Save As" feature
		});
		menuItemHelp.setOnAction(e -> {
			System.out.println("Menu Item \"Help\" Selected");
			// TODO: Implement the help info for the user
		});
		menuItemClose.setOnAction(e -> {
			System.out.println("Menu Item \"Close\" Selected");
			// TODO: Implement closing out of the application
		});
		
		menu.getItems().add(menuItemNew);
		menu.getItems().add(menuItemOpen);
		menu.getItems().add(menuItemSave);
		menu.getItems().add(menuItemSaveAs);
		menu.getItems().add(subMenu);
		menu.getItems().add(menuItemHelp);
		menu.getItems().add(menuItemClose);
		menuBar.getMenus().add(menu);
		
		Button undoButton = new Button();
		Button redoButton = new Button();
		Button resetZoomButton = new Button();
		Button zoomInButton = new Button();
		Button zoomOutButton = new Button();
		
		undoButton.setGraphic(new ImageView(ImageLoader.getImage("undo_24px.png")));
		redoButton.setGraphic(new ImageView(ImageLoader.getImage("redo_24px.png")));
		resetZoomButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-reset_24px.png")));
		zoomInButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-in_24px.png")));
		zoomOutButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-out_24px.png")));
		
		undoButton.setOnMouseClicked(e -> {
			System.out.println("\"Undo\" button clicked");
			// TODO: Implement the undo feature
		});
		redoButton.setOnMouseClicked(e -> {
			System.out.println("\"Redo\" button clicked");
			// TODO: Implement the redo feature
		});
		resetZoomButton.setOnMouseClicked(e -> {
			System.out.println("\"Reset Zoom\" button clicked");
			// TODO: Implement the "Reset Zoom" feature
		});
		zoomInButton.setOnMouseClicked(e -> {
			System.out.println("\"Zoom In\" button clicked");
			// TODO: Implement the zooming in like with the mouse wheel
		});
		zoomOutButton.setOnMouseClicked(e -> {
			System.out.println("\"Zoom Out\" button clicked");
			// TODO: Implement the zooming out like with the mouse wheel
		});
		undoRedoBox.getChildren().addAll(undoButton, redoButton);
		zoomBox.getChildren().addAll(resetZoomButton, zoomInButton, zoomOutButton);
		result.getChildren().addAll(menuBar, undoRedoBox, zoomBox);
		
		return result;
	}
	
	/**
	 * Initializes the bottom panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initBottomPanel() {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(196, 153, 143, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefHeight(BOT_HEIGHT);
		
		return result;
	}
	
	/**
	 * Initializes a dashed rectangle representing the bounds of the object being placed.
	 *
	 * @param width  the new's object bound's width in pixels
	 * @param height the new's object bound's height in pixels
	 * @return rectangle
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
	 * Initializes a new UI object at the given coordinates and with the given dimensions
	 *
	 * @param x      vertical position
	 * @param y      horizontal position
	 * @param width  the new's object width in pixels
	 * @param height the new's object height in pixels
	 * @return rectangle
	 */
	private Rectangle initObject(double x, double y, double width, double height) {
		Rectangle r = new Rectangle(x, y, width, height);
		// TODO: EventHandler for selecting, moving, and editing rectangles
		return r;
	}
	
	/**
	 * Updates position of object to mouse's position.
	 *
	 * @param event        mouse event
	 * @param objectBounds node
	 */
	private void updateBound(MouseEvent event, Node objectBounds) {
		objectBounds.setScaleX(drawPane.getScaleX());
		objectBounds.setScaleY(drawPane.getScaleY());
		
		objectBounds.setTranslateX(event.getSceneX() +
				(objectBounds.getBoundsInLocal().getWidth() / 2 * (objectBounds.getScaleX() - 1)));
		objectBounds.setTranslateY(event.getSceneY() +
				(objectBounds.getBoundsInLocal().getHeight() / 2 * (objectBounds.getScaleY() - 1)));
	}
}
