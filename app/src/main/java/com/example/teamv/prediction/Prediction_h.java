package com.example.teamv.prediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamv.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class Prediction_h extends AppCompatActivity {
    TextView btn_send;
    ImageView btn_back;
    RadioGroup radioGroupSex ;
    RadioGroup radioGroupEnv ;
    RadioGroup radioGroupHeal;
    RadioGroup radioGroupPathology ;
    RadioGroup radioGroupWork ;
    EditText edt_age;
    EditText edt_time;
    EditText edt_bmi;
    int sex,env,path,heal,work;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> stringList ;
    private void init(){
        btn_send = findViewById(R.id.btn_send);
        btn_back = findViewById(R.id.btn_back);
        radioGroupSex = findViewById(R.id.sex);
        radioGroupEnv = findViewById(R.id.env);
        radioGroupHeal = findViewById(R.id.health);
        radioGroupPathology = findViewById(R.id.pathology);
        radioGroupWork = findViewById(R.id.work);
        edt_age=findViewById(R.id.age);
        edt_time=findViewById(R.id.time);
        edt_bmi=findViewById(R.id.bmi);
    }
    public interface PredictionApi {
        @POST("/predict")
        retrofit2.Call<PredictionResponse> predictHeartDisease(@Body Map<String, float[]> inputData);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_h);

        String user="29HdH8oopxOFaMjm9SWYwns9sSB3";

        init();
        ImageView btn_his=findViewById(R.id.btn_history);
        btn_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Prediction_h.this,Predict_Lastview.class);
                intent.putExtra("userid",user);
                startActivity(intent);
            }
        });
        //------------------------------------------------------------------------------
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent= new Intent(Prediction_h.this,);
//                startActivity(intent);
            }
        });
        radioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.female) {
                    sex = 0;
                } else if (checkedId == R.id.male) {
                    sex = 1;
                }
            }
        });
        radioGroupEnv.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.env_1) {
                    env = 0;
                } else if (checkedId == R.id.env_2) {
                    env = 1;
                }else if (checkedId == R.id.env_3) {
                    env = 2;
                }else if (checkedId == R.id.env_4) {
                    env = 3;
                }
            }
        });
        radioGroupWork.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.work_1) {
                    work = 0;
                } else if (checkedId == R.id.work_2) {
                    work = 1;
                }else if (checkedId == R.id.work_3) {
                    work = 2;
                }
            }
        });
        radioGroupPathology.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.path_1) {
                    path = 0;
                } else if (checkedId == R.id.path_2) {
                    path = 1;
                } else if (checkedId == R.id.path_3) {
                    path = 2;
                }
            }
        });
        radioGroupHeal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.heal_1) {
                    heal = 0;
                } else if (checkedId == R.id.heal_2) {
                    heal = 1;
                } else if (checkedId == R.id.heal_3) {
                    heal = 2;
                }else if (checkedId == R.id.heal_4) {
                    heal = 3;
                }
            }
        });
        //--------------------------------------------------------------------------------
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float[] inputdata = {
                        Float.parseFloat(edt_age.getText().toString()),
                        sex,
                        work,
                        Float.parseFloat(edt_time.getText().toString()),
                        heal,
                        path,
                        env,
                        Float.parseFloat(edt_bmi.getText().toString())
                };
                stringList = new ArrayList<>();
                stringList.add(edt_age.getText().toString());
                stringList.add(edt_time.getText().toString());
                stringList.add(edt_bmi.getText().toString());
                stringList.add(String.valueOf(env));
                stringList.add(String.valueOf(path));
                stringList.add(String.valueOf(heal));
                stringList.add(String.valueOf(sex));
                stringList.add(String.valueOf(work));
                Map<String, Object> data = new HashMap<>();
                data.put(edt_age.getText().toString(), stringList);

                db.collection("Predict_Heal").document(user)
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Prediction_h.this, "đã up xong", Toast.LENGTH_SHORT).show();
                                Log.d("ff", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("ff", "Error writing document", e);
                            }
                        });

                makePrediction(inputdata);
            }
        });
    }
    public interface predictionapi {
        @POST("/predict")
        Call<PredictionResponse> predictHeartDisease(@Body Map<String, float[]> inputData);

    }
    private void makePrediction(float[] inputData) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.67:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        predictionapi predictionApi = retrofit.create(predictionapi.class);

        Map<String, float[]> data = new HashMap<>();
        data.put("features", inputData);

        Call<PredictionResponse> call = predictionApi.predictHeartDisease(data);

        call.enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful()) {
                    PredictionResponse predictionResponse = response.body();
                    int result = predictionResponse.getPrediction();

                    if (result==0){
                        try{
                            Dialog dialog = new Dialog(Prediction_h.this);
                            dialog.setContentView(R.layout.dialog_predict);
                            dialog.show();}
                        catch(Exception e){
                            Log.d("loisi", e.getMessage());
                        }
                    }else{
                        try{
                            Dialog dialog = new Dialog(Prediction_h.this);
                            dialog.setContentView(R.layout.dialog_predict);
                            TextView textView = dialog.findViewById(R.id.dialog_textview);
                            ImageView imageView = dialog.findViewById(R.id.dialog_imageview);
                            textView.setText("Với tình trạng hiện tại thì không sớm thì muôn bạn cũng gặp ông trong hình sớm, hãy chăm sóc sức khỏe thật tốt nhé!");
                            imageView.setImageResource(R.drawable.img_predict_warn);
                            dialog.show();}
                        catch (Exception e){
                            Log.d("loisi", e.getMessage());
                        }
                    }

                } else {
                    Log.d("lloisa",response.message());
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("loisa",t.getMessage());
            }
        });
    }
}