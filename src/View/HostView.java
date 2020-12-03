package View;

import Controller.Controller;
import Model.Model;
import Model.Spots;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to show a floor plan layout and is where the user will ask for safe positions
 * through the calculations and assign/remove guests from the event.
 */
public class HostView {
	// Pane Backgrounds
	static String leftPaneStyle =
			"-fx-background-color: #7B83A0;-fx-pref-width: 150px;-fx-pref-height: 300px;";
	static String rightPaneStyle =
			"-fx-background-color: #7B83A0;-fx-pref-width: 150px;-fx-pref-height: 300px;";
	static String infoStyle = "-fx-background-color: #434B64;-fx-alignment: top-center;" +
			"-fx-pref-width: 120px;-fx-pref-height: 120px;";
	static String infoLabelStyle = "-fx-font-weight: normal;-fx-font-style: regular;" +
			"-fx-font: 18px Arial;-fx-text-fill: white;-fx-pref-width: 120px;" +
			"-fx-pref-height: 30px;-fx-alignment: center;";
	static String infoValueStyle = "-fx-font-weight: bold;-fx-font-style: regular;" +
			"-fx-font: 30px Arial;-fx-text-fill: white;-fx-pref-width: 120px;" +
			"-fx-pref-height: 90px;-fx-alignment: center;";
	
	// The dimensions of the entire application
	final static int APP_WIDTH = 1200;
	final static int APP_HEIGHT = 800;
	
	// Dimensions for the panels inside the border pane
	final static int CENTER_WIDTH = (APP_WIDTH - (150 + 150));
	final static int CENTER_HEIGHT = (APP_HEIGHT - (50 + 50));
	
	private final View view;
	private final Model model;
	private final Controller controller;
	private final BorderPane root;
	private final Pane drawPane;
	
	static Label info1Value;
	static Label info2Value;
	static Label info3Value;
	static ToggleButton distanceToggle;
	
	File currentFile;
	String currentFileName;
	FileChooser fc;
	
	/**
	 * Constructs a HostView object
	 *
	 * @param root the border pane of the "construct" view
	 * @param model the current state of the product
	 * @param controller the controller of the product
	 * @param drawPane the canvas to draw on
	 */
	public HostView(View view, Stage stage, BorderPane root,
					Model model, Controller controller, Pane drawPane) {
		super();
		this.view = view;
		this.controller = controller;
		this.model = model;
		this.root = root;
		this.drawPane = drawPane;
		
		fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text", "*.txt"));
		// Initialize border pane's panels
		root.setCenter(initCenterPanel());
		root.setTop(initTopPanel(stage));
		root.setLeft(initLeftPanel());
		root.setRight(initRightPanel());
		// Call the controller to display the current state of the model
		controller.displayModel();
	}
	
	/**
	 * Initializes the top panel of the border pane
	 * <p>
	 * This panel is responsible for the controls not directly responsible for main "Host" features.
	 * Auxiliary features like manipulating the canvas and other things.
	 */
	private Pane initTopPanel(Stage stage) {
		Pane topPane = new Pane();
		topPane.setStyle(View.topPaneStyle);
		topPane.getChildren().add(initTopControls(stage));
		return topPane;
	}
	
	/**
	 * Initializes the controls in the top panel of the border pane
	 * <p>
	 * This takes care of creating the buttons and performing the actions of those buttons like
	 * "redo", "undo", "zoom in", etc.
	 *
	 * @return HBox
	 */
	private HBox initTopControls(Stage stage) {
		MenuBar menuBar = new MenuBar();
		MenuItem menuNew = new MenuItem("New");
		menuNew.setOnAction(menuEvent -> {
			// TODO: Implement "New" menu button for the host view
			System.out.println("Menu item \"New\" selected in Host View");
		});
		
		View.setupMenuBar(menuBar, stage, menuNew, controller, false);
		HBox zoomBox = View.setupZoomButtons(drawPane);
		
		Label topHeader = new Label("Hosting");
		topHeader.setStyle(View.viewHeader +
				"-fx-pref-width: 400px;-fx-pref-height: 50px;-fx-padding: 0 0 0 200;");
		
		HBox topControlBox = new HBox();
		topControlBox.setStyle("-fx-pref-width: 1200px;-fx-pref-height: 50px;-fx-spacing: 25px;");
		topControlBox.getChildren().addAll(menuBar, zoomBox, topHeader);
		return topControlBox;
	}
	
	/**
	 * Initializes the center panel of the border pane
	 * <p>
	 * This panel is responsible for performing the user's mouse actions.
	 */
	private Pane initCenterPanel() {
		Pane centerOuter = new Pane();
		Pane centerInner = drawPane; // Draw panel
		centerInner.setStyle(View.centerInnerStyle);
		centerInner.setPrefSize(CENTER_WIDTH * 3.0 / 4.0, CENTER_HEIGHT * 3.0 / 4.0);
		centerInner.setTranslateX((CENTER_WIDTH / 4.0) / 2);
		centerInner.setTranslateY((CENTER_HEIGHT / 4.0) / 2);
		centerInner.setClip(new Rectangle(centerInner.getPrefWidth(), centerInner.getPrefHeight()));
//		grid = initializeGrid();
//		centerInner.getChildren().add(grid);
		
		centerOuter.setPrefWidth(CENTER_WIDTH);
		centerOuter.setPrefHeight(CENTER_HEIGHT);
		centerOuter.setStyle(View.centerOuterStyle);
		// Allows right mouse drag to pan the child.
		View.setupCenterMouse(centerOuter, centerInner);
		
		centerOuter.getChildren().add(centerInner);
		return centerOuter;
	}
	
