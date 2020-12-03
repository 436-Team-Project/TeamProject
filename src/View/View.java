package View;

import Controller.Controller;
import Model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Displays what the user sees
 * <p>
 */
public class View extends Application implements Observer {
	// Pane Backgrounds
	static String topPaneStyle = "-fx-background-color: #C2988E;-fx-pref-width: 1200px;" +
			"-fx-pref-height: 50px;";
	static String leftPaneStyle = "-fx-background-color: #6DA08C;-fx-pref-width: 250px;" +
			"-fx-pref-height: 700px;-fx-alignment: center";
	static String centerOuterStyle = "-fx-background-color: #D1D1D1;";
	static String centerInnerStyle = "-fx-background-color: #FFFFFF;";
	static String bottomPaneStyle = "-fx-background-color: #C2988E;-fx-pref-width: 1200px;" +
			"-fx-pref-height: 50px;";
	static String labelStyle = "-fx-font-family: Arial, sans-serif; -fx-font-weight: bold;" +
			"-fx-font-size: 20px;";
	static String viewHeader = "-fx-font-family: Arial, sans-serif; -fx-font-weight: bold;" +
			"-fx-font-size: 30px;-fx-alignment: center;";
	static String buttonBoxStyle = "-fx-pref-height: 50px;-fx-alignment: center;-fx-spacing: 2px;";
	static String buttonStyle = "-fx-pref-width: 120px; -fx-pref-height: 40px;";
	
	// Colors
	static Color HIGHLIGHT = Color.web("#FDD500");
	static Color OCCUPIED = Color.web("#A8A8A8");
	static Color UNAVAILABLE = Color.web("#FD0000");
	static Color FREE = Color.web("#FFFFFF");
	static Color Safe = Color.web("#ACD6E4");
	
	// The dimensions of the entire application
	final static int APP_HEIGHT = 800;
	final static int APP_WIDTH = 1200;
	
	// Dimensions for the panels inside the border pane
	final static int LEFT_WIDTH = 250;
	final static int TOP_HEIGHT = 50;
	final static int BOT_HEIGHT = 50;
	final static int CENTER_WIDTH = (APP_WIDTH - LEFT_WIDTH);
	final static int CENTER_HEIGHT = (APP_HEIGHT - (TOP_HEIGHT + BOT_HEIGHT));
	
	// Default dimensions for objects created from buttons
	static double WALL_SIZE = 25;
	static double CHAIR_SIZE = 25;
	static double TABLE_SIZE = 60;
	
	// Utility Values
	static String CURR_FILE_NAME;
	static File CURR_FILE;
	static FileChooser FC;
	static KeyboardListener KBL;
	
	// Application States
	boolean isSelecting = false;
	boolean isDrawingWall = false;
	boolean isPlacingChair = false;
	boolean isPlacingObject = false;
	boolean isHosting = false;
	boolean isAssigningSeat = false;
	boolean isRemovingSeat = false;
	boolean updatingSelection = false;
	boolean toggleSelected = false;
	
	// JavaFx Objects
	Scene scene;
	Controller controller; // Controller of MVC
	Model model; // model of MVC
	BorderPane root; // Main pane
	Pane drawPane; // Drawing Canvas
	Canvas grid; // Grid overlaying canvas
	
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
		
		FC = new FileChooser();
		FC.setInitialDirectory(new File(System.getProperty("user.dir")));
		FC.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text", "*.txt"));
		
		root = new BorderPane();
		root.setCenter(initCenterPanel());
		root.setLeft(initLeftPanel());
		root.setTop(initTopPanel(primaryStage));
		root.setBottom(initBottomPanel(primaryStage));
		
		scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
		KBL = new KeyboardListener(scene, controller, drawPane);
		
		primaryStage.getIcons().add(ImageLoader.getImage("app_icon_black_60px.png"));
		primaryStage.setTitle("Covid Calc");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Update the view
	 *
	 * leftEnd and rightEnd are invisible circular regions acting as endpoints of the line to allow
	 * dragging of on end.
	 *
	 *
	 * @param observable Observable
	 * @param object     Object
	 */
	@Override
	public void update(Observable observable, Object object) {
		ArrayList<UIObjects> itemList = model.getObjects(); // items to be placed
		ArrayList<Label> labels = new ArrayList<>();
		
		drawPane.getChildren().clear(); // Clear central panel
		drawPane.getChildren().add(grid); // Redraw all items
		
		for(UIObjects obj : itemList) {
			if(obj instanceof Wall) {
//				System.out.println("Drawing wall");
				Line wall = setLine((Wall) obj);
				Circle leftEnd = new Circle(obj.getX(), obj.getY(), 10);
				Circle rightEnd = new Circle(obj.getX2(), obj.getY2(), 10);
				
				leftEnd.setFill(Color.TRANSPARENT);
				rightEnd.setFill(Color.TRANSPARENT);
				
				setEndPointMouseAction(leftEnd, wall, obj, true); // last argument is flag
				setEndPointMouseAction(rightEnd, wall, obj, false); // for left or right
				setMouseAction(wall, obj);
				
				Label measure = new Label();
				String length = String.format("%.1f", lineLength(wall) / 15);
				measure.setText(length);
				measure.setTranslateX((wall.getEndX() - wall.getStartX()) / 2 + wall.getStartX());
				measure.setTranslateY((wall.getEndY() - wall.getStartY()) / 2 + wall.getStartY());
				measure.setMouseTransparent(true);
				measure.setAlignment(Pos.CENTER);
				measure.setTextFill(Color.RED);
				labels.add(measure); // Keep track of this label to bring to the front
				
				drawPane.getChildren().addAll(wall, leftEnd, rightEnd, measure);
			} else if(obj instanceof Spots) {
//				System.out.println("Drawing chair");
				Circle chair = setChair((Spots) obj);
				setMouseAction(chair, obj);
				drawPane.getChildren().add(chair);
				toggleRings(toggleSelected);
			} else {
//				System.out.println("Drawing object");
				Rectangle rectangle = setObject(obj);
				setMouseAction(rectangle, obj);
				drawPane.getChildren().add(rectangle);
			}
		}
		
		// Bring the labels to the front
		for(Label label : labels) {
			label.toFront();
		}
	}
	
