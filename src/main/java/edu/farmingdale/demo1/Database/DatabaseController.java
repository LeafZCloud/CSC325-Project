package edu.farmingdale.demo1.Database;

import com.google.gson.Gson;
import edu.farmingdale.demo1.simulation.GameTypes;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import java.util.Map;
import java.util.HashMap;

public class DatabaseController extends GameTypes
{

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    public static final MediaType JSON = MediaType.get("application/json");

    //Firestore rest url
    private static final String FIRESTORE_URL = "https://firestore.googleapis.com/v1/projects/classproject-1717/databases/(default)/documents/gameStates/";


    public static Map<String, Object> gameStateObject(GameState gameState) {
        Map<String, Object> gameStateMap = new HashMap<>();

        //This is the map that gets sent to the firestore for the instance
        gameStateMap.put("cooldowns", gameState.cooldowns);
        gameStateMap.put("feedPosts", gameState.feedPosts);
        gameStateMap.put("eventLog", gameState.eventLog);
        gameStateMap.put("globalStats", gameState.globalStats);
        gameStateMap.put("year", gameState.year);
        gameStateMap.put("flashingRegions", gameState.flashingRegions);
        gameStateMap.put("lastEventId", gameState.lastEventId);
        gameStateMap.put("regions", gameState.regions);
        gameStateMap.put("planet", gameState.planet);

        return gameStateMap;
    }

    //uses the users authentication token to return true or false if it works or not
    public boolean saveGameState(GameState gameState, String idToken) {
        Map<String, Object> data = gameStateObject(gameState);
        return false;
    }

}
