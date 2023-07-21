package design_patterns.automation_game_v3;

import javafx.scene.canvas.GraphicsContext;
import java.util.HashMap;

public final class GameResources {
    /**
     * Holds all of game resources. These can be cloned for use in the game
     * Note: The Graphics context, name and value are set, as these are constants to said resource.
     * Other info, such as x and y position, must still be set by the cloner.
     */
    private static HashMap<String,GameObject> resourceMap; //Holds a copy of every in game resource
    private static boolean isCreated = false; //Indicates if utility has been instantiated yet
    /**
     * Instantiated utility, if it hasn't been already instantiated
     * @param gc Graphics context that resources are to be drawn on
     */
    public static void instantiateUtility(GraphicsContext gc){
        if (!isCreated){
            new GameResources(gc);
        }
    }
    /**
     * Constructor for GameResources
     * @param gc Graphics context that resources are to be drawn on
     */
    private GameResources(GraphicsContext gc){
        isCreated=true;
        resourceMap = new HashMap<>();
        //Create and add all game resource to hashmap
        Resource resource;
        resource = new Resource(gc,"tree_ore",0);
        resourceMap.put("tree_ore",resource);
        resource = new Resource(gc, "iron_ore" ,0);
        resourceMap.put("iron_ore", resource);
        resource = new Resource(gc, "copper_ore", 0);
        resourceMap.put("copper_ore", resource);
        resource = new Resource(gc,"plank",0.75);
        resourceMap.put("plank", resource);
        resource = new Resource(gc,"iron_ingot",1);
        resourceMap.put("iron_ingot",resource);
        resource = new Resource(gc, "copper_ingot", 1);
        resourceMap.put("copper_ingot", resource);
        resource = new Resource(gc,"tool", 6);
        resourceMap.put("tool", resource);
        resource = new Resource(gc, "iron_plate",3.75);
        resourceMap.put("iron_plate",resource);
        resource = new Resource(gc, "screw", 0.5);
        resourceMap.put("screw",resource);
        resource = new Resource(gc,"wire",  0.75);
        resourceMap.put("wire",resource);
        resource = new Resource(gc,"armor", 62);
        resourceMap.put("armor", resource);
        resource = new Resource(gc, "sword", 11);
        resourceMap.put("sword",resource);
        resource = new Resource(gc, "reinforced_iron_plate", 21);
        resourceMap.put("reinforced_iron_plate",resource);
        resource = new Resource(gc,"strengthened_sword", 81.25);
        resourceMap.put("strengthened_sword",resource);
        resource = new Resource(gc, "reinforced_armor", 130);
        resourceMap.put("reinforced_armor",resource);
    }
    /**
     * Creates a pointer to resource
     * @param resourceName The key of the resource requested (resource name)
     * @return The corresponding resource, or null if it doesn't exist
     */
    public static GameObject getCopy(String resourceName){
        return resourceMap.getOrDefault(resourceName, null);
    }
}