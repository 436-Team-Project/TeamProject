package View;

import Controller.Controller;
import Model.Model;
import Model.Spots;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
	static Background LEFT_BG = new Background(
			new BackgroundFill(Color.rgb(124, 132, 161, 1), CornerRadii.EMPTY, Insets.EMPTY));
	static Background RIGHT_BG = new Background(
			new BackgroundFill(Color.rgb(124, 132, 161, 1), CornerRadii.EMPTY, Insets.EMPTY));
	static Background CENTER_BG = new Background(
			new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY));
	static Background TOP_BG = new Background(
			new BackgroundFill(Color.rgb(196, 153, 143, 1), CornerRadii.EMPTY, Insets.EMPTY));
	
	static int INFO_FONT_SIZE = 12;
	static String INFO_FONT = "Arial";
	
	// The dimensions of the entire application
	final static int APP_WIDTH = 1200;
	final static int APP_HEIGHT = 800;
	
	// Dimensions for the panels inside the border pane
	final static int LEFT_WIDTH = 150;
	final static int RIGHT_WIDTH = 150;
	final static int TOP_HEIGHT = 50;
	final static int BOT_HEIGHT = 50;
	final static int CENTER_WIDTH = (APP_WIDTH - (LEFT_WIDTH + RIGHT_WIDTH));
	final static int CENTER_HEIGHT = (APP_HEIGHT - (TOP_HEIGHT + BOT_HEIGHT));
	
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
		HBox hBox = new HBox();
		Label topHeader = new Label("Hosting");
		HBox menuBar = initTopControls(stage);
		
		topPane.setBackground(TOP_BG);
		topPane.setPrefHeight(TOP_HEIGHT);
		topHeader.setFont(new Font(View.HEADER_FONT, View.HEADER_FONT_SIZE));
		topHeader.setPadding(new Insets(0, 0, 0, 100));
		topHeader.setStyle("-fx-font-weight: bold;");
		
		hBox.getChildren().addAll(menuBar, topHeader);
		topPane.getChildren().add(hBox);
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
		
		HBox hBox = new HBox();
		hBox.setSpacing(25);
		hBox.getChildren().addAll(menuBar, zoomBox);
		return hBox;
	}
	
	/**
	 * Initializes the center panel of the border pane
	 * <p>
	 * This panel is responsible for performing the user's mouse actions.
	 */
	private Pane initCenterPanel() {
		Pane centerOuter = new Pane();
		Pane centerInner = drawPane; // Draw panel
		centerOuter.setPrefWidth(CENTER_WIDTH);
		centerOuter.setPrefHeight(CENTER_HEIGHT);
		centerOuter.setBackground(CENTER_BG);
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
		VBox vbox = new VBox();
		VBox buttonBox = new VBox();
		Label leftPanelHeader = new Label("Host Elements");
		
		leftPane.setPrefWidth(LEFT_WIDTH);
		leftPane.setBackground(LEFT_BG);
		buttonBox.setSpacing(5);
		leftPanelHeader.setFont(new Font(View.HEADER_FONT, View.HEADER_FONT_SIZE));
		leftPanelHeader.setPadding(new Insets(0, 0, 0, 0));
		leftPanelHeader.setStyle("-fx-font-weight: bold");
		
		Button getSafePosButton = new Button("Get Safe Position");
		Button assignGuestButton = new Button("Assign Guest");
		Button removeGuestButton = new Button("Remove Guest");
		ToggleButton distanceToggle = new ToggleButton("6 Foot Radius");
		
		getSafePosButton.setPrefSize(View.BUTTON_WIDTH, View.BUTTON_HEIGHT);
		assignGuestButton.setPrefSize(View.BUTTON_WIDTH, View.BUTTON_HEIGHT);
		removeGuestButton.setPrefSize(View.BUTTON_WIDTH, View.BUTTON_HEIGHT);
		distanceToggle.setPrefSize(View.BUTTON_WIDTH, View.BUTTON_HEIGHT);
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
		vbox.getChildren().addAll(leftPanelHeader, buttonBox);
		leftPane.getChildren().add(vbox);
		return leftPane;
	}
	
	/**
	 * Initializes the right panel of the border pane
	 * <p>
	 * This panel shows the user the current values for various product attributes.
	 */
	private Pane initRightPanel() {
		Pane rightPane = new Pane();
		rightPane.setPrefWidth(RIGHT_WIDTH);
		rightPane.setBackground(RIGHT_BG);
		Label leftPanelHeader = new Label("Host Info");
		VBox hostInfoBox = new VBox();
		VBox vbox = new VBox();
		
		hostInfoBox.setSpacing(5);
		leftPanelHeader.setFont(new Font(View.HEADER_FONT, View.HEADER_FONT_SIZE));
		leftPanelHeader.setPadding(new Insets(0, 0, 0, 0));
		leftPanelHeader.setStyle("-fx-font-weight: bold");
		
		Label info1Label = new Label("Total: ");
		Label info2Label = new Label("Unavailable: ");
		Label info3Label = new Label("Free: ");
		String str1 = String.valueOf(controller.countSpotType("total"));
		String str2 = String.valueOf(controller.countSpotType("unavailable"));
		String str3 = String.valueOf(controller.countSpotType("free"));
		
		info1Value = new Label(str1);
		info2Value = new Label(str2);
		info3Value = new Label(str3);
		
		info1Label.setFont(new Font(INFO_FONT, INFO_FONT_SIZE));
		info2Label.setFont(new Font(INFO_FONT, INFO_FONT_SIZE));
		info3Label.setFont(new Font(INFO_FONT, INFO_FONT_SIZE));
		
		hostInfoBox.getChildren().addAll(
				new HBox(info1Label, info1Value),
				new HBox(info2Label, info2Value),
				new HBox(info3Label, info3Value));
		
		vbox.getChildren().addAll(leftPanelHeader, hostInfoBox);
		rightPane.getChildren().add(vbox);
		return rightPane;
	}
}
