package View;

import Controller.Controller;
import Model.Model;
import Model.Spots;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * This class is used to show a floor plan layout and is where the user will ask for safe positions
 * through the calculations and assign/remove guests from the event.
 */
public class HostView {
	// The dimensions of the entire application
	final int APP_HEIGHT = 800;
	final int APP_WIDTH = 1200;
	
	// Dimensions for the panels inside the border pane
	final int LEFT_WIDTH = 150;
	final int RIGHT_WIDTH = 150;
	final int TOP_HEIGHT = 50;
	final int BOT_HEIGHT = 50;
	final int CENTER_WIDTH = (APP_WIDTH - (LEFT_WIDTH + RIGHT_WIDTH));
	final int CENTER_HEIGHT = (APP_HEIGHT - (TOP_HEIGHT + BOT_HEIGHT));
	
	private final View view;
	private final Model model;
	private final Controller controller;
	private final BorderPane root;
	private final Pane drawPane;
	private Label info1Value;
	private Label info2Value;
	private Label info3Value;
	
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
	public HostView(View view, Stage stage, BorderPane root, Model model, Controller controller, Pane drawPane) {
		super();
		this.view = view;
		this.controller = controller;
		this.model = model;
		this.root = root;
		this.drawPane = drawPane;
		//drawPane.setDisable(true); use isHosting boolean instead of disabling the drawingPane
		
		fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text", "*.txt"));
		
		// Initialize border pane's panels
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
		topHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 30px;" +
				"-fx-padding: 0px 0px 0px 100px;");
		
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
		HBox result = new HBox();
		HBox undoRedoBox = new HBox();
		HBox zoomBox = new HBox();
		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("File");
		
		MenuItem menuItemOpen = new MenuItem("Open");
		MenuItem menuItemSave = new MenuItem("Save");
		MenuItem menuItemSaveAs = new MenuItem("Save As");
		MenuItem menuItemHelp = new MenuItem("Help");
		MenuItem menuItemClose = new MenuItem("Close");
		
		result.setStyle("-fx-spacing: 25px;");
		undoRedoBox.setStyle("-fx-spacing: 2px;");
		zoomBox.setStyle("-fx-spacing: 2px;");
		
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
		
		menuItemHelp.setOnAction(e -> {
			System.out.println("Menu Item \"Help\" Selected");
			// TODO: Implement the help info for the user
		});
		
		menuItemClose.setOnAction(e -> {
			System.out.println("Menu Item \"Close\" Selected");
			Platform.exit();
		});
		
		menu.getItems().add(menuItemOpen);
		menu.getItems().add(menuItemSave);
		menu.getItems().add(menuItemSaveAs);
		menu.getItems().add(menuItemHelp);
		menu.getItems().add(menuItemClose);
		menuBar.getMenus().add(menu);
		
		Button resetZoomButton = new Button();
		Button zoomInButton = new Button();
		Button zoomOutButton = new Button();
		
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
		result.getChildren().addAll(menuBar, undoRedoBox, zoomBox);
		
		return result;
	}
	
	/**
	 * Initializes the center panel of the border pane
	 * <p>
	 * This panel is responsible for performing the user's mouse actions.
	 */
	private Pane initCenterPanel() {
		Pane result = new Pane();
		Pane child = drawPane; // Draw panel
		
		result.setPrefWidth(CENTER_WIDTH);
		result.setPrefHeight(CENTER_HEIGHT);
		result.getChildren().add(child);
		result.setBackground(new Background(
				new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
		
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
			double changeX = event.getSceneX() - (bounds.getWidth()/2 + bounds.getMinX());
			double changeY = event.getSceneY() - (bounds.getHeight()/2 + bounds.getMinY());
			if(event.getDeltaY() < 0 && child.getScaleX() > 0.1) {
				changeScale = -0.1;
				child.setScaleX(child.getScaleX() * (1 + changeScale));
				child.setScaleY(child.getScaleY() * (1 + changeScale));
			} else if (event.getDeltaY() > 0 && child.getScaleX() < 5) {
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
	 * Initializes the left panel of the border pane
	 * <p>
	 * This panel has the buttons for the features the "Host" view is responsible for performing.
	 */
	private Pane initLeftPanel() {
		Pane result = new Pane();
		result.setPrefWidth(LEFT_WIDTH);
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(124, 132, 161, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		
		VBox vbox = new VBox();
		VBox buttonBox = new VBox();
		Label leftPanelHeader = new Label("Host Elements");
		
		buttonBox.setStyle("-fx-spacing: 5px");
		leftPanelHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 20px;" +
				"-fx-padding: 0px 0px 0px 0px;");
		
		Button getSafePosButton = new Button("Get Safe Position");
		Button assignGuestButton = new Button("Assign Guest");
		Button removeGuestButton = new Button("Remove Guest");
		
		getSafePosButton.setStyle("-fx-pref-width: 120px; -fx-pref-height: 40px;");
		assignGuestButton.setStyle("-fx-pref-width: 120px; -fx-pref-height: 40px");
		removeGuestButton.setStyle("-fx-pref-width: 120px; -fx-pref-height: 40px");
		
		getSafePosButton.setOnAction(e -> {
			System.out.println("\"Get Safe Position\" button clicked");
			Spots spot = controller.getBestSpot();
			controller.getBestSpot();
			controller.displayModel();
			// TODO: Implement the feature where an optimal safe space is calculated
		});
		assignGuestButton.setOnAction(e -> {
			System.out.println("\"Assign Guest\" button clicked");
			view.assigningSeat = true;
			view.removingSeat = false;
			// TODO: Implement assigning a guest to a chair/spot on the floor
		});
		removeGuestButton.setOnAction(e -> {
			System.out.println("\"Remove Guest\" button clicked");
			view.assigningSeat = false;
			view.removingSeat = true;
			// TODO: Implement removing a guest from the list of occupants in the floor
		});
		
		buttonBox.getChildren().addAll(getSafePosButton, assignGuestButton, removeGuestButton);
		vbox.getChildren().addAll(leftPanelHeader, buttonBox);
		result.getChildren().add(vbox);
		return result;
	}
	
	/**
	 * Initializes the right panel of the border pane
	 * <p>
	 * This panel shows the user the current values for various product attributes.
	 */
	private Pane initRightPanel() {
		Pane result = new Pane();
		result.setPrefWidth(RIGHT_WIDTH);
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(124, 132, 161, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		
		Label leftPanelHeader = new Label("Host Info");
		VBox hostInfoBox = new VBox();
		VBox vbox = new VBox();
		
		hostInfoBox.setStyle("-fx-spacing: 5px");
		leftPanelHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 20px;" +
				"-fx-padding: 0px 0px 0px 0px;");
		
		Label info1 = new Label("Occupants: ");
		Label info2 = new Label("Info 2: ");
		Label info3 = new Label("Info 3: ");
		
		// TODO: Implement counting the number of occupants currently on the floor and display
		info1Value = new Label("# of occupants");
		// TODO: Choose and implement a second attribute of the product to display to the user
		info2Value = new Label("Answer 2");
		// TODO: Choose and implement a third attribute of the product to display to the user
		info3Value = new Label("Answer 3");
		
		info1.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
		info2.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
		info3.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
		
		hostInfoBox.getChildren().addAll(
				new HBox(info1, info1Value),
				new HBox(info2, info2Value),
				new HBox(info3, info3Value));
		
		vbox.getChildren().addAll(leftPanelHeader, hostInfoBox);
		result.getChildren().add(vbox);
		return result;
	}
}
