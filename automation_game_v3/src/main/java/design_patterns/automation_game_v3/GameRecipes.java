package design_patterns.automation_game_v3;

import java.util.HashMap;

public final class GameRecipes {
    /**
     * Creates an instance of every recipe in the game, which is loaded into memory.
     * When an object requires a game recipe, a pointer is created to the already corresponding instance
     */
    private static HashMap<String, Recipe> recipes; //name of resource, associated recipe
    private String[] inputResources; //All resources required as inputs to the recipe
    private int[] inputResourceNeeded; //Amount of each resource required as inputs to the recipe
    private static boolean isCreated = false; //True if utility has been instantiated
    /**
     * Instantiates utility if it hasn't been instantiated already
     */
     public static void instantiateUtility(){
         if (!isCreated){
             new GameRecipes();
         }
     }
    /**
     * Constructor for GameRecipes
     * Private as only one GameRecipe should ever exist. Use instantiateUnity to create GameRecipe
     */
    private GameRecipes(){
        isCreated=true;
        recipes = new HashMap<>();
        //Format
        //For x amount of input resources
            //inputResource[x] = input resource name
            //inputResourceNeeded[x] = how many of corresponding input resource are required
        //Create recipe
        //Add recipe to recipes, with key of output resource name
        //Call reset()
        //Repeat for next recipe until all recipes are created
        reset();
        inputResources[0] = "tree_ore";
        inputResourceNeeded[0] = 1;
        Recipe  r = new Recipe(inputResources,inputResourceNeeded,1,"plank",1,500);
        recipes.put("plank", r);
        reset();
        inputResources[0] = "iron_ore";
        inputResourceNeeded[0] = 1;
        r = new Recipe(inputResources,inputResourceNeeded,1,"iron_ingot",1,750);
        recipes.put("iron_ingot",r);
        reset();
        inputResources[0] = "copper_ore";
        inputResourceNeeded[0] = 1;
        r = new Recipe(inputResources,inputResourceNeeded,1,"copper_ingot",1,750);
        recipes.put("copper_ingot",r);
        reset();
        inputResources[0] = "plank";
        inputResourceNeeded[0] = 1;
        inputResources[1] = "iron_ingot";
        inputResourceNeeded[1] = 4;
        r = new Recipe(inputResources,inputResourceNeeded,2,"tool",1,1500);
        recipes.put("tool",r);
        reset();
        inputResources[0] = "iron_ingot";
        inputResourceNeeded[0] = 3;
        r = new Recipe(inputResources,inputResourceNeeded,1,"iron_plate",1,750);
        recipes.put("iron_plate",r);
        reset();
        inputResources[0] = "iron_ingot";
        inputResourceNeeded[0] = 1;
        r = new Recipe(inputResources, inputResourceNeeded,1, "screw",4,1500);
        recipes.put("screw", r);
        reset();
        inputResources[0] = "copper_ingot";
        inputResourceNeeded[0] = 1;
        r = new Recipe(inputResources,inputResourceNeeded,1,"wire",2,750);
        recipes.put("wire",r);
        reset();
        inputResources[0] = "tool";
        inputResourceNeeded[0] = 2;
        inputResources[1] = "iron_plate";
        inputResourceNeeded[1] = 10;
        r = new Recipe(inputResources,inputResourceNeeded,2,"armor", 1, 3000);
        recipes.put("armor", r);
        reset();
        inputResources[0] = "plank";
        inputResourceNeeded[0] = 3;
        inputResources[1] = "copper_ingot";
        inputResourceNeeded[1] = 5;
        inputResources[2] = "screw";
        inputResourceNeeded[2] = 3;
        r = new Recipe(inputResources,inputResourceNeeded,3,"sword",1,2250);
        recipes.put("sword",r);
        reset();
        inputResources[0] = "iron_plate";
        inputResourceNeeded[0] = 2;
        inputResources[1] = "screw";
        inputResourceNeeded[1] = 12;
        inputResources[2] = "wire";
        inputResourceNeeded[2] = 4;
        r = new Recipe(inputResources,inputResourceNeeded,3,"reinforced_iron_plate" , 1, 3000);
        recipes.put("reinforced_iron_plate",r);
        reset();
        inputResources[0] = "tool";
        inputResourceNeeded[0] = 2;
        inputResources[1] = "sword";
        inputResourceNeeded[1] = 1;
        inputResources[2] = "reinforced_iron_plate";
        inputResourceNeeded[2] = 2;
        r = new Recipe(inputResources,inputResourceNeeded,3,"strengthened_sword", 1 , 4500);
        recipes.put("strengthened_sword",r);
        reset();
        inputResources[0] = "armor";
        inputResourceNeeded[0] = 1;
        inputResources[1] = "reinforced_iron_plate";
        inputResourceNeeded[1] =2;
        r = new Recipe(inputResources,inputResourceNeeded,2,"reinforced_armor",1,6000);
        recipes.put("reinforced_armor",r);
        inputResourceNeeded = null;
        inputResources=null;

    }
    /**
     * Clears contents of arrays
     */
    private void reset(){
        inputResources = new String[3];
        inputResourceNeeded = new int[3];
    }
    /**
     * Gets specified resource from HashMap
     * @param recipe Value relating to specified key, or null if it doesn't exist
     * @return recipe
     */
    public static Recipe get(String recipe){
           return recipes.getOrDefault(recipe, null);
    }
    /**
     * Return all keys in hashmap
     * @return All keys in hashmap
     */
    public static String[] getKeySet(){
        return recipes.keySet().toArray(new String[0]);
    }
}