	/**
	 * Set the event handling of mouse actions for a given javafx circle.
	 *
	 * @param endPoint the circle to set the handlers for
	 * @param wall     the wall for which the circle is an endpoint
	 * @param uiObj      the UIObjects associated with the wall
	 * @param isLeft   flag to determine whether it's left of right endpoint
	 */
	private void setEndPointMouseAction(Circle endPoint, Line wall, UIObjects uiObj, boolean isLeft) {
		// drag left end of wall
		if (!isHosting) {
			
			// When the mouse releases from a presses on end point circle
			endPoint.setOnMousePressed(pressEvent -> {
				isSelecting = false;
				isDrawingWall = false;
				isPlacingChair = false;
				isPlacingObject = false;
				
				endPoint.setOnMouseDragged(releaseEvent -> {
					if(isLeft) {
						wall.setStartX(releaseEvent.getX());
						wall.setStartY(releaseEvent.getY());
					} else {
						wall.setEndX(releaseEvent.getX());
						wall.setEndY(releaseEvent.getY());
					}
					endPoint.setCenterX(releaseEvent.getX());
					endPoint.setCenterY(releaseEvent.getY());
				});
			});
			
			// When the mouse releases from end point circle
			endPoint.setOnMouseReleased(releaseEvent -> {
				boolean inDrawPane = (endPoint.getCenterX() > 0
						&& endPoint.getCenterX() < drawPane.getWidth())
						&& (endPoint.getCenterY() > 0 && endPoint.getCenterY() < drawPane.getHeight());
				
				if(!inDrawPane) {
					System.out.println("Outside of central panel (setEndPointMouseAction)");
					controller.displayModel();
				} else {
					if(isLeft) {
						controller.updateCurrentObject(releaseEvent.getX(), releaseEvent.getY(),
								uiObj.getX2(), uiObj.getY2(), uiObj.getId());
					} else {
						controller.updateCurrentObject(uiObj.getX(), uiObj.getY(),
								releaseEvent.getX(), releaseEvent.getY(), uiObj.getId());
					}
				}
			});
		}
	}
	
	/**
	 * Set the event handling of mouse actions for a object
	 *
	 * @param shape the object to set the handlers for
	 * @param uiObj the UIObjects associated with the given object
	 */
	private void setMouseAction(Shape shape, UIObjects uiObj) {
		// When the mouse actions happen in the Construct view
		if (!isHosting) {
			// When the mouse enters the shape
			shape.setOnMouseEntered(enterEvent -> {
				shape.setStrokeWidth(shape.getStrokeWidth() + 1);
			});
			// When the mouse exits the shape
			shape.setOnMouseExited(exitedEvent -> {
				shape.setStrokeWidth(shape.getStrokeWidth() - 1);
			});
			// When the mouse presses the shape
			shape.setOnMousePressed(pressedEvent -> {
				// Update UI object's highlight value and circle's appearance
				if(!KBL.isKeyPressed(KeyCode.CONTROL)) {
					controller.deselectAll(false);
					clearSelectionUpdate();
				}
				uiObj.setHighlighted(!uiObj.isHighlighted());
				showSelectionUpdate();
				
				shape.setStroke(Color.PINK);
				shape.setStrokeWidth(shape.getStrokeWidth() + 2);
				// Update application states
				isSelecting = false;
				isDrawingWall = false;
				isPlacingChair = false;
				isPlacingObject = false;
				
				Point2D p = drawPane.sceneToLocal(
						pressedEvent.getSceneX(),
						pressedEvent.getSceneY());
				double mouseX = p.getX();
				double mouseY = p.getY();
				double objTransX = shape.getTranslateX();
				double objTransY = shape.getTranslateY();
				Circle ring = null;
				
				if(shape instanceof Circle && toggleSelected) {
					drawPane.getChildren().removeIf(child -> child instanceof Circle &&
							((Circle) child).getFill().equals(Color.rgb(0, 0, 0, 0)) &&
							((Circle) child).getCenterX() == ((Circle)shape).getCenterX() &&
							((Circle) child).getCenterY() == ((Circle)shape).getCenterY()
					);
					ring = setRing((Circle) shape);
					drawPane.getChildren().add(ring);
				}
				
				// When the mouse releases from a press on the shape
				Circle finalRing = ring;
				shape.setOnMouseDragged(dragEvent -> {
					Point2D p2 = drawPane.sceneToLocal(dragEvent.getSceneX(), dragEvent.getSceneY());
					if(shape instanceof Circle) {
						finalRing.setTranslateX(objTransX + (p2.getX() - mouseX));
						finalRing.setTranslateY(objTransY + (p2.getY() - mouseY));
					}
					shape.setTranslateX(objTransX + (p2.getX() - mouseX));
					shape.setTranslateY(objTransY + (p2.getY() - mouseY));
				});
			});
			// When the mouse releases the press on the shape
			shape.setOnMouseReleased(releaseEvent -> {
				controller.updateHighlightIndex();
				shape.setStrokeWidth(shape.getStrokeWidth() - 2);
				
				// check if placed within the draw pane
				Bounds objBounds = shape.getBoundsInParent();
				boolean inDrawPane = (objBounds.getMinX() > 0
						&& objBounds.getMaxX() < drawPane.getWidth())
						&& (objBounds.getMinY() > 0 && objBounds.getMaxY() < drawPane.getHeight());
				
				if(!inDrawPane) {
					System.out.println("Outside of central panel (setMouseAction)");
					controller.displayModel();
				} else {
					double transX = shape.getTranslateX();
					double transY = shape.getTranslateY();
					controller.updateCurrentObject(uiObj.getX() + transX, uiObj.getY() + transY,
							uiObj.getX2() + transX, uiObj.getY2() + transY, uiObj.getId());
				}
			});
		}
		
		// When the mouse action happen in the Host view and on a Circle
		if (isHosting && shape instanceof Circle) {
			Circle circle = (Circle) shape;
			shape.setOnMouseClicked(e -> {
				Spots spot = ((Spots) uiObj);
				if (isAssigningSeat) {
					controller.occupySpot(circle);
					System.out.println("occupied");
				} else if (isRemovingSeat) {
					spot.setSafety(false);
					System.out.println("freed");
				}
				HostView.info1Value.setText(String.valueOf(controller.countSpotType("total")));
				HostView.info2Value.setText(String.valueOf(controller.countSpotType("unavailable")));
				HostView.info3Value.setText(String.valueOf(controller.countSpotType("free")));
				controller.displayModel();
			});
		}
	}
	
