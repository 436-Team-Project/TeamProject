package View;

import Controller.Controller;
import Model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
 * CuCurrent colors of the panels inside the main border pane are temporary.
 */
public class View extends Application implements Observer {
	
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
	final static double WALL_WIDTH = 25;
	final static double WALL_HEIGHT = 25;
	final static double CHAIR_WIDTH = 25;
	final static double CHAIR_HEIGHT = 25;
	final static double TABLE_WIDTH = 60;
	final static double TABLE_HEIGHT = 60;
	static String currentFileName;
	static File currentFile;
	
	boolean selecting = false;
	boolean drawingWall = false;
	boolean placingChair = false;
	boolean placingObject = false;
	boolean isHosting = false;
	
	FileChooser fc;
	
	Scene scene;
	Controller controller; // Controller of MVC
	Model model; // model of MVC
	KeyboardListener kbListener;
	
	BorderPane root; // Main pane
	Pane drawPane; // Drawing Canvas
	Canvas grid; // Grid overlaying canvas
	
	/**
	 * Initialize
	 */
	@Override
	public void init() {
		// controller = new Controller();
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
		
		fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text", "*.txt"));
		
		root = new BorderPane();
		root.setCenter(initCenterPanel());
		root.setLeft(initLeftPanel());
		root.setTop(initTopPanel(primaryStage));
		root.setBottom(initBottomPanel(primaryStage));
		
		scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
		kbListener = new KeyboardListener(scene, controller, drawPane);
		
		primaryStage.getIcons().add(ImageLoader.getImage("app_icon_black_60px.png"));
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
		ArrayList<Label> labels = new ArrayList<>();
		
		drawPane.getChildren().clear(); // - clear central panel
		drawPane.getChildren().add(grid); // - redraw all items
		
		for(UIObjects obj : itemList) {
			if(obj instanceof Wall) {
//				System.out.println("Drawing wall");
				Line wall = initLine(obj.getX(), obj.getY(), obj.getX2(), obj.getY2(),
						obj.isHighlighted());
				
				// These are invisible circular regions acting as endpoints of the line
				// to allow dragging of on end.
				Circle leftEnd = new Circle(obj.getX(), obj.getY(), 10);
				Circle rightEnd = new Circle(obj.getX2(), obj.getY2(), 10);
				
				leftEnd.setFill(Color.TRANSPARENT);
				rightEnd.setFill(Color.TRANSPARENT);
				
				setEndPointMouseAction(leftEnd, wall, obj, true);     // last argument is flag
				setEndPointMouseAction(rightEnd, wall, obj, false);  // for left or right
				setMouseAction(wall, obj);
				
				Label measure = new Label(String.valueOf(lineLength(wall)));
				measure.setTranslateX((wall.getEndX() - wall.getStartX()) / 2 + wall.getStartX());
				measure.setTranslateY((wall.getEndY() - wall.getStartY()) / 2 + wall.getStartY());
				measure.setMouseTransparent(true);
				measure.setAlignment(Pos.CENTER);
				measure.setTextFill(Color.RED);
				labels.add(measure); // Keep track of this label so that it can be brought to the front
				
				drawPane.getChildren().add(wall);
				drawPane.getChildren().add(leftEnd);
				drawPane.getChildren().add(rightEnd);
				drawPane.getChildren().add(measure);
			} else if(obj instanceof Spots) {
//				System.out.println("Drawing chair");
				double radius = obj.getWidth() / 2;
				Circle chair = initChair(obj.getX() + radius, obj.getY() + radius, radius,
						obj.isHighlighted());
				
				setMouseAction(chair, obj);
				drawPane.getChildren().add(chair);
			} else {
//				System.out.println("Drawing object");
				Rectangle o = initObject(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(),
						obj.isHighlighted());
				
				setMouseAction(o, obj);
				drawPane.getChildren().add(o);
			}
		}
		for(Label l : labels) {
			l.toFront();
		}
	}
	
