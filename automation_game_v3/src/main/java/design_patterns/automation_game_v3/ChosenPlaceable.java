package design_patterns.automation_game_v3;

import javafx.scene.image.ImageView;
public class ChosenPlaceable {
    private static ChosenPlaceable INSTANCE;
    private String selectedPlaceable;
    private boolean isRotatable;
    private int selectedRotation;
    private final ImageView img;
    /**
     * Creates and returns the single allowed instance of ChosenPlaceable
     * @return the only ChosenPlaceable that will be created
     */
    public static ChosenPlaceable getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ChosenPlaceable();
        }
        return INSTANCE;
    }
    /**
     * Constructor for ChosenPlaceable
     * Private as only 1 ChosenPlaceable should ever exist.
     * Use getInstance() instead
     */
    private ChosenPlaceable(){
        isRotatable = false;
        selectedRotation = 0;
        img = new ImageView();
        img.setFitHeight(Main.getTilesHeight()*0.75);
        img.setPreserveRatio(true);
    }
    /**
     * Resets variables to a default value
     */
    public void reset(){
        selectedPlaceable = null;
        isRotatable = false;
        selectedRotation = 0;
        img.setX(-75); //Move image view out of the way
        img.setY(-75);
    }
    /**
     * Called  when user initiates first step of placing a placeable/selected placeable changes
     * @param isRotatable Indicates if chosen placeable can be rotated
     * @param selectedPlaceable The game name for the placeable
     * @param imgName The image name that corresponds to the placeable
     */
    public void newChosen(boolean isRotatable, String selectedPlaceable, String imgName){
        this.isRotatable = isRotatable; //Indicate if placeable is rotatable
        selectedRotation = 0; //Reset rotation
        this.selectedPlaceable = selectedPlaceable; //Set name of placeable
        img.setImage(GameImages.clone(imgName)); //Get image that corresponds to placeable
    }
    /**
     * If placeable is rotatable, rotate it and get new corresponding image
     */
    public void rotate(){
        if (isRotatable) {
            selectedRotation = (selectedRotation + 1) % 4; //Change rotation, change image
            img.setImage(GameImages.clone(selectedPlaceable + ((selectedRotation + 2) % 4) + "-" + selectedRotation));
        }
    }
    public String getSelectedPlaceable() {        return selectedPlaceable;    }
    public int getSelectedRotation() {        return selectedRotation;    }
    public ImageView getImg() {        return img;    }
}