	/**
	 * Initializes the top panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initTopPanel(Stage stage) {
		Pane topPane = new Pane();
		topPane.setStyle(topPaneStyle);
		// Event handling for the top panel
		topPane.setOnMouseClicked(mouseEvent -> {
			controller.deselectAll(true);
			clearSelectionUpdate();
		});
		
		topPane.getChildren().add(initTopControls(stage));
		return topPane;
	}
	
	/**
	 * Initializes the controls in the top panel of the main border pane
	 *
	 * @return HBox
	 */
	private HBox initTopControls(Stage stage) {
		// Set up menu bar on the top pane
		MenuBar menuBar = new MenuBar();
		MenuItem menuNew = new MenuItem("New");
		menuNew.setOnAction(menuEvent -> {
			System.out.println("Menu Item \"New\" Selected");
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("New Floor Plan");
			dialog.setHeaderText("What is the name of your new floor plan?");
			dialog.setContentText("Floor plan name:");
			Optional<String> dialogResult = dialog.showAndWait();
			// User provided filename and clicked "ok"; create temporary file
			if(dialogResult.isPresent()) {
				dialogResult.ifPresent(fileName -> CURR_FILE_NAME = fileName);
				FC.setInitialFileName(CURR_FILE_NAME);
				model = new Model();
				model.addObserver(this);
				controller = new Controller(model);
				KBL.setController(controller);
				try {
					File tempFile = new File(ImageLoader.floorPlanDir + CURR_FILE_NAME);
					if(tempFile.createNewFile()) {
						System.out.println("Temp file created: " + tempFile.getName());
						controller.save(tempFile);
						controller.load(tempFile);
					} else {
						System.out.println("File already exists.");
					}
				} catch(IOException ioException) {
					ioException.printStackTrace();
				}
			}
		});
		setupMenuBar(menuBar, stage, menuNew, controller, true);
		
		// Set up the zoom control buttons
		HBox zoomBox = setupZoomButtons(drawPane);
		
		// Set up buttons for Delete
		HBox manipulateBox = new HBox();
		manipulateBox.setStyle(buttonBoxStyle);
		
		Button deleteButton = new Button("Delete");
		deleteButton.setStyle(buttonStyle);
		deleteButton.setOnMouseClicked(e -> {
			System.out.println("\"Delete\" button clicked");
			controller.removeHighlighted();
			clearSelectionUpdate();
		});
		manipulateBox.getChildren().addAll(deleteButton);
		
		// Set up the buttons for redo and undo
		HBox undoRedoBox = new HBox();
		Button undoButton = new Button();
		Button redoButton = new Button();
		
		undoRedoBox.setStyle(buttonBoxStyle);
		undoButton.setGraphic(new ImageView(ImageLoader.getImage("undo_24px.png")));
		redoButton.setGraphic(new ImageView(ImageLoader.getImage("redo_24px.png")));
		undoButton.setOnMouseClicked(e -> {
			System.out.println("\"Undo\" button clicked");
			controller.undo();
		});
		redoButton.setOnMouseClicked(e -> {
			System.out.println("\"Redo\" button clicked");
			controller.redo();
		});
		undoRedoBox.getChildren().addAll(undoButton, redoButton);
		
		Label topHeader = new Label("Floor Plan Creator");
		topHeader.setStyle(viewHeader + "-fx-pref-width: 400px;-fx-pref-height: 50px;");
		
		// Setup the resulting box
		HBox topControlBox = new HBox();
		topControlBox.setStyle("-fx-pref-width: 1200px;-fx-pref-height: 50px;-fx-spacing: 25px;");
		topControlBox.getChildren().addAll(menuBar, undoRedoBox, zoomBox, manipulateBox, topHeader);
		return topControlBox;
	}
	
	/**
	 * Initializes the left panel in the root border pane
	 * <p>
	 * Places three buttons for now. More will be added later
	 *
	 * @return Pane
	 */
	private Pane initLeftPanel() {
		Pane leftPane = new Pane();
		VBox leftControlBox = new VBox();
		VBox buttonBox = new VBox();
		Label leftPanelHeader = new Label("Canvas Elements");
		
		leftPane.setStyle(leftPaneStyle);
		leftControlBox.setStyle("-fx-pref-width: 250px;-fx-pref-height: 700px;" +
				"-fx-alignment: top-center;");
		leftPanelHeader.setStyle(labelStyle+"-fx-pref-width: 250px;-fx-alignment: top-center;" +
				"-fx-padding: 20 0 60 0;");
		buttonBox.setStyle("-fx-pref-width: 250px;-fx-alignment: center;-fx-spacing: 20px;");
		
		// Event handling for the left panel
		leftPane.setOnMouseClicked(mouseEvent -> {
			controller.deselectAll(true);
			clearSelectionUpdate();
		});
		
		Button selection = new Button("Select");
		Button placeWall = new Button("Place Wall");
		Button placeChair = new Button("Place Chair");
		Button placeObject = new Button("Place Table");
		ToggleButton distanceToggle = new ToggleButton("6 Foot Radius");
		selection.setStyle(buttonStyle);
		placeWall.setStyle(buttonStyle);
		placeChair.setStyle(buttonStyle);
		placeObject.setStyle(buttonStyle);
		distanceToggle.setStyle(buttonStyle);
		distanceToggle.setSelected(false);
		
		// Event handling for "Select" button
		selection.setOnMouseClicked(event -> {
			isSelecting = true;
			isDrawingWall = false;
			isPlacingObject = false;
			isPlacingChair = false;
		});
		// Event handling for "Place Wall" button
		placeWall.setOnMouseClicked(event -> {
			isSelecting = false;
			isDrawingWall = true;
			isPlacingObject = false;
			isPlacingChair = false;
		});
		// Event handling for "Place Chair" button
		placeChair.setOnMousePressed(event -> {
			isSelecting = false;
			isDrawingWall = false;
			isPlacingObject = false;
			isPlacingChair = true;
		});
		// Event handling for "Place Object" button
		placeObject.setOnMousePressed(event -> {
			isSelecting = false;
			isDrawingWall = false;
			isPlacingChair = false;
			isPlacingObject = true;
		});
		// Event handling for "6 Foot Radius" button
		distanceToggle.setOnAction(clickEvent -> {
			System.out.println("Distance toggle pressed");
			if(distanceToggle.isSelected()) {
				toggleSelected = true;
			} else {
				toggleSelected = false;
			}
			toggleRings(toggleSelected);
		});
		buttonBox.getChildren().addAll(selection, placeWall, placeChair, placeObject, distanceToggle);
		leftControlBox.getChildren().addAll(leftPanelHeader, buttonBox);
		leftPane.getChildren().add(leftControlBox);
		return leftPane;
	}
	

	
	/**
	 * Just returns null, could be used to add more features in the future
	 *
	 * @return Pane
	 */
	private Pane initRightPanel() {
		return null;
	}
	
