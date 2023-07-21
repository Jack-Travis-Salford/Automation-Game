package design_patterns;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
/**
 * Creates the map and intractable objects
 */
public class Main extends Application {
    //Show FPS stuff
    protected final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;
    private Scene scene;
    protected Pane pane;
    protected Stage primaryStage;
   // protected AnimationTimer movePlayer;
    /**
     * Pane is to be made up of multiple layers:
     * canBackground: Holds the background image and resources
     * canLayer1: Holds conveyor belts and sell point
     * canLayer2: Holds constantly moving object, such as resources
     * canLayer3: Holds buildings, such as miner & constructors
     * canLayer4: Holds preview of placeable being moved
     * This split is designed to reduce redrawing objects that do not move.
     */
    protected boolean isPlayerMoving;
    protected Canvas canBackground;
    protected GraphicsContext gcBackground;
    protected Canvas canLayer1;
    protected GraphicsContext gcLayer1;
    protected Canvas canLayer2;
    protected GraphicsContext gcLayer2;
    protected Canvas canLayer3;
    protected GraphicsContext gcLayer3;
    protected Canvas canLayer4;
    protected GraphicsContext gcLayer4;
    protected BorderPane userView; //What the user can see. Essentially a camera
    protected Canvas canUserView;
    protected GraphicsContext gcUserView;
    private Player player; //Holds player
    protected boolean isPlaceableSelected; //Indicates if player is currently selected a placeable
    protected ChosenPlaceable chosenPlaceable;  //Holds preview of selected placeable (if one is chosen)
    protected int tilesX, xBcOffset;//Holds values relating to X direction of display
    public static int tilesWidth;
    protected int tilesY, yBcOffset;; //Same, but for y direction
    public static int tilesHeight;
    protected GridSegment[][] grid; //Array that holds all rectangles that  make up grid
    protected HashMap<String, GameObject> allPlaceables; //Holds a reference to all placeables currently on the map
    protected GridSegment reqDelete; //If the user has requested to delete a placeable, a reference is added here until CPU time is assigned to delete it
    protected GridSegment reqAdd; //If the user has requested to add a placeable, a reference is added here until CPU time is assigned to situate it
    protected double mouseX, mouseY; //The position of the mouse pointer
    protected double lastDrawX, lastDrawY; //Position where placeable preview image was last drawn


    /**
     *  stageSizeListener - Triggered if user resizes screen
     */
    private final ChangeListener<Number> stageSizeListener = ((observable, oldValue, newValue) -> {
        movePane();
    });

