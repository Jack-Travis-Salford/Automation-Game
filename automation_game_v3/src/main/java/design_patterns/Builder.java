package design_patterns;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class Builder extends Placeable implements PlaceableIF{

    /**
     * Creates a Placeable
     *
     * @param gc          Graphics context that the Placeable will be drawn on
     * @param x           X position of top left corner of placeable
     * @param y           Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param uID         Unique identifier of this placeable
     */
    public Builder(GraphicsContext gc, double x, double y, GridSegment gridSegment, String uID) {
        super(gc, x, y, gridSegment, uID);
        next = new GameObject[4];
        prev = new GameObject[1];
        type = "builder";
        img = GameImages.clone(type);

    }

    public void setNext(GameObject next){}
    public void setPrev(GameObject prev){}
    public void reqDelete(){}
    public void unsetNext(GameObject gameObject){}
    public void unsetPrev(GameObject gameObject){}
    public boolean incomingSendRequest(GameObject resource, GameObject placeable){return false;}
    public void  incomingGetRequest(){}
    public void performAction(){}
}