	/**
	 * Initializes the center panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initCenterPanel() {
		Pane centerOuter = new Pane();
		Pane centerInner = initCenterInnerPanel(); // Draw panel
		
		
		centerOuter.setId("Center Outer");
		centerInner.setId("Stack pane");
		centerOuter.setStyle(centerOuterStyle);
		centerOuter.setPrefWidth(CENTER_WIDTH);
		centerOuter.setPrefHeight(CENTER_HEIGHT);
		
		// Allows right mouse drag to pan the child.
		setupCenterMouse(centerOuter, centerInner);
		
		centerOuter.getChildren().add(centerInner);
		centerOuter.setOnMouseClicked(pressEvent -> {
			controller.deselectAll(true);
			clearSelectionUpdate();
		});
		
		return centerOuter;
	}
	
	/**
	 * Initializes the inner panel for the center panel of the root border pane
	 *
	 * @return pane
	 */
	private Pane initCenterInnerPanel() {
		Pane centerInner = new Pane();
		drawPane = centerInner;
		centerInner.setStyle(centerInnerStyle);
		
		double INNER_WIDTH = 2100;
		double INNER_HEIGHT = 2100;
		centerInner.setPrefSize(INNER_WIDTH, INNER_HEIGHT);
		centerInner.setTranslateX(-((INNER_WIDTH / 2.0) - (CENTER_WIDTH / 2.0)));
		centerInner.setTranslateY(-((INNER_HEIGHT / 2.0) - (CENTER_HEIGHT / 2.0)));
		
		centerInner.setClip(new Rectangle(centerInner.getPrefWidth(), centerInner.getPrefHeight()));
		grid = initializeGrid();
		centerInner.getChildren().add(grid);
		
		// Event-handling for mouse on drawing canvas depending on which tool is selected.
		centerInner.setOnMousePressed(pressEvent -> {
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(
					pressEvent.getSceneX() - LEFT_WIDTH,
					pressEvent.getSceneY() - TOP_HEIGHT, 1, 1);
			
			if(pressEvent.getButton() == MouseButton.PRIMARY && inDrawPane && !isHosting) {
				
				if(isDrawingWall) {
					Line wallBound = setLineBounds(pressEvent);
					Label measurement = new Label("0");
					StackPane stackpane = new StackPane();
					
					measurement.setMinSize(100, 100);
					measurement.setAlignment(Pos.CENTER);
					measurement.setTextFill(Color.RED);
					
					stackpane.setTranslateX(pressEvent.getSceneX());
					stackpane.setTranslateY(pressEvent.getSceneY());
					stackpane.getChildren().add(wallBound);
					stackpane.getChildren().add(measurement);
					stackpane.setMaxSize(APP_WIDTH, APP_HEIGHT);
					root.getChildren().add(stackpane);
					
					// Dragging wall on creation
					centerInner.setOnMouseDragged(dragEvent -> {
						wallBound.setEndX(dragEvent.getSceneX());
						wallBound.setEndY(dragEvent.getSceneY());
						
						stackpane.setTranslateX((dragEvent.getSceneX() - pressEvent.getSceneX()) /
								2 + pressEvent.getSceneX());
						stackpane.setTranslateY((dragEvent.getSceneY() - pressEvent.getSceneY()) /
								2 + pressEvent.getSceneY());
						
						String length = String.format("%.1f", lineLength(wallBound) / 15);
						measurement.setText(length);
					});
					// Releasing wall on creation
					centerInner.setOnMouseReleased(releaseEvent -> {
						boolean inDrawPaneEnd = drawPane.getBoundsInParent().intersects(
								releaseEvent.getSceneX() - LEFT_WIDTH,
								releaseEvent.getSceneY() - TOP_HEIGHT,
								1, 1);
						
						if(!inDrawPaneEnd) {
							System.out.println("Outside of central panel (drawing wall)");
						} else if(releaseEvent.getButton() == MouseButton.PRIMARY && isDrawingWall) {
							controller.createNewObject("wall",
									pressEvent.getX(),
									pressEvent.getY(),
									releaseEvent.getX(),
									releaseEvent.getY());
						}
						root.getChildren().remove(stackpane);
					});
				}
				if(isPlacingChair) {
					Rectangle chairBounds = setObjectBounds(CHAIR_SIZE, CHAIR_SIZE);
					updateBound(pressEvent, chairBounds);
					root.getChildren().add(chairBounds);
					
					// Dragging chair on creation
					centerInner.setOnMouseDragged(dragEvent -> {
						updateBound(dragEvent, chairBounds);
					});
					// Releasing chair on creation
					centerInner.setOnMouseReleased(releaseEvent -> {
						boolean inDrawPaneEnd = drawPane.getBoundsInParent().intersects(
								releaseEvent.getSceneX() - LEFT_WIDTH,
								releaseEvent.getSceneY() - TOP_HEIGHT,
								1, 1) &&
								drawPane.getBoundsInParent().intersects(
										releaseEvent.getSceneX() - LEFT_WIDTH + chairBounds.getWidth(),
										releaseEvent.getSceneY() - TOP_HEIGHT + chairBounds.getHeight(),
										1, 1);
						if(!inDrawPaneEnd) {
							System.out.println("Outside of central panel (placing chair)");
							root.getChildren().remove(chairBounds);
						} else if(isPlacingChair && releaseEvent.getButton() == MouseButton.PRIMARY) {
							Point2D p = drawPane.sceneToLocal(
									releaseEvent.getSceneX(),
									releaseEvent.getSceneY());
							
							double x2 = p.getX() + CHAIR_SIZE;
							double y2 = p.getY() + CHAIR_SIZE;
							
							controller.createNewObject("chair", p.getX(), p.getY(), x2, y2);
							root.getChildren().remove(chairBounds);
						}
					});
				}
				if(isPlacingObject) {
					Rectangle objectBounds = setObjectBounds(TABLE_SIZE, TABLE_SIZE);
					isDrawingWall = false;
					isPlacingChair = false;
					isPlacingObject = true;
					updateBound(pressEvent, objectBounds);
					root.getChildren().add(objectBounds);
					
					// Dragging object on creation
					centerInner.setOnMouseDragged(dragEvent -> {
						updateBound(dragEvent, objectBounds);
					});
					// Releasing object on creation
					centerInner.setOnMouseReleased(releaseEvent -> {
						boolean inDrawPaneEnd = drawPane.getBoundsInParent().intersects(
								releaseEvent.getSceneX() - LEFT_WIDTH,
								releaseEvent.getSceneY() - TOP_HEIGHT, 1, 1) &&
								drawPane.getBoundsInParent().intersects(
										releaseEvent.getSceneX() - LEFT_WIDTH + objectBounds.getWidth(),
										releaseEvent.getSceneY() - TOP_HEIGHT + objectBounds.getHeight(),
										1, 1);
						if(!inDrawPaneEnd) {
							System.out.println("Outside of central panel (placing object)");
							root.getChildren().remove(objectBounds);
						} else if(isPlacingObject && releaseEvent.getButton() == MouseButton.PRIMARY) {
							Point2D p = drawPane.sceneToLocal(
									releaseEvent.getSceneX(),
									releaseEvent.getSceneY());
							
							double x2 = p.getX() + TABLE_SIZE;
							double y2 = p.getY() + TABLE_SIZE;
							
							controller.createNewObject("object", p.getX(), p.getY(), x2, y2);
							root.getChildren().remove(objectBounds);
						}
					});
				}
			}
			// When the user drags with LMB to select multiple items
			if(isSelecting && pressEvent.isPrimaryButtonDown() && inDrawPane) {
				System.out.println("Selecting");
				Point2D p1 = drawPane.sceneToLocal(pressEvent.getSceneX(), pressEvent.getSceneY());
				
				// Pressing the rectangular selection
				Rectangle rectBound = setRectBounds(p1);
				drawPane.getChildren().add(rectBound);
				
				double startX = p1.getX();
				double startY = p1.getY();
				
				// Dragging the rectangular selection
				centerInner.setOnMouseDragged(dragEvent -> {
					Point2D p2 = drawPane.sceneToLocal(dragEvent.getSceneX(), dragEvent.getSceneY());
					double endX = p2.getX();
					double endY = p2.getY();
					double height = endX - startX;
					double width = endY - startY;
					
					// mouse goes on top of the starting point
					if(height < 0) {
						rectBound.setX(p2.getX());
						rectBound.setWidth(startX - endX);
					} else {
						rectBound.setWidth(height);
					}
					// mouse goes left of the starting point
					if(width < 0) {
						rectBound.setY(p2.getY());
						rectBound.setHeight(startY - endY);
					} else {
						rectBound.setHeight(width);
					}
					
					// Releasing the rectangular selection
					centerInner.setOnMouseReleased(releaseEvent -> {
						if(dragEvent.isPrimaryButtonDown() && isSelecting) {
							double heightFinal = endX - startX;
							double widthFinal = endY - startY;
							double x1 = rectBound.getX();
							double y1 = rectBound.getY();
							double y2 = rectBound.getY() + rectBound.getHeight();
							double x2 = rectBound.getX() + rectBound.getWidth();
							controller.highlightSelected(x1, y1, x2, y2);
							showSelectionUpdate();
							drawPane.getChildren().remove(rectBound);
							isSelecting = false;
							root.requestFocus();
						}
					});
				});
			}
		});
		return centerInner;
	}

	
	/**
	 * Initializes the bottom panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initBottomPanel(Stage stage) {
		Pane bottomPane = new Pane();
		bottomPane.setStyle(bottomPaneStyle);
		bottomPane.setOnMousePressed(pressEvent -> {
			controller.deselectAll(true);
			clearSelectionUpdate();
		});
		bottomPane.getChildren().add(initBottomControls(stage));
		return bottomPane;
	}
	
	/**
	 * Initializes the controls for the bottom panel
	 *
	 * @return HBox
	 */
	private HBox initBottomControls(Stage stage) {
		HBox bottomControlsBox = new HBox();
		Button hostButton = new Button("Host");
		Button constructButton = new Button("Construct");
		
		bottomControlsBox.setStyle("-fx-pref-width: 1200px;-fx-pref-height: 50px;" +
				"-fx-alignment: center;-fx-spacing: 50px;");
		hostButton.setStyle(buttonStyle);
		constructButton.setStyle(buttonStyle);
		
		// Construct Button clicked
		constructButton.setOnMouseClicked(e -> {
			isHosting = false;
			isSelecting = false;
			isDrawingWall = false;
			isPlacingChair = false;
			isPlacingObject = false;
			isAssigningSeat = false;
			isRemovingSeat = false;
			updatingSelection = false;
			root.setCenter(initCenterPanel());
			root.setTop(initTopPanel(stage));
			root.setLeft(initLeftPanel());
			root.setRight(initRightPanel());
			controller.resetFromHosting();
			controller.displayModel();
			root.setBottom(initBottomPanel(stage));
		});
		// Host Button clicked
		hostButton.setOnMouseClicked(e -> {
			isHosting = true;
			isSelecting = false;
			isDrawingWall = false;
			isPlacingChair = false;
			isPlacingObject = false;
			isAssigningSeat = false;
			isRemovingSeat = false;
			updatingSelection = false;
			controller.deselectAll(true);
			HostView hostRoot = new HostView(this, stage, root, model, controller, drawPane);
			root.setBottom(initBottomPanel(stage));
		});
		// Setting the visibility for the Construct and Host buttons
		if(!isHosting) {
			hostButton.setVisible(true);
			constructButton.setVisible(false);
		} else {
			hostButton.setVisible(false);
			constructButton.setVisible(true);
		}
		
		bottomControlsBox.getChildren().addAll(constructButton, hostButton);
		return bottomControlsBox;
	}
	