    public static void main(String[] args) throws FileNotFoundException {
        new GameImages();
        launch(args);
    }
    /**
     * Sets up game
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    public void start(Stage primaryStage) {
        allPlaceables = new HashMap<>();
        isPlaceableSelected = false;
        isPlayerMoving = false;
        chosenPlaceable = ChosenPlaceable.getInstance();
        tilesX = 50;
        xBcOffset = 25;
        tilesWidth = 64;
        tilesY = 50;
        yBcOffset = 25;
        tilesHeight = 64;
        mouseX =0;
        mouseY=0;
        lastDrawX =-10000;
        lastDrawY =-10000;
        double requiredBackgroundWidth = (tilesX * tilesWidth) + (xBcOffset * 2); //The required size for the background
        double requiredBackgroundHeight = (tilesY * tilesHeight) + (yBcOffset * 2);
        createBackground(requiredBackgroundWidth, requiredBackgroundHeight); //Creates background and adds image
        layerCreator(requiredBackgroundWidth, requiredBackgroundHeight); //Create other layers
        pane = new Pane(canBackground, canLayer1, canLayer2, canLayer3, canLayer4);//Add to pane, which has static size
        pane.setMinSize(requiredBackgroundWidth, requiredBackgroundHeight); //Make pane exactly the size of the background - Prevent dynamically resizing with Stage
        pane.setPrefSize(requiredBackgroundWidth, requiredBackgroundHeight);
        pane.setMaxSize(requiredBackgroundWidth, requiredBackgroundHeight);
        player = Player.getInstance(); //Create player
        userView = new BorderPane(pane);
        scene = new Scene(userView,1280,720); //Creates user view


        canUserView = new Canvas(1000,1000);
        gcUserView = canUserView.getGraphicsContext2D();
       // userView.getChildren().add(canUserView);
       // gcUserView.setFill(Color.RED);
        //gcUserView.fillRect(200,200,200,200);
        //userView.getChildren().remove(1);


        primaryStage.setScene(scene); //Add scene to stage
        primaryStage.setTitle("A game"); //Give app a name
        primaryStage.getIcons().add(GameImages.clone("icon"));
        primaryStage.setFullScreen(true);
        primaryStage.show();
        movePane();
        scene.widthProperty().addListener(stageSizeListener); //Listen out for changes in app size
        scene.heightProperty().addListener(stageSizeListener);
        scene.setOnKeyPressed(event -> processKey(event.getCode(), true));
        scene.setOnKeyReleased(event -> processKey(event.getCode(), false));
        pane.getChildren().add(player.getPlayer()); //get Image view of player & add it to pane
        addGrid();
        addResources();

        //For everything the user can move the mouse over (top layers), add event listener for mouse movement.
        canLayer4.setOnMouseMoved(addingPlaceable); //Add eventhandler to canvas
        for (GridSegment[] arrGs : grid) {
            for (GridSegment gs : arrGs) {
                gs.setOnMouseMoved(addingPlaceable); //Also add to grid
            }
        }


        performAllActions.start();
        this.primaryStage = primaryStage;
    }
    /**
     * Creates the background canvas to required size, and adds background image
     * @param width Required width of canvas
     * @param height Required height of canvas
     */
    private void createBackground(double width, double height){
        canBackground = new Canvas(width,height);
        gcBackground = canBackground.getGraphicsContext2D();
        Image img = GameImages.clone("background");
        int totalImagesWidth = (int)(width /img.getWidth()); //Work out how many times the image has to repeat in x-axis direction to cover Canvas
        if (width%img.getWidth() != 0){ //If there was some remainder (not exact fit)
            totalImagesWidth++; //Add another image in x-axis direction
        }
        int totalImagesHeight = (int)(height/img.getHeight()); //How many times the image has to repeat in the y-axis direction to cover the Canvas
        if (height%img.getHeight() != 0){ //If there was some remainder (not exact fit)
            totalImagesHeight++; //Add another image in y-axis direction
        }
        for(int x=0; x<totalImagesWidth; x++){ //Using the numbers calculated above, add background image, repeating if necessary
            for(int y=0; y<totalImagesHeight; y++){
                gcBackground.drawImage(img,(img.getWidth()*x),(img.getHeight()*y));
            }
        }
    }
    /**
     * Creates other layers
     */
    private void layerCreator(double width, double height){
        canLayer1 = new Canvas(width,height);
        gcLayer1 = canLayer1.getGraphicsContext2D();
        canLayer2 = new Canvas(width,height);
        gcLayer2 = canLayer2.getGraphicsContext2D();
        canLayer3 = new Canvas(width,height);
        gcLayer3 = canLayer3.getGraphicsContext2D();
        canLayer4 = new Canvas(width,height);
        gcLayer4 = canLayer4.getGraphicsContext2D();
        gcLayer1.setFill(Color.TRANSPARENT);
        gcLayer2.setFill(Color.TRANSPARENT);
        gcLayer3.setFill(Color.TRANSPARENT);
        gcLayer4.setFill(Color.TRANSPARENT);
    }
    /**
     * Has job such that player is visible on screen, and scene doesnt go past parameters of background canvas
     */
    private void movePane(){
        if(scene.getWidth() > pane.getWidth()) { //If the scene is wider than the pane
            pane.setTranslateX(0); //Dont translate in x direction
        }
        else{ ///if width of scene is wider than pane
            double newX = player.getPlayer().getX() - 0.5 * (scene.getWidth() - player.getPlayer().getImage().getWidth()); //Work out new X for scene where player is in the centre
            double maxX = pane.getWidth() - scene.getWidth(); //Work out max X value where the scene doesn't show past the background
            if(maxX < newX){ //If keeping the user centered would cause a view showing past the end of the background (right side of screen)
                newX = maxX; //Set the new x, so only the view doesn't go further than the background image (right side of screen at edge of background, not past it)
            }
            else if(newX < 1){ //Else, if new x would cause showing past the left side of the background
                newX = 0; //Set new x to 0, so left edge of background is shown instead
            }
            pane.setTranslateX(-(newX)); //Move the pane to new x co-ordinate
        }
        if(scene.getHeight() > pane.getHeight()) { //If the scene is longer than the pane
            pane.setTranslateY(0); //Dont translate in y direction
        }
        else { ///if height of scene is wider than pane
            double newY = player.getPlayer().getY() - 0.5 * (scene.getHeight() - player.getPlayer().getImage().getHeight());  //Work out new Y for scene where player is in the centre
            double maxY = pane.getHeight() - scene.getHeight(); //Work out max Y value where the scene doesn't show past the background
            if (maxY < newY) { //If keeping the user centered would cause a view showing past the end of the background (bottom of screen)
                newY = maxY; //Set the new Y, so only the view doesn't go further than the background image (bottom of screen at edge of background, not past it)
            } else if (newY < 1) {  //Else, if new Y would cause showing past the top side of the background
                newY = 0; //Set new Y to 0, so top of background is shown instead, not before it
            }
            pane.setTranslateY(-(newY)); //Move the pane to new Y co-ordinate
        }
    }
    /**
     * Called when a key is press. Decides how to proceed
     */
    private void processKey(KeyCode code, boolean isPressed){
        switch (code){
            case LEFT:case A:
                player.setLeft(isPressed);
                break;
            case RIGHT: case D:
                player.setRight(isPressed);
                break;
            case UP: case W:
                player.setUp(isPressed);
                break;
            case DOWN: case S:
                player.setDown(isPressed);
                break;
            case DIGIT1: case NUMPAD1: //Conveyor
                if (isPressed){ //Only fire on keydown, not keyup
                    if (isPlaceableSelected && chosenPlaceable.selectedPlaceable != null && chosenPlaceable.selectedPlaceable.equals("conveyor")){
                        noLongerPlacingObject();
                    }
                    else {
                        chosenPlaceable.isRotatable = true;
                        chosenPlaceable.selectedRotation = 0;
                        chosenPlaceable.selectedPlaceable = "conveyor";
                        chosenPlaceable.img = GameImages.clone("conveyor2-0");
                        if (!isPlaceableSelected){
                            isPlaceableSelected = true;
                        }
                    }
                }
                break;
            case DIGIT2: case NUMPAD2:
                if(isPressed){
                    if (isPlaceableSelected && chosenPlaceable.selectedPlaceable != null && chosenPlaceable.selectedPlaceable.equals("miner")){
                        noLongerPlacingObject();
                    }
                    else {
                        chosenPlaceable.isRotatable = false;
                        chosenPlaceable.selectedRotation = 0;
                        chosenPlaceable.selectedPlaceable = "miner";
                        chosenPlaceable.img = GameImages.clone("iron_miner");
                        if (!isPlaceableSelected){
                            isPlaceableSelected = true;
                        }
                    }
                }
                break;
            case DIGIT3: case NUMPAD3:
                if(isPressed){
                    if (isPlaceableSelected && chosenPlaceable.selectedPlaceable != null && chosenPlaceable.selectedPlaceable.equals("sell_point")){
                        noLongerPlacingObject();
                    }
                    else {
                        chosenPlaceable.isRotatable = false;
                        chosenPlaceable.selectedRotation = 0;
                        chosenPlaceable.selectedPlaceable = "sell_point";
                        chosenPlaceable.img = GameImages.clone("sell_point");
                        if (!isPlaceableSelected){
                            isPlaceableSelected = true;
                        }
                    }
                }
                break;
            case DIGIT4: case NUMPAD4:
                if(isPressed){
                    if (isPlaceableSelected && chosenPlaceable.selectedPlaceable != null && chosenPlaceable.selectedPlaceable.equals("splurger")){
                        noLongerPlacingObject();
                    }
                    else {
                        chosenPlaceable.isRotatable = false;
                        chosenPlaceable.selectedRotation = 0;
                        chosenPlaceable.selectedPlaceable = "splurger";
                        chosenPlaceable.img = GameImages.clone("splurger");
                        if (!isPlaceableSelected){
                            isPlaceableSelected = true;
                        }
                    }
                }
                break;
            case DIGIT5: case NUMPAD5:
                if(isPressed){
                    if (isPlaceableSelected && chosenPlaceable.selectedPlaceable != null && chosenPlaceable.selectedPlaceable.equals("builder")){
                        noLongerPlacingObject();
                    }
                    else {
                        chosenPlaceable.isRotatable = false;
                        chosenPlaceable.selectedRotation = 0;
                        chosenPlaceable.selectedPlaceable = "builder";
                        chosenPlaceable.img = GameImages.clone("builder");
                        if (!isPlaceableSelected){
                            isPlaceableSelected = true;
                        }
                    }
                }
                break;
            case R: //Rotate (if allowed)
                if(isPressed) {
                    if (chosenPlaceable.isRotatable) {
                        chosenPlaceable.selectedRotation = (chosenPlaceable.selectedRotation + 1) % 4; //Change rotation, change image
                        chosenPlaceable.img = GameImages.clone(chosenPlaceable.selectedPlaceable + ((chosenPlaceable.selectedRotation+2)%4) + "-" + chosenPlaceable.selectedRotation);
                    }
                }
                break;
            case DELETE: case BACK_SPACE: case Q: case Z:
                if (isPressed){
                    if (isPlaceableSelected && chosenPlaceable.selectedPlaceable != null && chosenPlaceable.selectedPlaceable.equals("bin")){
                        noLongerPlacingObject();
                    }
                    else {
                        chosenPlaceable.isRotatable = false;
                        chosenPlaceable.selectedRotation = 0;
                        chosenPlaceable.selectedPlaceable = "bin";
                        chosenPlaceable.img = GameImages.clone("bin");
                        if (!isPlaceableSelected){
                            isPlaceableSelected = true;
                        }
                    }
                }
                break;
            case ESCAPE:
                noLongerPlacingObject();
                break;
            default:
                break;
        }
        //Do/Skip section in animation timer that controls user movement, based on if user is/isn't moving respectively
        if (!player.getDown() && !player.getRight() && !player.getLeft() && !player.getUp()){
            isPlayerMoving = false;
        }else {
            isPlayerMoving = true;
        }
    }
    /**
     * Adds the interactive interface
     */
    private void addGrid(){
        grid = new GridSegment[tilesX][tilesY];
        for (int x = 0; x < tilesX; x++) { //Add all tiles (GridSegment) for placeables
            for (int y = 0; y < tilesY; y++) {
                grid[x][y] = new GridSegment(this,(x * tilesWidth)+xBcOffset, (y * tilesHeight)+yBcOffset, tilesWidth, tilesHeight,x,y); //Position tiles next to each other of specified size, with a 25px offset
                pane.getChildren().add(grid[x][y]); //Add rectangle to pane
            }
        }
    }
    /**
     * Reset values to default when user is no longer placing objects
     */
    private void noLongerPlacingObject(){
        chosenPlaceable.reset();
        isPlaceableSelected = false;
        gcLayer4.clearRect(lastDrawX,lastDrawY,tilesWidth*0.75,tilesHeight*0.75);
    }
    /**
     * Adds resources to the map in random places
     */
    public void addResources()  {
        Random rand = new Random();
        for (int x = 0; x<3; x++){
            int amountOfResource;
            int totalTiles = tilesX*tilesY;
            int minResources, deviance, direction;
            String resourceType;
            switch (x){
                case 0:  //Add rocks
                    resourceType="copper";
                    minResources = (int)((2.0/10.0)*0.005*totalTiles)+1;
                    deviance = (int)(0.2*minResources);
                    direction = rand.nextInt(2);
                    switch (direction){
                        case 0:
                            amountOfResource = minResources + rand.nextInt(deviance+1);
                            break;
                        case 1:
                            amountOfResource = minResources - rand.nextInt(deviance+1);
                            break;
                        default:
                            amountOfResource=0;
                            System.out.println("This shouldnt be possible");
                            break;
                    }
                    amountOfResource = 3 + rand.nextInt(5);
                    break;
                case 1:
                    resourceType="tree";
                    minResources = (int)((3.0/9.0)*0.005*totalTiles)+1;
                    deviance = (int)(0.2*minResources);
                    direction = rand.nextInt(2);
                    switch (direction){
                        case 0:
                            amountOfResource = minResources + rand.nextInt(deviance+1);
                            break;
                        case 1:
                            amountOfResource = minResources - rand.nextInt(deviance+1);
                            break;
                        default:
                            amountOfResource=0;
                            System.out.println("This shouldnt be possible");
                            break;
                    }
                    break;
                case 2:
                    resourceType="iron";
                    minResources = (int)((5.0/9.0)*0.005*totalTiles)+1;
                    deviance = (int)(0.2*minResources);
                    direction = rand.nextInt(2);
                    switch (direction){
                        case 0:
                            amountOfResource = minResources + rand.nextInt(deviance+1);
                            break;
                        case 1:
                            amountOfResource = minResources - rand.nextInt(deviance+1);
                            break;
                        default:
                            amountOfResource =0;
                            System.out.println("This shouldnt be possible");
                            break;
                    }
                    break;
                default:
                    amountOfResource =0;
                    resourceType = "";
                    break;
            }
            for (int y = 0; y< amountOfResource; y++) {
                boolean added = false;
                while (!added) {
                    int posX = rand.nextInt(grid.length); //Picks a random index grid array
                    int posY = rand.nextInt(grid[0].length);
                    if (grid[posX][posY].getResourceType().equals("plain")) {
                        grid[posX][posY].setResourceType(resourceType);
                        Image img = GameImages.clone(resourceType);
                        gcBackground.drawImage(img, (posX * tilesWidth) + xBcOffset, (posY * tilesHeight) + yBcOffset,tilesWidth,tilesHeight);
                        added = true;
                    }
                }
            }
        }
    }
    EventHandler<MouseEvent> addingPlaceable = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            mouseX = event.getX();
            mouseY = event.getY();


        }
    };

    /**
     * Main controller - Provides CPU time for everything that requires it
     */
    AnimationTimer performAllActions = new AnimationTimer() {
        @Override
        public void handle(long now) {
            //Show mini image of placeable if placeable is selected
            if (isPlaceableSelected) {
                if (lastDrawX != mouseX || lastDrawY != mouseY) {
                    gcLayer4.clearRect(lastDrawX, lastDrawY, tilesWidth * 0.75, tilesWidth * 0.75); //Clear canvas
                    gcLayer4.drawImage(chosenPlaceable.img, mouseX + 10, mouseY + 10, tilesWidth * 0.75, tilesWidth * 0.75); //Draw image, slight offset to mouse pointer
                    lastDrawX = mouseX + 10;
                    lastDrawY = mouseY + 10;
                }
            }

            //Move player
            if (isPlayerMoving){
                Player.move(pane); //Move player
                movePane();  //Move pane
            }

            //Update fps
            long oldFrameTime = frameTimes[frameTimeIndex];
            frameTimes[frameTimeIndex] = now;
            frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;

            if (frameTimeIndex == 0) {
                arrayFilled = true;
            }
            if (arrayFilled) {
                long elapsedNanos = now - oldFrameTime;
                long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                int frameRate = (int) (1_000_000_000 / elapsedNanosPerFrame);
                primaryStage.setTitle(String.format(frameRate + "fps"));
            }

            //Deal with delete
            if (reqDelete != null){
                reqDelete.deletePlaceable();
                reqDelete = null;
            }
            //Deal with add
            if (reqAdd != null){
                reqAdd.addPlaceable();
                reqAdd = null;
            }

            //Update Placeables
            if (!allPlaceables.isEmpty()){
                for (GameObject placeable: allPlaceables.values()){
                    ((Placeable)placeable).performAction();
                }
            }
        }
    };


}
