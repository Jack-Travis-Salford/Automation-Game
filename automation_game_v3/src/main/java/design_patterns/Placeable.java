package design_patterns;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;
public class Placeable extends GameObject implements PlaceableIF {
    /**
     * prev - Holds all grid segments that feed into this placeable
     * next - Holds all grid segments that this placeable feeds
     * uID - Identifier for this placeable, relates to which GridSegment it belongs to
     * type - The type of placeable it is (Eg - Conveyor, Miner ...)
     * resourceReadyToSend - True if this placeable has a resource assigned to it, and it has finished moving (ready to feed to next placeable)
     * currentResource - Pointer to the resource assigned to placeable
     * moveResource- Animation timer, which has the job of moving the resource
     * relatedGrid - Pointer to the GridSegment that this placeable belongs to
     */
    GameObject[] prev, next;
    String uID, type;
    boolean resourceReadyToSend;
    GameObject currentResource;
    GridSegment relatedTile;

    /**
     * Creates a Placeable
     * @param gc Graphics context that the Placeable will be drawn on
     * @param x X position of top left corner of placeable
     * @param y Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param uID Unique identifier of this placeable
     */
    public Placeable(GraphicsContext gc, double x, double y, GridSegment gridSegment, String uID){
        super(gc,x,y); //Super constructor
        this.relatedTile = gridSegment; //Set variables
        gc.setFill(Color.TRANSPARENT);
        this.uID = uID;
        resourceReadyToSend = false;
        currentResource = null;
        update(); //Draw placeable on canvas.
    }

    /**
     * Draws image of placeable on canvas
     */
    public void  update(){
        if (img != null){
            gc.clearRect(x+1,y+1,Main.tilesWidth-1,Main.tilesHeight-1);
            gc.drawImage(img,x,y,Main.tilesWidth,Main.tilesHeight);
        }
    }

    public void setNext(GameObject next){}
    public void setPrev(GameObject prev){}

    /**
     * Code ran when placeable is deleted. Informs any other placeable that its connected to that its being deleted
     */
    public void reqDelete(){
        //Get all connections, and remove reference to this object
        if (prev != null) {
            for (GameObject gameObject : prev) {
                if (gameObject != null) {
                    ((Placeable) gameObject).unsetNext(this);
                }
            }
        }
        if (next != null) {
            for (GameObject gameObject : next) {
                if (gameObject != null) {
                    ((Placeable) gameObject).unsetPrev(this);
                }
            }
        }
        gc.clearRect(x+1,y+1,Main.tilesWidth-1,Main.tilesHeight-1); //Remove 1 from map
    }

    public void unsetNext(GameObject gameObject){}
    public void unsetPrev(GameObject gameObject){}
    public void incomingGetRequest(){}
    public boolean incomingSendRequest(GameObject gameObject, GameObject gameObject2){return false;}
    public void performAction(){}







}
