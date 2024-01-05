package com.example.teamv.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class upload_file extends AppCompatActivity {
    ListView lv;
    ArrayList<String> emails = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=findViewById(R.id.listview);
//        -----------------------------------------------------------------------------
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("Users");

        usersCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String user_email = documentSnapshot.getString("email");
                    emails.add(user_email);
                    updateListView(emails);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Error load info", "Error reading documents", e);
            }
        });
//        ------------------------------------------------------------------------------
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    updateListView(emails);
                } else {
                    List<String> searchResults = performSearch(newText);
                    updateListView(searchResults);
                }
                return true;
            }
        });
        updateListView(emails);
    }
    private void updateListView(List<String> emailList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emailList);
        lv.setAdapter(adapter);
    }
    private List<String> performSearch(String query) {
        List<String> searchResults = new ArrayList<>();
        for (String email : emails) {
            if (email.toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(email);
            }
        }
        return searchResults;
    }

}