	/**
	 * Initializes the grid that overlays the drawing canvas
	 *
	 * @return Canvas
	 */
	private Canvas initializeGrid() {
		double width = drawPane.getPrefWidth();
		double height = drawPane.getPrefHeight();
		Canvas grid = new Canvas(width, height);
		grid.setMouseTransparent(true);
		GraphicsContext gc = grid.getGraphicsContext2D();
		gc.setStroke(Color.rgb(100,100,100,0.1));
		gc.setLineWidth(1);
		double offset = 30;
		for(double i = offset; i < width; i += offset) {
			gc.strokeLine(i, 0, i, height);
			gc.strokeLine(0, i, width, i);
		}
		return grid;
	}
	
	/**
	 * Show the rings
	 *
	 * @param showRings boolean
	 */
	private void toggleRings(boolean showRings) {
		List<Circle> rings = new ArrayList<>();
		if(showRings) {
			for(Node child : drawPane.getChildren()) {
				if(child instanceof Circle) {
					Circle circle = (Circle) child;
					// Distinguish from the circles used for handling the walls
					if(circle.getFill() != Color.TRANSPARENT) {
						rings.add(setRing(circle));
					}
				}
			}
			// Add the rings to the canvas
			for(Circle ring : rings) {
				drawPane.getChildren().add(ring);
			}
		} else {
			// Remove the rings
			drawPane.getChildren().removeIf(child -> child instanceof Circle &&
					((Circle) child).getFill().equals(Color.rgb(0, 0, 0, 0)));
		}
	}
	
