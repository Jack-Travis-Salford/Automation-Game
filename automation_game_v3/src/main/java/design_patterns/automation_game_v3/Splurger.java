package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class Splurger extends MultiInputPlaceables{
    private int totalNext;
    private int nextToSend;
    private int acceptedToBuffer;
    private final ArrayList<GameObject> buffer;
    private final ArrayList<GameObject> prevSendRequests;
    private final ArrayList<GameObject> incomingResources; //All resources moving toward placeable
    /**
     * Creates a Placeable
     *
     * @param gc          Graphics context that the Placeable will be drawn on
     * @param x           X position of top left corner of placeable
     * @param y           Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param uID         Unique identifier of this placeable
     */
    public Splurger(GraphicsContext gc, double x, double y, GridSegment gridSegment, String uID) {
        super(gc, x, y, gridSegment, uID,4,4,"splurger");
        totalNext = 0;
        incomingResources = new ArrayList<>();
        buffer = new ArrayList<>();
        prevSendRequests = new ArrayList<>();
        setImg(GameImages.clone(getType()));
        nextToSend = 0;
        acceptedToBuffer =0;
    }
    /**
     * Sets the next grid segment when one is present
     * @param gameObject Next grid segment
     */
    public void setNext(GameObject gameObject) {
        getNext()[totalNext] = gameObject;
        totalNext++;
    }
    /**
     * Set previous placeable
     * @param gameObject Previous placeable
     */
    public void setPrev(GameObject gameObject) {
        super.setPrev(gameObject);
    }
    /**
     * Called upon deletion of placeable. Removes all reference of it from next and previous placeables, and
     * removes any resources associated with this
     */
    public void reqDelete() {
        super.reqDelete();
        if (!incomingResources.isEmpty()){ //Delete resources if any are assigned
            for (GameObject resource: incomingResources){
                ((Resource)resource).reqDelete();
            }
        }
    }
    /**
     * Called when next is deleted. Removes next from this
     * @param gameObject Former next placeable
     */
    public void unsetNext(GameObject gameObject) {
        boolean found = false;
        int currentPos = 0;
        while (!found && currentPos<totalNext){
            if (getNext()[currentPos] == gameObject){ //If gameObject is at this position of prev[]
                getNext()[currentPos] = null; //Remove gameObject
                found = true;  //End loop
                totalNext--; //Decrement totalPrevs
                if (totalNext == 0){
                    nextToSend = 0;
                }else {
                    nextToSend %= totalNext;
                }
            }else {
                currentPos++; //Try next x position
            }
        }
        for (; currentPos<totalNext; currentPos++){ //For any elements present past where game object was found
            getNext()[currentPos] = getNext()[currentPos+1]; //Move em
            getNext()[currentPos+1] = null;
        }
    }
    /**
     * Called when prev is deleted. Removes prev from this
     * @param gameObject Former prev placeable
     */
    public void unsetPrev(GameObject gameObject) {
       super.unsetPrev(gameObject);
        boolean found = false;
        int currentPos=0;
        while (!found && currentPos<prevSendRequests.size()){
            if (prevSendRequests.get(currentPos) == gameObject){
                prevSendRequests.remove(currentPos);
                found = true;
                currentPos++;
            }
        }
    }
    /**
     * Called when a prev attempts to give this a resource
     * @param resource The resource being passed on
     * @param placeable The placeable sending the resource
     */
    public boolean incomingSendRequest(GameObject resource, GameObject placeable) {
        if (acceptedToBuffer < 10){ //If there's space in the buffer
            prepareMovement(resource); //Accept the resource
            acceptedToBuffer++;
            return true;
        }else { //Else
            prevSendRequests.add(placeable); //Make note that placeable is ready to send
            return false;
        }

    }
    /**
     * Checks to see if this is ready to send a resource.
     * If it is, it attempts to send the retrieve.
     * If resource is sent, it attempts to retrieve the next resource from previous
     */
    private void send(int nextPos) {
        boolean sent = ((Placeable)getNext()[nextPos]).incomingSendRequest(buffer.get(0),this);
        if (sent){
            buffer.remove(0);
            acceptedToBuffer--;
            retrieve();
        }
    }
    /**
     * Checks if a previous placeable is set.
     * Requests to get resource.
     * If there is a resource (returned), save resource to currentResource and start moving
     */
    private void retrieve(){
        if (!prevSendRequests.isEmpty()){ //If at least 1 conveyor is requested to send a resource previously
            ((Placeable)prevSendRequests.get(0)).incomingGetRequest(); //Ask that conveyor for the resource
            prevSendRequests.remove(0); //Remove the requests from the request queue
        }
    }
    public void performAction() {
        //Movement of incoming
        if (!incomingResources.isEmpty()){
            int pos= 0; //How many elements into incomingResources the loop is in
            int y = 0; //How many elements into toRemove the loop is in
            int[] toRemove = new int[incomingResources.size()]; //Set toRemove to max potential size needed
            for (GameObject resource: incomingResources){ //For every resource in incomingResources
                resource.update(); //Update their location
                ((Resource)resource).incrementResourcePosition(); //Increment their counter
                if (((Resource)resource).getResourcePosition() ==((Resource)resource).getDrawsInMovement()){ //If they've finished moving
                    ((Resource)resource).setResourcePosition(0);
                    ((Resource) resource).reqDelete(); //Remove resource from canvas
                    buffer.add(resource); //Move resource to buffer
                    toRemove[y] = pos; //Take note of position in array for deletion afterwards
                    y++;
                }
                pos++;
            }
            for (; y>0; y--){ //Remove all resources that finished moving (end of life) in reverse order they're in incoming resources
                incomingResources.remove(toRemove[y-1]);
            }
        }
        int current = 0;
        //Sending of resources in buffer
        while (!buffer.isEmpty() && current<totalNext){ //Whilst there's an item in the buffer, and this hasn't attempted to send items to all in next
            send(nextToSend); //Attempt to send
            nextToSend = (nextToSend+1)%totalNext; //Update who is next to send
            current++; //Increment current
        }
    }
    /**
     * Sets necessary values of variable prior to adding to array (and subsequently moving it)
     */
    public void prepareMovement(GameObject gameObject){
        double moveToX = getX() + (0.25*Main.getTilesHeight());
        double moveToY = getY() + (0.25*Main.getTilesWidth());
        ((Resource)gameObject).newCoords(moveToX,moveToY);
        incomingResources.add(gameObject);
    }
}