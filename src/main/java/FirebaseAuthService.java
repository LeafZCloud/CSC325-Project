import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;

public class FirebaseAuthService
{

    //Found the proper URL for the sign in and sign up in the Firebase documentations- https://firebase.google.com/docs/reference/rest/auth#section-fetch-providers-for-email
    //These are static and final because they will not change until the program is terminated
    //The sign in URL
    private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyBgR0ye0g-uEpIezZEbsVxgNBZmLmlOZ9k";

    //The signup URL
    private static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=AIzaSyBgR0ye0g-uEpIezZEbsVxgNBZmLmlOZ9k";

    //instantiation of the OkHttpClient
    private final OkHttpClient client = new OkHttpClient();

    //Instantiation fo the Gson field
    private final Gson gson = new Gson();


    //Login, POST request
    public boolean login(String email, String password){

        JsonObject body = new JsonObject();

        //adding properties to the JsonObject body, the returnSecureToken is always true
        body.addProperty("email", email); //email for the account
        body.addProperty("password", password); //password for the account
        body.addProperty("returnSecureToken", true);


        return true;
    }
}