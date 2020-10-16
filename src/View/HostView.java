package View;

import Controller.Controller;
import Model.Model;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

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
	
	private final Model model;
	private final Controller controller;
	private final BorderPane root;
	private final Pane drawPane;
	private Label info1Value;
	private Label info2Value;
	private Label info3Value;
	
	/**
	 * Constructs a HostView object
	 *
	 * @param root the border pane of the "construct" view
	 * @param model the current state of the product
	 * @param controller the controller of the product
	 * @param drawPane the canvas to draw on
	 */
	public HostView(BorderPane root, Model model, Controller controller, Pane drawPane) {
		super();
		this.controller = controller;
		this.model = model;
		this.root = root;
		this.drawPane = drawPane;
		
		root.setCenter(initCenterPanel());
		root.setTop(initTopPanel());
		root.setLeft(initLeftPanel());
		root.setRight(initRightPanel());
		
		controller.displayModel();
	}
	
	/**
	 * Initializes the top panel of the border pane
	 */
	private Pane initTopPanel() {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(196, 153, 143, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefHeight(TOP_HEIGHT);
		
		HBox hBox = new HBox();
		Label topHeader = new Label("Hosting");
		topHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 30px;" +
				"-fx-padding: 0px 0px 0px 100px;");
		
		HBox menuBar = initTopControls();
		hBox.getChildren().addAll(menuBar, topHeader);
		result.getChildren().add(hBox);
		return result;
	}
	
	/**
	 * Initializes the controls in the top panel of the main border pane
	 *
	 * @return HBox
	 */
	private HBox initTopControls() {
		HBox result = new HBox();
		HBox undoRedoBox = new HBox();
		HBox zoomBox = new HBox();
		MenuBar menuBar = new MenuBar();
		
		Menu menu = new Menu("File");
		MenuItem menuItemHelp = new MenuItem("Help");
		MenuItem menuItemClose = new MenuItem("Close");
		
		result.setStyle("-fx-spacing: 25px;");
		undoRedoBox.setStyle("-fx-spacing: 2px;");
		zoomBox.setStyle("-fx-spacing: 2px;");
		
		menuItemHelp.setOnAction(e -> {
			System.out.println("Menu Item \"Help\" Selected");
			// TODO: Implement the help info for the user
		});
		menuItemClose.setOnAction(e -> {
			System.out.println("Menu Item \"Close\" Selected");
			Platform.exit();
		});
		
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
	 */
	private Pane initCenterPanel() {
		Pane result = new Pane();
		Pane child = drawPane; // Draw panel
		
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
	 * Initializes the left panel of the border pane
	 */
	private Pane initLeftPanel() {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(124, 132, 161, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(LEFT_WIDTH);
		
		VBox vbox = new VBox();
		VBox buttonBox = new VBox();
		
		Label leftPanelHeader = new Label("Host Elements");
		Button getSafePos = new Button("Get Safe Position");
		Button assignGuest = new Button("Assign Guest");
		Button removeGuest = new Button("Remove Guest");
		
		leftPanelHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 20px;" +
				"-fx-padding: 0px 0px 0px 0px;");
		buttonBox.setStyle("-fx-spacing: 5px");
		getSafePos.setStyle("-fx-pref-width: 120px; -fx-pref-height: 40px;");
		assignGuest.setStyle("-fx-pref-width: 120px; -fx-pref-height: 40px");
		removeGuest.setStyle("-fx-pref-width: 120px; -fx-pref-height: 40px");
		
		buttonBox.getChildren().addAll(getSafePos, assignGuest, removeGuest);
		vbox.getChildren().addAll(leftPanelHeader, buttonBox);
		result.getChildren().add(vbox);
		return result;
	}
	
	/**
	 * Initializes the right panel of the border pane
	 */
	private Pane initRightPanel() {
		Pane result = new Pane();
		result.setBackground(new Background(
				new BackgroundFill(Color.rgb(124, 132, 161, 1), CornerRadii.EMPTY, Insets.EMPTY)));
		result.setPrefWidth(RIGHT_WIDTH);
		
		VBox vbox = new VBox();
		VBox hostInfoBox = new VBox();
		
		Label leftPanelHeader = new Label("Host Info");
		Label info1 = new Label("Occupants: ");
		Label info2 = new Label("Info 2: ");
		Label info3 = new Label("Info 3: ");
		info1Value = new Label("Answer 1");
		info2Value = new Label("Answer 2");
		info3Value = new Label("Answer 3");
		
		leftPanelHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 20px;" +
				"-fx-padding: 0px 0px 0px 0px;");
		info1.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
		info2.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
		info3.setStyle("-fx-font-weight: bold;-fx-font-size: 12px;");
		hostInfoBox.setStyle("-fx-spacing: 5px");
		
		hostInfoBox.getChildren().addAll(
				new HBox(info1, info1Value),
				new HBox(info2, info2Value),
				new HBox(info3, info3Value));
		
		vbox.getChildren().addAll(leftPanelHeader, hostInfoBox);
		result.getChildren().add(vbox);
		return result;
	}
}