	/**
	 * Sets ring
	 *
	 * @param circle circle
	 * @return Circle
	 */
	private Circle setRing(Circle circle) {
		Circle ring = new Circle(circle.getCenterX(), circle.getCenterY(), 90);
		ring.setStroke(Color.rgb(130,132,161,0.5));
		ring.setStrokeWidth(4);
		ring.getStrokeDashArray().addAll(15d, 25d);
		ring.setFill(Color.rgb(0,0,0,0));
		ring.setMouseTransparent(true);
		return ring;
	}
	
	/**
	 * Creates the rectangle when the user drags the mouse to select multiple objects
	 *
	 * @param point The position in the canvas
	 * @return rectangle
	 */
	private Rectangle setRectBounds(Point2D point) {
		Rectangle rectangle = new Rectangle(point.getX(), point.getY(), 0, 0);
		rectangle.setStroke(Color.rgb(75, 161, 219));
		rectangle.setStrokeWidth(1);
		rectangle.getStrokeDashArray().addAll(5.0);
		rectangle.setFill(Color.rgb(75, 161, 219, 0.5));
		return rectangle;
	}
	
	/**
	 * Initializes a dashed rectangle representing the bounds of the object being
	 * placed.
	 *
	 * @param width  the new's object bound's width in pixels
	 * @param height the new's object bound's height in pixels
	 * @return rectangle
	 */
	private Rectangle setObjectBounds(double width, double height) {
		Rectangle rectangle = new Rectangle();
		rectangle.setWidth(width);
		rectangle.setHeight(height);
		rectangle.setStroke(Color.BLACK);
		rectangle.setStrokeWidth(1);
		rectangle.getStrokeDashArray().addAll(5.0);
		rectangle.setFill(Color.TRANSPARENT);
		return rectangle;
	}
	
	/**
	 * Initializes a dashed line representing the bounds of the wall being drawn.
	 *
	 * @param pressEvent The press event
	 * @return rectangle
	 */
	private Line setLineBounds(MouseEvent pressEvent) {
		double x = pressEvent.getSceneX(); // object bound's width in pixels
		double y = pressEvent.getSceneY(); // object bound's height in pixels
		Line line = new Line(x, y, x, y);
		line.setStroke(Color.BLACK);
		line.setStrokeWidth(1);
		line.getStrokeDashArray().addAll(5.0);
		line.setFill(Color.TRANSPARENT);
		return line;
	}
	
	/**
	 * Initializes a new UI object at the given coordinates and with the given
	 * dimensions
	 *
	 * @param obj iuo
	 * @return rectangle
	 */
	private Rectangle setObject(UIObjects obj) {
		Rectangle r = new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
		
		// Determine the color of the object
		if(obj.isHighlighted()) {
			r.setFill(Color.GOLD);
		} else {
			r.setFill(Color.GRAY);
		}
		return r;
	}
	
	/**
	 * Initializes a new UI object at the given coordinates and with the given
	 * dimensions (For objects represented as a line)
	 *
	 * @param wall  The Wall UIObject
	 * @return line
	 */
	private Line setLine(Wall wall) {
		Line line = new Line(wall.getX(), wall.getY(), wall.getX2(), wall.getY2());
		line.setStrokeWidth(5);
		
		// Determine color of the line
		if(wall.isHighlighted()) {
			line.setStroke(Color.GOLD);
		} else {
			line.setStroke(Color.BLACK);
		}
		return line;
	}
	
