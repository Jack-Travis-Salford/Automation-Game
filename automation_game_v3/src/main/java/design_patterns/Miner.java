package design_patterns;

import javafx.scene.canvas.GraphicsContext;

import java.util.Timer;
import java.util.TimerTask;
public class Miner extends Placeable implements PlaceableIF {
    /**
     * buffer - How many items have been created, but not sent out. Stops at 10.
     * miningInterval - Mines a resource every x milliseconds
     * mineItem - The task the miningInterval timer does
     */
    public int buffer;
    public long resourceMineStartTime;
    public int craftTime;
    //Timer miningInterval;
    //TimerTask mineItem;


    /**
     * Creates a Placeable
     *
     * @param gc          Graphics context that the Placeable will be drawn on
     * @param x           X position of top left corner of placeable
     * @param y           Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param uID         Unique identifier of this placeable
     * @param resource    Name of resource miner is on
     * @param resourceGC  Pointer to GraphicsContext next will be drawn on
     */
    public Miner(GraphicsContext gc, double x, double y, GridSegment gridSegment, String resource, GraphicsContext resourceGC, String uID) {
        super(gc, x, y, gridSegment, uID);  //Initialise variables
        next = new GameObject[1];
        prev = null;
        resourceReadyToSend = false;
        type = "miner";
        //buffer = 0;
        img = GameImages.clone(resource + "_" + type);
        double resourceXPos = x + (0.25*Main.tilesWidth);
        double resourceYPos = y + (0.25*Main.tilesHeight);
        craftTime = 250;
        resourceMineStartTime = System.currentTimeMillis();
        currentResource = new Resource(resourceGC,resourceXPos,resourceYPos,resource + "_ore"); //Create a resource
      //  createTimer(); //Sets up timer and start it
    }

    public void performAction(){
        if (buffer!= 50){ //If the buffer isnt full
            if (System.currentTimeMillis() - resourceMineStartTime > craftTime){ //If enough time has past to create a new resource
                buffer++;
                resourceMineStartTime = System.currentTimeMillis();
            }
            if (buffer>0){
                send();
            }
        }

        //If buffer isnt full, see if new item should be added to it
        //If buffer has at least 1 item, try to send item
    }

    /**
     * Creates timer to represent mining
     */
    //public void createTimer(){
        //miningInterval = new Timer(false);
        //mineItem = new TimerTask() {
        //    @Override
        //    public void run() {
        //        if (buffer < 10){
        //            buffer++;
        //        }
        //        send();
        //    }
        //};
        //miningInterval.schedule(mineItem,1000,200); //Start mining (1s "wind up" time, followed by a resource every 0.2s
    //}
    /**
     * Sets the next grid segment when one is present
     * @param gameObject Next grid segment
     */
    public void setNext(GameObject gameObject){
        if (next[0] == null) {    //Ensure next isnt already set
            next[0] = gameObject; //Set new next
            if (buffer > 0) {
                send(); //See if this is ready to send an object
            }
        }
    }
    /**
     * Tries to send resource to next, if next is present and this has a resource
     * If buffer is at limit, stop mining
     * IF buffer no longer full, start mining
     */
    public void send(){
        if (buffer > 0 && next[0] != null){ //If next is present and theres an item in the buffer
            boolean sent = ((Placeable)next[0]).incomingSendRequest(currentResource,this); //Send resource
            if (sent){ //If accepted
                currentResource = ((Resource)currentResource).clone(); //Create a new resource
                buffer--; //Decrement buffer
            }
        }
    }
    public void setPrev(GameObject prev){}

    /**
     * Called upon deletion of placeable. Removes all reference of it from next and previous placeables
     *
     */
    public void reqDelete(){
        //miningInterval.cancel(); //Stop timer
        super.reqDelete();
    }
    /**
     * Called when next is deleted. Removes next from this
     * @param gameObject Former next placeable
     */
    public void unsetNext(GameObject gameObject){
        next[0] = null;
        relatedTile.checkForNextConveyor(1);
    }
    public void unsetPrev(GameObject gameObject){}
    public boolean incomingSendRequest(GameObject gameObject, GameObject gameObject2){return false;}
    public void  incomingGetRequest(){
        send();
    }
}
