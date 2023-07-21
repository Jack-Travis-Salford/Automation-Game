package design_patterns;

import javafx.scene.image.Image;

public class ChosenPlaceable {
    private static ChosenPlaceable INSTANCE;
    protected String selectedPlaceable;
    protected Boolean isRotatable;
    protected int selectedRotation;
    protected Image img;
    private ChosenPlaceable(){
        isRotatable = false;
        selectedRotation = 0;
    }
    public void reset(){
        selectedPlaceable = null;
        isRotatable = false;
        selectedRotation = 0;
        img = null;
    }
    public static ChosenPlaceable getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ChosenPlaceable();
        }
        return INSTANCE;
    }
}