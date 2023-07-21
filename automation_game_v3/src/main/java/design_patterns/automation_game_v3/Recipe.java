package design_patterns.automation_game_v3;

import java.util.HashMap;
public class Recipe {
    /**
     * Game recipe. Denotes what is needed to create a new product, and what said output is.
     * Used by constructors
     */
    private final HashMap<String, RecipeInput> inputResources; //The details of the inputs. Resource class not used, as there is no overlap (other than name) between a Resource, and an inputResource
    private final int totalInputs; //Amount of different resource types required as inputs
    private final int craftTime; //Time it takes to craft output in ms
    private final int outputsProduced; //Amount of outputs produced per each crafting cycle
    private final GameObject output; //Generic version of created output
    /**
     * Constructor of recipe. Create a recipe that can be used in the game
     * @param inputResources All input resources of recipe
     * @param inputResourceNeeded Amount of each resource needed (position in array relates to corresponding inputResources)
     * @param totalInputs Total amount of different resource types requires as inputs
     * @param outputResource //The name of the output resource
     * @param outputsProduced //Amount of output resource produced each crafting cycle
     * @param craftTime //Time it takes to craft resource in ms
     */
    public Recipe(String[] inputResources, int[] inputResourceNeeded, int totalInputs, String outputResource, int outputsProduced, int craftTime ){
        this.inputResources = new HashMap<>();
        this.totalInputs = totalInputs;
        this.outputsProduced = outputsProduced;
        this.craftTime = craftTime;
        output = GameResources.getCopy(outputResource); //Get a  generic copy of created resource
        for(int x=0; x<totalInputs;x++){ //For every input
            String title = "input" + (x+1); //Create a string with value input[x]
            RecipeInput ri = new RecipeInput(inputResources[x], inputResourceNeeded[x]); //Create a RecipeInput, with corresponding resource name and quantity needed
            this.inputResources.put(title,ri); //Add input to hashmap
        }
    }
    public HashMap<String, RecipeInput> getInputResources() {        return inputResources;    }
    public int getTotalInputs() {        return totalInputs;    }
    public int getCraftTime() {        return craftTime;    }
    public int getOutputsProduced() {        return outputsProduced;    }
    public GameObject getOutput() {        return output;    }
}