	/**
	 * Initializes a new UI object at the given coordinates and with the given
	 * dimensions
	 *
	 * @param spot Spot
	 * @return rectangle
	 */
	private Circle setChair(Spots spot) {
		double radius = spot.getWidth() / 2;
		Circle circle = new Circle(spot.getX() + radius, spot.getY() + radius, radius);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(1);
		
		// Determine the color of the circle
		if(spot.isHighlighted()) {
			circle.setFill(HIGHLIGHT);
		} else if (spot.isOccupied()) {
			circle.setFill(OCCUPIED);
		} else if (spot.isSafe()) {
			circle.setFill(Safe);
		} else if (!spot.isAvailable()) {
			circle.setFill(UNAVAILABLE);
		} else {
			circle.setFill(FREE);
		}
		return circle;
	}
	
	/**
	 * Updates position of object to mouse's position.
	 *
	 * @param event Mouse event
	 * @param objectBounds Node
	 */
	private void updateBound(MouseEvent event, Node objectBounds) {
		objectBounds.setScaleX(drawPane.getScaleX());
		objectBounds.setScaleY(drawPane.getScaleY());
		objectBounds.setTranslateX(event.getSceneX() + (objectBounds.getBoundsInLocal().getWidth()
				/ 2 * (objectBounds.getScaleX() - 1)));
		objectBounds.setTranslateY(event.getSceneY() + (objectBounds.getBoundsInLocal().getHeight()
				/ 2 * (objectBounds.getScaleY() - 1)));
	}
	
	/**
	 * Returns length of a given line in pixels
	 *
	 * @param l length
	 */
	private double lineLength(Line l) {
		return Math.sqrt(Math.pow(Math.abs(l.getStartX() - l.getEndX()), 2)
				+ Math.pow(Math.abs(l.getStartY() - l.getEndY()), 2));
	}
	
	
	/**
	 * filters a given list of UIObjects to return a new list containing
	 * objects of only the desired type.
	 *
	 * @param objs        is the list of UIObjects to be filtered
	 * @param desiredType is the desired type to be returned
	 */
	private ArrayList<UIObjects> filterObjs(ArrayList<UIObjects> objs, Class<?> desiredType) {
		ArrayList<UIObjects> result = new ArrayList<UIObjects>();
		for(UIObjects o : objs) {
			if(desiredType.isInstance(o)) {
				result.add(o);
			}
		}
		return result;
	}
	
	/**
	 * Determines of the given list contains different types of objects
	 *
	 * @param objs is the list in question
	 */
	private boolean objectsVary(ArrayList<UIObjects> objs) {
		boolean containsTables = false;
		boolean containsChairs = false;
		
		for(UIObjects o : objs) {
			if(o instanceof Tables) {
				containsTables = true;
			} else if(o instanceof Spots) {
				containsChairs = true;
			}
		}
		return (containsTables && containsChairs);
	}
	
	
	/**
	 * showSelectionUpdate
	 */
	private void showSelectionUpdate() {
		// check if previous call is still in action
		if(updatingSelection)
			return;
		
		List<UIObjects> objs = controller.getHighlightedObjects();
		
		// Do nothing if the list is empty
		if(objs.isEmpty())
			return;
		
		updatingSelection = true;
		
		// Setup radio buttons if different types are selected
		ToggleGroup group    = new ToggleGroup();
		RadioButton tableBtn = new RadioButton("Tables");
		RadioButton chairBtn = new RadioButton("Chairs");
		
		tableBtn.setToggleGroup(group);
		chairBtn.setToggleGroup(group);
		
		UIObjects first = objs.get(0);
		if(first instanceof Tables) {
			tableBtn.setSelected(true);
		} else if(first instanceof Spots){
			chairBtn.setSelected(true);
		}
		
		// setup text fields
		TextField width = createTextField(String.format("%.2f", objs.get(0).getWidth() / 15));
		TextField height = createTextField(String.format("%.2f", objs.get(0).getHeight() / 15));
		
		// setup parent node
		VBox vbox = new VBox();
		vbox.setStyle("-fx-alignment: center;-fx-spacing: 5px; -fx-padding: 40px 0px 0px 0px;");
		
		vbox.setOnKeyPressed(key -> {
			if(key.getCode() == KeyCode.ENTER) {
				ArrayList<UIObjects> toUpdate = controller.getHighlightedObjects();
				String selected = ((RadioButton)group.getSelectedToggle()).getText();
				double newWidth;
				double newHeight;
				
				// Determine which type of object is selected
				if(selected.equals("Tables")) {
					toUpdate = filterObjs(toUpdate, Tables.class);
					newHeight = Double.parseDouble(height.getText()) * 15;
				} else if(selected.equals("Chairs")) {
					toUpdate = filterObjs(toUpdate, Spots.class);
					newHeight = Double.parseDouble(width.getText()) * 15;
				} else {
					return;
				}
				newWidth  = Double.parseDouble(width.getText()) * 15;
				
				controller.resizeAll(toUpdate, newWidth, newHeight);
			}
		});
		
		// display the options of type to be resized
		vbox.getChildren().addAll(tableBtn, chairBtn, width, height);
		((VBox)((Pane)root.getLeft()).getChildren().get(0)).getChildren().add(vbox);
	}
	
	/**
	 * Creates and returns a text field
	 *
	 * @param str is the initial text in the field
	 */
	private TextField createTextField(String str) {
		TextField result = new TextField(str);
		result.setMaxWidth(80);
		result.setMaxHeight(40);
		return result;
	}
	
	/**
	 * Clear the selection for the floor plan objects
	 */
	private void clearSelectionUpdate() {
		// Check if showSelectionUpdate has been called
		if(!updatingSelection)
			return;
		// get the VBox where the text fields are placed
		VBox left = (VBox)((Pane)root.getLeft()).getChildren().get(0);
		// remove the last added node
		left.getChildren().remove(left.getChildren().size()-1);
		updatingSelection = false;
	}
	
