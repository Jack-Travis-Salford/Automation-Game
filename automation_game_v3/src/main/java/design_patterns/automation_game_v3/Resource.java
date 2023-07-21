package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;

public class Resource extends GameObject{
    private final String resourceName; //Name of resource
    private final double value; //Value of resource
    private int resourcePosition; //How many times the resource has been moved when travelling between placeable
    private double moveX; //How many pixels in x direction to move with each draw
    private double moveY; //How many pixels in y direction to move with each draw
    private int drawsInMovement;
    /**
     * Creates a default, reusable instance of a resource.
     * @param gc GraphicsContext that a resource will be drawn on
     * @param resourceName Name of corresponding resource
     * @param value //In game value of resource
     */
    public Resource(GraphicsContext gc, String resourceName,double value) {
        super(gc, 0,0);
        this.resourceName =resourceName;
        setImg(GameImages.clone(resourceName));
        resourcePosition =0;
        this.value = value;
        double totalDraws;
        totalDraws = 0.125*Main.getAchievedFrameRate();
        drawsInMovement = (int)totalDraws;
    }
    /**
     * Creates more specific Resource with set coordinates
     * @param gc GraphicsContext that a resource will be drawn on
     * @param x X position on canvas
     * @param y Y position on canvas
     * @param resourceName Name of corresponding resource
     * @param value In game value of resource
     */
    private Resource(GraphicsContext gc, double x, double y, String resourceName, double value){
        this(gc,resourceName,value); //Call less specific constructor
        setX(x);//Set position coordinates
        setY(y);
    }
    /**
     * Moves resource by xChange and yChange pixels
     */
    public void update() {
        getGc().clearRect(getX(),getY(),0.5*Main.getTilesWidth(),0.5*Main.getTilesHeight()); //Clear where image is drawn
        increaseX(moveX); //Calculate new position
        increaseY(moveY);
        getGc().drawImage(getImg(),getX(),getY(),0.5*Main.getTilesWidth(),0.5*Main.getTilesHeight()); //Draw image in new location
    }
    /**
     * Passed where resource is to move to at end of "animation"
     * @param newX X position of new location
     * @param newY Y position of new location
     */
    public void newCoords(double newX, double newY){
        double totalDraws;
        totalDraws = 0.125*Main.getAchievedFrameRate();
        drawsInMovement = (int)totalDraws;
        moveX = (newX-getX())/drawsInMovement;
        moveY = (newY-getY())/drawsInMovement;
    }
    /**
     * Removes drawing of image. Required when instance of image is to be deleted
     */
    public void reqDelete(){
        getGc().clearRect(getX(),getY(),0.5*Main.getTilesWidth(),0.5*Main.getTilesHeight());
    }
    /**
     * Returns a copy of this resource
     * @return A copy of this resource
     */
    public GameObject clone() {
       return  new Resource(getGc(),getX(),getY(),resourceName,value);
    }
    /**
     * Sets the coordinates of resource
     * @param x X position of resource
     * @param y Y position of resource
     */
    public void setCoordinates(double x, double y){
        setX(x);
        setY(y);
    }
    public void setResourcePosition(int resourcePosition) {        this.resourcePosition = resourcePosition;    }
    public void incrementResourcePosition(){        resourcePosition++;    }
    public int getResourcePosition(){return resourcePosition;}
    public String getResourceName() {        return resourceName;    }
    public double getValue() {        return value;    }

    public int getDrawsInMovement() {
        return drawsInMovement;
    }
}