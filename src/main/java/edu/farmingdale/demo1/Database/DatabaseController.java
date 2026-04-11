package edu.farmingdale.demo1.Database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.farmingdale.demo1.simulation.GameTypes;
import okhttp3.*;
import java.io.IOException;
import java.util.*;

public class DatabaseController extends GameTypes
{
    //Tool initialization
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    public static final MediaType JSON = MediaType.get("application/json");

    //Firestore rest url
    private static final String FIRESTORE_URL = "https://firestore.googleapis.com/v1/projects/classproject-1717/databases/(default)/documents/gameStates/";

    //This methods will save the users game state when the trigger of either Exiting the game, Closing the game
    //or using the save button
    public boolean saveGameState(GameState gameState, String userId, String idToken) {

        String gameStateJson = gson.toJson(gameState);
        JsonObject stringValue = new JsonObject();
        stringValue.addProperty("stringValue", gameStateJson);

        JsonObject fields = new JsonObject();
        fields.add("data", stringValue);

        JsonObject body = new JsonObject();
        body.add("fields", fields);

        RequestBody requestBody = RequestBody.create(body.toString(), JSON);
        Request request = new Request.Builder()
                .url(FIRESTORE_URL + userId)
                .patch(requestBody)
                .addHeader("Authorization", "Bearer " + idToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Game state saved for user: " + userId);
                return true;
            } else {
                System.out.println("Save failed. Code: " + response.code()
                        + " Body: " + response.body().string());
                return false;
            }
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
            return false;
        }
    }
}
