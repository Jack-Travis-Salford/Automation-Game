package design_patterns.automation_game_v3;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class GridSegment extends Rectangle {
    private String resourceType; //Type of resource on map
    private final Main main; //Points to main
    private GameObject gameObject; //Holds building
    private final int xPosInGrid;
    private final int yPosInGrid; //Where in grid array this grid segment is located
    /**
     * Main constructor for Grid Segment
     * @param main Pointer to the Main class
     * @param xPos Position on scene that the top left pixel of this GridSegment is located
     * @param yPos Position on scene that the top left pixel of this GridSegment is located
     * @param width Width of tile on grid
     * @param height Height of tile on grid
     * @param xPosInGrid  Reference to what column this GridSegment is in the grid
     * @param yPosInGrid Reference to what row this GridSegment is in the grid
     */
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
            if (main.isPlaceableSelected()){ //If a placeable is selected
                if (gameObject == null){ //If the tile doesn't currently have a game object
                   if (main.getReqAdd() == null) {
                       if (resourceType.equals("plain")) { //If tile is normal (no assigned resource)
                           String uID = xPosInGrid + "_" + yPosInGrid; //Create unique id for placeable
                           switch (main.getChosenPlaceable().getSelectedPlaceable()) { //Get what placeable is chosen
                               case "conveyor":
                                   if (main.getTotalConveyors() < 10){ //If less than 10 conveyors have been placed (first 10 are free)
                                       gameObject = new Conveyor(main.getGcLayer1(), getX(), getY(), this, main.getChosenPlaceable().getSelectedRotation(), uID); //Send Graphics Context, position, rotation of conveyor and image
                                       main.reqAdd(this); //Request cpu time for purposes, such as drawing
                                       main.changeTotalConveyors(1); //Increment total conveyor count
                                   } else if(Main.getBalance() >= 25){ //Else if user can afford placeable
                                       gameObject = new Conveyor(main.getGcLayer1(), getX(), getY(), this, main.getChosenPlaceable().getSelectedRotation(), uID); //Send Graphics Context, position, rotation of conveyor and image
                                       main.reqAdd(this); //Request cpu time for purposes, such as drawing
                                       Main.changeBalance(-25); //Charge user
                                       main.changeTotalConveyors(1); //Increment total conveyor count
                                   }
                                   break;
                               case "sell_point":
                                   if (main.getTotalSellPoints() < 1){//If less than 1 sell points have been placed (first 1 is free)
                                       gameObject = new SellPoint(main.getGcLayer3(), getX(), getY(), this, uID);
                                       main.reqAdd(this); //Request cpu time for purposes, such as drawing
                                       main.changeTotalSellPoints(1); //Increment total sell points count

                                   } else if (Main.getBalance() >= 75) { //Else if user can afford placeable
                                       gameObject = new SellPoint(main.getGcLayer3(), getX(), getY(), this, uID);
                                       main.reqAdd(this); //Request cpu time for purposes, such as drawing
                                       Main.changeBalance(-75); //Charge user
                                       main.changeTotalSellPoints(1); //Increment total sell points count
                                   }
                                   break;
                               case "splurger":
                                   if (Main.getBalance() >=100) { //Else if user can afford placeable
                                       gameObject = new Splurger(main.getGcLayer3(), getX(), getY(), this, uID);
                                       main.reqAdd(this); //Request cpu time for purposes, such as drawing
                                       Main.changeBalance(-100); //Charge user
                                   }
                                   break;
                               case "builder":
                                   if (main.getTotalBuilders() < 2){ //If less than 2 builders have been placed (first 2 are free)
                                       gameObject = new Builder(main.getGcLayer3(), getX(),getY(),this, uID);
                                       main.reqAdd(this); //Request cpu time for purposes, such as drawing
                                       main.changeTotalBuilders(1);//Increment total builders count
                                   }
                                   else if (Main.getBalance()>=250){ //Else if user can afford placeable
                                       gameObject = new Builder(main.getGcLayer3(), getX(),getY(),this, uID);
                                       main.reqAdd(this); //Request cpu time for purposes, such as drawing
                                       Main.changeBalance(-250); //Charge user
                                       main.changeTotalBuilders(1);//Increment total builders count
                                   }
                                   break;
                               default:
                                   break;
                           }
                       } else if (main.getChosenPlaceable().getSelectedPlaceable().equals("miner")) { //If selected tile is a resource and user has chosen a miner
                           if (main.getTotalMiners() < 1){ //If first miner hasn't been placed (first is free)
                               String uID = xPosInGrid + "_" + yPosInGrid; //Create unique id for placeable
                               gameObject = new Miner(main.getGcLayer3(), getX(), getY(), this, resourceType, uID);
                               main.reqAdd(this); //Request cpu time for purposes, such as drawing
                               main.changeTotalMiners(1);//Increment total miners count
                           }
                           else if (Main.getBalance()>=150){ //Else if user can afford placeable
                               String uID = xPosInGrid + "_" + yPosInGrid; //Create unique id for placeable
                               gameObject = new Miner(main.getGcLayer3(), getX(), getY(), this, resourceType, uID);
                               main.reqAdd(this); //Request cpu time for purposes, such as drawing
                               Main.changeBalance(-150); //Charge user
                               main.changeTotalMiners(1); //Increment total miners count
                           }
                       }
                   }
                } else if (main.getChosenPlaceable().getSelectedPlaceable().equals("bin")) { //If tile does have a game object, and "bin" is currently selected
                    main.reqDelete(this);//Request deletion during the next frame
                }
            }else if(gameObject != null && ((Placeable)gameObject).getType().equals("builder")){ //If gameObject associated with this GridSegment is a builder
                main.builderWantsFocus(gameObject); //Request focus in builder UI during next frame
            } else if (main.isBuilderUIVisible()) { //Else if the builder UI is visible
                main.setReqRemBuilderUI(true); //Request removal of builder UI during the next frame
            }
        });
    }
    /**
     * Deletes placeable when cpu time is allocated by Main centralised controller
     */
    public void deletePlaceable(){
        switch (((Placeable)gameObject).getType()){ //Decrement relevant counter (if there is one)
            case "conveyor":
                main.changeTotalConveyors(-1);
                break;
            case "builder":
                main.changeTotalBuilders(-1);
                break;
            case "miner":
                main.changeTotalMiners(-1);
                break;
            case "sell_point":
                main.changeTotalSellPoints(-1);
                break;
            default:
                break;
        }
        GameObject temp = gameObject; //Creates pointer to gameObject
        gameObject=null; //Removes game object from grid segment, so anything this is connected doesn't try to reconnect
        main.getAllPlaceables().remove(((Placeable)temp).getUID()); //Remove reference from AllPlaceables
        ((Placeable)temp).reqDelete(); //Remove all references of game object, remove drawing of placeable, remove drawing of assigned resources
        gameObject = null; //Remove reference to gameObject
    }
    /**
     * Adds placeable when cpu time is allocated by Main centralised controller
     */
    public void addPlaceable(){
        main.getAllPlaceables().putIfAbsent(((Placeable)gameObject).getUID(), gameObject); //Add placeable to AllPlaceables (so performAction() will be called by the centralised every frame
        gameObject.update(); //Draw image
        switch (((Placeable)gameObject).getType()) {
            case "conveyor": //If conveyor
                checkForNext(); //Check for 1 next placeable
                checkForPrev(); //Check for 1 previous placeable
                break;
            case "sell_point": //If sell point
                checkForPrevConveyor(); //Get all previous conveyors
                break;
            case "miner": //If miner
                checkForNextConveyor(1); //Get upto 1 next conveyor
                break;
            case "splurger": //If splurger
                checkForNextConveyor(4); //Get all next conveyors
                checkForPrevConveyor(); //Get all prev conveyors
                break;
            case "builder": //If builder
                checkForNextConveyor(1); //Get upto 1 next conveyor
                checkForPrevConveyor(); //Get all prev conveyors
            default:
                break;
        }
    }
    /**
     * Checks for present of free conveyor around placeable
     */
    public void checkForNextConveyor(int maxConnections){
        int linksFound = 0;
        int x=0;
        while (linksFound<maxConnections && x<4){ //While an acceptable next isn't found, and all options aren't exhausted
            int[] neighbourPos = getNeighbour(x); //Get grid position of possible next
            if (neighbourPos !=null){ //If a  grid position was returned
                GridSegment possibleNext = main.getGrid()[neighbourPos[0]][neighbourPos[1]]; //Get GridSegment
                if (possibleNext.gameObject != null){ //If the grid segment has a set gameObject
                    if (((Placeable)possibleNext.gameObject).getType().equals("conveyor") && ((Placeable)possibleNext.gameObject).getPrev()[0] == null){ //If that gameObject is a conveyor
                        if (((Conveyor)possibleNext.gameObject).getRotation() != ((x+2)%4)){ //If the conveyor is facing any direction but inward
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
     * Checks in all valid positions for a previous applicable placeable
     */
    public void checkForPrev(){
        boolean linkFound = false;
        int x= 0;
        while (!linkFound && x<4){ //Do until acceptable prev found, or all options are exhausted
            if (x != ((Conveyor)gameObject).getRotation()){ //If that prev is the objects next, skip
                int[] neighbourPos = getNeighbour(x); //Get grid position of target
                if(neighbourPos!= null){ //If target exists
                    GridSegment possiblePrev = main.getGrid()[neighbourPos[0]][neighbourPos[1]]; //Get target
                    if (possiblePrev.gameObject != null){ //If tile has a placeable
                        if (((Placeable)possiblePrev.gameObject).getNext() != null){ //If tile allows next and doesn't already have a next
                            if (((Placeable)possiblePrev.gameObject).getNext()[0] == null) {
                                switch (((Placeable) possiblePrev.gameObject).getType()) {
                                    case "conveyor":
                                        if (((Conveyor) possiblePrev.gameObject).getRotation() == ((x + 2) % 4)) { //If tiles is facing the correct way
                                            ((Placeable) gameObject).setPrev(possiblePrev.gameObject); //Link tiles
                                            ((Placeable) possiblePrev.gameObject).setNext(this.gameObject);
                                            linkFound = true; //End loop
                                        }
                                        break;
                                    case "miner":
                                    case "splurger":
                                    case "builder":
                                        ((Placeable) gameObject).setPrev(possiblePrev.gameObject); //Link tiles
                                        ((Placeable) possiblePrev.gameObject).setNext(this.gameObject);
                                        linkFound = true; //End loop
                                        break;
                                    default:
                                        break;
                                }
                            }else if(((Placeable)possiblePrev.gameObject).getType().equals("splurger")){
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
     * Looks for possible prev conveyors
     */
    private void checkForPrevConveyor(){
        int rotation=0;
        while (rotation<4){ //While the max amount of acceptable prev isn't found, and all options aren't exhausted
            int[] neighbourPos = getNeighbour(rotation); //Get grid position of possible next
            if (neighbourPos != null){ //If a  grid position was returned
                GridSegment possiblePrev = main.getGrid()[neighbourPos[0]][neighbourPos[1]]; //Get GridSegment
                if (possiblePrev.gameObject != null){ //If the grid segment has a set gameObject
                    if (((Placeable)possiblePrev.gameObject).getType().equals("conveyor") && ((Placeable)possiblePrev.gameObject).getNext()[0] == null) { //If that gameObject is a conveyor
                        if(((Conveyor)possiblePrev.gameObject).getRotation() == ((rotation+2)%4)){ //If the conveyor is facing inward
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
     * Checks for next for conveyors
     */
    private void checkForNext()  {
        GridSegment nextGs;
        int[] neighbourPos = getNeighbour(((Conveyor)gameObject).getRotation()); //Gets "next" tile position in grid
        if(neighbourPos != null){
            nextGs = main.getGrid()[neighbourPos[0]][neighbourPos[1]]; //Uses returned data to get "next" tile
            if (nextGs.gameObject != null) {//If the next tile has a placeable
                switch (((Placeable)nextGs.gameObject).getType()){ //Do action based on what type of building is present in grid
                    case "conveyor": //If it's a conveyor
                        if (((Placeable) nextGs.gameObject).getPrev()[0] == null) {//If the next tiles previous hasn't yet been set
                            ((Placeable) gameObject).setNext(nextGs.gameObject);  //Set next
                            ((Placeable) nextGs.gameObject).setPrev(this.gameObject); //Sets previous for next
                        }
                        break;
                    case "sell_point": case "splurger": case "builder":
                        ((Placeable) gameObject).setNext(nextGs.gameObject);  //Set next
                        ((Placeable) nextGs.gameObject).setPrev(this.gameObject); //Sets previous for next
                    default:
                        break;
                }
            }
        }
    }
    /**
     * Uses the location of this grid, and the rotation of the object to calculate where next object is
     * If the next object would lead to an out-of-bounds position, null is returned instead
     * @param rotation Orientation of placeable
     * @return x and y position for wanted neighbour. Null if neighbour doesn't exist
     */
    private int[] getNeighbour(int rotation){
        int[] neighborPos = new int[2];
        switch (rotation){
            case 0: //No rotation: next is [+1,0]
                if (xPosInGrid+1< main.getGrid().length) {
                    neighborPos[0] = xPosInGrid+1;
                    neighborPos[1] = yPosInGrid;
                    return neighborPos;
                }
                break;
            case 1://Rotation down: next is [0,+1]
                if (yPosInGrid+1< main.getGrid()[0].length) {
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
    public int getXPosInGrid() {        return xPosInGrid;    }
    public int getYPosInGrid() {        return yPosInGrid;    }
}