	/**
	 * Sets up the top menu bar
	 *
	 * @param menuBar         Menu bar
	 * @param stage           Stage
	 * @param menuNew         Menu item responsible for the behavior of 'New'
	 * @param controller      Controller
	 * @param isConstructView boolean
	 */
	static void setupMenuBar(MenuBar menuBar, Stage stage, MenuItem menuNew,
							 Controller controller, boolean isConstructView) {
		Menu menu = new Menu("File");
		MenuItem menuOpen = new MenuItem("Open");
		MenuItem menuSave = new MenuItem("Save");
		MenuItem menuSaveAs = new MenuItem("Save As");
		MenuItem menuClose = new MenuItem("Close");
		// Open menu option
		menuOpen.setOnAction(menuEvent -> {
			System.out.println("Menu Item \"Open\" Selected");
			FC.setTitle("Open");
			CURR_FILE = FC.showOpenDialog(stage);
			CURR_FILE_NAME = CURR_FILE.getName();
			System.out.println("open file name: "+ CURR_FILE_NAME);
			controller.load(CURR_FILE);
		});
		// Save menu option
		menuSave.setOnAction(menuEvent -> {
			System.out.println("Menu Item \"Save\" Selected");
			File tempFile = new File(ImageLoader.floorPlanDir + CURR_FILE_NAME);
			// Make sure the file name has the text extension
			if(!CURR_FILE_NAME.contains(".txt"))
				CURR_FILE_NAME += ".txt";
			CURR_FILE = new File(ImageLoader.floorPlanDir+CURR_FILE_NAME);
			// Delete the temporary file
			if(tempFile.delete()) {
				System.out.println("Temp file deletion successful");
			} else {
				System.out.println("Temp file deletion unsuccessful");
			}
			// Make sure the file has been created
			if(CURR_FILE == null) {
				FC.setTitle("Save As");
				CURR_FILE = FC.showSaveDialog(stage);
			}
			controller.save(CURR_FILE);
		});
		// Save As menu option
		menuSaveAs.setOnAction(menuEvent -> {
			System.out.println("Menu Item \"Save As\" Selected");
			FC.setTitle("Save As...");
			CURR_FILE = FC.showSaveDialog(stage);
			File tempFile = new File(ImageLoader.floorPlanDir + CURR_FILE_NAME);
			if(tempFile.delete()) {
				System.out.println("Temp file deletion successful");
			} else {
				System.out.println("Temp file deletion unsuccessful");
			}
			controller.save(CURR_FILE);
		});
		// Close menu option
		menuClose.setOnAction(menuEvent -> {
			System.out.println("Menu Item \"Close\" Selected");
			Platform.exit();
		});
		
		menu.getItems().addAll(menuNew, menuOpen, menuSave, menuSaveAs, menuClose);
		menuBar.getMenus().add(menu);
	}
	
	/**
	 * Sets up the mouse controls for the center pane and the inner center pane.
	 *
	 * @param center      pane
	 * @param innerCenter pane
	 */
	static void setupCenterMouse(Pane center, Pane innerCenter) {
		// Allows right mouse drag to pan the child.
		center.setOnMousePressed((event) -> {
			if(event.isPrimaryButtonDown())
				return;
			double mouseX = event.getSceneX();
			double mouseY = event.getSceneY();
			double paneX = innerCenter.getTranslateX();
			double paneY = innerCenter.getTranslateY();
			
			center.setOnMouseDragged((event2) -> {
				if(event2.isPrimaryButtonDown())
					return;
				innerCenter.setTranslateX(paneX + (event2.getSceneX() - mouseX));
				innerCenter.setTranslateY(paneY + (event2.getSceneY() - mouseY));
			});
		});
		// Allows mouse scroll wheel to zoom in and out the child
		center.setOnScroll((event) -> {
			double changeScale = 0;
			Bounds bounds = innerCenter.localToScene(innerCenter.getBoundsInLocal());
			double changeX = event.getSceneX() - (bounds.getWidth() / 2 + bounds.getMinX());
			double changeY = event.getSceneY() - (bounds.getHeight() / 2 + bounds.getMinY());
			
			if(event.getDeltaY() < 0 && innerCenter.getScaleX() > 0.1) {
				changeScale = -0.1;
				innerCenter.setScaleX(innerCenter.getScaleX() * (1 + changeScale));
				innerCenter.setScaleY(innerCenter.getScaleY() * (1 + changeScale));
			} else if(event.getDeltaY() > 0 && innerCenter.getScaleX() < 5) {
				changeScale = 0.1;
				innerCenter.setScaleX(innerCenter.getScaleX() * (1 + changeScale));
				innerCenter.setScaleY(innerCenter.getScaleY() * (1 + changeScale));
			}
			
			innerCenter.setTranslateX(innerCenter.getTranslateX() - changeScale * changeX);
			innerCenter.setTranslateY(innerCenter.getTranslateY() - changeScale * changeY);
		});
	}
	
	/**
	 * Button behavior when the reset zoom button is clicked
	 *
	 * @param drawPane The draw pane
	 */
	static HBox setupZoomButtons(Pane drawPane) {
		HBox zoomBox = new HBox();
		Button zoomInButton = new Button();
		Button zoomOutButton = new Button();
		Button resetZoomButton = new Button();
		resetZoomButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-reset_24px.png")));
		zoomInButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-in_24px.png")));
		zoomOutButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-out_24px.png")));
		zoomBox.setStyle(buttonBoxStyle);
		
		// Reset zoom
		resetZoomButton.setOnMouseClicked(e -> {
			System.out.println("\"Reset Zoom\" button clicked");
			drawPane.setScaleX(0.3);
			drawPane.setScaleY(0.3);
			drawPane.setTranslateX(-((2100/2.0)-(CENTER_WIDTH/2.0)));
			drawPane.setTranslateY(-((2100/2.0)-(CENTER_HEIGHT/2.0)));
		});
		// Zoom in
		zoomInButton.setOnMouseClicked(e -> {
			System.out.println("\"Zoom In\" button clicked");
			drawPane.setScaleX(drawPane.getScaleX() * 1.1);
			drawPane.setScaleY(drawPane.getScaleY() * 1.1);
		});
		// Zoom out
		zoomOutButton.setOnMouseClicked(e -> {
			System.out.println("\"Zoom Out\" button clicked");
			drawPane.setScaleX(drawPane.getScaleX() / 1.1);
			drawPane.setScaleY(drawPane.getScaleY() / 1.1);
		});
		
		zoomBox.getChildren().addAll(resetZoomButton, zoomInButton, zoomOutButton);
		return zoomBox;
	}
}