	/**
	 * Initializes the left panel of the border pane
	 * <p>
	 * This panel has the buttons for the features the "Host" view is responsible for performing.
	 */
	private Pane initLeftPanel() {
		Pane leftPane = new Pane();
		VBox leftControlBox = new VBox();
		VBox buttonBox = new VBox();
		Label leftPanelHeader = new Label("Host Elements");
		
		leftPane.setStyle(leftPaneStyle);
		leftControlBox.setStyle("-fx-pref-width: 150px;-fx-alignment: top-center;");
		buttonBox.setStyle("-fx-pref-width: 150px;-fx-alignment: center;-fx-spacing: 20px;");
		leftPanelHeader.setStyle(View.labelStyle +
				"-fx-pref-width: 150px;-fx-alignment: top-center;-fx-padding: 20 0 60 0;");
		
		Button getSafePosButton = new Button("Get Safe Position");
		Button assignGuestButton = new Button("Assign Guest");
		Button removeGuestButton = new Button("Remove Guest");
		ToggleButton distanceToggle = new ToggleButton("6 Foot Radius");
		getSafePosButton.setStyle(View.buttonStyle);
		assignGuestButton.setStyle(View.buttonStyle);
		removeGuestButton.setStyle(View.buttonStyle);
		distanceToggle.setStyle(View.buttonStyle);
		
		distanceToggle.setSelected(false);
		// Event handling for "Get Safe Position" button
		getSafePosButton.setOnAction(e -> {
			System.out.println("\"Get Safe Position\" button clicked");
			Spots spot = controller.getBestSpot();
			controller.getBestSpot();
			controller.displayModel();
		});
		// Event handling for "Assign Guest" button
		assignGuestButton.setOnAction(e -> {
			System.out.println("\"Assign Guest\" button clicked");
			view.isAssigningSeat = true;
			view.isRemovingSeat = false;
		});
		// Event handling for "Remove Guest" button
		removeGuestButton.setOnAction(e -> {
			System.out.println("\"Remove Guest\" button clicked");
			view.isAssigningSeat = false;
			view.isRemovingSeat = true;
		});
		// Event handling for "6 Foot Radius" button
		distanceToggle.setOnAction(clickEvent -> {
			System.out.println("Distance toggle pressed");
			
			List<Circle> rings = new ArrayList<>();
			
			if(distanceToggle.isSelected()) {
				for(Node child : drawPane.getChildren()) {
					if(child instanceof Circle) {
						Circle circle = (Circle) child;
						
						// Distinguish from the circles used for handling the walls
						if(circle.getFill() != Color.TRANSPARENT) {
							Circle ring = new Circle(circle.getCenterX(), circle.getCenterY(), 90);
							ring.setStroke(Color.rgb(130,132,161,0.5));
							ring.setStrokeWidth(4);
							ring.setFill(Color.rgb(0,0,0,0));
							rings.add(ring);
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
		});
		
		buttonBox.getChildren().addAll(getSafePosButton, assignGuestButton,
				removeGuestButton, distanceToggle);
		leftControlBox.getChildren().addAll(leftPanelHeader, buttonBox);
		leftPane.getChildren().add(leftControlBox);
		return leftPane;
	}
	

	
	/**
	 * Initializes the right panel of the border pane
	 * <p>
	 * This panel shows the user the current values for various product attributes.
	 */
	private Pane initRightPanel() {
		Pane rightPane = new Pane();
		VBox rightPaneBox = new VBox();
		Label rightPanelHeader = new Label("Information");
		VBox hostInfoBox = new VBox();
		
		rightPane.setStyle(leftPaneStyle);
		rightPaneBox.setStyle("-fx-alignment: center;");
		rightPanelHeader.setStyle(View.labelStyle +
				"-fx-pref-width: 150px;-fx-alignment: top-center;-fx-padding: 20 0 60 0;");
		hostInfoBox.setStyle("-fx-alignment: top-center;-fx-pref-width: 150px;" +
				"-fx-pref-height: 500px;-fx-spacing: 20px;-fx-fill-width: false;");
		
		Label info1Label = new Label("Total: ");
		Label info2Label = new Label("Unavailable: ");
		Label info3Label = new Label("Free: ");
		info1Label.setStyle(infoLabelStyle);
		info2Label.setStyle(infoLabelStyle);
		info3Label.setStyle(infoLabelStyle);
		
		info1Value = new Label(String.valueOf(controller.countSpotType("total")));
		info2Value = new Label(String.valueOf(controller.countSpotType("unavailable")));
		info3Value = new Label(String.valueOf(controller.countSpotType("free")));
		info1Value.setStyle(infoValueStyle);
		info2Value.setStyle(infoValueStyle);
		info3Value.setStyle(infoValueStyle);
		
		VBox infoTotalBox = new VBox();
		VBox infoUnavailableBox = new VBox();
		VBox infoFreeBox = new VBox();
		infoTotalBox.setStyle(infoStyle);
		infoUnavailableBox.setStyle(infoStyle);
		infoFreeBox.setStyle(infoStyle);
		infoTotalBox.getChildren().addAll(info1Label, info1Value);
		infoUnavailableBox.getChildren().addAll(info2Label, info2Value);
		infoFreeBox.getChildren().addAll(info3Label, info3Value);
		
		hostInfoBox.getChildren().addAll(infoTotalBox, infoUnavailableBox, infoFreeBox);
		rightPaneBox.getChildren().addAll(rightPanelHeader, hostInfoBox);
		rightPane.getChildren().add(rightPaneBox);
		return rightPane;
	}
}
