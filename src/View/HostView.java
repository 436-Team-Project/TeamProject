package View;

import Controller.Controller;
import Model.Model;
import Model.Spots;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
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
	private  Pane anim;
	private Label info1Value;
	private Label info2Value;
	private Label info3Value;
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
	public HostView(View view, Stage stage, BorderPane root, Model model, Controller controller,
					Pane drawPane, Pane anim) {
		super();
		this.view = view;
		this.controller = controller;
		this.model = model;
		this.root = root;
		this.drawPane = drawPane;
		this.anim = anim;
		
		fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text", "*.txt"));
		
//		 Initialize border pane's panels
		root.setCenter(initCenterPanel());
		root.setTop(initTopPanel(stage));
		root.setLeft(initLeftPanel());
		root.setRight(initRightPanel());
		
		controller.displayModel(); // Call the controller to display the current state of the model
	}
	
	
	/**
	 * Initializes the top panel of the border pane
	 * <p>
	 * This panel is responsible for the controls not directly responsible for main "Host" features.
	 * Auxiliary features like manipulating the canvas and other things.
	 */
	private Pane initTopPanel(Stage stage) {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(196, 153, 143, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefHeight(TOP_HEIGHT);
		
		HBox hBox = new HBox();
		Label topHeader = new Label("Hosting");
		topHeader.setFont(new Font("Arial", 30));
		topHeader.setPadding(new Insets(0, 0, 0, 100));
		topHeader.setStyle("-fx-font-weight: bold;");
		
		HBox menuBar = initTopControls(stage);
		hBox.getChildren().addAll(menuBar, topHeader);
		result.getChildren().add(hBox);
		return result;
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
		
		// Set up the menu bar
		MenuBar menuBar = new MenuBar();
		MenuItem menuNew = new MenuItem("New");
		menuNew.setOnAction(menuEvent -> {
			// TODO: Implement "New" menu button for the host view
		});
		View.setupMenuBar(menuBar, stage, menuNew, controller, false);
		HBox zoomBox = View.setupZoomButtons(drawPane);
		HBox result = new HBox();
		result.setSpacing(25);
		result.getChildren().addAll(menuBar, zoomBox);
		
		return result;
	}
	
	/**
	 * Initializes the center panel of the border pane
	 * <p>
	 * This panel is responsible for performing the user's mouse actions.
	 */
	private Pane initCenterPanel() {
//		Pane result = new Pane();
		Pane result = new Pane();
		StackPane centerInner = new StackPane();
		Pane child = drawPane; // Draw panel
		Pane child2 = anim;
		anim.setMouseTransparent(true);
		centerInner.getChildren().addAll(child, child2);
		
		result.setPrefWidth(CENTER_WIDTH);
		result.setPrefHeight(CENTER_HEIGHT);
		result.setBackground(CENTER_BG);
		result.getChildren().add(centerInner);
		
		// Allows right mouse drag to pan the child.
		View.setupCenterMouse(result, child);
		return result;
	}
	
	/**
	 * Initializes the left panel of the border pane
	 * <p>
	 * This panel has the buttons for the features the "Host" view is responsible for performing.
	 */
	private Pane initLeftPanel() {
		Pane result = new Pane();
		result.setPrefWidth(LEFT_WIDTH);
		result.setBackground(LEFT_BG);
		
		VBox vbox = new VBox();
		VBox buttonBox = new VBox();
		Label leftPanelHeader = new Label("Host Elements");
		
		buttonBox.setSpacing(5);
		leftPanelHeader.setFont(new Font(View.HEADER_FONT, View.HEADER_FONT_SIZE));
		leftPanelHeader.setPadding(new Insets(0, 0, 0, 0));
		leftPanelHeader.setStyle("-fx-font-weight: bold");
		
		Button getSafePosButton = new Button("Get Safe Position");
		Button assignGuestButton = new Button("Assign Guest");
		Button removeGuestButton = new Button("Remove Guest");
		ToggleButton distanceToggle = new ToggleButton("6 Foot Radius");
//		tb1.setToggleGroup(group);
		distanceToggle.setSelected(false);
		
		getSafePosButton.setPrefSize(View.BUTTON_WIDTH, View.BUTTON_HEIGHT);
		assignGuestButton.setPrefSize(View.BUTTON_WIDTH, View.BUTTON_HEIGHT);
		removeGuestButton.setPrefSize(View.BUTTON_WIDTH, View.BUTTON_HEIGHT);
		distanceToggle.setPrefSize(View.BUTTON_WIDTH, View.BUTTON_HEIGHT);
		
		getSafePosButton.setOnAction(e -> {
			System.out.println("\"Get Safe Position\" button clicked");
			Spots spot = controller.getBestSpot();
			controller.getBestSpot();
			controller.displayModel();
		});
		assignGuestButton.setOnAction(e -> {
			System.out.println("\"Assign Guest\" button clicked");
			view.isAssigningSeat = true;
			view.isRemovingSeat = false;
			// TODO: Implement assigning a guest to a chair/spot on the floor
		});
		removeGuestButton.setOnAction(e -> {
			System.out.println("\"Remove Guest\" button clicked");
			view.isAssigningSeat = false;
			view.isRemovingSeat = true;
			// TODO: Implement removing a guest from the list of occupants in the floor
		});
		distanceToggle.setOnAction(clickEvent -> {
			System.out.println("Distance toggle pressed");
			if(distanceToggle.isSelected()) {
//				view.distanceToggled = true;
				for(Node child : drawPane.getChildren()) {
					if(child instanceof Circle) {
						Circle circle = (Circle) child;
						Circle current = new Circle(circle.getCenterX(), circle.getCenterY(), 50);
						current.setFill(Color.rgb(0,0,0,0));
						current.setStroke(Color.BLUE);
						current.setStrokeWidth(4);
						anim.getChildren().add(current);
					}
					
				}
				System.out.println("Toggle value true");

				
//				drawPane.getChildren().add(new Rectangle(10,10,100,200));
			} else {
//				view.distanceToggled = false;
				System.out.println("Toggle values false");
				anim.getChildren().removeIf(child -> child instanceof Circle);
			}
		});
		
		buttonBox.getChildren().addAll(getSafePosButton, assignGuestButton, removeGuestButton, distanceToggle);
		vbox.getChildren().addAll(leftPanelHeader, buttonBox);
		result.getChildren().add(vbox);
		return result;
	}
	
	private DoubleProperty startX = new SimpleDoubleProperty();
	private DoubleProperty startY = new SimpleDoubleProperty();
	private DoubleProperty shownX = new SimpleDoubleProperty();
	private DoubleProperty shownY = new SimpleDoubleProperty();
	
	/**
	 * Initializes the right panel of the border pane
	 * <p>
	 * This panel shows the user the current values for various product attributes.
	 */
	private Pane initRightPanel() {
		Pane result = new Pane();
		result.setPrefWidth(RIGHT_WIDTH);
		result.setBackground(RIGHT_BG);
		
		Label leftPanelHeader = new Label("Host Info");
		VBox hostInfoBox = new VBox();
		VBox vbox = new VBox();
		
		hostInfoBox.setSpacing(5);
		leftPanelHeader.setFont(new Font(View.HEADER_FONT, View.HEADER_FONT_SIZE));
		leftPanelHeader.setPadding(new Insets(0, 0, 0, 0));
		leftPanelHeader.setStyle("-fx-font-weight: bold");
		
		Label info1Label = new Label("Occupants: ");
		Label info2Label = new Label("Info 2: ");
		Label info3Label = new Label("Info 3: ");
		
		// TODO: Implement counting the number of occupants currently on the floor and display
		info1Value = new Label("# of occupants");
		info1Value.textProperty().bind(
				Bindings.format("(%.1f, %.1f)", startX, startY)
		);
		
		// TODO: Choose and implement a second attribute of the product to display to the user
		info2Value = new Label("Answer 2");
		info2Value.textProperty().bind(
				Bindings.format("(%.1f, %.1f)", startX, startY)
		);
		
		// TODO: Choose and implement a third attribute of the product to display to the user
		info3Value = new Label("Answer 3");
		info3Value.textProperty().bind(
				Bindings.format("(%.1f, %.1f)", startX, startY)
		);
		
		info1Label.setFont(new Font(INFO_FONT, INFO_FONT_SIZE));
		info2Label.setFont(new Font(INFO_FONT, INFO_FONT_SIZE));
		info3Label.setFont(new Font(INFO_FONT, INFO_FONT_SIZE));
		
		hostInfoBox.getChildren().addAll(
				new HBox(info1Label, info1Value),
				new HBox(info2Label, info2Value),
				new HBox(info3Label, info3Value));
		
		vbox.getChildren().addAll(leftPanelHeader, hostInfoBox);
		result.getChildren().add(vbox);
		return result;
	}
}
