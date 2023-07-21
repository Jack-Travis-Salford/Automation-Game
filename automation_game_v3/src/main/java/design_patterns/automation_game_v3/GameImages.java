package design_patterns.automation_game_v3;

import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Objects;

/**
 * A class that adds all images to memory.
 * When an image is required, this class is called to get a copy of said image
 */
public final class GameImages {
    private static HashMap<String,Image>  imageHolder;
    private static boolean created=false;
    /**
     * Add all images that game uses into hashmap
     * @throws FileNotFoundException Game images may not be found. If this is the case, the game will not work
     */
    public static void instantiateUtility() throws FileNotFoundException {
        if(!created){
            new GameImages();
        }
    }
    /**
     * Constructor for GameImages. Holds every game image in ram to all for quick access
     * Private as only one should ever be created. Use instantiateUnity() to create utility
     * @throws FileNotFoundException Gets game images. If they are not found, the game cannot function
     */
    private GameImages() throws FileNotFoundException {
        created=true;
        imageHolder = new HashMap<>();
        //Image location for JAR files
        //Background
        imageHolder.put("background", new Image(Objects.requireNonNull(GameImages.class.getResource("BG.png")).toExternalForm()));
        //Conveyor pieces
        imageHolder.put("conveyor0-1", new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor0-1.png")).toExternalForm()));
        imageHolder.put("conveyor0-2", new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor0-2.png")).toExternalForm()));
        imageHolder.put("conveyor0-3", new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor0-3.png")).toExternalForm()));
        imageHolder.put("conveyor1-0",new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor1-0.png")).toExternalForm()));
        imageHolder.put("conveyor1-2",new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor1-2.png")).toExternalForm()));
        imageHolder.put("conveyor1-3",new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor1-3.png")).toExternalForm()));
        imageHolder.put("conveyor2-0", new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor2-0.png")).toExternalForm()));
        imageHolder.put("conveyor2-1", new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor2-1.png")).toExternalForm()));
        imageHolder.put("conveyor2-3", new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor2-3.png")).toExternalForm()));
        imageHolder.put("conveyor3-0", new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor3-0.png")).toExternalForm()));
        imageHolder.put("conveyor3-1", new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor3-1.png")).toExternalForm()));
        imageHolder.put("conveyor3-2", new Image(Objects.requireNonNull(GameImages.class.getResource("conveyor3-2.png")).toExternalForm()));
        //Map Resources
        imageHolder.put("iron", new Image(Objects.requireNonNull(GameImages.class.getResource("iron.png")).toExternalForm()));
        imageHolder.put("copper", new Image(Objects.requireNonNull(GameImages.class.getResource("copper.png")).toExternalForm()));
        imageHolder.put("tree", new Image(Objects.requireNonNull(GameImages.class.getResource("tree.png")).toExternalForm()));
        //Buildings
        imageHolder.put("tree_miner", new Image(Objects.requireNonNull(GameImages.class.getResource("tree_miner.png")).toExternalForm()));
        imageHolder.put("iron_miner", new Image(Objects.requireNonNull(GameImages.class.getResource("iron_miner.png")).toExternalForm()));
        imageHolder.put("copper_miner", new Image(Objects.requireNonNull(GameImages.class.getResource("copper_miner.png")).toExternalForm()));
        imageHolder.put("sell_point", new Image(Objects.requireNonNull(GameImages.class.getResource("sell_point.png")).toExternalForm()));
        imageHolder.put("splurger", new Image(Objects.requireNonNull(GameImages.class.getResource("splurger.png")).toExternalForm()));
        imageHolder.put("builder", new Image(Objects.requireNonNull(GameImages.class.getResource("builder.png")).toExternalForm()));
        //Resources
        imageHolder.put("iron_ore",new Image(Objects.requireNonNull(GameImages.class.getResource("iron_ore.png")).toExternalForm()));
        imageHolder.put("copper_ore", new Image(Objects.requireNonNull(GameImages.class.getResource("copper_ore.png")).toExternalForm()));
        imageHolder.put("tree_ore", new Image(Objects.requireNonNull(GameImages.class.getResource("tree_ore.png")).toExternalForm()));
        imageHolder.put("copper_ingot", new Image(Objects.requireNonNull(GameImages.class.getResource("copper_ingot.png")).toExternalForm()));
        imageHolder.put("iron_ingot", new Image(Objects.requireNonNull(GameImages.class.getResource("iron_ingot.png")).toExternalForm()));
        imageHolder.put("iron_plate", new Image(Objects.requireNonNull(GameImages.class.getResource("iron_plate.png")).toExternalForm()));
        imageHolder.put("plank", new Image(Objects.requireNonNull(GameImages.class.getResource("plank.png")).toExternalForm()));
        imageHolder.put("reinforced_armor", new Image(Objects.requireNonNull(GameImages.class.getResource("reinforced_armor.png")).toExternalForm()));
        imageHolder.put("reinforced_iron_plate", new Image(Objects.requireNonNull(GameImages.class.getResource("reinforced_iron_plate.png")).toExternalForm()));
        imageHolder.put("screw", new Image(Objects.requireNonNull(GameImages.class.getResource("screw.png")).toExternalForm()));
        imageHolder.put("strengthened_sword", new Image(Objects.requireNonNull(GameImages.class.getResource("strengthened_sword.png")).toExternalForm()));
        imageHolder.put("sword", new Image(Objects.requireNonNull(GameImages.class.getResource("sword.png")).toExternalForm()));
        imageHolder.put("tool", new Image(Objects.requireNonNull(GameImages.class.getResource("tool.png")).toExternalForm()));
        imageHolder.put("wire", new Image(Objects.requireNonNull(GameImages.class.getResource("wire.png")).toExternalForm()));
        imageHolder.put("armor", new Image(Objects.requireNonNull(GameImages.class.getResource("armor.png")).toExternalForm()));
        //Other
        imageHolder.put("icon", new Image(Objects.requireNonNull(GameImages.class.getResource("icon.png")).toExternalForm()));
        imageHolder.put("bin", new Image(Objects.requireNonNull(GameImages.class.getResource("bin.png")).toExternalForm()));
        imageHolder.put("player_left", new Image(Objects.requireNonNull(GameImages.class.getResource("player_left.png")).toExternalForm()));
        imageHolder.put("player_right", new Image(Objects.requireNonNull(GameImages.class.getResource("player_right.png")).toExternalForm()));
        imageHolder.put("bottom_bar", new Image(Objects.requireNonNull(GameImages.class.getResource("bottom_bar.png")).toExternalForm()));
     
    }
    /**
     * returns image that matches passed string
     * @param reqImage Name of key where image should be
     * @return Requested image
     */
    public static Image clone(String reqImage){
        return imageHolder.getOrDefault(reqImage, null);
    }
}
