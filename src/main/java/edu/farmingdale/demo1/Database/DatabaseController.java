package edu.farmingdale.demo1.Database;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import edu.farmingdale.demo1.Launcher;

public class DatabaseController
{

    public void fireStoreManager(){

        Firestore firestore = Launcher.firestore;
        firestore = FirestoreClient.getFirestore();
        firestore.batch();

    }
}
