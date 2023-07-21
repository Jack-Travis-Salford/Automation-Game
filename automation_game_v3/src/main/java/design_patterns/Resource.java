package design_patterns;

import javafx.scene.canvas.GraphicsContext;
public class Resource extends GameObject{
    protected String resourceName;
    protected int resourcePosition; //How many times the resource has been moved when travelling between placeable
    protected double moveX, moveY;
    public Resource(GraphicsContext gc, double x, double y, String resourceName) {
        super(gc, x, y);
        this.img = img;
        this.resourceName =resourceName;
        img = GameImages.clone(resourceName);
        resourcePosition =0;
    }
    /**
     * Moves resource by xChange and yChange pixels
     */
    public void update() {
        gc.clearRect(x,y,0.5*Main.tilesWidth,0.5*Main.tilesHeight);
        x+=moveX;
        y+=moveY;
        gc.drawImage(img,x,y,0.5*Main.tilesWidth,0.5*Main.tilesHeight);


    }
    public void newCoords(double newX, double newY){
        moveX = (newX-x)/12;
        moveY = (newY-y)/12;
    }
    public void reqDelete(){
        gc.clearRect(x,y,0.5*Main.tilesWidth,0.5*Main.tilesHeight);
    }

    public GameObject clone() {
        return  new Resource(gc, x,y,resourceName);
    }

    public void setResourcePosition(int resourcePosition) {
        this.resourcePosition = resourcePosition;
    }
    public void incrementResourcePosition(){
        resourcePosition++;
    }
    public int getResourcePosition(){return resourcePosition;}
}
