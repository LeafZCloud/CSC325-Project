package edu.farmingdale.demo1;

import com.google.firebase.auth.FirebaseAuth;
import javafx.application.Application;

import java.io.*;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
        FirebaseAuth.getInstance();

            InputStream inputStream = Launcher.class.getResourceAsStream("/classproject-1717-firebase-adminsdk-fbsvc-4ed10da603.json");

    }
}
