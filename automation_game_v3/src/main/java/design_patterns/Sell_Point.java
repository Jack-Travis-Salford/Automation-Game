package design_patterns;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class Sell_Point extends Placeable implements  PlaceableIF{
    int totalPrevs; //How many placeables this sell point has connected
    ArrayList<GameObject> incomingResources; //All resources moving toward placeable
    /**
     * Creates a Placeable
     *
     * @param gc          Graphics context that the Placeable will be drawn on
     * @param x           X position of top left corner of placeable
     * @param y           Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param uID         Unique identifier of this placeable
     */
    public Sell_Point(GraphicsContext gc, double x, double y, GridSegment gridSegment, String uID) {
        super(gc, x, y, gridSegment, uID);
        totalPrevs = 0;
        next = null;
        prev = new GameObject[4];
        type = "sell_point";
        incomingResources = new ArrayList<>();
        img = GameImages.clone(type);
    }

    @Override
    public void performAction() {
        if (!incomingResources.isEmpty()){
            int pos= 0; //How many elements into incomingResources the loop is in
            int y = 0; //How many elements into toRemove the loop is in
            int[] toRemove = new int[incomingResources.size()]; //Set toRemove to max potential size needed
            for (GameObject resource: incomingResources){ //For every resource in incomingResources
                resource.update(); //Update their location
                ((Resource)resource).incrementResourcePosition(); //Increment their counter
                if (((Resource)resource).getResourcePosition() ==12){ //If they've finished moving
                    ((Resource) resource).reqDelete(); //Remove resource from canvas
                    toRemove[y] = pos; //Take note of position in array for deletion afterwards
                    y++;
                }
                pos++;
            }
            for (; y>0; y--){ //Remove all resources that finished moving (end of life) in reverse order they're in incoming resources
                incomingResources.remove(toRemove[y-1]);
            }
        }
    }


    public void setNext(GameObject next){}
    /**
     * Set previous placeable in next available space in array
     * @param gameObject Previous placeable
     */
    public void setPrev(GameObject gameObject){
        prev[totalPrevs] = gameObject; //Add prev to first empty space in array
        totalPrevs++; //Increment totalPrevs
    }
    public void reqDelete(){
        super.reqDelete(); //Inform next and prevs
        if (!incomingResources.isEmpty()){ //Delete resources if any are assigned
            for (GameObject resource: incomingResources){
                ((Resource)resource).reqDelete();
            }
        }
    }
    /**
     * Called when next is deleted. Removes next from array. Moves any item in next to remove empty space if required
     * @param gameObject Former next placeable
     */
    public void unsetPrev(GameObject gameObject){
        boolean found = false;
        int currentPos = 0;
        while (!found && currentPos<totalPrevs){
            if (prev[currentPos] == gameObject){ //If gameObject is at this position of prev[]
                prev[currentPos] = null; //Remove gameObject
                found = true;  //End loop
                totalPrevs--; //Decrement totalPrevs
            }else {
                currentPos++; //Try next x position
            }
        }
        for (; currentPos<totalPrevs; currentPos++){ //For any elements present past where game object was found
            prev[currentPos] = prev[currentPos+1]; //Move em
            prev[currentPos+1] = null;
        }
    }
    public void unsetNext(GameObject gameObject){}
    /**
     * Called when a prev attempts to give this a resource
     * @param resource The resource being passed on
     * @param placeable The placeable sending the resource
     */
    public boolean incomingSendRequest(GameObject resource, GameObject placeable){
        prepareMovement(resource);
        return true;
    }
    /**
     * Sets necessary values of variable prior to adding to array (and subsequently moving it)
     */
    public void prepareMovement(GameObject gameObject){
        double moveToX = x + (0.25*Main.tilesHeight);
        double moveToY = y + (0.25*Main.tilesWidth);
        ((Resource)gameObject).newCoords(moveToX,moveToY);
        incomingResources.add(gameObject);
    }
    public void  incomingGetRequest(){}
}
