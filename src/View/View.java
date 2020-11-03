package View;

import Controller.Controller;
import Model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
	final double TABLE_WIDTH = 60;
	final double TABLE_HEIGHT = 60;
	
	boolean selecting = false;
	boolean drawingWall = false;
	boolean placingChair = false;
	boolean placingObject = false;
	boolean isHosting = false;
	
	File currentFile;
	String currentFileName;
	FileChooser fc;
	
	Controller controller; // Controller of MVC
	Model model; // model of MVC
	
	BorderPane root; // Main pane
	Pane drawPane;
	
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
		
		Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
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
		// - clear central panel
		drawPane.getChildren().clear();
		// - redraw all items
		
		for(UIObjects obj : itemList) {
			if(obj instanceof Wall) {
				System.out.println("Drawing wall");
				Line wall = initLine(obj.getX(), obj.getY(), obj.getX2(), obj.getY2());
				
				// These are invisible circular regions acting as endpoints of the line
				// to allow dragging of on end.
				Circle leftEnd = new Circle(obj.getX(), obj.getY(), 10);
				Circle rightEnd = new Circle(obj.getX2(), obj.getY2(), 10);
				
				leftEnd.setFill(Color.TRANSPARENT);
				rightEnd.setFill(Color.TRANSPARENT);
				
				setEndPointMouseAction(leftEnd, wall, obj, true);     // last argument is flag
				setEndPointMouseAction(rightEnd, wall, obj, false);  // for left or right
				
				setMouseAction(wall, obj);
				
				drawPane.getChildren().add(wall);
				drawPane.getChildren().add(leftEnd);
				drawPane.getChildren().add(rightEnd);
			} else if(obj instanceof Spots) {
				System.out.println("Drawing chair");
				double radius = obj.getWidth() / 2;
				Circle chair = initChair(obj.getX() + radius, obj.getY() + radius, radius);
				
				setMouseAction(chair, obj);
				
				drawPane.getChildren().add(chair);
			} else {
				System.out.println("Drawing object");
				Rectangle o = initObject(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
				o.setFill(Color.GRAY);
				
				setMouseAction(o, obj);
				
				drawPane.getChildren().add(o);
			}
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
			drawingWall = false;
			
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
			
			boolean inDrawPane = (endPoint.getCenterX() > 0 && endPoint.getCenterX() < drawPane.getWidth()) &&
					(endPoint.getCenterY() > 0 && endPoint.getCenterY() < drawPane.getHeight());
			
			if(!inDrawPane) {
				System.out.println("Outside of central panel");
				controller.displayModel();
			} else {
				if(isLeft) {
					controller.updateCurrentObject(event.getX(), event.getY(),
							uio.getX2(), uio.getY2(), uio.getId());
				} else {
					controller.updateCurrentObject(uio.getX(), uio.getY(),
							event.getX(), event.getY(), uio.getId());
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
			drawingWall = false;
			
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
			
			boolean inDrawPane = (objBounds.getMinX() > 0 && objBounds.getMaxX() < drawPane.getWidth()) &&
					(objBounds.getMinY() > 0 && objBounds.getMaxY() < drawPane.getHeight());
			
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
		
		Rectangle wallBounds = initObjectBounds(WALL_WIDTH, WALL_HEIGHT);
		Rectangle chairBounds = initObjectBounds(CHAIR_WIDTH, CHAIR_HEIGHT);
		// Temporarily using default wall dimensions
		Rectangle objectBounds = initObjectBounds(WALL_WIDTH, WALL_HEIGHT);
		
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
		
		/*
		 * placeWall.setOnMousePressed(event -> { updateBound(event, wallBounds);
		 * root.getChildren().add(wallBounds); });
		 *
		 * placeWall.setOnMouseDragged(event2 -> { updateBound(event2, wallBounds); });
		 *
		 * placeWall.setOnMouseReleased(event3 -> { boolean inDrawPane =
		 * drawPane.getBoundsInParent().intersects( event3.getSceneX() - LEFT_WIDTH,
		 * event3.getSceneY() - TOP_HEIGHT, 1, 1);
		 *
		 * if (!inDrawPane) { System.out.println("Outside of central panel"); } else {
		 * Point2D p = drawPane.sceneToLocal(event3.getSceneX(), event3.getSceneY());
		 * controller.createNewObject("wall", p.getX(), p.getY(), WALL_WIDTH,
		 * WALL_HEIGHT); } root.getChildren().remove(wallBounds); });
		 */
		
		// --- Event handling "Place Chair" button ---
		placeChair.setOnMousePressed(event -> {
			selecting = false;
			drawingWall = false;
			placingObject = false;
			placingChair = true;
			updateBound(event, chairBounds);
			root.getChildren().add(chairBounds);
			
			placeChair.setOnMouseDragged(event2 -> {
				updateBound(event2, chairBounds);
			});
			
			placeChair.setOnMouseReleased(event3 -> {
				boolean inDrawPane = drawPane.getBoundsInParent().intersects(
						event3.getSceneX() - LEFT_WIDTH, event3.getSceneY() - TOP_HEIGHT, 1, 1);
				
				if(!inDrawPane) {
					System.out.println("Outside of central panel");
				} else {
					Point2D p = drawPane.sceneToLocal(event3.getSceneX(), event3.getSceneY());
					double x2 = p.getX() + CHAIR_WIDTH;
					double y2 = p.getY() + CHAIR_HEIGHT;
					controller.createNewObject("chair", p.getX(), p.getY(), x2, y2);
				}
				root.getChildren().remove(chairBounds);
			});
		});
		
		// --- Event handling "Place Object" button ---
		placeObject.setOnMousePressed(event -> {
			selecting = false;
			drawingWall = false;
			placingChair = false;
			placingObject = true;
			updateBound(event, objectBounds);
			root.getChildren().add(objectBounds);
			
			placeObject.setOnMouseDragged(event2 -> {
				updateBound(event2, objectBounds);
			});
			
			placeObject.setOnMouseReleased(event3 -> {
				boolean inDrawPane = drawPane.getBoundsInParent().intersects(
						event3.getSceneX() - LEFT_WIDTH, event3.getSceneY() - TOP_HEIGHT, 1, 1);
				
				if(!inDrawPane) {
					System.out.println("Outside of central panel");
				} else {
					Point2D p = drawPane.sceneToLocal(event3.getSceneX(), event3.getSceneY());
					double x2 = p.getX() + TABLE_WIDTH;
					double y2 = p.getY() + TABLE_HEIGHT;
					controller.createNewObject("object", p.getX(), p.getY(), x2, y2);
				}
				root.getChildren().remove(objectBounds);
			});
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
		result.setBackground(
				new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(CENTER_WIDTH * 3.0 / 4.0);
		result.setPrefHeight(CENTER_HEIGHT * 3.0 / 4.0);
		result.setTranslateX((CENTER_WIDTH / 4.0) / 2);
		result.setTranslateY((CENTER_HEIGHT / 4.0) / 2);
		drawPane = result;
		
		// Event-handling for drawing wall.
		result.setOnMousePressed(event -> {
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(
					event.getSceneX() - LEFT_WIDTH, event.getSceneY() - TOP_HEIGHT, 1, 1);
			
			if(drawingWall && event.isPrimaryButtonDown() && inDrawPane) {
				System.out.println("Drawing");
				Point2D p = drawPane.sceneToLocal(event.getSceneX(), event.getSceneY());
				Line wallBound = initLineBounds(p.getX(), p.getY());
				double startX = p.getX();
				double startY = p.getY();
				
				drawPane.getChildren().add(wallBound);
				
				result.setOnMouseDragged(event2 -> {
					Point2D p2 = drawPane.sceneToLocal(event2.getSceneX(), event2.getSceneY());
					wallBound.setEndX(p2.getX());
					wallBound.setEndY(p2.getY());
					
					result.setOnMouseReleased(event3 -> {
						if(event2.isPrimaryButtonDown() && drawingWall) {
							controller.createNewObject("wall", p.getX(), p.getY(), p2.getX(), p2.getY());
							drawPane.getChildren().remove(wallBound);
						}
					});
				});
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
							
							controller.removeSelected(x1, y1, x2, y2);
							drawPane.getChildren().remove(rectBound);
						}
					});
				});
			}
		});
		return result;
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
		
		menuItemNew.setOnAction(e -> {
			System.out.println("Menu Item \"New\" Selected");
			
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("New Floor Plan");
			dialog.setHeaderText("What is the name of your new floor plan?");
			dialog.setContentText("Floor plan name:");
			Optional<String> dialogResult = dialog.showAndWait();
			dialogResult.ifPresent(fileName -> currentFileName = fileName);
			
			System.out.println("currentFileName = " + currentFileName);
			
			fc.setInitialFileName(currentFileName);
			
			model = new Model();
			model.addObserver(this);
			controller = new Controller(model);
			
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
		});
		menuItemOpen.setOnAction(e -> {
			System.out.println("Menu Item \"Open\" Selected");
			fc.setTitle("Open");
			currentFile = fc.showOpenDialog(stage);
			controller.load(currentFile);
		});
		menuItemSave.setOnAction(e -> {
			System.out.println("Menu Item \"Save\" Selected");
			if(currentFile == null) {
				fc.setTitle("Save As");
				currentFile = fc.showSaveDialog(stage);
				controller.save(currentFile);
			} else {
				controller.save(currentFile);
			}
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
			
		});
		resetZoomButton.setOnMouseClicked(e -> {
			System.out.println("\"Reset Zoom\" button clicked");
			drawPane.setScaleX(1);
			drawPane.setScaleY(1);
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
		undoRedoBox.getChildren().addAll(undoButton, redoButton);
		zoomBox.getChildren().addAll(resetZoomButton, zoomInButton, zoomOutButton);
		result.getChildren().addAll(menuBar, undoRedoBox, zoomBox);
		
		return result;
	}
	
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
	private Rectangle initObject(double x, double y, double width, double height) {
		Rectangle r = new Rectangle(x, y, width, height);
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
	private Line initLine(double x, double y, double x2, double y2) {
		Line l = new Line(x, y, x2, y2);
		l.setStrokeWidth(5);
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
	private Circle initChair(double x, double y, double radius) {
		Circle c = new Circle(x, y, radius);
		c.setStroke(Color.BLACK);
		c.setStrokeWidth(1);
		c.setFill(Color.WHITE);
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
}
