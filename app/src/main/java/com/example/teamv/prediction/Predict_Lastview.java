package com.example.teamv.prediction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.teamv.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Predict_Lastview extends AppCompatActivity {
    ArrayList<Predict_class> list;
    Adapter_Predict_h adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_lastview);
//---------------------------------------------------------------------------------------
        list=new ArrayList<>();

        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");
        //--------------------------------------------------------------------------------------------------
        db.collection("Predict_Heal")
                .document(userid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Object fieldValue = documentSnapshot.get("1");
                        Map<String, Object> data = documentSnapshot.getData();
                        //-----------------------------------------------------------------------
                        if (data != null) {
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                String key = entry.getKey();
                                Object value = entry.getValue();
                                List<String> stringList = (List<String>) value;
                                Predict_class pre= new Predict_class(Integer.parseInt(stringList.get(0)),Integer.parseInt(
                                        stringList.get(1)),Float.parseFloat(stringList.get(2)),Integer.parseInt(stringList.get(3)),
                                        Integer.parseInt(stringList.get(4)),Integer.parseInt(stringList.get(5)),Integer.parseInt(stringList.get(6)),
                                        Integer.parseInt(stringList.get(7))
                                );

                                list.add(pre);

                            }
                            adapter= new Adapter_Predict_h(list);
                            RecyclerView recyclerView= findViewById(R.id.recyclerview);
                            recyclerView.setLayoutManager(new LinearLayoutManager(this));
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("ol", "Document data is null.");
                        }
//--------------------------------------------------------------------------------------------------------------------
                    } else {
                        Log.d("loi r", "Document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("hu hu hu ", "Error fetching document.", e);
                });


    }
}