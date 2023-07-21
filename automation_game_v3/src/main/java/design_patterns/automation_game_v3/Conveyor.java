package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;

public class Conveyor extends Placeable {
    private final int rotation; //The rotation of the conveyor 0: left->Right 1: Up->Down 2:Right->Left 3:Down->Up
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
        super(gc, x, y, gridSegment, uID,1,1,"conveyor");
        this.rotation = rotation;
        int prevPosition = (rotation+2)%4;
        setImg(GameImages.clone(getType() + prevPosition +"-" +rotation));
    }
    public void performAction(){
        if (getCurrentResource() != null && !isResourceReadyToSend()) {
            getCurrentResource().update();
            ((Resource) getCurrentResource()).incrementResourcePosition();
            if (((Resource) getCurrentResource()).getResourcePosition() == ((Resource)getCurrentResource()).getDrawsInMovement()) { //If resource has reached destination
                endMovement();
            }
        }
    }
    /**
     * Sets the next grid segment when one is present
     * @param gameObject Next grid segment
     */
    public void setNext(GameObject gameObject) {
        if (getNext()[0] == null) {    //Ensure next isn't already set
            getNext()[0] = gameObject; //Set new next
            send(); //See if this is ready to send an object
        }
    }
    /**
     * Checks to see if this is ready to send a resource.
     * If it is, it attempts to send the retrieve.
     * If resource is sent, it attempts to retrieve the next resource from previous
     */
    private void send() {
        if (isResourceReadyToSend() && getNext()[0] != null) { //If resource is ready to be sent, and next is sent
            boolean sent = ((Placeable) getNext()[0]).incomingSendRequest(getCurrentResource(), this); //See if next placeable want resource
            if (sent) { //If it took it
                setCurrentResource(null); //Remove pointer
                setResourceReadyToSend(false); //Denote lack of ready item
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
        if (getPrev()[0] != null){ //If prev exists
            ((Placeable)getPrev()[0]).incomingGetRequest(); //Request resource
        }
    }
    /**
     * Called when a prev attempts to give this a resource
     * @param resource The resource being passed on
     * @param placeable The placeable sending the resource
     */
    public boolean  incomingSendRequest(GameObject resource, GameObject placeable){
        if (getCurrentResource() == null){ //If this doesn't have a resource
            setCurrentResource(resource); //Accept resource
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
        double moveToX = getX() + (0.25*Main.getTilesHeight());
        double moveToY = getY() + (0.25*Main.getTilesWidth());
        ((Resource)getCurrentResource()).newCoords(moveToX,moveToY);
    }
    private void endMovement(){
        setResourceReadyToSend(true);
        ((Resource)getCurrentResource()).setResourcePosition(0); //Reset current position counter
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
        if (getPrev()[0] == null){
            getPrev()[0] = gameObject;
            int xDiff = getRelatedTile().getXPosInGrid() - ((Placeable)getPrev()[0]).getRelatedTile().getXPosInGrid();
            int yDiff = getRelatedTile().getYPosInGrid() - ((Placeable)getPrev()[0]).getRelatedTile().getYPosInGrid();
            switch (xDiff){
                case 0:
                    switch (yDiff){
                        case 1:
                            setImg(GameImages.clone(getType() + 3 +"-" +rotation));
                            break;
                        case -1:
                            setImg(GameImages.clone(getType() + 1 +"-" +rotation));
                            break;
                        default:
                            break;
                    }
                    break;
                case 1:
                    setImg(GameImages.clone(getType() + 2 +"-" +rotation));
                    break;
                case -1:
                    setImg(GameImages.clone(getType() + 0 +"-" +rotation));
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
        getNext()[0] = null;
    }

    /**
     * Called when prev is deleted. Removes prev from this
     * @param gameObject Former prev placeable
     */
    public void unsetPrev(GameObject gameObject){
        getPrev()[0] = null;
        getRelatedTile().checkForPrev();
    }
    /**
     * Called upon deletion of placeable. Removes all reference of it from next and previous placeables, and
     * removes any resources associated with this
      */
    public void reqDelete(){
        super.reqDelete();
        if (getCurrentResource() != null){
            ((Resource)getCurrentResource()).reqDelete();
        }
    }
    public int getRotation() {
        return rotation;
    }
}