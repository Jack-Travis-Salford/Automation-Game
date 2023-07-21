package design_patterns.automation_game_v3;

import java.util.HashMap;

public final class GameLanguageConverter {
    /**Changes words used in code to their display equivalent.
      *Eg. In code: "reinforced_iron_plate" Equivalent for user: "Reinforced iron plate"
      */
    private static HashMap<String, String> dictionary; //Holds key-value pair game word to display word, and display word to game word
    private static boolean created=false; //Indicates if utility has already been created
    /**
     * Creates instance of GameLanguageConverter, if one isn't already present
     */
    public static void instantiateUtility(){
        if (!created){
            new GameLanguageConverter();
        }
    }
    /**
     * Constructor for GameLanguageConverter
     * Private as only one instance of this utility should ever exist. Use instantiateUnity to create instance.
     */
    private GameLanguageConverter(){
        created=true;
        dictionary = new HashMap<>();
        dictionary.put("tree_ore", "Log");
        dictionary.put("Log", "tree_ore");
        dictionary.put("iron_ore", "Iron ore");
        dictionary.put("Iron ore", "iron_ore");
        dictionary.put("copper_ore","Copper ore");
        dictionary.put("Copper ore","copper_ore");
        dictionary.put("plank", "Plank");
        dictionary.put("Plank","plank");
        dictionary.put("iron_ingot","Iron ingot");
        dictionary.put("Iron ingot", "iron_ingot");
        dictionary.put("copper_ingot","Copper ingot");
        dictionary.put("Copper ingot", "copper_ingot");
        dictionary.put("tool","Tool");
        dictionary.put("Tool","tool");
        dictionary.put("iron_plate", "Iron plate");
        dictionary.put("Iron plate", "iron_plate");
        dictionary.put("screw", "Screw");
        dictionary.put("Screw", "screw");
        dictionary.put("wire", "Wire");
        dictionary.put("Wire","wire");
        dictionary.put("armor", "Armor");
        dictionary.put("Armor","armor");
        dictionary.put("sword","Sword");
        dictionary.put("Sword","sword");
        dictionary.put("reinforced_iron_plate","Reinforced iron plate");
        dictionary.put("Reinforced iron plate","reinforced_iron_plate");
        dictionary.put("strengthened_sword","Strengthened Sword");
        dictionary.put("Strengthened Sword","strengthened_sword");
        dictionary.put("reinforced_armor","Reinforced armor");
        dictionary.put("Reinforced armor","reinforced_armor");
    }
    public static String getEquivalentWord(String word){
        return dictionary.getOrDefault(word, "");
    }
}