package design_patterns.automation_game_v3;
public class BuilderInput {
    /**
     * Handles inputs for builders. Tracks how many of each resource is in buffer
     */
    private int inBuffer; //Amount of resource stored in buffer
    private final int noRequired; //Amount required per craft cycle
    /**
     * Constructor for BuilderInput
     * @param noRequired Amount of resource required per craft cycle
     */
    public BuilderInput(int noRequired){
        this.noRequired = noRequired;
        inBuffer = 0;
    }
    public void incrementBuffer(){
        inBuffer++;
    }
    public int getInBuffer(){
        return inBuffer;
    }
    public void removeFromBuffer(){
        inBuffer -= noRequired;
    }
    public boolean enoughResources(){
        return inBuffer >= noRequired;
    }
}
