package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;
public class Miner extends Placeable {
    /**
     * buffer - How many items have been created, but not sent out. Stops at 10.
     * miningInterval - Mines a resource every x milliseconds
     * mineItem - The task the miningInterval timer does
     */
    private int buffer;
    private long resourceMineStartTime;
    private final int craftTime;
    /**
     * Creates a Placeable
     *
     * @param gc          Graphics context that the Placeable will be drawn on
     * @param x           X position of top left corner of placeable
     * @param y           Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param uID         Unique identifier of this placeable
     * @param resource    Name of resource miner is on
     */
    public Miner(GraphicsContext gc, double x, double y, GridSegment gridSegment, String resource, String uID) {
        super(gc, x, y, gridSegment, uID,1,0,"miner");  //Initialise variables
        buffer=0;
        setImg(GameImages.clone(resource + "_" + getType()));
        double resourceXPos = x + (0.25*Main.getTilesWidth());
        double resourceYPos = y + (0.25*Main.getTilesHeight());
        craftTime = 250;
        resourceMineStartTime = System.currentTimeMillis();
        setCurrentResource(((Resource)GameResources.getCopy(resource + "_ore")).clone()); //Create a resource
        ((Resource)getCurrentResource()).setCoordinates(resourceXPos, resourceYPos);
    }
    public void performAction(){
        if (buffer!= 20){ //If the buffer isn't full
            if (System.currentTimeMillis() - resourceMineStartTime > craftTime){ //If enough time has past to create a new resource
                buffer++;
                resourceMineStartTime = System.currentTimeMillis();
            }
            if (buffer>0){
                send();
            }
        }
    }
    /**
     * Sets the next grid segment when one is present
     * @param gameObject Next grid segment
     */
    public void setNext(GameObject gameObject){
        if (getNext()[0] == null) {    //Ensure next isn't already set
            getNext()[0] = gameObject; //Set new next
            send(); //See if this is ready to send an object
        }
    }
    /**
     * Tries to send resource to next, if next is present and this has a resource
     * If buffer is at limit, stop mining
     * IF buffer no longer full, start mining
     */
    public void send(){
        if (buffer > 0 && getNext()[0] != null){ //If next is present and there's an item in the buffer
            boolean sent = ((Placeable)getNext()[0]).incomingSendRequest(getCurrentResource(),this); //Send resource
            if (sent){ //If accepted
                setCurrentResource(((Resource)getCurrentResource()).clone()); //Create a new resource
                buffer--; //Decrement buffer
            }
        }
    }
    /**
     * Called when next is deleted. Removes next from this
     * @param gameObject Former next placeable
     */
    public void unsetNext(GameObject gameObject){
        getNext()[0] = null;
        getRelatedTile().checkForNextConveyor(1);
    }
    public boolean incomingSendRequest(GameObject gameObject, GameObject gameObject2){return false;}
    public void  incomingGetRequest(){
        send();
    }
}