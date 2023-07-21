package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;

public abstract class MultiInputPlaceables extends Placeable{
    private int totalPrev;
    /**
     * Creates a Placeable
     *
     * @param gc          Graphics context that the Placeable will be drawn on
     * @param x           X position of top left corner of placeable
     * @param y           Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param uID         Unique identifier of this placeable
     */
    public MultiInputPlaceables(GraphicsContext gc, double x, double y, GridSegment gridSegment, String uID, int prevSize, int nextSize, String type) {
        super(gc, x, y, gridSegment, uID,prevSize,nextSize,type);
        totalPrev=0;
    }
    public void unsetPrev(GameObject gameObject){
        boolean found = false;
        int currentPos = 0;
        while (!found && currentPos<totalPrev){
            if (getPrev()[currentPos] == gameObject){ //If gameObject is at this position of prev[]
                getPrev()[currentPos] = null; //Remove gameObject
                found = true;  //End loop
                totalPrev--; //Decrement totalPrev
            }else {
                currentPos++; //Try next x position
            }
        }
        for (; currentPos<totalPrev; currentPos++){ //For any elements present past where game object was found
            getPrev()[currentPos] = getPrev()[currentPos+1]; //Move em
            getPrev()[currentPos+1] = null;
        }
    }
    public void setPrev(GameObject gameObject){
        getPrev()[totalPrev] = gameObject;
        totalPrev++;
    }
}