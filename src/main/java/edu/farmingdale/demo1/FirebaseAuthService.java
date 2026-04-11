package edu.farmingdale.demo1;

import com.google.auth.oauth2.IdToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.javafx.css.parser.Token;
import okhttp3.Request;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.awt.*;

public class FirebaseAuthService
{

    //Found the proper URL for the sign in and sign up in the Firebase documentations- https://firebase.google.com/docs/reference/rest/auth#section-fetch-providers-for-email
    //These are static and final because they will not change until the program is terminated
    //information is automatically encrypted using https

    //The sign in URL
    private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyBgR0ye0g-uEpIezZEbsVxgNBZmLmlOZ9k";

    //The signup URL
    private static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=AIzaSyBgR0ye0g-uEpIezZEbsVxgNBZmLmlOZ9k";

    //The request URL for the real time database
    private static final String REAL_TIME_DB_URL = "https://classproject-1717-default-rtdb.firebaseio.com/users/";

    //instantiation of the OkHttpClient
    private final OkHttpClient client = new OkHttpClient();

    //Instantiation fo the Gson field
    private final Gson gson = new Gson();

    //Media type is to tell the server what kind of data you are sending (application/json)
    public static final MediaType JSON = MediaType.get("application/json");

    private String SaveIdToken;
    private String SaveLocalIdToken;

    public void storeTokens(JsonObject json){
        this.SaveIdToken = json.get("idToken").getAsString();
        this.SaveLocalIdToken = json.get("idToken").getAsString();
    }

    //Sign In
    public boolean signUp(String email, String password, String username){

        String inputResponse =  signUpInputValidation(email, password, username);
        JsonObject body = new JsonObject();

        //creating the properties for sending the JSON data
        body.addProperty("email", email);
        body.addProperty("password", password);
        body.addProperty("returnSecureToken", true);

        //this is the request payload containing the properties above
        RequestBody requestBody = RequestBody.create(body.toString(),JSON);
        Request request = new Request.Builder().url(SIGN_UP_URL).post(requestBody).build();

        try(Response response = client.newCall(request).execute()) {
            //Converts the response body into the JsonObject by parsing it into a string using the .gson method
            JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

            if(json.has("idToken")) {
                //Variables to store the idToken and localId token from the response
                storeTokens(json);
                String idToken = json.get("idToken").getAsString();
                String localId = json.get("localId").getAsString();

                //JSON object for the realtime database request
                JsonObject rtDBObject = new JsonObject();
                rtDBObject.addProperty("username", username);

                //Request creating, using the RealTime URL and the rtDBObject in the request parameters
                RequestBody realTime = RequestBody.create(rtDBObject.toString(), JSON);
                //Dynamic URL input, for the request
                Request realTimeRequest = new Request.Builder().url(REAL_TIME_DB_URL + localId + ".json?auth=" + idToken).put(realTime).build();

                //Execute the request
                try (Response response2 = client.newCall(realTimeRequest).execute()) {

                    if (response2.isSuccessful()){
                        return true;
                    }

                } catch (Exception e) {
                    System.out.println("Something went wrong " + e.getMessage());
                }
            }

        } catch (Exception ex) {
            System.out.println("User already exists, error message: " + ex.getMessage());
        }
        return false;
    }

    //Login, POST request
    public boolean login(String email, String password){
        String inputResponse = loginInputValidation(email, password);

        if(inputResponse != null){
            return inputResponse.contains("Failure");
        }

        JsonObject body = new JsonObject();

        //adding properties to the JsonObject body, the returnSecureToken is always true
        body.addProperty("email", email); //email for the account
        body.addProperty("password", password); //password for the account
        body.addProperty("returnSecureToken", true);

        //This instantiates the RequestBody, with the parameters of the JSON body in string format and the type of data that you are sending it as (JSON)
        RequestBody requestBody = RequestBody.create(body.toString(),JSON);

        //This makes the request to the URL and gives it the already instantiated requestBody
        Request request = new Request.Builder().url(SIGN_IN_URL).post(requestBody).build();

        //This tries with resources and executes the request
        try(Response response = client.newCall(request).execute()) {
            //Converts the response body into the JsonObject by parsing it into a string using the .gson method
            JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

            //Checks to see if the json request contains an "idToken" in the json object
            if(json.has("idToken")) {
                storeTokens(json);
                return true;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    //These would check for if the input is improper or malicious
    public String signUpInputValidation(String email,  String password, String username){

        if (email == null || email.isBlank())
            return "Email cannot be empty.";
        if (password == null || password.isBlank())
            return "Password cannot be empty.";
        if (username == null || username.isBlank())
            return "Username cannot be empty.";
        emailCheck(email);

        if(password.length() < 6){
            return "This password must be over 6 characters";
        }

        if(password.length() > 120){
            return "This password cannot be over 20 characters";
        }

        if (email.contains(" "))
            return "Email cannot contain spaces.";
        if (password.contains(" "))
            return "Password cannot contain spaces.";
        if (username.contains(" "))
            return "Username cannot contain spaces.";


        return null;
    }

    public String loginInputValidation(String email, String password)
    {
        if (email == null || email.isBlank())
            return "Email cannot be empty.";
        if (password == null || password.isBlank())
            return "Password cannot be empty.";
        emailCheck(email);

        if(password.length() < 6){
            return "This password must be over 6 characters";
        }
        if(password.length() > 120){
            return "This password cannot be over 20 characters";
        }
        if (email.contains(" "))
            return "Email cannot contain spaces.";
        if (password.contains(" "))
            return "Password cannot contain spaces.";

        return null;
    }

    public String emailCheck(String email)
    {
        if(!email.contains("@") || !email.contains(".")){
            return "Invalid email, characters like @  and . are needed";
        }
        return null;
    }
}
