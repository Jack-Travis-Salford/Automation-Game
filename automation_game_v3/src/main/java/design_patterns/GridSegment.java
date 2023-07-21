package design_patterns;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class GridSegment extends Rectangle {
    protected String resourceType; //Type of resource on map
    protected int xPosInGrid, yPosInGrid; //Where in grid array this grid segment is located
    protected Main main; //Points to main
    protected GameObject gameObject; //Holds building
    public GridSegment(Main main,double xPos, double yPos, double width, double height, int xPosInGrid, int yPosInGrid){
        super(xPos, yPos, width, height); //Call to rectangle - Set position and size
        this.xPosInGrid = xPosInGrid; ///Set vars with remaining passed info
        this.yPosInGrid = yPosInGrid;
        this.main = main;
        resourceType = "plain";
        setFill(Color.TRANSPARENT); //Make rectangle invisible
        setOnMouseEntered((event -> { //Make rectangle change to colour blue when hovered over
            setFill(Color.BLUE);
            setFill(Color.rgb(228,50,50,0.5));
        }));
        setOnMouseExited(event -> { //Make rectangle change back to clear when no longer hovered over
            setFill(Color.TRANSPARENT);
        });
        setOnMouseClicked((e)->{
            if (main.isPlaceableSelected){ //If a placeable is selected
                if (gameObject == null){ //If the tile doesnt currently have a game object
                   if (main.reqAdd == null) {
                       if (resourceType.equals("plain")) { //If tile is normal (no assigned resource)
                           String uID = xPosInGrid + "_" + yPosInGrid; //Create unique id for placeable
                           switch (main.chosenPlaceable.selectedPlaceable) { //Get what placeable is chosen
                               case "conveyor":
                                   gameObject = new Conveyor(main.gcLayer1, getX(), getY(), this, main.chosenPlaceable.selectedRotation, uID); //Send Graphics Context, position, rotation of conveyor and image
                                   main.reqAdd=this;
                                   break;
                               case "sell_point":
                                   gameObject = new Sell_Point(main.gcLayer3, getX(), getY(), this, uID);
                                   main.reqAdd=this;
                                   break;
                               case "splurger":
                                   gameObject = new Splurger(main.gcLayer3, getX(),getY(),this, uID);
                                   main.reqAdd=this;
                                   break;
                               case "builder":
                                   gameObject = new Builder(main.gcLayer3, getX(),getY(),this, uID);
                                   main.reqAdd=this;
                                   break;
                               default:
                                   break;
                           }
                       } else if (main.chosenPlaceable.selectedPlaceable.equals("miner")) { //If selected tile is a resource and user has chosen a miner
                           String uID = xPosInGrid + "_" + yPosInGrid; //Create unique id for placeable
                           gameObject = new Miner(main.gcLayer3, getX(), getY(), this, resourceType, main.gcLayer2, uID);
                           main.reqAdd=this;
                       }
                   }
                } else if (main.chosenPlaceable.selectedPlaceable.equals("bin")) { //If tile does have a game object, and "bin" is currently selected
                    if (main.reqDelete == null) {
                        main.reqDelete = this;
                    }
                }
            }else if(gameObject != null && ((Placeable)gameObject).type.equals("builder")){
                //Open up dialog
            }
        });
    }

    public void deletePlaceable(){
        main.allPlaceables.remove(((Placeable)gameObject).uID);
        GameObject holder = gameObject;
        gameObject = null;
        ((Placeable) holder).reqDelete();
    }

    public void addPlaceable(){
        main.allPlaceables.putIfAbsent(((Placeable)gameObject).uID, gameObject);
        switch (((Placeable)gameObject).type) {
            case "conveyor":
                gameObject.update(); //Draw image
                checkForNext();
                checkForPrev();
                break;
            case "sell_point":
                gameObject.update();
                checkForPrevConveyor();
                break;
            case "miner":
                gameObject.update();
                checkForNextConveyor(1);
                break;
            case "splurger":
                gameObject.update();
                checkForNextConveyor(4);
                checkForPrevConveyor();
                break;
            case "builder":
                gameObject.update();
            default:
                break;
        }
    }
    /**
     * Looks for possible prev conveyors
     */
    public void checkForPrevConveyor(){
        int rotation=0;
        while (rotation<4){ //While the max amount of acceptable prev isnt found, and all options arent exhausted
            int[] neighbourPos = getNeighbour(rotation); //Get grid position of possible next
            if (neighbourPos != null){ //If a  grid position was returned
                GridSegment possiblePrev = main.grid[neighbourPos[0]][neighbourPos[1]]; //Get GridSegment
                if (possiblePrev.gameObject != null){ //If the grid segment has a set gameObject
                    if (((Placeable)possiblePrev.gameObject).type.equals("conveyor") && ((Placeable)possiblePrev.gameObject).next[0] == null) { //If that gameObject is a conveyor
                        if(((Conveyor)possiblePrev.gameObject).rotation == ((rotation+2)%4)){ //If the conveyor is facing inward
                            ((Placeable)gameObject).setPrev(possiblePrev.gameObject);//Create link
                            ((Placeable)possiblePrev.gameObject).setNext(this.gameObject);
                        }
                    }
                }
            }
            rotation++;
        }
    }
    /**
     * Checks for present of free conveyor around placeable
     */
    public void checkForNextConveyor(int maxConections){
        int linksFound = 0;
        int x=0;
        while (linksFound<maxConections && x<4){ //While a acceptable next isnt found, and all options arent exhausted
            int[] neighbourPos = getNeighbour(x); //Get grid position of possible next
            if (neighbourPos !=null){ //If a  grid position was returned
                GridSegment possibleNext = main.grid[neighbourPos[0]][neighbourPos[1]]; //Get GridSegment
                if (possibleNext.gameObject != null){ //If the grid segment has a set gameObject
                    if (((Placeable)possibleNext.gameObject).type.equals("conveyor") && ((Placeable)possibleNext.gameObject).prev[0] == null){ //If that gameObject is a conveyor
                        if (((Conveyor)possibleNext.gameObject).rotation != ((x+2)%4)){ //If the conveyor is facing any direction but inward
                            ((Placeable)gameObject).setNext(possibleNext.gameObject); //Create link
                            ((Placeable)possibleNext.gameObject).setPrev(this.gameObject);
                            linksFound++; //End loop
                        }
                    }
                }
            }
            x++; //Try next x value
        }
    }
    /**
     * Checks for next for conveyors
     */
    public void checkForNext()  {
        GridSegment nextGs = null;
        int[] neighbourPos = getNeighbour(((Conveyor)gameObject).rotation); //Gets "next" tile position in grid
        if(neighbourPos != null){
            nextGs = main.grid[neighbourPos[0]][neighbourPos[1]]; //Uses returned data to get "next" tile
            if (nextGs.gameObject != null) {//If the next tile has a placeable
                switch (((Placeable)nextGs.gameObject).type){ //Do action based on what type of building is present in grid
                    case "conveyor": //If its a conveyor
                        if (((Placeable) nextGs.gameObject).prev[0] == null) {//If the next tiles previous hasn't yet been set
                            ((Placeable) gameObject).setNext(nextGs.gameObject); ; //Set next
                            ((Placeable) nextGs.gameObject).setPrev(this.gameObject); //Sets previous for next
                        }
                        break;
                    case "sell_point": case "splurger":
                        ((Placeable) gameObject).setNext(nextGs.gameObject); ; //Set next
                        ((Placeable) nextGs.gameObject).setPrev(this.gameObject); //Sets previous for next
                    default:
                        break;
                }
            }
        }
    }

    public void checkForPrev(){
        boolean linkFound = false;
        int x= 0;
        while (!linkFound && x<4){ //Do until acceptable prev found, or all options are exhausted
            if (x != ((Conveyor)gameObject).rotation){ //If that prev is the objects next, skip
                int[] neighbourPos = getNeighbour(x); //Get grid position of target
                if(neighbourPos!= null){ //If target exists
                    GridSegment possiblePrev = main.grid[neighbourPos[0]][neighbourPos[1]]; //Get target
                    if (possiblePrev.gameObject != null){ //If tile has a placeable
                        if (((Placeable)possiblePrev.gameObject).next != null){ //If tile allows next and doesnt already have a next
                            if (((Placeable)possiblePrev.gameObject).next[0] == null) {
                                switch (((Placeable) possiblePrev.gameObject).type) {
                                    case "conveyor":
                                        if (((Conveyor) possiblePrev.gameObject).rotation == ((x + 2) % 4)) { //If tiles is facing the correct way
                                            ((Placeable) gameObject).setPrev(possiblePrev.gameObject); //Link tiles
                                            ((Placeable) possiblePrev.gameObject).setNext(this.gameObject);
                                            linkFound = true; //End loop
                                        }
                                        break;
                                    case "miner":
                                    case "splurger":
                                        ((Placeable) gameObject).setPrev(possiblePrev.gameObject); //Link tiles
                                        ((Placeable) possiblePrev.gameObject).setNext(this.gameObject);
                                        linkFound = true; //End loop
                                        break;
                                    default:
                                        break;
                                }
                            }else if(((Placeable)possiblePrev.gameObject).type.equals("splurger")){
                                ((Placeable) gameObject).setPrev(possiblePrev.gameObject); //Link tiles
                                ((Placeable) possiblePrev.gameObject).setNext(this.gameObject);
                                linkFound = true; //End loop
                            }
                        }
                    }
                }
            }
            x++; //Increment x to check next rotation, or end loop
        }
    }
    /**
     * Uses the location of this grid, and the rotation of the object to calculate where next object is
     * If the next object would lead to an out of bounds position, null is returned instead
     * @param rotation Orientation of placeable
     * @return x and y position for wanted neighbour. Null if neighbour doesnt exist
     */
    public int[] getNeighbour(int rotation){
        int[] neighborPos = new int[2];
        switch (rotation){
            case 0: //No rotation: next is [+1,0]
                if (xPosInGrid+1< main.grid.length) {
                    neighborPos[0] = xPosInGrid+1;
                    neighborPos[1] = yPosInGrid;
                    return neighborPos;
                }
                break;
            case 1://Rotation down: next is [0,+1]
                if (yPosInGrid+1< main.grid[0].length) {
                    neighborPos[0] = xPosInGrid;
                    neighborPos[1] = yPosInGrid+1;
                    return neighborPos;
                }
                break;
            case 2: //No rotation: next is [-1,0]
                if (xPosInGrid-1>=0) {
                    neighborPos[0] = xPosInGrid-1;
                    neighborPos[1] = yPosInGrid;
                    return neighborPos;
                }
                break;
            case 3://Rotation down: next is [0,-1]
                if (yPosInGrid-1>=0) {
                    neighborPos[0] = xPosInGrid;
                    neighborPos[1] = yPosInGrid-1;
                    return neighborPos;
                }
                break;
            default:
                break;
        }
        return null;
    }
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    public String getResourceType() {
        return resourceType;
    }
}