	/**
	 * Set the event handling of mouse actions for a given javafx circle.
	 *
	 * @param endPoint the circle to set the handlers for
	 * @param wall     the wall for which the circle is an endpoint
	 * @param uio      the UIObjects associated with the wall
	 * @param isLeft   flag to determine whether it's left of right endpoint
	 */
	private void setEndPointMouseAction(Circle endPoint, Line wall, UIObjects uio, boolean isLeft) {
		// drag left end of wall
		endPoint.setOnMousePressed(event -> {
			selecting = false;
			drawingWall = false;
			placingChair = false;
			placingObject = false;
			
			endPoint.setOnMouseDragged(event2 -> {
				if(isLeft) {
					wall.setStartX(event2.getX());
					wall.setStartY(event2.getY());
				} else {
					wall.setEndX(event2.getX());
					wall.setEndY(event2.getY());
				}
				endPoint.setCenterX(event2.getX());
				endPoint.setCenterY(event2.getY());
			});
		});
		
		endPoint.setOnMouseReleased(event -> {
			boolean inDrawPane = (endPoint.getCenterX() > 0
					&& endPoint.getCenterX() < drawPane.getWidth())
					&& (endPoint.getCenterY() > 0 && endPoint.getCenterY() < drawPane.getHeight());
			
			if(!inDrawPane) {
				System.out.println("Outside of central panel");
				controller.displayModel();
			} else {
				if(isLeft) {
					controller.updateCurrentObject(event.getX(), event.getY(), uio.getX2(),
							uio.getY2(), uio.getId());
				} else {
					controller.updateCurrentObject(uio.getX(), uio.getY(), event.getX(),
							event.getY(), uio.getId());
				}
			}
		});
	}
	
	/**
	 * Set the event handling of mouse actions for a object
	 *
	 * @param obj the object to set the handlers for
	 * @param uio the UIObjects associated with the given object
	 */
	private void setMouseAction(Shape obj, UIObjects uio) {
		obj.setOnMousePressed(event -> {
			selecting = false;
			drawingWall = false;
			placingChair = false;
			placingObject = false;
			
			Point2D p = drawPane.sceneToLocal(event.getSceneX(), event.getSceneY());
			double mouseX = p.getX();
			double mouseY = p.getY();
			double objTransX = obj.getTranslateX();
			double objTransY = obj.getTranslateY();
			
			obj.setOnMouseDragged(event2 -> {
				Point2D p2 = drawPane.sceneToLocal(event2.getSceneX(), event2.getSceneY());
				obj.setTranslateX(objTransX + (p2.getX() - mouseX));
				obj.setTranslateY(objTransY + (p2.getY() - mouseY));
			});
		});
		
		obj.setOnMouseReleased(event -> {
			
			// check if placed within the draw pane
			Bounds objBounds = obj.getBoundsInParent();
			
			boolean inDrawPane = (objBounds.getMinX() > 0
					&& objBounds.getMaxX() < drawPane.getWidth())
					&& (objBounds.getMinY() > 0 && objBounds.getMaxY() < drawPane.getHeight());
			
			if(!inDrawPane) {
				System.out.println("Outside of central panel");
				controller.displayModel();
			} else {
				double transX = obj.getTranslateX();
				double transY = obj.getTranslateY();
				
				controller.updateCurrentObject(uio.getX() + transX, uio.getY() + transY,
						uio.getX2() + transX, uio.getY2() + transY, uio.getId());
			}
		});
	}
	
