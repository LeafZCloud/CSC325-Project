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
    public boolean saveGameState(GameState gameState, String userId, String idToken)
    {

        String gameStateJson = gson.toJson(gameState);
        JsonObject stringValue = new JsonObject();
        stringValue.addProperty("stringValue", gameStateJson);

        //This JSON object will hold the entire game state in one place
        JsonObject fields = new JsonObject();
        fields.add("data", stringValue);

        //This is a necessary wrapper JSON object because that is how firestore requires the JSON to be formated
        JsonObject body = new JsonObject();
        body.add("fields", fields);

        //Creation of the actual request being made
        RequestBody requestBody = RequestBody.create(body.toString(), JSON);
        Request request = new Request.Builder()
                .url(FIRESTORE_URL + userId)
                .patch(requestBody)
                .addHeader("Authorization", "Bearer " + idToken)  //Bearer is the authorization header in a request like this
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Game state saved for user: " + userId);
                return true;
            } else {
                System.out.println("Save failed: " + response.code()
                        + " Body: " + response.body().string());
                return false;
            }
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
            return false;
        }
    }

    //Loading the game state from firestore, creating the request using the active user IdToken
    public GameState loadGameState(String userId, String idToken)
    {

        //Firestore authorization header for verification of the user
        Request request = new Request.Builder()
                .url(FIRESTORE_URL + userId)
                .get()
                .addHeader("Authorization", "Bearer " + idToken)
                .build();

        //Try with resources for the database request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("No game state in database found: " + response.code());
                return null;
            }

            //If the request passes then the game state JSON object is sent here and then converted using GSON
            JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
            String gameStateJson = json
                    .getAsJsonObject("fields")
                    .getAsJsonObject("data")
                    .get("stringValue")
                    .getAsString();

            //Returns the GSON from the database so that we can use it in java again
            return gson.fromJson(gameStateJson, GameState.class);

        } catch (Exception e) {
            System.out.println("Load error: " + e.getMessage());
            return null;
        }
    }
}