package design_patterns;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class Conveyor extends Placeable implements PlaceableIF {
    int rotation; //The rotation of the conveyor 0: left->Right 1: Up->Down 2:Right->Left 3:Down->Up

    /**
     * Creates a Placeable
     *
     * @param gc          Graphics context that the Placeable will be drawn on
     * @param x           X position of top left corner of placeable
     * @param y           Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param rotation    The rotation of the conveyor
     * @param uID         Unique identifier of this placeable
     */
    public Conveyor(GraphicsContext gc, double x, double y, GridSegment gridSegment, int rotation, String uID) {
        super(gc, x, y, gridSegment, uID);
        next = new GameObject[1];
        prev = new GameObject[1];
        type = "conveyor";
        this.rotation = rotation;
        int prevPosition = (rotation+2)%4;
        img = GameImages.clone(type + prevPosition +"-" +rotation);

    }

    public void performAction(){
        if (currentResource != null && !resourceReadyToSend) {
            currentResource.update();
            ((Resource) currentResource).incrementResourcePosition();
            if (((Resource) currentResource).getResourcePosition() == 12) { //If resource has reached destination
                endMovement();
            }
        }
    }

    /**
     * Sets the next grid segment when one is present
     * @param gameObject Next grid segment
     */
    public void setNext(GameObject gameObject) {
        if (next[0] == null) {    //Ensure next isnt already set
            next[0] = gameObject; //Set new next
            send(); //See if this is ready to send an object
        }
    }
    /**
     * Checks to see if this is ready to send an resource.
     * If it is, it attempts to send the retrieve.
     * If resource is sent, it attempts to retrieve the next resource from previous
     */
    private void send() {
        if (resourceReadyToSend && next[0] != null) { //If resource is ready to be sent, and next is sent
            boolean sent = ((Placeable) next[0]).incomingSendRequest(currentResource, this); //See if next placeable want resource
            if (sent) { //If it took it
                currentResource = null; //Remove pointer
                resourceReadyToSend = false; //Denote lack of ready item
                retrieve(); //See if prev has item for this
            }
        }
    }
    /**
     * Checks if a previous placeable is set.
     * Requests to get resource.
     * If there is a resource (returned), save resource to currentResource and start moving
     */
    private void retrieve(){
        if (prev[0] != null){ //If prev exists
            ((Placeable)prev[0]).incomingGetRequest(); //Request resource
        }
    }
    /**
     * Called when a prev attempts to give this a resource
     * @param resource The resource being passed on
     * @param placeable The placeable sending the resource
     */
    public boolean  incomingSendRequest(GameObject resource, GameObject placeable){
        if (currentResource == null){ //If this doesnt have a resource
            currentResource = resource; //Accept resource
            prepareMovement(); //First step to begin moving resource to new location
            return true;
        }else{ //Decline resource
            return false;
        }
    }
    /**
     * Sets necessary values of variable prior to moving a received resource
     */
    private void prepareMovement(){
        double moveToX = x + (0.25*Main.tilesHeight);
        double moveToY = y + (0.25*Main.tilesWidth);
        ((Resource)currentResource).newCoords(moveToX,moveToY);
    }
    private void endMovement(){
        resourceReadyToSend =true;
        ((Resource)currentResource).setResourcePosition(0); //Reset current position counter
        send();
    }
    /**
     * Called when a next attempts to get resource from this
     */
    public void incomingGetRequest(){
            send();
    }
    /**
     * Set previous placeable
     * @param gameObject Previous placeable
     */
    public void setPrev(GameObject gameObject){
        if (prev[0] == null){
            prev[0] = gameObject;
            //Get prev grid coords
            //Compare grid coords to this one
            //Draw correct conveyor
            //Get position of prev in relation to this tile (one should be 0, the other should be -1 or 1
            int xDiff = relatedTile.xPosInGrid - ((Placeable)prev[0]).relatedTile.xPosInGrid;
            int yDiff = relatedTile.yPosInGrid - ((Placeable)prev[0]).relatedTile.yPosInGrid;
            switch (xDiff){
                case 0:
                    switch (yDiff){
                        case 1:
                            img = GameImages.clone(type + 3 +"-" +rotation);
                            break;
                        case -1:
                            img = GameImages.clone(type + 1 +"-" +rotation);
                            break;
                        default:
                            break;
                    }
                    break;
                case 1:
                    img = GameImages.clone(type + 2 +"-" +rotation);
                    break;
                case -1:
                    img = GameImages.clone(type + 0 +"-" +rotation);
                    break;
                default:
                    break;
            }
            update();
        }
    }

    /**
     * Called when next is deleted. Removes next from this
     * @param gameObject Former next placeable
     */
    public void unsetNext(GameObject gameObject){
        next[0] = null;
    }

    /**
     * Called when prev is deleted. Removes prev from this
     * @param gameObject Former prev placeable
     */
    public void unsetPrev(GameObject gameObject){
        prev[0] = null;
        relatedTile.checkForPrev();
    }

    /**
     * Called upon deletion of placeable. Removes all reference of it from next and previous placeables, and
     * removes any resources associated with this
      */
    public void reqDelete(){
        super.reqDelete();
        if (currentResource != null){
            ((Resource)currentResource).reqDelete();
        }
    }


}