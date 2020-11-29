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
import javafx.scene.effect.Light;
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
	// Pane Backgrounds
	static Background LEFT_BG = new Background(
			new BackgroundFill(Color.rgb(110, 161, 141, 1), CornerRadii.EMPTY, Insets.EMPTY));
	static Background RIGHT_BG = new Background(
			new BackgroundFill(Color.rgb(124, 132, 161, 1), CornerRadii.EMPTY, Insets.EMPTY));
	static Background CENTER_OUTER_BG = new Background(
			new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY));
	static Background CENTER_INNER_BG = new Background(
			new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
	static Background TOP_BG = new Background(
			new BackgroundFill(Color.rgb(196, 153, 143, 1), CornerRadii.EMPTY, Insets.EMPTY));
	
	// Colors
	static Color HIGHLIGHT = Color.GOLD;
	static Color OCCUPIED = Color.DARKGRAY;
	static Color UNAVAILABLE = Color.RED;
	static Color FREE = Color.WHITE;
	static Color Safe = Color.LIGHTBLUE;
	
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
	static int BUTTON_WIDTH = 120;
	static int BUTTON_HEIGHT = 40;
	static int HEADER_FONT_SIZE = 20;
	static String HEADER_FONT = "Arial";
	
	// Utility Values
	static String CURR_FILE_NAME;
	static File CURR_FILE;
	static FileChooser FC;
	static KeyboardListener KBL;
	
	boolean isSelecting = false;
	boolean isDrawingWall = false;
	boolean isPlacingChair = false;
	boolean isPlacingObject = false;
	boolean isHosting = false;
	boolean isAssigningSeat = false;
	boolean isRemovingSeat = false;
	boolean updatingSelection = false;
	
	Scene scene;
	Controller controller; // Controller of MVC
	Model model; // model of MVC
	
	BorderPane root; // Main pane
	Pane drawPane; // Drawing Canvas
	Pane anim;
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
				Spots s = (Spots) obj;
//				System.out.println("Drawing chair");
				double radius = obj.getWidth() / 2;
				Circle chair = initChair(s);
				setMouseAction(chair, obj);
				drawPane.getChildren().add(chair);
			} else {
//				System.out.println("Drawing object");
				Rectangle o = initObject(obj);
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
		if (!isHosting) {
			endPoint.setOnMousePressed(event -> {
				isSelecting = false;
				isDrawingWall = false;
				isPlacingChair = false;
				isPlacingObject = false;
				
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
					System.out.println("Outside of central panel (setEndPointMouseAction)");
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
	}
	
	/**
	 * Set the event handling of mouse actions for a object
	 *
	 * @param obj the object to set the handlers for
	 * @param uio the UIObjects associated with the given object
	 */
	private void setMouseAction(Shape obj, UIObjects uio) {
		if (!isHosting) {
			obj.setOnMouseEntered(enterEvent -> {
//				System.out.println("setOnMouseEntered - "+ uio.toString());
				obj.setStrokeWidth(obj.getStrokeWidth() + 1);
			});
			
			obj.setOnMouseExited(exitedEvent -> {
//				System.out.println("setOnMouseExited - "+ uio.toString());
				obj.setStrokeWidth(obj.getStrokeWidth() - 1);
			});
			
			obj.setOnMousePressed(event -> {
//				System.out.println("setOnMousePressed - "+ uio.toString());
				obj.setStroke(Color.PINK);
				obj.setStrokeWidth(obj.getStrokeWidth() + 2);
				isSelecting = false;
				isDrawingWall = false;
				isPlacingChair = false;
				isPlacingObject = false;
				
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
//				System.out.println("setOnMouseReleased - "+ uio.toString());
				obj.setStrokeWidth(obj.getStrokeWidth() - 2);
				
				// check if placed within the draw pane
				Bounds objBounds = obj.getBoundsInParent();
				boolean inDrawPane = (objBounds.getMinX() > 0
						&& objBounds.getMaxX() < drawPane.getWidth())
						&& (objBounds.getMinY() > 0 && objBounds.getMaxY() < drawPane.getHeight());
				
				uio.setHighlighted(!uio.isHighlighted());
				showSelectionUpdate();
//				System.out.printf("Clicked on: %s\n", uio.toString());
				
				if(!inDrawPane) {
					System.out.println("Outside of central panel (setMouseAction)");
					controller.displayModel();
				} else {
					double transX = obj.getTranslateX();
					double transY = obj.getTranslateY();
					controller.updateCurrentObject(uio.getX() + transX, uio.getY() + transY,
							uio.getX2() + transX, uio.getY2() + transY, uio.getId());
					uio.setHighlighted(!uio.isHighlighted());
					showSelectionUpdate();
				}
			});
		}
		if (isHosting && obj instanceof Circle) {
			Circle c = (Circle) obj;
			obj.setOnMouseClicked(e -> {
				Spots spot = ((Spots) uio);
				if (isAssigningSeat) {
					controller.occupySpot(c.getCenterX(), c.getCenterY());
					spot.setOccupancy(true);
					System.out.println("occupied");
				} else if (isRemovingSeat) {
					spot.setOccupancy(false);
					spot.setSafety(false);
					System.out.println("freed");
				}
				String str1 = String.valueOf(controller.countSpotType("total"));
				String str2 = String.valueOf(controller.countSpotType("unavailable"));
				String str3= String.valueOf(controller.countSpotType("free"));
				HostView.info1Value.setText(str1);
				HostView.info2Value.setText(str2);
				HostView.info3Value.setText(str3);
				controller.displayModel();
			});
		}
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
		result.setOnMousePressed(pressEvent -> {
			controller.deselectAll(null);
			clearSelectionUpdate();
		});
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
			isSelecting = false;
			isDrawingWall = false;
			isPlacingChair = false;
			isPlacingObject = false;
			isAssigningSeat = false;
			isRemovingSeat = false;
			updatingSelection = false;
			controller.deselectAll(null);
			
			HostView hostRoot = new HostView(this, stage, root, model, controller, drawPane,anim);
			root.setBottom(initBottomPanel(stage));
		});
		
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
		result.setOnMousePressed(pressEvent -> {
			controller.deselectAll(null);
			clearSelectionUpdate();
		});
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
			isSelecting = true;
			isDrawingWall = false;
			isPlacingObject = false;
			isPlacingChair = false;
		});
		// --- Event handling "Place Wall" button ---\
		placeWall.setOnMouseClicked(event -> {
			isSelecting = false;
			isDrawingWall = true;
			isPlacingObject = false;
			isPlacingChair = false;
		});
		
		// --- Event handling "Place Chair" button ---
		placeChair.setOnMousePressed(event -> {
			isSelecting = false;
			isDrawingWall = false;
			isPlacingObject = false;
			isPlacingChair = true;
		});
		
		// --- Event handling "Place Object" button ---
		placeObject.setOnMousePressed(event -> {
			isSelecting = false;
			isDrawingWall = false;
			isPlacingChair = false;
			isPlacingObject = true;
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
		Pane centerOuter = new Pane();
		centerOuter.setId("Center Outer");
		Pane child = initCenterInnerPanel(); // Draw panel
		child.setId("Stack pane");
		centerOuter.setBackground(CENTER_OUTER_BG);
		centerOuter.setPrefWidth(CENTER_WIDTH);
		centerOuter.setPrefHeight(CENTER_HEIGHT);
		
		// Allows right mouse drag to pan the child.
		setupCenterMouse(centerOuter, child);
		
		centerOuter.getChildren().add(child);
		centerOuter.setOnMousePressed(pressEvent -> {
			controller.deselectAll(null);
			clearSelectionUpdate();
		});
		
		return centerOuter;
	}
	
	/**
	 * Initializes the inner panel for the center panel of the root border pane
	 *
	 * @return pane
	 */
	private StackPane initCenterInnerPanel() {
		StackPane centerInner = new StackPane();
		Pane result = new Pane();
		result.setId("Draw Pane");
		drawPane = result;
		anim = new Pane();
		anim.setId("Anim Pane");
		anim.setMouseTransparent(true);
		
		result.setBackground(
				new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefSize(CENTER_WIDTH * 3.0 / 4.0, CENTER_HEIGHT * 3.0 / 4.0);
		anim.setPrefSize(CENTER_WIDTH * 3.0 / 4.0, CENTER_HEIGHT * 3.0 / 4.0);
		result.setTranslateX((CENTER_WIDTH / 4.0) / 2);
		result.setTranslateY((CENTER_HEIGHT / 4.0) / 2);
		anim.setTranslateX((CENTER_WIDTH / 4.0) / 2);
		anim.setTranslateY((CENTER_HEIGHT / 4.0) / 2);
		
		result.setClip(new Rectangle(result.getPrefWidth(), result.getPrefHeight()));
		
		grid = initializeGrid();
		result.getChildren().add(grid);
		centerInner.getChildren().addAll(result, anim);

		// Event-handling for mouse on drawing canvas depending on which tool is selected.
		result.setOnMousePressed(pressEvent -> {
			Point2D pressedPoint = drawPane.sceneToLocal(pressEvent.getSceneX(), pressEvent.getSceneY());
			UIObjects pressedObject = controller.getObject(pressedPoint.getX(), pressedPoint.getY());
			if(pressedObject == null) {
				controller.deselectAll(null);
			}
//
			boolean inDrawPane = drawPane.getBoundsInParent().intersects(
					pressEvent.getSceneX() - LEFT_WIDTH, pressEvent.getSceneY() - TOP_HEIGHT, 1, 1);
			
			
			if(!isSelecting &&pressEvent.getButton() == MouseButton.PRIMARY && inDrawPane && !isHosting) {
				
				if(isDrawingWall) {
					Line wallBound = initLineBounds(pressEvent.getSceneX(), pressEvent.getSceneY());
					Label measurement = new Label("0");
					measurement.setMinSize(100, 100);
					measurement.setAlignment(Pos.CENTER);
					measurement.setTextFill(Color.RED);
					StackPane sp = new StackPane();
					sp.setTranslateX(pressEvent.getSceneX());
					sp.setTranslateY(pressEvent.getSceneY());
					sp.getChildren().add(wallBound);
					sp.getChildren().add(measurement);
					sp.setMaxSize(APP_WIDTH, APP_HEIGHT);
					//root.getChildren().add(wallBound);
					root.getChildren().add(sp);
					
					result.setOnMouseDragged(event2 -> {
						wallBound.setEndX(event2.getSceneX());
						wallBound.setEndY(event2.getSceneY());
						sp.setTranslateX((event2.getSceneX() - pressEvent.getSceneX()) / 2 + pressEvent.getSceneX());
						sp.setTranslateY((event2.getSceneY() - pressEvent.getSceneY()) / 2 + pressEvent.getSceneY());
						double length = lineLength(wallBound);
						measurement.setText(String.valueOf(length));
						
						result.setOnMouseReleased(event3 -> {
							boolean inDrawPaneEnd = drawPane.getBoundsInParent().intersects(
									event3.getSceneX() - LEFT_WIDTH, event3.getSceneY() - TOP_HEIGHT, 1, 1);
							if(!inDrawPaneEnd) {
								System.out.println("Outside of central panel (drawing wall)");
							} else if(event3.getButton() == MouseButton.PRIMARY && isDrawingWall) {
								controller.createNewObject("wall", pressEvent.getX(), pressEvent.getY(),
										event3.getX(), event3.getY());
							}
							root.getChildren().remove(sp);
						});
					});
				}
				if(isPlacingChair) {
					Rectangle chairBounds = initObjectBounds(CHAIR_SIZE, CHAIR_SIZE);
					updateBound(pressEvent, chairBounds);
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
							System.out.println("Outside of central panel (placing chair)");
							root.getChildren().remove(chairBounds);
						} else if(isPlacingChair && event3.getButton() == MouseButton.PRIMARY) {
							Point2D p = drawPane.sceneToLocal(event3.getSceneX(), event3.getSceneY());
							double x2 = p.getX() + CHAIR_SIZE;
							double y2 = p.getY() + CHAIR_SIZE;
							controller.createNewObject("chair", p.getX(), p.getY(), x2, y2);
							root.getChildren().remove(chairBounds);
						}
					});
				}
				if(isPlacingObject) {
					Rectangle objectBounds = initObjectBounds(TABLE_SIZE, TABLE_SIZE);
					isDrawingWall = false;
					isPlacingChair = false;
					isPlacingObject = true;
					updateBound(pressEvent, objectBounds);
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
							System.out.println("Outside of central panel (placing object)");
							root.getChildren().remove(objectBounds);
						} else if(isPlacingObject && event3.getButton() == MouseButton.PRIMARY) {
							Point2D p = drawPane.sceneToLocal(event3.getSceneX(), event3.getSceneY());
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
				Rectangle rectBound = initRectBounds(pressedPoint.getX(), pressedPoint.getY());

				double startX = pressedPoint.getX();
				double startY = pressedPoint.getY();
				anim.getChildren().add(rectBound);

				
				result.setOnMouseDragged(dragEvent -> {
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
			
					result.setOnMouseReleased(releaseEvent -> {
						if(dragEvent.isPrimaryButtonDown() && isSelecting) {
							double heightFinal = endX - startX;
							double widthFinal = endY - startY;
							double x1 = rectBound.getX();
							double y1 = rectBound.getY();
							double y2 = rectBound.getY() + rectBound.getHeight();
							double x2 = rectBound.getX() + rectBound.getWidth();
							controller.highlightSelected(x1, y1, x2, y2);
							showSelectionUpdate();
							anim.getChildren().remove(rectBound);
							isSelecting= false;
						}
					});
				});
			}
		});
		return centerInner;
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
		
		result.setOnMousePressed(pressEvent -> {
			controller.deselectAll(null);
			clearSelectionUpdate();
		});
		
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
		
		// Set up all the menu items for the top menu bar
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
		
		HBox zoomBox = setupZoomButtons(drawPane);
		
		// Set up the buttons for canvas manipulation (delete/placeholder)
		HBox manipulateBox = new HBox();
		Button deleteButton = new Button("Delete");
		Button placeholderButton = new Button("Placeholder");
		deleteButton.setOnMouseClicked(e -> {
			System.out.println("\"Delete\" button clicked");
			controller.removeHighlighted();
			clearSelectionUpdate();
		});
		placeholderButton.setOnMouseClicked(e -> {
			System.out.println("\"Placeholder\" button clicked");
			// TODO: Implement a Placeholder feature
		});
		manipulateBox.getChildren().addAll(deleteButton, placeholderButton);
		
		// Set up the buttons for redo and undo
		HBox undoRedoBox = new HBox();
		undoRedoBox.setStyle("-fx-spacing: 2px;");
		Button undoButton = new Button();
		Button redoButton = new Button();
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
		
		HBox result = new HBox();
		result.setStyle("-fx-spacing: 25px;");
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
	 * @param obj iuo
	 * @return rectangle
	 */
	private Rectangle initObject(UIObjects obj) {
		Rectangle r = new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
		
		if(obj.isHighlighted()) {
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
	 * @param spot Spot
	 * @return rectangle
	 */
	private Circle initChair(Spots spot) {
		double radius = spot.getWidth() / 2;
		Circle c = new Circle(spot.getX() + radius, spot.getY()+radius, radius);
		c.setStroke(Color.BLACK);
		c.setStrokeWidth(1);
		
		if(spot.isHighlighted()) {
			c.setFill(HIGHLIGHT);
		} else if (spot.isOccupied()) {
			c.setFill(OCCUPIED);
		} else if (spot.isSafe()) {
			c.setFill(Safe);
		} else if (!spot.isAvailable()) {
			c.setFill(UNAVAILABLE);
		} else {
			c.setFill(FREE);
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
	 * showSelectionUpdate
	 */
	private void showSelectionUpdate() {
		// check if previous call is still in action
		if(updatingSelection)
			return;
		
		ArrayList<UIObjects> objs = controller.getHighlightedObjects();

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


		// setup text fields
		TextField w = createTextField(String.valueOf(objs.get(0).getWidth()));
		TextField h = createTextField(String.valueOf(objs.get(0).getHeight()));


		// setup parant node
		VBox vbox = new VBox();

		vbox.setOnKeyPressed(key -> {
			if(key.getCode() == KeyCode.ENTER) {
				ArrayList<UIObjects> toUpdate = controller.getHighlightedObjects();

				String selected = ((RadioButton)group.getSelectedToggle()).getText();

				if(selected.equals("Tables")) {
					toUpdate = filterObjs(toUpdate, Tables.class);
				} else if(selected.equals("Chairs")) {
					toUpdate = filterObjs(toUpdate, Spots.class);
				} else {
					return;
				}

				double newWidth  = Double.parseDouble(w.getText());
				double newHeight = Double.parseDouble(h.getText());

				controller.resizeAll(toUpdate, newWidth, newHeight);
			}
		});

		vbox.setStyle("-fx-alignment: center;-fx-spacing: 5px; -fx-padding: 40px 0px 0px 0px;");

		// display the options of type to be resized
		vbox.getChildren().addAll(tableBtn, chairBtn);

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
		// Check if showSelectionUpdate has been called
		if(!updatingSelection)
			return;


		// get the VBox where the text fields are placed
		VBox left = (VBox)((Pane)root.getLeft()).getChildren().get(0);

		// remove the last added node
		left.getChildren().remove(left.getChildren().size()-1);

		updatingSelection = false;
		anim.getChildren().clear();
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
//		MenuItem menuNew = new MenuItem("New");
		MenuItem menuOpen = new MenuItem("Open");
		MenuItem menuSave = new MenuItem("Save");
		MenuItem menuSaveAs = new MenuItem("Save As");
		MenuItem menuClose = new MenuItem("Close");
		
		
		
		menuOpen.setOnAction(menuEvent -> {
			System.out.println("Menu Item \"Open\" Selected");
			FC.setTitle("Open");
			CURR_FILE = FC.showOpenDialog(stage);
			CURR_FILE_NAME = CURR_FILE.getName();
			System.out.println("open file name: "+ CURR_FILE_NAME);
			controller.load(CURR_FILE);
		});
		
		
		menuSave.setOnAction(menuEvent -> {
			System.out.println("Menu Item \"Save\" Selected");
			File tempFile = new File(ImageLoader.floorPlanDir + CURR_FILE_NAME);
			
			if(!CURR_FILE_NAME.contains(".txt")){
				CURR_FILE_NAME += ".txt";
			}
			CURR_FILE = new File(ImageLoader.floorPlanDir+CURR_FILE_NAME);
			
			if(tempFile.delete()) {
				System.out.println("Temp file deletion successful");
			} else {
				System.out.println("Temp file deletion unsuccessful");
			}
			if(CURR_FILE == null) {
				FC.setTitle("Save As");
				CURR_FILE = FC.showSaveDialog(stage);
			}
			controller.save(CURR_FILE);
		});
		
		
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
		zoomBox.setSpacing(2);
		Button zoomInButton = new Button();
		Button zoomOutButton = new Button();
		Button resetZoomButton = new Button();
		resetZoomButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-reset_24px.png")));
		zoomInButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-in_24px.png")));
		zoomOutButton.setGraphic(new ImageView(ImageLoader.getImage("zoom-out_24px.png")));

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
		
		zoomBox.getChildren().addAll(resetZoomButton, zoomInButton, zoomOutButton);
		return zoomBox;
	}
}
