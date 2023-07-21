package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.HashMap;

public class Builder extends MultiInputPlaceables{
    private int outputBuffer;
    private long craftStartTime;
    private HashMap<String,BuilderInput> inputResources;
    private Recipe recipe;
    private ArrayList<GameObject> incomingResources; //All resources moving toward placeable
    private boolean currentlyCrafting;
    /**
     * Creates a Placeable
     * @param gc          Graphics context that the Placeable will be drawn on
     * @param x           X position of top left corner of placeable
     * @param y           Y position of top left corner of placeable
     * @param gridSegment The gridSegment this placeable belongs to
     * @param uID         Unique identifier of this placeable
     */
    public Builder(GraphicsContext gc, double x, double y, GridSegment gridSegment, String uID) {
        super(gc, x, y, gridSegment, uID,1,4,"builder");
        setImg(GameImages.clone(getType()));
        recipe = null;
        inputResources = new HashMap<>();
        outputBuffer=0;
        currentlyCrafting = false;
        incomingResources = new ArrayList<>();
        craftStartTime = 0;
    }
    public void setRecipe(Recipe recipe){
        this.recipe = recipe;
        outputBuffer=0;
        currentlyCrafting = false;
        for (GameObject gameObject: incomingResources){
            ((Resource)gameObject).reqDelete();
        }
        incomingResources = new ArrayList<>();
        inputResources = new HashMap<>();
        if (recipe!= null) {
            for (String key : recipe.getInputResources().keySet()) {
                RecipeInput ri = recipe.getInputResources().get(key);
                inputResources.put(ri.getResourceName(), new BuilderInput(ri.getNoRequired()));
            }
            double resourceXPos = getX() + (0.25 * Main.getTilesWidth());
            double resourceYPos = getY() + (0.25 * Main.getTilesHeight());
            setCurrentResource(((Resource) recipe.getOutput()).clone());
            ((Resource)getCurrentResource()).setCoordinates(resourceXPos, resourceYPos);
        }
    }
    public void setNext(GameObject gameObject){
        if (getNext()[0] == null) {    //Ensure next isn't already set
            getNext()[0] = gameObject; //Set new next
            send(); //See if this is ready to send an object
        }
    }
    public void setPrev(GameObject gameObject){
        super.setPrev(gameObject);
    }
    public void reqDelete(){
        super.reqDelete();
        if (!incomingResources.isEmpty()){ //Delete resources if any are assigned
            for (GameObject resource: incomingResources){
                ((Resource)resource).reqDelete();
            }
        }
    }
    public void unsetNext(GameObject gameObject){
        getNext()[0] = null;
        getRelatedTile().checkForNextConveyor(1);
    }
    public void unsetPrev(GameObject gameObject){
        super.unsetPrev(gameObject);
    }
    public boolean incomingSendRequest(GameObject resource, GameObject placeable){
        BuilderInput bi = inputResources.getOrDefault(((Resource)resource).getResourceName(), null);
        if(bi != null){
            if (bi.getInBuffer() < 25){
                prepareMovement(resource);
                return true;
            }else{
                return false;
            }
        }else {
            return false;
        }
    }
    private void prepareMovement(GameObject gameObject){
        double moveToX = getX() + (0.25*Main.getTilesHeight());
        double moveToY = getY() + (0.25*Main.getTilesWidth());
        ((Resource)gameObject).newCoords(moveToX,moveToY);
        incomingResources.add(gameObject);
    }
    public void  incomingGetRequest(){
        if (outputBuffer>0){
            send();
        }
    }
    public void performAction(){
        //Movement of incoming
        if (recipe!=null){
            if (!incomingResources.isEmpty()){
                int pos= 0; //How many elements into incomingResources the loop is in
                int y = 0; //How many elements into toRemove the loop is in
                int[] toRemove = new int[incomingResources.size()]; //Set toRemove to max potential size needed
                for (GameObject resource: incomingResources){ //For every resource in incomingResources
                    resource.update(); //Update their location
                    ((Resource)resource).incrementResourcePosition(); //Increment their counter
                    if (((Resource)resource).getResourcePosition() ==((Resource)resource).getDrawsInMovement()){ //If they've finished moving
                        ((Resource)resource).setResourcePosition(0);
                        inputResources.get(((Resource)resource).getResourceName()).incrementBuffer();
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
            if (currentlyCrafting){ //If there's an item currently being crafted.
                long timePassed = System.currentTimeMillis() - craftStartTime; //Calculate time passed
                if (timePassed >= recipe.getCraftTime()){ //If enough time has passed
                    outputBuffer+= recipe.getOutputsProduced(); //Increment output buffer
                    currentlyCrafting = false; //Indicate that nothing is being crafted
                }
            }
            send(); //Attempt to send
            if (!currentlyCrafting && outputBuffer<25){ //If output buffer isn't full
                boolean enoughResources = true;
                for (String key: inputResources.keySet()){ //Check If there's enough of each resource
                    if(!inputResources.get(key).enoughResources()){
                        enoughResources = false;
                    }
                }
                if(enoughResources){ //If there's enough of each resource
                    for(String key: inputResources.keySet()){
                        inputResources.get(key).removeFromBuffer(); //Remove the resources
                    }
                    craftStartTime = System.currentTimeMillis(); //Start timer
                    currentlyCrafting = true; //Indicate crafting has begun
                }
            }
            retrieve();
        }
    }
    private void retrieve(){
        for (GameObject gameObject: getPrev()){
            if (gameObject!=null) {
                ((Placeable) gameObject).incomingGetRequest();
            }
        }
    }
    private void send(){
        if (outputBuffer>0 && getNext()[0] != null){
            boolean sent = ((Placeable)getNext()[0]).incomingSendRequest(getCurrentResource(),this); //Send resource
            if (sent){ //If accepted
                setCurrentResource(((Resource)getCurrentResource()).clone()); //Create a new resource
                outputBuffer--; //Decrement buffer
            }
        }
    }
    public Recipe getRecipe() {
        return recipe;
    }
}
