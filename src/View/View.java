package View;

import Model.*;
import Controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Displays what the user sees
 * <p>
 * CuCurrent colors of the panels inside the main border pane are temporary.
 *
 */
public class View extends Application implements Observer {
	
	Model 	   model;	   // model of MVC
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
	
	Pane drawPane;
	boolean inDrawPane = false;
	
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
	 * @param  primaryStage Stage
	 * @throws Exception ex
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

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
	 * @param object Object
	 */
	@Override
	public void update(Observable observable, Object object) {
		ArrayList<UIObjects> itemList = model.getObjects();		// items to be placed
		// TODO - clear central panel 
		drawPane.getChildren().clear();
		// TODO - redraw all items
		for(UIObjects obj : itemList)
		{
			if(obj instanceof Wall) {
				System.out.println("Drawing wall");
				//Rectangle wall = initObject(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
				Line wall = new Line(obj.getX(), obj.getY(), obj.getX2(), obj.getY2());
				wall.setStrokeWidth(5);

				// These are invisible circular regions acting as endpoints of the line
				// to allow dragging of on end.
				Circle leftEnd  = new Circle(obj.getX(), obj.getY(), 10);
				Circle rightEnd = new Circle(obj.getX2(), obj.getY2(), 10);

				leftEnd.setFill(Color.TRANSPARENT);
				rightEnd.setFill(Color.TRANSPARENT);

				// drag left end of wall
				leftEnd.setOnMouseDragged(event -> {
					wall.setStartX(event.getX());
					wall.setStartY(event.getY());

					leftEnd.setCenterX(event.getX());
					leftEnd.setCenterY(event.getY());
				});

				leftEnd.setOnMouseReleased(event -> {

					// TODO - Check if in the draw pane
					controller.updateObject(obj.getId(), event.getX(), event.getY(), obj.getX2(), obj.getY2());
				});

				// drag right end wall
				rightEnd.setOnMouseDragged(event -> {
					wall.setEndX(event.getX());
					wall.setEndY(event.getY());

					rightEnd.setCenterX(event.getX());
					rightEnd.setCenterY(event.getY());
				});

				rightEnd.setOnMouseReleased(event -> {

					// TODO - Check if in the draw pane
					controller.updateObject(obj.getId(), obj.getX(), obj.getY(), event.getX(), event.getY());
				});

				wall.setOnMousePressed(event -> {
					Point2D p = drawPane.sceneToLocal(event.getSceneX(), event.getSceneY());

					double mouseX = p.getX();
					double mouseY = p.getY();
					double wallX = wall.getTranslateX();
					double wallY = wall.getTranslateY();

					wall.setOnMouseDragged(event2 -> {
						Point2D p2 = drawPane.sceneToLocal(event2.getSceneX(), event2.getSceneY());
						wall.setTranslateX(wallX + (p2.getX() - mouseX));
						wall.setTranslateY(wallY + (p2.getY() - mouseY));
					});
				});

				wall.setOnMouseReleased(event -> {
					

					// TODO - check if placed within the draw pane
					/*
					if(<not_in_draw_pane>) {
						  // Rest the wall to original place
						wall.setStartX(obj.getX()); wall.setStartY(obj.getY());
						wall.setEndX(obj.getX2()); wall.setEndY(obj.getY());
						return;
					}*/

					double transX = wall.getTranslateX();
					double transY = wall.getTranslateY();

					controller.updateObject(obj.getId(), obj.getX() + transX, obj.getY() + transY,
											obj.getX2() + transX, obj.getY2() + transY);
				});

				drawPane.getChildren().add(wall);
				drawPane.getChildren().add(leftEnd);
				drawPane.getChildren().add(rightEnd);
			} 
			/*else if(obj instanceof Chair) {
				System.out.println("Drawing chair");
			}*/
			else {
				System.out.println("Drawing object");

				Rectangle newObj = initObject(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
				newObj.setOnMousePressed(event -> {
					Point2D p = drawPane.sceneToLocal(event.getSceneX(), event.getSceneY());

					double mouseX = p.getX();
					double mouseY = p.getY();
					double newObjX = newObj.getTranslateX();
					double newObjY = newObj.getTranslateY();

					newObj.setOnMouseDragged(event2 -> {
						Point2D p2 = drawPane.sceneToLocal(event2.getSceneX(), event2.getSceneY());
						newObj.setTranslateX(newObjX + (p2.getX() - mouseX));
						newObj.setTranslateY(newObjY + (p2.getY() - mouseY));
					});
				});

				newObj.setOnMouseReleased(event -> {
					

					// TODO - check if placed within the draw pane
					/*
					if(<not_in_draw_pane>) {
						  // Rest the wall to original place
						wall.setStartX(obj.getX()); wall.setStartY(obj.getY());
						wall.setEndX(obj.getX2()); wall.setEndY(obj.getY());
						return;
					}*/

					double transX = newObj.getTranslateX();
					double transY = newObj.getTranslateY();

					controller.updateObject(obj.getId(), obj.getX() + transX, obj.getY() + transY,
											obj.getX2() + transX, obj.getY2() + transY);
				});

				drawPane.getChildren().add(newObj);
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
		
		/*TODO:
			- Change the model via controller (addWall, addChair, addObject)
			- Show that the object has been placed in view
			
			--- Add event-handling to allow the placed object to be changed (widgets on sides of wall to allow change in width, height, and rotation)
			--- Change the model via controller (updateWall, updateChair, updateObject)
		 */
		// --- Event handling "Place Wall" button ---
		placeWall.setOnMousePressed(event -> {
			updateBound(event, wallBounds);
			root.getChildren().add(wallBounds);
		});
			
		placeWall.setOnMouseDragged(event2 -> {
			updateBound(event2, wallBounds);
		});
			
		placeWall.setOnMouseReleased(event3 -> {
			//if (!isInCenter(event3.getSceneX(), event3.getSceneY())) {
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(event3.getSceneX() - LEFT_WIDTH, 
																		event3.getSceneY() - TOP_HEIGHT, 1, 1);
			if (!inDrawPane) {
				System.out.println("Outside of central panel");
			} else {
				// TODO: Notify controller that user wants to place wall at (mouseX, mouseY) position with WALL_WIDTH and WALL_HEIGHT.
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
			
		placeChair.setOnMouseDragged(event2 -> {
			updateBound(event2, chairBounds);
		});
			
		placeChair.setOnMouseReleased(event3 -> {
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(event3.getSceneX() - LEFT_WIDTH, event3.getSceneY() - TOP_HEIGHT, 1, 1);
			if (!inDrawPane) {
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
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(event3.getSceneX() - LEFT_WIDTH, event3.getSceneY() - TOP_HEIGHT, 1, 1);
			if (!inDrawPane) {
				System.out.println("Outside of central panel");
			} else {
				System.out.println("Inside of central panel");
				// TODO: Notify controller that user wants to place object at (mouseX, mouseY) position with the default width and height.
				// TODO: might consider user input for width and height
				Point2D p = drawPane.sceneToLocal(event3.getSceneX(), event3.getSceneY());
				controller.createNewObject("object", p.getX(), p.getY(), 10, 10);
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
		Pane child = initCenterInnerPanel();
		result.setBackground(new Background(
				new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(CENTER_WIDTH);
		result.setPrefHeight(CENTER_HEIGHT);
		result.getChildren().add(child);
		
		// Allows right mouse drag to pan the child.
		result.setOnMousePressed((event) -> {
			if (event.isPrimaryButtonDown()) return;
			double mouseX = event.getSceneX();
			double mouseY = event.getSceneY();
			double paneX = child.getTranslateX();
			double paneY = child.getTranslateY();
			
			result.setOnMouseDragged((event2) -> {
				if (event2.isPrimaryButtonDown()) return;
				child.setTranslateX(paneX + (event2.getSceneX() - mouseX));
				child.setTranslateY(paneY + (event2.getSceneY() - mouseY));
			});
		});
		
		result.setOnScroll((event) -> {
			if (event.getDeltaY() < 0) {
				child.setScaleX(child.getScaleX() / 1.1);
				child.setScaleY(child.getScaleY() / 1.1);
			} else {
				child.setScaleX(child.getScaleX() * 1.1);
				child.setScaleY(child.getScaleY() * 1.1);
			}
		});
		
		
		return result;
	}
	
	private Pane initCenterInnerPanel() {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(CENTER_WIDTH/2);
		result.setPrefHeight(CENTER_HEIGHT/2);
		drawPane = result;
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
	
	private Rectangle initObject(double x, double y, double width, double height) {
		Rectangle r = new Rectangle(x, y, width, height);
		// TODO: EventHandler for selecting, moving, and editing rectangles
		return r;
	}
	
	/**
	 * Updates position of object to mouse's position.
	 * @param event
	 * @param objectBounds
	 */
	private void updateBound(MouseEvent event, Node objectBounds) {
		objectBounds.setScaleX(drawPane.getScaleX());
		objectBounds.setScaleY(drawPane.getScaleY());
		objectBounds.setTranslateX(event.getSceneX() + (objectBounds.getBoundsInLocal().getWidth() / 2 * (objectBounds.getScaleX() - 1)));
		objectBounds.setTranslateY(event.getSceneY() + (objectBounds.getBoundsInLocal().getHeight() / 2 * (objectBounds.getScaleY() - 1)));
	}
}
