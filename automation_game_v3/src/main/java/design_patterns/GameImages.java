package design_patterns;

import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
/**
 * A class that adds all images to memory.
 * When an image is required, this class is called to get a copy of said image
 */
public class GameImages {


    private static HashMap<String,Image>  imageHolder;
    /**
     * Add all images that game uses into hashmap
     * @throws FileNotFoundException
     */
    public GameImages() throws FileNotFoundException {

//        imageHolder.put("background", new Image(GameImages.class.getResource("BG.png").toExternalForm()));
//        imageHolder.put("conveyor0", new Image(GameImages.class.getResource("conveyor0.png").toExternalForm()));
//        imageHolder.put("conveyor1",new Image(GameImages.class.getResource("conveyor1.png").toExternalForm()));
//        imageHolder.put("conveyor2", new Image(GameImages.class.getResource("conveyor2.png").toExternalForm()));
//        imageHolder.put("conveyor3", new Image(GameImages.class.getResource("conveyor3.png").toExternalForm()));
//        imageHolder.put("icon", new Image(GameImages.class.getResource("icon.png").toExternalForm()));
//        imageHolder.put("iron", new Image(GameImages.class.getResource("iron.png").toExternalForm()));
//        imageHolder.put("iron_ore",new Image(GameImages.class.getResource("iron_ore.png").toExternalForm()));
//        imageHolder.put("player", new Image(GameImages.class.getResource("player.png").toExternalForm()));
//        imageHolder.put("rock", new Image(GameImages.class.getResource("rock.png").toExternalForm()));
//        imageHolder.put("tree", new Image(GameImages.class.getResource("tree.png").toExternalForm()));
//        imageHolder.put("bin", new Image(GameImages.class.getResource("bin.png").toExternalForm()));
//        imageHolder.put("miner", new Image(GameImages.class.getResource("miner.png").toExternalForm()));
        imageHolder = new HashMap<>();
        imageHolder.put("background", new Image(new FileInputStream("BG.png")));
        imageHolder.put("conveyor0-1", new Image(new FileInputStream("conveyor0-1.png")));
        imageHolder.put("conveyor0-2", new Image(new FileInputStream("conveyor0-2.png")));
        imageHolder.put("conveyor0-3", new Image(new FileInputStream("conveyor0-3.png")));
        imageHolder.put("conveyor1-0",new Image(new FileInputStream("conveyor1-0.png")));
        imageHolder.put("conveyor1-2",new Image(new FileInputStream("conveyor1-2.png")));
        imageHolder.put("conveyor1-3",new Image(new FileInputStream("conveyor1-3.png")));
        imageHolder.put("conveyor2-0", new Image(new FileInputStream("conveyor2-0.png")));
        imageHolder.put("conveyor2-1", new Image(new FileInputStream("conveyor2-1.png")));
        imageHolder.put("conveyor2-3", new Image(new FileInputStream("conveyor2-3.png")));
        imageHolder.put("conveyor3-0", new Image(new FileInputStream("conveyor3-0.png")));
        imageHolder.put("conveyor3-1", new Image(new FileInputStream("conveyor3-1.png")));
        imageHolder.put("conveyor3-2", new Image(new FileInputStream("conveyor3-2.png")));
        imageHolder.put("icon", new Image(new FileInputStream("icon.png")));
        imageHolder.put("iron", new Image(new FileInputStream("iron.png")));
        imageHolder.put("iron_ore",new Image(new FileInputStream("iron_ore.png")));
        imageHolder.put("player", new Image(new FileInputStream("player.png")));
        imageHolder.put("copper", new Image(new FileInputStream("copper.png")));
        imageHolder.put("copper_ore", new Image(new FileInputStream("copper_ore.png")));
        imageHolder.put("tree", new Image(new FileInputStream("tree.png")));
        imageHolder.put("tree_ore", new Image(new FileInputStream("tree_ore.png")));
        imageHolder.put("bin", new Image(new FileInputStream("bin.png")));
        imageHolder.put("tree_miner", new Image(new FileInputStream("tree_miner.png")));
        imageHolder.put("iron_miner", new Image(new FileInputStream("iron_miner.png")));
        imageHolder.put("copper_miner", new Image(new FileInputStream("copper_miner.png")));
        imageHolder.put("miner", new Image(new FileInputStream("miner.png")));
        imageHolder.put("sell_point", new Image(new FileInputStream("sell_point.png")));
        imageHolder.put("splurger", new Image(new FileInputStream("splurger.png")));
        imageHolder.put("builder", new Image(new FileInputStream("builder.png")));

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
