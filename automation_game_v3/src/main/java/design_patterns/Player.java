package design_patterns;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
/**
 * Create the player. Only one player is allowed
 */
public class Player {
    private static Player INSTANCE;
    private static Boolean up =false,down =false,left=false,right=false; //True indicates that user is travelling in that direction.
    private static int playerMovementSpeed = 10; //How fast the user moves (pixels/frame)
    private static ImageView player;
    private Player(){
        player = new ImageView(GameImages.clone("player"));
        player.setFitWidth(0.75*Main.tilesWidth);
        player.setFitHeight(0.75*Main.tilesHeight);
    }
    public static  Player getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Player();
        }
        return INSTANCE;
    }
    public static void move(Pane pane){
        double deltaX = 0, deltaY =0; //Movement speed by axis
        if (right) deltaX += playerMovementSpeed ; //Boolean value for direction is true (key is pressed)
        if (left) deltaX -= playerMovementSpeed ;  //Increase/Decrease movement speed in said direction by
        if (down) deltaY += playerMovementSpeed ;  //playerMovementSpeed
        if (up) deltaY -= playerMovementSpeed ;
        double newX = player.getX()+deltaX; //Gets value of where the player will be moved to
        double newY = player.getY()+deltaY;
        if(newX <= (pane.getWidth() -player.getImage().getWidth())&& newX>0){ //If players new position (x-axis) is less than the pane width - the width of the player ,and  the new position (x-axis) is greater than 0, then...
            player.setX(newX); //Move player to new co-ordinates
        } else if(newX < 1){ //Else, if new player position is a negative x value
            player.setX(0); //Move player to 0 on x-axis
        } else { //Otherwise
            player.setX(pane.getWidth()-player.getImage().getWidth()); //Move player to furthest x position where player is still on Pane
        }
        if(newY <= (pane.getHeight()-player.getImage().getHeight()) && newY>0){ //If players new position (y-axis) is less than the pane width - the height of the player ,and  the new position (y-axis) is greater than 0, then...
            player.setY(newY); //Move player to new co-ordinates
        } else if (newY < 1) { //Else, if new player position is a negative y value
            player.setY(0); //Move player to 0 on y-axis
        } else { //Otherwise
            player.setY(pane.getHeight()-player.getImage().getHeight()); //Move player to furthest y position where player is still on Pane
        }
    }
    public ImageView getImg() {
        return player;
    }
    public ImageView getPlayer() {
        return player;
    }
    public Boolean getDown() {
        return down;
    }
    public Boolean getLeft() {
        return left;
    }
    public Boolean getRight() {
        return right;
    }
    public Boolean getUp() {
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
}
