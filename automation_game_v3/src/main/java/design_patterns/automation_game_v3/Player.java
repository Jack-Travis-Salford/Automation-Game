package design_patterns.automation_game_v3;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.awt.GraphicsEnvironment;

/**
 * Create the player. Only one player is allowed
 */
public class Player {
    private static Player INSTANCE; //Hold the only instance of Player
    private static boolean up; //True is key relating to upward movement is pressed
    private static boolean down; //True is key relating to downward movement is pressed
    private static boolean left;//True is key relating to left movement is pressed
    private static boolean right; //True is key relating to right movement is pressed
    private static double playerMovementSpeed;
    private static int lastXMovement; //1 if user last moved right, -1 if user last moved left
    private static ImageView player; //ImageView of player character
    private static Main main; //Pointer to Main
    /**
     * Creates and returns the only instance Player
     * @param main The Main class
     * @return this
     */
    public static  Player getInstance(Main main){
        if (INSTANCE == null){
            INSTANCE = new Player(main);
        }
        return INSTANCE;
    }

    /**
     * Constructor for Player
     * Private as only one Player should every exist. Use getInstance to create/get instance
     * @param main The Main class
     */
    private Player(Main main){
        int refreshRate = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode().getRefreshRate();
        if (refreshRate==0){
            playerMovementSpeed = 8;
        }else{
            playerMovementSpeed = 480.0/refreshRate;
        }
        player = new ImageView(GameImages.clone("player_right")); //Set player image
        player.setPreserveRatio(true); //Ensure aspect ratio is maintained
        player.setFitHeight(Main.getTilesHeight()); //Set Height of player to be equal to that of each tile
        up = false; //Set all movement variables to false (player is nor moving
        down = false;
        left = false;
        right = false;
        Player.main = main; //Set main
    }

    /**
     * Move player in the direction the user want
     * @param pane Pane where the player on
     */
    public static void move(Pane pane){
        double deltaX = 0, deltaY =0; //Movement speed by axis
        //How fast the user moves (pixels/frame)
        if (right) deltaX += playerMovementSpeed; //Boolean value for direction is true (key is pressed)
        if (left) deltaX -= playerMovementSpeed;  //Increase/Decrease movement speed in said direction by
        if (down) deltaY += playerMovementSpeed;  //playerMovementSpeed
        if (up) deltaY -= playerMovementSpeed;
        if (deltaX > 0 && lastXMovement != 1){ //If player is moving right and was moving left or was stationary last frame
            lastXMovement = 1; //Indicate player is moving right
            player.setImage(GameImages.clone("player_right")); //Draw correct image of player
        }else if (deltaX<0 && lastXMovement!=-1){ //If player is moving left and was moving right or was stationary last frame
            lastXMovement = -1; //Indicate player is moving left
            player.setImage(GameImages.clone("player_left")); //Draw correct image of player
        }
        double newX = player.getX()+deltaX; //Gets value of where the player will be moved to
        double newY = player.getY()+deltaY;
        if(newX <= (pane.getWidth() -player.getFitWidth()-main.getXBcOffset())&& newX>main.getXBcOffset()){ //If players new position (x-axis) is less than the pane width - the width of the player ,and  the new position (x-axis) is greater than 0, then...
            player.setX(newX); //Move player to new co-ordinates
        } else if(newX <= main.getXBcOffset()){ //Else, if new player position is a negative x value
            player.setX(pane.getWidth()-player.getFitWidth()-main.getXBcOffset()); //Move player to 0 on x-axis
        } else if(newX>(pane.getWidth() -player.getFitWidth()-main.getXBcOffset())) {
            //Otherwise
            player.setX(main.getXBcOffset()); //Move player to furthest x position where player is still on Pane
        }
        if(newY <= (pane.getHeight()-player.getFitHeight()-main.getYBottomBcOffset()) && newY>main.getYTopBcOffset()){ //If players new position (y-axis) is less than the pane width - the height of the player ,and  the new position (y-axis) is greater than 0, then...
            player.setY(newY); //Move player to new co-ordinates
        } else if (newY <= main.getYTopBcOffset()) { //Else, if new player position is a negative y value
            player.setY(pane.getHeight()-player.getFitHeight()-main.getYBottomBcOffset()); //Move player to 0 on y-axis
        } else if(newY>(pane.getHeight()-player.getFitHeight()-main.getYBottomBcOffset())) { //Otherwise
            player.setY(main.getYTopBcOffset()); //Move player to furthest y position where player is still on Pane
        }
    }
    public ImageView getPlayer() {
        return player;
    }
    public boolean getDown() {
        return down;
    }
    public boolean getLeft() {
        return left;
    }
    public boolean getRight() {
        return right;
    }
    public boolean getUp() {
        return up;
    }
    public void setDown(Boolean down) {
        Player.down = down;
    }
    public void setLeft(Boolean left) {
        Player.left = left;
    }
    public void setRight(Boolean right) {
        Player.right = right;
    }
    public void setUp(Boolean up) {
        Player.up = up;
    }

    public static void setPlayerMovementSpeed(double playerMovementSpeed) {
        Player.playerMovementSpeed = 480.0/playerMovementSpeed;
    }
}