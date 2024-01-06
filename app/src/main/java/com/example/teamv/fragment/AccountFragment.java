package com.example.teamv.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamv.R;
import com.example.teamv.activity.LoginActivity;
import com.example.teamv.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class AccountFragment extends Fragment {
    private ImageView ivAvatar, ivEditProfile;
    private TextView tvFullname, tvEmail, tvDecription;
    private View view;
    private Button btnLogout;
    // Firebase
    private FirebaseAuth firebaseAuth;

    // Chuc Thien
    private String PREFERENCE_KEY = "LogIn_SharePreferences";
    private String LOGIN_KEY = "LOGIN";
    // Toan
    private TextView btnchangepass;
    private String password;
    private String userID;
    private String Emailau;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        // get view
        findViewByIds(view);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "Đã xảy ra lỗi. Thông tin người dùng hiện không có sẵn",
                    Toast.LENGTH_LONG).show();
        } else {
            userID = firebaseUser.getUid();

//            // Trích xuất thông tin người dùng từ CSDL cho "Users"
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

//            databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    User readUserDetails = snapshot.getValue(User.class);
//                    if (readUserDetails != null) {
//                        tvFullname.setText(readUserDetails.fullname);
//                        tvEmail.setText(firebaseUser.getEmail());
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(getActivity(), "Đã xảy ra lỗi",
//                            Toast.LENGTH_LONG).show();
//                }
//            });

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference userReference = firestore.collection("User").document(userID);
            userReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                // need to edit here for security problems
                                tvFullname.setText(user.getFullname());
                                tvEmail.setText(user.getEmail());

                                password=user.getPassword();
                                Emailau=user.getEmail();

                                if(user.getEmail().toString().equalsIgnoreCase("minhthienluu2406@gmail.com")){
                                    TextView tvsex=(TextView) view.findViewById(R.id.tv_sex);
                                    TextView tvage=(TextView) view.findViewById(R.id.tv_age);
                                    tvsex.setText("Giới tính: Nam");
                                    tvage.setText("Tuổi: 21");
                                }
                            } else {
                                Toast.makeText(getActivity(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new EditProfileFragment());
                fragmentTransaction.commit();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(LOGIN_KEY, false); // Cập nhật giá trị LOGIN_KEY thành false
                editor.apply(); // Lưu thay đổi vào SharedPreferences
//                startActivity(new Intent(getActivity(), LoginActivity.class));
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        // thay đổi pass word
        btnchangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Change Password");

                // Inflate layout
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_changepass, null);
                builder.setView(view);

                final EditText etOldPassword = view.findViewById(R.id.etOldPassword);
                final EditText etNewPassword = view.findViewById(R.id.etNewPassword);
                final EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
                CheckBox chkShowPassword = view.findViewById(R.id.chkShowPassword);
                chkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            etOldPassword.setTransformationMethod(null);
                            etNewPassword.setTransformationMethod(null);
                            etConfirmPassword.setTransformationMethod(null);
                        } else {
                            etOldPassword.setTransformationMethod(new PasswordTransformationMethod());
                            etNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                            etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                        }
                    }
                });
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the update password logic here
                        String oldPassword = etOldPassword.getText().toString().trim();
                        String newPassword = etNewPassword.getText().toString().trim();
                        String confirmPassword = etConfirmPassword.getText().toString().trim();
                        if(password.compareTo(oldPassword)!=0){
                            Toast.makeText(getActivity(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(newPassword.compareTo(confirmPassword)!=0){
                            Toast.makeText(getActivity(), "Kiểm tra lại phần xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            DocumentReference userReference = firestore.collection("User").document(userID);

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("password", newPassword);  // Thay mật khẩu mới

                            userReference.update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Xử lý thành công
                                            password=newPassword;
                                            dialog.dismiss();
                                            Log.d("hihihi", "Dữ liệu đã được cập nhật thành công!");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Cập nhập không thành công", Toast.LENGTH_SHORT).show();
                                            // Xử lý lỗi
                                        }
                                    });


                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            firebaseAuth.signInWithEmailAndPassword(Emailau, oldPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        // Lấy thông tin của User đã nhập
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                        // Kiểm tra email đã được xác nhận chưa trước khi cho phép đăng nhập
                                        if (firebaseUser.isEmailVerified()) {
                                            updatePasswordForUser(firebaseUser, newPassword);
                                        }
                                    }
                                }
                                private void updatePasswordForUser(FirebaseUser user, String newPassword) {
                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    // Cập nhật mật khẩu thành công
                                                    Toast.makeText(getActivity(), "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Cập nhật mật khẩu thất bại
                                                    Toast.makeText(getActivity(), "Cập nhật mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });

                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    private void findViewByIds(View vỉew) {
        ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tvFullname = (TextView) view.findViewById(R.id.tv_fullname);
        tvEmail = (TextView) view.findViewById(R.id.tv_email);
        ivEditProfile = (ImageView) view.findViewById(R.id.iv_edit_profile);
        tvDecription = (TextView) view.findViewById(R.id.tv_decription);
        btnLogout = (Button) view.findViewById(R.id.btn_logout);
        btnchangepass=(TextView) view.findViewById(R.id.btn_change_pass);
    }
}