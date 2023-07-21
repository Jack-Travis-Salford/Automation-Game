package design_patterns.automation_game_v3;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Creates the map and intractable objects
 */
public class Main extends Application {
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private int framesPast;
    private static double zoomLevel; //If the windows setting "Make Everything Bigger" is not at 100%
    private static  double achievedFrameRate; //The approximate frame rate that the game is managing to achieve
    private static double balance; //How much money the user has
    private static double tilesHeight; //The height of each tile
    private static double tilesWidth; //The width of each GridSegment
    private Scene scene; //The main scene
    private Pane pane; //The main pane
    private Pane builderUIPane; //Pane that provides UI for builders
    private boolean isPlayerMoving; //True if a key relating to movement is pressed
    private boolean actionCausedByUiInitiation; //Indicates if change in ChoiceBox value was due to the user (false), or .setValue() somewhere in the code (true)
    private boolean isPlaceableSelected; //True if user has selected a placeable to place
    private boolean hasBuilderReqFocus; //Indicates if a placed builder has requested to open the builderUI
    private boolean isBuilderUIVisible; //Indicates if the builder UI is currently open
    private boolean reqRemBuilderUI; //Indicates if user has clicked outside the builderUI, and not on a builder, indicating to close the builderUI
    private BorderPane userView; //What the user can see. Essentially a camera
    private Player player; //Holds player
    private int tilesX;//How columns of GridSegments there are
    private int tilesY; //The amount of GridSegments in the y direction
    private double xBcOffset;//Holds values relating to X direction of display
    private int totalMiners; //How many miners are currently on map
    private int totalConveyors; //How many conveyors are on the map
    private int totalSellPoints; //How many sell points are on the map
    private int totalBuilders; //How many builders are on the map
    private double mouseX; //The x position of the mouse pointer when last moved
    private double mouseY; //The y position of the mouse pointer when last moved
    private double yBottomBcOffset; //The offset at the bottom of the screen after the grid of GridSegments ends
    private double yTopBcOffset; //The offset at the top of the screen before the grid of GridSegments begins
    private ChoiceBox<String> recipeSelector; //Dropdown box with recipe options
    private Label  lblRequiredResource1; //Label for builderUI - Holds details of input resource 1
    private Label lblRequiredResource2; //Label for builderUI -Holds details of input resource 2
    private Label lblRequiredResource3; //Label for builderUI -Holds details of input resource 3
    private Label lblOutputResource; //Label for builderUI - Holds details of output resource
    private Label lblCraftTime; //Label for builderUI - Holds details on crafting time of resource
    private Label lblValue; //Label for builderUI - Holds details on value of resource
    private Label lblBalance; //Label to inform user how much money they have
    private Canvas builderUICanvas; //A canvas for the builderUI - Used to set a background colour
    private GameObject builderWithFocus; //Reference to builder that relates to currently open builderUI (if it is)
    private GameObject builderWantsFocus; //Reference to builder that wants to open the builderUI
    private ImageView bottomBar; //The image at the bottom of the screen, informing the user of what button does what
    private ChosenPlaceable chosenPlaceable;  //Holds preview of selected placeable (if one is chosen)
    private GridSegment[][] grid; //Array that holds all rectangles that  make up grid
    private HashMap<String, GameObject> allPlaceables; //Holds a reference to all placeables currently on the map
    private GridSegment reqDelete; //If the user has requested to delete a placeable, a reference is added here until CPU time is assigned to delete it
    private GridSegment reqAdd; //If the user has requested to add a placeable, a reference is added here until CPU time is assigned to situate it
    /**
     * pane is to be made up of multiple layers:
     * canBackground: Holds the background image and resources
     * canLayer1: Holds conveyor belts and sell point
     * canLayer2: Holds constantly moving object, such as resources
     * canLayer3: Holds buildings, such as miner & constructors
     *
     * Each has a graphicsContext associated, aptly named gcBackground, gcLayer1, ... respectively
     *
     * This split is designed to reduce redrawing objects that do not move.
     */
    private Canvas canBackground;
    private Canvas canLayer1;
    private Canvas canLayer2;
    private Canvas canLayer3;
    private GraphicsContext gcBackground;
    private GraphicsContext gcLayer1;
    private GraphicsContext gcLayer2;
    private GraphicsContext gcLayer3;
    private Rectangle rectangle; //A 1px oscillating rectangle offscreen to ensure AnimationTimer runs at a consistent framerate
    /**
     * stageSizeListener - Triggered if user resizes screen
     */
    private final ChangeListener<Number> stageSizeListener = ((observable, oldValue, newValue) -> movePane());
    /**
     * addingPlaceable - Updates the mouse coordinates when moved
     */
    private final EventHandler<MouseEvent> addingPlaceable = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            mouseX = event.getX();
            mouseY = event.getY();
        }
    };
    /**
     * Main controller - Provides CPU time for everything that requires it
     */
    final AnimationTimer performAllActions = new AnimationTimer() {
        @Override
        public void handle(long now) {
            long oldFrameTime = frameTimes[frameTimeIndex] ;
            frameTimes[frameTimeIndex] = now ;
            frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
            if (framesPast%2==0){
                rectangle.setX(-5);
            }else{
                rectangle.setX(-6);
            }
            framesPast++;
            if(framesPast==200){
                framesPast=0;
                long elapsedNanos = now - oldFrameTime ;
                long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
                achievedFrameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
                Player.setPlayerMovementSpeed(achievedFrameRate);
            }

            //Show mini image of placeable if placeable is selected
            if (isPlaceableSelected) { //If a placeable is selected
                chosenPlaceable.getImg().setX((mouseX+10)); //Update location of preview
                chosenPlaceable.getImg().setY((mouseY+10));
            }
            lblBalance.setText("Money:" + (int)balance + " Coins"); //Update balance of user
            //Open UI
            if (hasBuilderReqFocus){ //If a builder wants to open the builderUI (user clicked it)
                builderWithFocus = builderWantsFocus; //Provide focus
                builderWantsFocus = null; //Clear variable value, as builder has focus
                hasBuilderReqFocus = false; //Indicate that no builder has requested focus that doesn't already have it
                if (((Builder)builderWithFocus).getRecipe() != null) { //If the builder has a recipe already set
                    actionCausedByUiInitiation = true; //Indicate that the change in choice box value wasn't due to user
                    //Get name of output resource, change game word to display word and set choice box to said word
                    recipeSelector.setValue(GameLanguageConverter.getEquivalentWord(((Resource) ((Builder) builderWithFocus).getRecipe().getOutput()).getResourceName()));
                }
                else {
                    actionCausedByUiInitiation = true;//Indicate that the change in choice box value wasn't due to user
                    recipeSelector.setValue("--No Option Selected--"); //Set value to default
                }
                updateBuilderUI(); //Fill details relating to relevant recipe
                actionCausedByUiInitiation = false; //Reset back to false
                if (!isBuilderUIVisible) { //If the builder ui is still hidden
                    stopPlayer(); //Stop player movement
                    userView.getChildren().add(builderUIPane); //Display builder UI
                    isBuilderUIVisible = true; //Indicates UI is visible
                }
            }
            if (reqRemBuilderUI && isBuilderUIVisible){ //If request to remove builder UI has occurred, and it is visible
                builderWithFocus = null; //Remove focus for builder that had it
                userView.getChildren().remove(builderUIPane); //Remove builderUI from screen
                reqRemBuilderUI = false; //Indicate request was dealt with
                isBuilderUIVisible = false;//Indicate BuilderUI is no longer visible
            }
            //Move player
            if (isPlayerMoving) { //If the player has at least 1 movement key pressed
                Player.move(pane); //Move player
                movePane();  //Move pane
            }
            if (reqDelete != null) {//If user has requested to delete a placeable
                reqDelete.deletePlaceable(); //Provide time to delete placeable
                reqDelete = null; //Reset variable
            }
            if (reqAdd != null) { //If user has requested to place a placeable
                reqAdd.addPlaceable(); //Provide time to place placeable
                reqAdd = null; //Reset variable
            }
            if (!allPlaceables.isEmpty()) { //If there is at least 1 placeable on the map
                for (GameObject placeable : allPlaceables.values()) { //For every placeable
                    ((Placeable) placeable).performAction(); //Provide cpu time to perform action/draw
                }
            }
        }
    };
    /**
     * Application start point
     */
    public static void main(String[] args)  {
        launch(args);
    }
    /**
     * Sets up game
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     */
    public void start(Stage primaryStage) {
        try {
            GameImages.instantiateUtility(); //Instantiated GameImages utility
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        //Set default/beginning values to global variables
        allPlaceables = new HashMap<>();
        isPlaceableSelected = false;
        double greatestZoom = 1;
        isPlayerMoving = false;
        for(Screen screen: Screen.getScreens()){ //Check to see if any of the users monitors are zoomed in
            if (screen.getOutputScaleX() > greatestZoom){
                greatestZoom = screen.getOutputScaleX();
            }
        }
        greatestZoom-=0.001; //Reduce by small amount, so (for example) 2.0 becomes 1.999 and 1.001 becomes 1
        greatestZoom=(int)(greatestZoom)+1.1; //Add 1.1 to the integer value of zoom
        framesPast =0;
        zoomLevel=  greatestZoom;
        tilesX = (int)(85/greatestZoom); //How many columns of GridSegments there are, scaled with screen zoom (stops canvas being too big for gpu rendering)
        xBcOffset = 25; //Offset at left and right side of map before and after grid of GridSegments begins/ends
        tilesWidth = 64; //Width of each GridSegment
        tilesY = (int)(85/greatestZoom); //How many rows of GridSegments there are
        yTopBcOffset = 50; //Offset at top of screen before grid of GridSegments begins
        yBottomBcOffset = 10 + GameImages.clone("bottom_bar").getHeight(); //Offset at bottom of screen after grid of GridSegments ends
        tilesHeight = 64; //Height of each GridSegment
        mouseX = 0;
        mouseY = 0;
        chosenPlaceable = ChosenPlaceable.getInstance();
        balance = 500; //Users starting funds
        hasBuilderReqFocus = false;
        reqRemBuilderUI = false;
        isBuilderUIVisible = false;
        builderWantsFocus = null;
        builderWithFocus = null;
        totalConveyors = 0;
        totalSellPoints = 0;
        totalBuilders = 0;
        totalMiners =0;
        rectangle = new Rectangle(-5,-5,1,1);
        actionCausedByUiInitiation = false;
        achievedFrameRate = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode().getRefreshRate();
        if (achievedFrameRate ==0){
            achievedFrameRate=60;
        }
        double requiredBackgroundWidth = (tilesX * tilesWidth) + (xBcOffset * 2); //The required size for the backgrounds width
        double requiredBackgroundHeight = (tilesY * tilesHeight) + (yTopBcOffset + yBottomBcOffset); //The required size for the backgrounds height
        createBackground(requiredBackgroundWidth, requiredBackgroundHeight); //Creates background and adds image
        layerCreator(requiredBackgroundWidth, requiredBackgroundHeight); //Create other layers
        try{
            createUtilities(); //Create remaining utility classes
        }catch (Exception ex1){
            System.out.println("Issue creating utilities: Game will break");
        }
        pane = new Pane(rectangle,canBackground, canLayer1, canLayer2, canLayer3);//Add to pane, which has static size
        pane.setMinSize(requiredBackgroundWidth, requiredBackgroundHeight); //Make pane exactly the size of the background - Prevent dynamically resizing with Stage
        pane.setPrefSize(requiredBackgroundWidth, requiredBackgroundHeight);
        pane.setMaxSize(requiredBackgroundWidth, requiredBackgroundHeight);
        player = Player.getInstance(this); //Create player
        userView = new BorderPane(pane);
        scene = new Scene(userView, 1280, 720); //Creates user view
        primaryStage.setScene(scene); //Add scene to stage
        primaryStage.setTitle("Automation Game"); //Give app a name
        primaryStage.getIcons().add(GameImages.clone("icon"));
        scene.widthProperty().addListener(stageSizeListener); //Listen out for changes in app size
        scene.heightProperty().addListener(stageSizeListener);
        scene.setOnKeyPressed(event -> processKey(event.getCode(), true)); //Triggers event listener if a key is pressed
        scene.setOnKeyReleased(event -> processKey(event.getCode(), false)); //Triggers an event listener if a key is released
        pane.getChildren().add(player.getPlayer()); //get Image view of player & add it to pane
        addGrid(); //Adds the grid of GridSegments
        addResources(); //Adds resources to the game
        userView.setOnMouseMoved(addingPlaceable); //Trigger for when the users mouse moves
        lblBalance = new Label("Money: " + (int)balance + " Coins"); //Set text on label that informs user of how much money they have
        lblBalance.setTextFill(Color.ORANGE);
        lblBalance.setFont(new Font(25)); //Sets font size of label
        //A pane to hold hte balance label
        Pane balancePane = new Pane(lblBalance); //Create a pane and add the balance label to it
        userView.getChildren().add(balancePane); //Add paint to "camera"
        bottomBar = new ImageView(GameImages.clone("bottom_bar")); //Bottom information bar on screen
        userView.getChildren().add(bottomBar); //Add to "camera"
        prepareBuilderUI(); //Prepares a base template for builderUI
        movePane(); //Moves the "camera" and associated children to a starting position
        player.getPlayer().setX(0.5*userView.getWidth()); //Places player in the centre of the "camera"
        player.getPlayer().setY(0.5*userView.getHeight());
        userView.getChildren().add(chosenPlaceable.getImg()); //Add chosenPlaceable img (preview of selected placeable)
        primaryStage.setMaximized(true); //Makes app full screen
        primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth()*0.75); //Sets screen size to 75% of screen size when maximized is exited
        primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight()*0.75);
        primaryStage.show(); //Show the stage
        performAllActions.start(); //Starts the centralised controller.

}

    /**
     * Adds resources to the map in random places
     */
    private void addResources() {
        int totalTiles = tilesX * tilesY; //Total tiles in grid
        for (int x = 0; x < 3; x++) { //For all 3 resources
            int amountOfResource;  //Will hold the amount of a resource that will be added to the map
            int baseAmountOfResources; //Will hold the base amount of resource that will be added to the map
            String resourceType; //Holds the name of the corresponding resource
            switch (x) {
                case 0:  //Add copper nodes
                    resourceType = "copper"; //Set resource type to copper
                    baseAmountOfResources = (int) ((2.0 / 10.0) * 0.01 * totalTiles) + 1; //Set min resources to 1 + 2/10's of approximate total tiles assigned to resources (0.7%)
                    amountOfResource = HowManyOfResource(baseAmountOfResources); //+ or - a di
                    break;
                case 1: //Add tree nodes
                    resourceType = "tree"; //Set resource type to tree
                    baseAmountOfResources = (int) ((3.0 / 10.0) * 0.01 * totalTiles) + 1; //Set min resources to 1 + 3/10's of approximate total tiles assigned to resources (0.7%)
                    amountOfResource = HowManyOfResource(baseAmountOfResources);
                    break;
                case 2: //Add iron nodes
                    resourceType = "iron"; //Set resource type to iron
                    baseAmountOfResources = (int) ((5.0 / 10.0) * 0.01 * totalTiles) + 1; //Set min resources to 1 + 5/10's of approximate total tiles assigned to resources (0.7%)
                    amountOfResource = HowManyOfResource(baseAmountOfResources);
                    break;
                default: //This shouldn't run, but failsafe is in place if it does (it won't add any resources)
                    amountOfResource = 0;
                    resourceType = "";
                    break;
            }
            for (int y = 0; y < amountOfResource; y++) { //Do until all resources are added
                boolean added = false; //Indicates if resource was successfully added
                while (!added) { //While yet to add resource
                    Random rand = new Random();
                    int posX = rand.nextInt(grid.length); //Picks a random index grid array
                    int posY = rand.nextInt(grid[0].length);
                    if (grid[posX][posY].getResourceType().equals("plain")) { //If the GridSegment chosen doesn't have an assigned resource
                        grid[posX][posY].setResourceType(resourceType); //Add resource
                        Image img = GameImages.clone(resourceType); //Provide corresponding image
                        gcBackground.drawImage(img, (posX * tilesWidth) + xBcOffset, (posY * tilesHeight) + yTopBcOffset, tilesWidth, tilesHeight); //Draw image to canvas
                        added = true; //Indicate resource was added to escape while loop
                    }
                }
            }
        }
    }
    /**
     * Prepares the interface where the user can choose what recipe they want for a specific builder
     */
    private void prepareBuilderUI(){
        builderUICanvas = new Canvas(500,250); //Create a canvas for buildingMenu (to be used to add a background)
        builderUIPane = new Pane(builderUICanvas); //Add canvas to associated pane
        GraphicsContext gc2 = builderUICanvas.getGraphicsContext2D(); //Get graphics context of canvas
        gc2.setFill(Color.LIGHTBLUE); //Set background colour of builderUI
        gc2.fillRect(0,0,500,250); //Add draw background of builderUI
        recipeSelector = new ChoiceBox<>(); //Set up everything on buildingMenu
        recipeSelector.getItems().add("--No Option Selected--"); //Add default value
        String[] arr = GameRecipes.getKeySet(); //Get all game recipes
        Arrays.sort(arr); //Sort the array alphabetically
        for (String recipe: arr){ //For each recipe
            recipeSelector.getItems().add(GameLanguageConverter.getEquivalentWord(recipe)); //Add the display name to dropdown selector
        }
        //Set default text & position of all labels that relate to builderUI
        recipeSelector.setValue("--No Option Selected--");
        recipeSelector.setLayoutX(50);
        recipeSelector.setLayoutY(20);
        //Label for builderUI - Holds Heading "Required Resources"
        Label lblRequiredResourcesTitle = new Label("Required Resources:");
        lblRequiredResourcesTitle.setLayoutX(50);
        lblRequiredResourcesTitle.setLayoutY(45);
        lblRequiredResource1 = new Label();
        lblRequiredResource1.setLayoutX(50);
        lblRequiredResource1.setLayoutY(70);
        lblRequiredResource2 = new Label();
        lblRequiredResource2.setLayoutX(50);
        lblRequiredResource2.setLayoutY(95);
        lblRequiredResource3 = new Label();
        lblRequiredResource3.setLayoutX(50);
        lblRequiredResource3.setLayoutY(120);
        //Label for builderUI -Holds Heading "Output Resources"
        Label lblOutputResourceTitle = new Label("Resource Created:");
        lblOutputResourceTitle.setLayoutX(50);
        lblOutputResourceTitle.setLayoutY(145);
        lblOutputResource = new Label();
        lblOutputResource.setLayoutX(50);
        lblOutputResource.setLayoutY(170);
        lblCraftTime = new Label();
        lblCraftTime.setLayoutX(50);
        lblCraftTime.setLayoutY(195);
        lblValue = new Label();
        lblValue.setLayoutX(50);
        lblValue.setLayoutY(220);
        Button btnClose = new Button(); //Place a red button with the text of 'X' in the right-hand corner of the builderUI
        btnClose.setText("X");
        btnClose.setBackground(new Background(new BackgroundFill(Color.RED, null , null)));
        btnClose.setLayoutX(472);
        btnClose.setLayoutY(5);
        //Add all elements of builderUI to the related pane
        builderUIPane.getChildren().addAll(recipeSelector, lblRequiredResourcesTitle, lblRequiredResource1, lblRequiredResource2, lblRequiredResource3, lblOutputResourceTitle, lblOutputResource,lblCraftTime,lblValue, btnClose);
        recipeSelector.setOnAction(event -> { //Trigger when recipe selection in Choice Box changes
            if(builderWithFocus != null) { //If a builder has focus
                if(!actionCausedByUiInitiation){ //If the change wasn't caused by the code (user initiated)
                    //Set the recipe of the builder with focus to the related recipe in the choice box, obtained using GameRecipe by getting the game equivalent word for said recipe (GameLanguageConvertor)
                    ((Builder)builderWithFocus).setRecipe(GameRecipes.get(GameLanguageConverter.getEquivalentWord(recipeSelector.getValue())));
                    updateBuilderUI(); //Call itself to change values in labels/choice box
                }
            }
        });
        btnClose.setOnAction(event -> { //If the close button on the builderUi is clicked
            if(isBuilderUIVisible){ //If the builderUI is visible (should always be the case)
                builderWithFocus = null; //Remove focus from builder that did have focus
                updateBuilderUI(); //Call itself to reset builder UI values
                userView.getChildren().remove(builderUIPane); //Remove builderUI from pane (no longer visible or interact-able
                isBuilderUIVisible = false; //Indicate that builderUI isn't visible
            }
        });
    }
    /**
     * Creates the background canvas to required size, and adds background image
     *
     * @param width  Required width of canvas
     * @param height Required height of canvas
     */
    private void createBackground(double width, double height) {
        canBackground = new Canvas(width, height);
        gcBackground = canBackground.getGraphicsContext2D();
        Image img = GameImages.clone("background");
        int totalImagesWidth = (int) (width / img.getWidth()); //Work out how many times the image has to repeat in x-axis direction to cover Canvas
        if (width % img.getWidth() != 0) { //If there was some remainder (not exact fit)
            totalImagesWidth++; //Add another image in x-axis direction
        }
        int totalImagesHeight = (int) (height / img.getHeight()); //How many times the image has to repeat in the y-axis direction to cover the Canvas
        if (height % img.getHeight() != 0) { //If there was some remainder (not exact fit)
            totalImagesHeight++; //Add another image in y-axis direction
        }
        for (int x = 0; x < totalImagesWidth; x++) { //Using the numbers calculated above, add background image, repeating if necessary
            for (int y = 0; y < totalImagesHeight; y++) {
                gcBackground.drawImage(img, (img.getWidth() * x), (img.getHeight() * y));
            }
        }
    }

    /**
     * Create required canvas layers & corresponding graphics contexts
     * @param width Required width of the canvases
     * @param height Required height of the canvases
     */
    private void layerCreator(double width, double height) {
        //Create three layers of canvases  with respective graphic contexts of the same size as the background
        canLayer1 = new Canvas(width, height);
        gcLayer1 = canLayer1.getGraphicsContext2D();
        canLayer2 = new Canvas(width, height);
        gcLayer2 = canLayer2.getGraphicsContext2D();
        canLayer3 = new Canvas(width, height);
        gcLayer3 = canLayer3.getGraphicsContext2D();
    }
    /**
     * Has the job such that player is visible on screen, and scene doesn't go past parameters of background canvas
     */
    private void movePane() {
        if (scene.getWidth() > pane.getWidth()) { //If the scene is wider than the pane
            pane.setTranslateX(0); //Dont translate in x direction
        } else { ///if width of scene is wider than pane
            double newX = player.getPlayer().getX() - 0.5 * (scene.getWidth() - player.getPlayer().getImage().getWidth()); //Work out new X for scene where player is in the centre
            double maxX = pane.getWidth() - scene.getWidth(); //Work out max X value where the scene doesn't show past the background
            if (maxX < newX) { //If keeping the user centered would cause a view showing past the end of the background (right side of screen)
                newX = maxX; //Set the new x, so only the view doesn't go further than the background image (right side of screen at edge of background, not past it)
            } else if (newX < 1) { //Else, if new x would cause showing past the left side of the background
                newX = 0; //Set new x to 0, so left edge of background is shown instead
            }
            pane.setTranslateX(-(newX)); //Move the pane to new x co-ordinate
        }
        if (scene.getHeight() > pane.getHeight()) { //If the scene is longer than the pane
            pane.setTranslateY(0); //Don't translate in y direction
        } else { ///if height of scene is wider than pane
            double newY = player.getPlayer().getY() - 0.5 * (scene.getHeight() - player.getPlayer().getImage().getHeight());  //Work out new Y for scene where player is in the centre
            double maxY = pane.getHeight() - scene.getHeight(); //Work out max Y value where the scene doesn't show past the background
            if (maxY < newY) { //If keeping the user centered would cause a view showing past the end of the background (bottom of screen)
                newY = maxY; //Set the new Y, so only the view doesn't go further than the background image (bottom of screen at edge of background, not past it)
            } else if (newY < 1) {  //Else, if new Y would cause showing past the top side of the background
                newY = 0; //Set new Y to 0, so top of background is shown instead, not before it
            }
            pane.setTranslateY(-(newY)); //Move the pane to new Y co-ordinate
        }
        bottomBar.setX(0.5*(userView.getWidth()-bottomBar.getImage().getWidth())); //Re-align the bottom bar bottom-centre
        bottomBar.setY(userView.getHeight()-bottomBar.getImage().getHeight());
        builderUIPane.setTranslateX(0.5*(userView.getWidth()-builderUICanvas.getWidth())); //Re-align builderUI centre screen
        builderUIPane.setTranslateY(0.5*(userView.getHeight()-builderUICanvas.getHeight()));
        lblBalance.setTranslateX(pane.getLayoutX()+5); //Re-align balance to top left of screen (required in case where Stage is bigger than scene)
        lblBalance.setTranslateY(pane.getLayoutY()+5);
    }

    /**
     * Called when a key is pressed
     * @param code The keycode of pressed key
     * @param isPressed Indicates if event was a keydown or keyup event (true/false respectively)
     */
    private void processKey(KeyCode code, boolean isPressed) {
        if(!isBuilderUIVisible) { //If the builder UI interface isn't visible
            switch (code) {
                case LEFT: //If key indicates left movement
                case A:
                    player.setLeft(isPressed); //Set/unset left movement, based on if isPressed is true/false respectively
                    break;
                case RIGHT: //If key indicates right movement
                case D:
                    player.setRight(isPressed); //Set/unset right movement, based on if isPress is true/false respectively
                    break;
                case UP: //If key indicates upward movement
                case W:
                    player.setUp(isPressed); //Set/unset upward movement, based on if isPress is true/false respectively
                    break;
                case DOWN: //If key indicates downward movement
                case S:
                    player.setDown(isPressed); //Set/unset downward movement, based on if isPress is true/false respectively
                    break;
                case DIGIT1:
                case NUMPAD1: //User wants to place conveyor
                    setChosenPlaceable(isPressed, true, "conveyor", "conveyor2-0"); //Handle key press
                    break;
                case DIGIT2:
                case NUMPAD2: //User wants to place a splitter/merger (splurger)
                    setChosenPlaceable(isPressed,false,"splurger","splurger");//Handle key press
                    break;
                case DIGIT3:
                case NUMPAD3: //User wants to place a miner
                    setChosenPlaceable(isPressed,false,"miner","iron_miner"); //Handle key press
                    break;
                case DIGIT4:
                case NUMPAD4: //User wants to place a builder
                    setChosenPlaceable(isPressed,false,"builder","builder"); //Handle key press
                    break;
                case DIGIT5:
                case NUMPAD5: //User wants to place a sell point
                    setChosenPlaceable(isPressed,false,"sell_point", "sell_point"); //Handle key press
                    break;
                case R: //User wants to rotate placeable (if allowed)
                    if (isPressed) { //If this was caused by a key down event
                        chosenPlaceable.rotate();  //Rotate the placeable (if allowed)
                    }
                    break;
                case DELETE:
                case BACK_SPACE:
                case Q:
                case Z: //If user wants to delete
                    setChosenPlaceable(isPressed,false,"bin","bin"); //Handle key press
                    break;
                case ESCAPE: //If user wants to "escape"
                    noLongerPlacingObject(); //make it so user is no longer placing a placeable
                    break;
                default: //If any other key is pressed
                    if (isPlaceableSelected){ //If a placeable is selected
                        noLongerPlacingObject(); //Remove selection
                    }
                    break;
            }
            //Indicate if player is moving or stationary (true/false respectively)
            isPlayerMoving = player.getDown() || player.getRight() || player.getLeft() || player.getUp();
        }else { //If builderUI is visible
            if(code == KeyCode.ESCAPE){ //If user wants to "escape"
                builderWithFocus = null; //Remove focus to builder
                userView.getChildren().remove(builderUIPane); //Remove builder UI from userView (no longer interact-able)
                isBuilderUIVisible=false; //Indicate builderUI is no longer visible
            }
        }
    }

    /**
     * Called by key events that related to placing/deleting placeables
     * @param isPressed If event was a keydown or keyup event
     * @param isRotatable If selected building can be rotated
     * @param placeableName The name of the placeable
     * @param placeableImage The corresponding image to selected placeable
     */
    private void setChosenPlaceable(boolean isPressed, boolean isRotatable, String placeableName, String placeableImage){
        if (isPressed) { //If this was caused by a key down event
            if (isPlaceableSelected && chosenPlaceable.getSelectedPlaceable().equals(placeableName)) { //If a placeable is already selected, and is the same as placeableName
                noLongerPlacingObject(); //Stop attempting to place item
            } else { //Otherwise
                chosenPlaceable.newChosen(isRotatable, placeableName, placeableImage); //Change chosen placeable
                if (!isPlaceableSelected) { //If isPlaceableSelected is false
                    isPlaceableSelected = true; //Make it true, indicating user is attempting to add placeable
                }
            }
        }
    }
    /**
     * Adds the interactive interface
     */
    private void addGrid() {
        grid = new GridSegment[tilesX][tilesY]; //Set Array that will hold reference to all GridSegments
        for (int x = 0; x < tilesX; x++) { //Add all tiles (GridSegment) for placeables
            for (int y = 0; y < tilesY; y++) {
                grid[x][y] = new GridSegment(this, (x * tilesWidth) + xBcOffset, (y * tilesHeight) + yTopBcOffset, tilesWidth, tilesHeight, x, y); //Position tiles next to each other of specified size, with a 25px offset
                pane.getChildren().add(grid[x][y]); //Add GridSegments to pane
            }
        }
    }
    /**
     * Reset values to default when user is no longer placing objects
     */
    private void noLongerPlacingObject() {
        chosenPlaceable.reset(); //Reset chosenPlaceable to default values
        isPlaceableSelected = false; //Indicate that no placeable is currently selected
    }
    /**
     * Calculates exactly the amount of resources to add
     * @param baseAmountOfResources Base amount of resources to add
     * @return Actual amount of resources to add
     */
    private int HowManyOfResource(int baseAmountOfResources) {
        Random random = new Random();
        int amountOfResource; //will hold actual amount of resources to add
        int deviance = (int) (0.2 * baseAmountOfResources); //Calculate deviance of 20% from the base amount of resources to add, truncated to an integer value
        int direction = random.nextInt(2); //Choose whether it will be + or - deviance
        switch (direction) {
            case 0: //+ deviance
                amountOfResource = baseAmountOfResources + random.nextInt(random.nextInt(deviance + 1)+1); //Base amount of resources + a maximum of 20% more
                break;
            case 1: //- deviance
                amountOfResource = baseAmountOfResources - random.nextInt(random.nextInt(deviance + 1)+1); //Base amount of resources - a maximum of 20% less
                break;
            default: //Failsafe - Shouldn't ever run
                amountOfResource = 0;
                break;
        }
        return amountOfResource; //Return exactly the amount of resources to add
    }
    /**
     * Updates the Builder UI interface with data relevant to option in choice box
     */
    private void updateBuilderUI(){
        Recipe recipe; //Will hold recipe relating to choice box choice
        if(builderWithFocus != null) { //If a builder has focus
            recipe = ((Builder) builderWithFocus).getRecipe(); //Get its current recipe
        }else { //Otherwise
            recipe=null; //Set to null
        }
        if(recipe!= null){ //If a specific recipe is chosen
            String resourceName; //Will hold name of resource
            int amountNeeded; //Will hold amount of resourceName is needed in recipe
            int totalInputs = recipe.getTotalInputs();
            switch (totalInputs){
                case 3: //If theres 3 inputs, execute all code
                    resourceName = GameLanguageConverter.getEquivalentWord(recipe.getInputResources().get("input3").getResourceName()); //Gets input 3 of recipe, changes returned String from game word to display word
                    amountNeeded = recipe.getInputResources().get("input3").getNoRequired(); //Gets how many of input 3 is needed per crafting cycle
                    lblRequiredResource3.setText(amountNeeded + "X " + resourceName); //Added info to builderUI
                case 2: //If there's 2 inputs, execute from this point onward
                    resourceName = GameLanguageConverter.getEquivalentWord(recipe.getInputResources().get("input2").getResourceName()); //Gets input 2 of recipe, changes returned String from game word to display word
                    amountNeeded = recipe.getInputResources().get("input2").getNoRequired(); //Gets how many of input 3 is needed per crafting cycle
                    lblRequiredResource2.setText(amountNeeded + "X " + resourceName); //Added info to builderUI
                case 1: //If there's 1 inputs, execute from this point onward
                    resourceName = GameLanguageConverter.getEquivalentWord(recipe.getInputResources().get("input1").getResourceName()); //Gets input 1 of recipe, changes returned String from game word to display word
                    amountNeeded = recipe.getInputResources().get("input1").getNoRequired(); //Gets how many of input 3 is needed per crafting cycle
                    lblRequiredResource1.setText(amountNeeded + "X " + resourceName); //Added info to builderUI
                    break;
                default:
                    break;
            }
            switch (totalInputs){ //Remove text (if any) from unused labels
                case 1:
                    lblRequiredResource2.setText("");
                case 2:
                    lblRequiredResource3.setText("");
                default:
                    break;
            }
            resourceName = GameLanguageConverter.getEquivalentWord(((Resource)recipe.getOutput()).getResourceName()); //Gets name of output resource (String), coverts game word to display word
            lblOutputResource.setText(recipe.getOutputsProduced() + "X " + resourceName); //Displays to user what the recipe outputs
            lblCraftTime.setText("Craft Time: " + recipe.getCraftTime() + "ms"); //Displays craft time of recipe
            lblValue.setText("Value: " + ((Resource)recipe.getOutput()).getValue() + " coins"); //Displays value of outputted resource

        }else { //If default value, clear everything
            lblRequiredResource1.setText("");
            lblRequiredResource2.setText("");
            lblRequiredResource3.setText("");
            lblValue.setText("");
            lblCraftTime.setText("");
            lblOutputResource.setText("");
        }
    }

    /**
     * Creates remaining utility classes
     * GameImages has already been instantiated (needed immediately at game launch)
     * GameResources requires gcLayer2 to be set prior to creation
     * GameRecipes requires GameResources to be instantiated.
     * GameLanguageConverter has no dependencies
     */
    private void createUtilities() {
        GameResources.instantiateUtility(gcLayer2);
        GameRecipes.instantiateUtility();
        GameLanguageConverter.instantiateUtility();
    }

    /**
     * Sets all variables relating to player movement to false.
     * This makes the player stationary until a movement key is pressed
     */
    private void stopPlayer(){
        player.setDown(false);
        player.setLeft(false);
        player.setRight(false);
        player.setUp(false);
        isPlayerMoving = false;
    }
    public boolean isBuilderUIVisible() {        return isBuilderUIVisible;    }
    public GridSegment[][] getGrid() {        return grid;    }
    public HashMap<String, GameObject> getAllPlaceables() {        return allPlaceables;    }
    public GridSegment getReqAdd() {        return reqAdd;    }
    public int getTotalBuilders() {return totalBuilders;    }
    public int getTotalConveyors() { return totalConveyors;    }
    public int getTotalMiners() { return totalMiners; }
    public int getTotalSellPoints() {  return totalSellPoints;    }
    public static double getTilesWidth() {
        return tilesWidth;
    }
    public static double getTilesHeight() {
        return tilesHeight;
    }
    public double getXBcOffset() {
        return xBcOffset;
    }
    public double getYBottomBcOffset() {
        return yBottomBcOffset;
    }
    public double getYTopBcOffset() {
        return yTopBcOffset;
    }
    public ChosenPlaceable getChosenPlaceable() {
        return chosenPlaceable;
    }
    public boolean isPlaceableSelected() {
        return isPlaceableSelected;
    }
    public GraphicsContext getGcLayer1() {
        return gcLayer1;
    }
    public GraphicsContext getGcLayer3() {
        return gcLayer3;
    }
    public static double getBalance() {
        return balance;
    }
    public static double getZoomLevel() {
        return zoomLevel;
    }
    public static void changeBalance(double changeBy){
        balance += changeBy;
    }
    public void changeTotalMiners(int changeBy){ totalMiners+=changeBy; }
    public void changeTotalConveyors(int changeBy){ totalConveyors+=changeBy;}
    public void changeTotalBuilders(int changeBy){ totalBuilders+=changeBy;    }
    public void changeTotalSellPoints(int changeBy){totalSellPoints+=changeBy;    }

    public void reqAdd(GridSegment reqAdd) {
        if (this.reqAdd==null){ //If a grid segment isn't queued for creation this frame
            this.reqAdd = reqAdd; //Queue creation
        }
    }
    public void reqDelete(GridSegment reqDelete) {
        if (this.reqDelete==null){ //If a grid segment isn't queued for deletion this frame
            this.reqDelete = reqDelete; //Queue deletion
        }
    }
    public void setReqRemBuilderUI(boolean reqRemBuilderUI) {
        this.reqRemBuilderUI = reqRemBuilderUI;
    }
    public void builderWantsFocus(GameObject builderWantsFocus) {
        if (this.builderWantsFocus==null){ //If no game object has requested focus this frame
            this.builderWantsFocus = builderWantsFocus; //Queue focus
            hasBuilderReqFocus=true;
        }
    }

    public static double getAchievedFrameRate() {
        return achievedFrameRate;
    }
}