	/**
	 * Initializes the bottom panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initBottomPanel(Stage stage) {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(196, 153, 143, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setOnMouseClicked(mouseEvent -> controller.deselectAll());
		result.setPrefHeight(BOT_HEIGHT);
		result.getChildren().add(initBottomControls(stage));
		return result;
	}
	
	/**
	 * Initializes the controls for the bottom panel
	 *
	 * @return HBox
	 */
	private HBox initBottomControls(Stage stage) {
		HBox result = new HBox();
		Button hostButton = new Button("Host");
		Button constructButton = new Button("Construct");
		
		hostButton.setStyle("-fx-pref-width: 120px; -fx-pref-height: 45px;");
		constructButton.setStyle("-fx-pref-width: 120px; -fx-pref-height: 45px;");
		
		hostButton.setOnMouseClicked(e -> {
			isHosting = true;
			HostView hostRoot = new HostView(stage, root, model, controller, drawPane);
			root.setBottom(initBottomPanel(stage));
		});
		
		constructButton.setOnMouseClicked(e -> {
			isHosting = false;
			root.setCenter(initCenterPanel());
			root.setTop(initTopPanel(stage));
			root.setLeft(initLeftPanel());
			root.setRight(initRightPanel());
			controller.displayModel();
			root.setBottom(initBottomPanel(stage));
		});
		
		if(!isHosting) {
			hostButton.setVisible(true);
			constructButton.setVisible(false);
		} else {
			hostButton.setVisible(false);
			constructButton.setVisible(true);
		}
		
		result.getChildren().addAll(hostButton, constructButton);
		
		HBox.setHgrow(result, Priority.ALWAYS);
		return result;
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
		result.setOnMouseClicked(mouseEvent -> controller.deselectAll());
		VBox vbox = new VBox();
		VBox buttonBox = new VBox();
		
		Label leftPanelHeader = new Label("Canvas Elements");
		
		Button selection = new Button("Select");
		Button placeWall = new Button("Place Wall");
		Button placeChair = new Button("Place Chair");
		Button placeObject = new Button("Place Table");
		
		leftPanelHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 20px;" +
				"-fx-padding: 10px 50px 20px 50px;");
		buttonBox.setStyle("-fx-alignment: center;-fx-spacing: 5px");
		selection.setStyle("-fx-pref-width: 100px; -fx-pref-height: 40px;");
		placeWall.setStyle("-fx-pref-width: 100px; -fx-pref-height: 40px;");
		placeChair.setStyle("-fx-pref-width: 100px; -fx-pref-height: 40px");
		placeObject.setStyle("-fx-pref-width: 100px; -fx-pref-height: 40px");
		
		selection.setOnMouseClicked(event -> {
			selecting = true;
			drawingWall = false;
			placingObject = false;
			placingChair = false;
		});
		// --- Event handling "Place Wall" button ---\
		placeWall.setOnMouseClicked(event -> {
			selecting = false;
			drawingWall = true;
			placingObject = false;
			placingChair = false;
		});
		
		// --- Event handling "Place Chair" button ---
		placeChair.setOnMousePressed(event -> {
			selecting = false;
			drawingWall = false;
			placingObject = false;
			placingChair = true;
		});
		
		// --- Event handling "Place Object" button ---
		placeObject.setOnMousePressed(event -> {
			selecting = false;
			drawingWall = false;
			placingChair = false;
			placingObject = true;
		});
		
