package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class SellPoint extends MultiInputPlaceables{
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
    public SellPoint(GraphicsContext gc, double x, double y, GridSegment gridSegment, String uID) {
        super(gc, x, y, gridSegment, uID,0,4,"sell_point");
        incomingResources = new ArrayList<>();
        setImg(GameImages.clone(getType()));
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
                if (((Resource)resource).getResourcePosition() ==((Resource)resource).getDrawsInMovement()){ //If they've finished moving
                    Main.changeBalance(((Resource)resource).getValue());
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
    /**
     * Set previous placeable in next available space in array
     * @param gameObject Previous placeable
     */
    public void setPrev(GameObject gameObject){
        super.setPrev(gameObject);
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
        super.unsetPrev(gameObject);
    }

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
    private void prepareMovement(GameObject gameObject){
        double moveToX = getX() + (0.25*Main.getTilesHeight());
        double moveToY = getY() + (0.25*Main.getTilesWidth());
        ((Resource)gameObject).newCoords(moveToX,moveToY);
        incomingResources.add(gameObject);
    }
}