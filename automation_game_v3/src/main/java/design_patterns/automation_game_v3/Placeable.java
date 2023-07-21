package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Placeable extends GameObject {
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
    private final GridSegment relatedTile; //GridSegment that this placeable relates to
    private final String uID; //Unique id of placeable.
    private final GameObject[] prev; //All placeables that feed to this placeable
    private final GameObject[] next; //All placeables that this placeable feeds to
    private final String type; //Name of placeable
    private boolean resourceReadyToSend; //True if resource is ready to be sent
    private GameObject currentResource; //Resource in placeables position
    /**
     * Creates a Placeable
     * @param gc Graphics context that the Placeable will be drawn on
     * @param x X position of top left corner of placeable
     * @param y Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param uID Unique identifier of this placeable
     * @param nextSize Size of next[] (how many next placeables are allowed)
     * @param prevSize Size of prev[] (how many prev placeables  are allowed)
     * @param type Name of placeable type
     */
    public Placeable(GraphicsContext gc, double x, double y, GridSegment gridSegment, String uID, int nextSize, int prevSize, String type){
        super(gc,x,y); //Super constructor
        if (nextSize>0){ //If placeable allows at least 1 output
            next = new GameObject[nextSize]; //Set to next
        }else { //Otherwise
            next = null; //Set to null
        }
        if (prevSize>0){ //If placeable allows at least 1 input
            prev = new GameObject[prevSize]; //Set to prev
        }else{ //otherwise
            prev=null; //St to null
        }
        this.type = type;
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
        if (getImg() != null){
            getGc().clearRect(getX()+(1/Main.getZoomLevel()), getY()+(1/Main.getZoomLevel()),Main.getTilesWidth()-(1/Main.getZoomLevel()),Main.getTilesHeight()-(1/Main.getZoomLevel()));
            getGc().drawImage(getImg(),getX(),getY(),Main.getTilesWidth(),Main.getTilesHeight());
        }
    }
    public void setNext(GameObject next){}
    public void setPrev(GameObject prev){}
    /**
     * Code ran when placeable is deleted. Informs any other placeable that it's connected to that its being deleted
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
        getGc().clearRect(getX()+(1/Main.getZoomLevel()), getY()+(1/Main.getZoomLevel()),Main.getTilesWidth()-(1/Main.getZoomLevel()),Main.getTilesHeight()-(1/Main.getZoomLevel()));
    }
    public void unsetNext(GameObject gameObject){}
    public void unsetPrev(GameObject gameObject){}
    public void incomingGetRequest(){}
    public boolean incomingSendRequest(GameObject gameObject, GameObject gameObject2){return false;}
    public void performAction(){}
    public GameObject[] getPrev() {        return prev;    }

    public GameObject[] getNext() {        return next;    }
    public GridSegment getRelatedTile() {        return relatedTile;    }
    public String getUID() {        return uID;    }
    public String getType() {        return type;    }
    public boolean isResourceReadyToSend() {        return resourceReadyToSend;    }
    public GameObject getCurrentResource() {        return currentResource;    }
    public void setCurrentResource(GameObject currentResource) {        this.currentResource = currentResource;    }
    public void setResourceReadyToSend(boolean resourceReadyToSend) {        this.resourceReadyToSend = resourceReadyToSend;    }
}