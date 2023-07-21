package design_patterns.automation_game_v3;

public class RecipeInput {
    /**
     * Holds necessary details about a resource that is an input for a recipe
     */
    private final String resourceName; //The name of the resource
    private final int noRequired; //Amount of said resource required in 1 iteration of crafting
    /**
     * Constructor for Recipe Input
     * @param resourceName Name of resource
     * @param noRequired quantity of resource needed as inputs to related recipe
     */
    public RecipeInput(String resourceName, int noRequired){
        this.noRequired = noRequired;
        this.resourceName = resourceName;
    }
    public String getResourceName() {        return resourceName;    }
    public int getNoRequired() {        return noRequired;    }
}