		buttonBox.getChildren().addAll(selection, placeWall, placeChair, placeObject);
		vbox.getChildren().addAll(leftPanelHeader, buttonBox);
		result.getChildren().add(vbox);
		return result;
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
		Pane result = new Pane();
		Pane child = initCenterInnerPanel(); // Draw panel
		result.setOnMouseClicked(mouseEvent -> controller.deselectAll());
		result.setBackground(new Background(
				new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(CENTER_WIDTH);
		result.setPrefHeight(CENTER_HEIGHT);
		result.getChildren().add(child);
		
		// Allows right mouse drag to pan the child.
		result.setOnMousePressed((event) -> {
			if(event.isPrimaryButtonDown())
				return;
			double mouseX = event.getSceneX();
			double mouseY = event.getSceneY();
			double paneX = child.getTranslateX();
			double paneY = child.getTranslateY();
			
			result.setOnMouseDragged((event2) -> {
				if(event2.isPrimaryButtonDown())
					return;
				child.setTranslateX(paneX + (event2.getSceneX() - mouseX));
				child.setTranslateY(paneY + (event2.getSceneY() - mouseY));
			});
		});
		
		// Allows mouse scroll wheel to zoom in and out the child
		result.setOnScroll((event) -> {
			double changeScale = 0;
			Bounds bounds = child.localToScene(child.getBoundsInLocal());
			double changeX = event.getSceneX() - (bounds.getWidth() / 2 + bounds.getMinX());
			double changeY = event.getSceneY() - (bounds.getHeight() / 2 + bounds.getMinY());
			if(event.getDeltaY() < 0 && child.getScaleX() > 0.1) {
				changeScale = -0.1;
				child.setScaleX(child.getScaleX() * (1 + changeScale));
				child.setScaleY(child.getScaleY() * (1 + changeScale));
			} else if(event.getDeltaY() > 0 && child.getScaleX() < 5) {
				changeScale = 0.1;
				child.setScaleX(child.getScaleX() * (1 + changeScale));
				child.setScaleY(child.getScaleY() * (1 + changeScale));
			}
			child.setTranslateX(child.getTranslateX() - changeScale * changeX);
			child.setTranslateY(child.getTranslateY() - changeScale * changeY);
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
		drawPane = result;
		result.setBackground(
				new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(CENTER_WIDTH * 3.0 / 4.0);
		result.setPrefHeight(CENTER_HEIGHT * 3.0 / 4.0);
		result.setTranslateX((CENTER_WIDTH / 4.0) / 2);
		result.setTranslateY((CENTER_HEIGHT / 4.0) / 2);
		result.setClip(new Rectangle(result.getPrefWidth(), result.getPrefHeight()));
		grid = initializeGrid();
		result.getChildren().add(grid);
		
		// Event-handling for mouse on drawing canvas depending on which tool is selected.
		result.setOnMousePressed(event -> {
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(
					event.getSceneX() - LEFT_WIDTH, event.getSceneY() - TOP_HEIGHT, 1, 1);
			
			if(event.getButton() == MouseButton.PRIMARY && inDrawPane) {
				Point2D click = drawPane.sceneToLocal(event.getSceneX(), event.getSceneY());
				UIObjects clickedObject = controller.getObject(click.getX(), click.getY());
				// If ALT is not held down deselect all highlighted
				if(!kbListener.isKeyPressed(KeyCode.CONTROL)) {
					controller.deselectAll();
				}
				if(clickedObject != null) {
					clickedObject.setHighlighted(!clickedObject.isHighlighted());
					System.out.printf("Clicked on: %s\n", clickedObject.toString());
//					System.out.printf("Items:\n%s\n", controller.printItems());
				}
				
				if(drawingWall) {
					Line wallBound = initLineBounds(event.getSceneX(), event.getSceneY());
					Label measurement = new Label("0");
					measurement.setMinSize(100, 100);
					measurement.setAlignment(Pos.CENTER);
					measurement.setTextFill(Color.RED);
					StackPane sp = new StackPane();
					sp.setTranslateX(event.getSceneX());
					sp.setTranslateY(event.getSceneY());
					sp.getChildren().add(wallBound);
					sp.getChildren().add(measurement);
					sp.setMaxSize(APP_WIDTH, APP_HEIGHT);
					//root.getChildren().add(wallBound);
					root.getChildren().add(sp);
					
					result.setOnMouseDragged(event2 -> {
						wallBound.setEndX(event2.getSceneX());
						wallBound.setEndY(event2.getSceneY());
						sp.setTranslateX((event2.getSceneX() - event.getSceneX()) / 2 + event.getSceneX());
						sp.setTranslateY((event2.getSceneY() - event.getSceneY()) / 2 + event.getSceneY());
						double length = lineLength(wallBound);
						measurement.setText(String.valueOf(length));
						
						result.setOnMouseReleased(event3 -> {
							boolean inDrawPaneEnd = drawPane.getBoundsInParent().intersects(
									event3.getSceneX() - LEFT_WIDTH, event3.getSceneY() - TOP_HEIGHT, 1, 1);
							if(!inDrawPaneEnd) {
								System.out.println("Outside of central panel");
							} else if(event3.getButton() == MouseButton.PRIMARY && drawingWall) {
								controller.createNewObject("wall", event.getX(), event.getY(),
										event3.getX(), event3.getY());
							}
							root.getChildren().remove(sp);
							//root.getChildren().remove(wallBound);
						});
					});
				}
				if(placingChair) {
					Rectangle chairBounds = initObjectBounds(CHAIR_WIDTH, CHAIR_HEIGHT);
					updateBound(event, chairBounds);
					root.getChildren().add(chairBounds);
					
					result.setOnMouseDragged(event2 -> {
						updateBound(event2, chairBounds);
					});
					
					result.setOnMouseReleased(event3 -> {
						boolean inDrawPaneEnd = drawPane.getBoundsInParent().intersects(
								event3.getSceneX() - LEFT_WIDTH,
								event3.getSceneY() - TOP_HEIGHT, 1, 1) &&
								drawPane.getBoundsInParent().intersects(
										event3.getSceneX() - LEFT_WIDTH + chairBounds.getWidth(),
										event3.getSceneY() - TOP_HEIGHT + chairBounds.getHeight(), 1, 1);
						if(!inDrawPaneEnd) {
							System.out.println("Outside of central panel");
							root.getChildren().remove(chairBounds);
						} else if(placingChair && event3.getButton() == MouseButton.PRIMARY) {
							Point2D p = drawPane.sceneToLocal(event3.getSceneX(), event3.getSceneY());
							double x2 = p.getX() + CHAIR_WIDTH;
							double y2 = p.getY() + CHAIR_HEIGHT;
							controller.createNewObject("chair", p.getX(), p.getY(), x2, y2);
							root.getChildren().remove(chairBounds);
						}
					});
				}
				if(placingObject) {
					Rectangle objectBounds = initObjectBounds(TABLE_WIDTH, TABLE_HEIGHT);
					drawingWall = false;
					placingChair = false;
					placingObject = true;
					updateBound(event, objectBounds);
					root.getChildren().add(objectBounds);
					
					result.setOnMouseDragged(event2 -> {
						updateBound(event2, objectBounds);
					});
					
					result.setOnMouseReleased(event3 -> {
						boolean inDrawPaneEnd = drawPane.getBoundsInParent().intersects(
								event3.getSceneX() - LEFT_WIDTH,
								event3.getSceneY() - TOP_HEIGHT, 1, 1) &&
								drawPane.getBoundsInParent().intersects(
										event3.getSceneX() - LEFT_WIDTH + objectBounds.getWidth(),
										event3.getSceneY() - TOP_HEIGHT + objectBounds.getHeight(), 1, 1);
						if(!inDrawPaneEnd) {
							System.out.println("Outside of central panel");
							root.getChildren().remove(objectBounds);
						} else if(placingObject && event3.getButton() == MouseButton.PRIMARY) {
							Point2D p = drawPane.sceneToLocal(event3.getSceneX(), event3.getSceneY());
							double x2 = p.getX() + TABLE_WIDTH;
							double y2 = p.getY() + TABLE_HEIGHT;
							controller.createNewObject("object", p.getX(), p.getY(), x2, y2);
							root.getChildren().remove(objectBounds);
						}
					});
				}
			}
			
			// When the user drags with LMB to select multiple items
			if(selecting && event.isPrimaryButtonDown() && inDrawPane) {
				System.out.println("Selecting");
				Point2D p = drawPane.sceneToLocal(event.getSceneX(), event.getSceneY());
				Rectangle rectBound = initRectBounds(p.getX(), p.getY());
				
				double startX = p.getX();
				double startY = p.getY();
				drawPane.getChildren().add(rectBound);
				
				result.setOnMouseDragged(event2 -> {
					Point2D p2 = drawPane.sceneToLocal(event2.getSceneX(), event2.getSceneY());
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
					result.setOnMouseReleased(event3 -> {
						if(event2.isPrimaryButtonDown() && selecting) {
							double heightFinal = endX - startX;
							double widthFinal = endY - startY;
							double x1 = rectBound.getX();
							double y1 = rectBound.getY();
							double y2 = rectBound.getY() + rectBound.getHeight();
							double x2 = rectBound.getX() + rectBound.getWidth();
							System.out.printf("Width[%.1f], Height[%.1f] | " +
											"x1[%.1f], y1[%.1f], x2[%.1f], y2[%.1f]\n",
									heightFinal, widthFinal, x1, y1, x2, y2);
							
							controller.highlightSelected(x1, y1, x2, y2);
							drawPane.getChildren().remove(rectBound);
						}
					});
				});
			}
		});
		return result;
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
		gc.setStroke(Color.GRAY);
		gc.setLineWidth(1);
		double offset = 30;
		for(double i = offset; i < width; i += offset) {
			gc.strokeLine(i, 0, i, height);
			gc.strokeLine(0, i, width, i);
		}
		return grid;
	}
	
	/**
	 * Initializes the top panel in the root border pane
	 *
	 * @return Pane
	 */
	private Pane initTopPanel(Stage stage) {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(196, 153, 143, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefHeight(TOP_HEIGHT);
		
		result.setOnMouseClicked(mouseEvent -> controller.deselectAll());
		
		HBox hBox = new HBox();
		Label topHeader = new Label("Floor Plan Creator");
		topHeader.setStyle(
				"-fx-font-weight: bold;-fx-font-size: 30px;" + "-fx-padding: 0px 0px 0px 100px;");
		
		HBox menuBar = initTopControls(stage);
		hBox.getChildren().addAll(menuBar, topHeader);
		result.getChildren().add(hBox);
		return result;
	}
	
	/**
	 * Initializes the controls in the top panel of the main border pane
	 *
	 * @return HBox
	 */
	private HBox initTopControls(Stage stage) {
		HBox result = new HBox();
		HBox undoRedoBox = new HBox();
		HBox zoomBox = new HBox();
		HBox manipulateBox = new HBox();
		MenuBar menuBar = new MenuBar();
		
		Menu menu = new Menu("File");
		MenuItem menuItemNew = new MenuItem("New");
		MenuItem menuItemOpen = new MenuItem("Open");
		MenuItem menuItemSave = new MenuItem("Save");
		MenuItem menuItemSaveAs = new MenuItem("Save As");
		MenuItem menuItemClose = new MenuItem("Close");
		
		result.setStyle("-fx-spacing: 25px;");
		undoRedoBox.setStyle("-fx-spacing: 2px;");
		zoomBox.setStyle("-fx-spacing: 2px;");
		zoomBox.setStyle("-fx-spacing: 2px;");
		
		menuItemNew.setOnAction(e -> {
			System.out.println("Menu Item \"New\" Selected");
			
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("New Floor Plan");
			dialog.setHeaderText("What is the name of your new floor plan?");
			dialog.setContentText("Floor plan name:");
			Optional<String> dialogResult = dialog.showAndWait();
			
			// User provided filename and clicked "ok"; create temporary file
			if(dialogResult.isPresent()) {
				dialogResult.ifPresent(fileName -> currentFileName = fileName);
				fc.setInitialFileName(currentFileName);
				model = new Model();
				model.addObserver(this);
				controller = new Controller(model);
				kbListener.setController(controller);
				try {
					File tempFile = new File("Saved/" + currentFileName);
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
		menuItemOpen.setOnAction(e -> {
			System.out.println("Menu Item \"Open\" Selected");
			fc.setTitle("Open");
			currentFile = fc.showOpenDialog(stage);
			controller.load(currentFile);
		});
		menuItemSave.setOnAction(e -> {
			System.out.println("Menu Item \"Save\" Selected");
			File tempFile = new File("Saved/" + currentFileName);
			tempFile.delete();
			if(currentFile == null) {
				fc.setTitle("Save As");
				currentFile = fc.showSaveDialog(stage);
			}
			controller.save(currentFile);
		});
		menuItemSaveAs.setOnAction(e -> {
			System.out.println("Menu Item \"Save As\" Selected");
			fc.setTitle("Save As...");
			currentFile = fc.showSaveDialog(stage);
			controller.save(currentFile);
		});
		
		menuItemClose.setOnAction(e -> {
			System.out.println("Menu Item \"Close\" Selected");
			Platform.exit();
		});
		
		menu.getItems().add(menuItemNew);
		menu.getItems().add(menuItemOpen);
		menu.getItems().add(menuItemSave);
		menu.getItems().add(menuItemSaveAs);
		menu.getItems().add(menuItemClose);
		menuBar.getMenus().add(menu);
		
		Button undoButton = new Button();
		Button redoButton = new Button();
		Button resetZoomButton = new Button();
		Button zoomInButton = new Button();
		Button zoomOutButton = new Button();
		Button deleteButton = new Button("Delete");
		Button placeholderButton = new Button("Placeholder");
		
		undoButton.setGraphic(new ImageView(ImageLoader.getImage("undo_24px.png")));
		redoButton.setGraphic(new ImageView(ImageLoader.getImage("redo_24px.png")));
		resetZoomButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-reset_24px.png")));
		zoomInButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-in_24px.png")));
		zoomOutButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-out_24px.png")));
		
		undoButton.setOnMouseClicked(e -> {
			System.out.println("\"Undo\" button clicked");
			controller.undo();
		});
		redoButton.setOnMouseClicked(e -> {
			System.out.println("\"Redo\" button clicked");
			// TODO: Implement the redo feature
			controller.redo();
			
		});
		resetZoomButton.setOnMouseClicked(e -> {
			System.out.println("\"Reset Zoom\" button clicked");
			drawPane.setScaleX(1);
			drawPane.setScaleY(1);
			drawPane.setTranslateX((CENTER_WIDTH / 4.0) / 2);
			drawPane.setTranslateY((CENTER_HEIGHT / 4.0) / 2);
		});
		zoomInButton.setOnMouseClicked(e -> {
			System.out.println("\"Zoom In\" button clicked");
			drawPane.setScaleX(drawPane.getScaleX() * 1.1);
			drawPane.setScaleY(drawPane.getScaleY() * 1.1);
		});
		zoomOutButton.setOnMouseClicked(e -> {
			System.out.println("\"Zoom Out\" button clicked");
			drawPane.setScaleX(drawPane.getScaleX() / 1.1);
			drawPane.setScaleY(drawPane.getScaleY() / 1.1);
		});
		deleteButton.setOnMouseClicked(e -> {
			System.out.println("\"Delete\" button clicked");
			controller.removeHighlighted();
		});
		placeholderButton.setOnMouseClicked(e -> {
			System.out.println("\"Placeholder\" button clicked");
			// TODO: Implement a Placeholder feature
		});
		undoRedoBox.getChildren().addAll(undoButton, redoButton);
		zoomBox.getChildren().addAll(resetZoomButton, zoomInButton, zoomOutButton);
		manipulateBox.getChildren().addAll(deleteButton, placeholderButton);
		result.getChildren().addAll(menuBar, undoRedoBox, zoomBox, manipulateBox);
		
		return result;
	}
	
	/**
	 *
	 * @param x  the new's object bound's width in pixels
	 * @param y the new's object bound's height in pixels
	 * @return rectangle
	 */
	private Rectangle initRectBounds(double x, double y) {
		Rectangle r = new Rectangle(x, y, 0, 0);
		r.setStroke(Color.rgb(75, 161, 219));
		r.setStrokeWidth(1);
		r.getStrokeDashArray().addAll(5.0);
		r.setFill(Color.rgb(75, 161, 219, 0.5));
		return r;
	}
	
	/**
	 * Initializes a dashed rectangle representing the bounds of the object being
	 * placed.
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
	 * Initializes a dashed line representing the bounds of the wall being drawn.
	 *
	 * @param x the new's object bound's width in pixels
	 * @param y the new's object bound's height in pixels
	 * @return rectangle
	 */
	private Line initLineBounds(double x, double y) {
		Line l = new Line(x, y, x, y);
		l.setStroke(Color.BLACK);
		l.setStrokeWidth(1);
		l.getStrokeDashArray().addAll(5.0);
		l.setFill(Color.TRANSPARENT);
		return l;
	}
	
	/**
	 * Initializes a new UI object at the given coordinates and with the given
	 * dimensions
	 *
	 * @param x      vertical position
	 * @param y      horizontal position
	 * @param width  the new object's radius
	 * @param height the new object's radius
	 * @return rectangle
	 */
	private Rectangle initObject(double x, double y, double width, double height, boolean highlight) {
		Rectangle r = new Rectangle(x, y, width, height);
		if(highlight) {
			r.setFill(Color.GOLD);
		} else {
			r.setFill(Color.GRAY);
		}
		// TODO: EventHandler for selecting, moving, and editing rectangles
		return r;
	}
	
	/**
	 * Initializes a new UI object at the given coordinates and with the given
	 * dimensions (For objects represented as a line)
	 *
	 * @param x  vertical start position
	 * @param y  horizontal start position
	 * @param x2 vertical end position
	 * @param y2 horizontal end position
	 * @return line
	 */
	private Line initLine(double x, double y, double x2, double y2, boolean highlight) {
		Line l = new Line(x, y, x2, y2);
		l.setStrokeWidth(5);
		if(highlight) {
			l.setStroke(Color.GOLD);
		} else {
			l.setStroke(Color.BLACK);
		}
		// TODO: EventHandler for selecting, moving, and editing lines
		return l;
	}
	
	/**
	 * Initializes a new UI object at the given coordinates and with the given
	 * dimensions
	 *
	 * @param x      vertical position
	 * @param y      horizontal position
	 * @param radius the new object's radius in pixels
	 * @return rectangle
	 */
	private Circle initChair(double x, double y, double radius, boolean highlight) {
		Circle c = new Circle(x, y, radius);
		c.setStroke(Color.BLACK);
		c.setStrokeWidth(1);
		
		if(highlight) {
			c.setFill(Color.GOLD);
		} else {
			c.setFill(Color.WHITE);
		}
		
		// TODO: EventHandler for selecting, moving, and editing circles
		return c;
	}
	
	/**
	 * Updates position of object to mouse's position.
	 *
	 * @param event
	 * @param objectBounds
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
	 * @param l
	 */
	private double lineLength(Line l) {
		return Math.round(Math.sqrt(Math.pow(Math.abs(l.getStartX() - l.getEndX()), 2)
				+ Math.pow(Math.abs(l.getStartY() - l.getEndY()), 2)));
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
	 *
	 *
	 * @param objs is a list of the selected objects
	 */
	private void showSelectionUpdate(ArrayList<UIObjects> objs) {
		// Do nothing if the list is empty
		if(objs.isEmpty())
			return;

		ToggleGroup group = null;
		RadioButton tableBtn = null;
		RadioButton chairBtn = null;


		// setup text fields
		TextField w = createTextField(String.valueOf(objs.get(0).getWidth()));
		TextField h = createTextField(String.valueOf(objs.get(0).getHeight()));


		// setup parant node
		VBox vbox = new VBox();

		vbox.setOnKeyPressed(key -> {
			if(key.getCode() == KeyCode.ENTER) {
				ArrayList<UIObjects> toUpdate = objs;

				if(objectsVary(objs)) {
					String selected = ((RadioButton)group.getSelectedToggle()).getText();

					if(selected.equals("Tables")) {
						toUpdate = filterObjs(objs, Tables.class);
					} else if(selected.equals("Chairs")) {
						toUpdate = filterObjs(objs, Spots.class);
					} else {
						return;
					}
				}

				for(UIObjects o : toUpdate) {
					double newWidth  = Double.parseDouble(w.getText());
					double newHeight = Double.parseDouble(h.getText());
					controller.resize(o, newWidth, newHeight);
				}
			}
		});

		vbox.setStyle("-fx-alignment: center;-fx-spacing: 5px; -fx-padding: 40px 0px 0px 0px;");

		// Setup radio buttons if different types are selected
		if(objectsVary(objs)) {
			group = new ToggleGroup();
			tableBtn = new RadioButton("Tables");
			chairBtn = new RadioButton("Chairs");

			tableBtn.setToggleGroup(group);
			chairBtn.setToggleGroup(group);

			vbox.getChildren().addAll(tableBtn, chairBtn);
		}

		vbox.getChildren().addAll(w, h);

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

	private void clearSelectionUpdate() {
		// get the VBox where the text fields are placed
		VBox left = ((Pane)root.getLeft()).getChildren().get(0);

		// remove the last added node
		left.getChildren().remove(left.getChildren().size()-1);
	}
}
