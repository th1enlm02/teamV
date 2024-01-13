package com.example.teamv.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamv.R;
import com.example.teamv.activity.LoginActivity;
import com.example.teamv.my_interface.UserDataCallback;
import com.example.teamv.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment implements UserDataCallback {
    private ImageView ivAvatar;
    private TextView tvFullname, tvEmail, tvGender, tvAge;
    private CardView cvChangePassword, cvLogout, cvEditProfile;
    // Chuc Thien
    private String PREFERENCE_KEY = "LogIn_SharePreferences";
    private String LOGIN_KEY = "LOGIN";
    private View view;
    // Firebase
    private String userID = getUserID();
    private User myInfor;
    private String password;
    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        // get view
        findViewByIds(view);

        // get user infor
        getMyInfor(this);

        // logout
        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(LOGIN_KEY, false); // Cập nhật giá trị LOGIN_KEY thành false
                editor.apply(); // Lưu thay đổi vào SharedPreferences
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        // change password
        cvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Đổi mật khẩu");

                // Inflate layout
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_password, null);
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
                builder.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the update password logic here
                        String oldPassword = etOldPassword.getText().toString().trim();
                        String newPassword = etNewPassword.getText().toString().trim();
                        String confirmPassword = etConfirmPassword.getText().toString().trim();
                        if(password.compareTo(oldPassword) != 0){
                            Toast.makeText(getActivity(), "Nhập sai mật khẩu!", Toast.LENGTH_SHORT).show();
                        }
                        else if(newPassword.compareTo(confirmPassword) != 0){
                            Toast.makeText(getActivity(), "Xác nhận mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
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
                                            password = newPassword;
                                            dialog.dismiss();
                                            Toast.makeText(getActivity(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("ChangePassword", "Changed password failed");
                                        }
                                    });
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            firebaseAuth.signInWithEmailAndPassword(email, oldPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
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
                                                    Log.d("ChangePassword", "Changed password successfully");
                                                } else {
                                                    Log.e("ChangePassword", "Changed password failed");
                                                }
                                            });
                                }
                            });

                        }
                    }
                });

                builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        cvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogEditProfile();
            }
        });
        return view;
    }
    private void openDialogEditProfile() {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_edit_profile);

        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(true);

        TextView tvEmail = (TextView) dialog.findViewById(R.id.tv_email);
        CardView cvCancelEditProfile = (CardView) dialog.findViewById(R.id.cv_edit_profile_cancel);
        CardView cvSaveEditProfile = (CardView) dialog.findViewById(R.id.cv_edit_profile_save);
        EditText etFullname = (EditText) dialog.findViewById(R.id.et_fullname);
        EditText etAge = (EditText) dialog.findViewById(R.id.et_age);
        RadioGroup radioGroupGender = (RadioGroup) dialog.findViewById(R.id.radio_group_gender);
        RadioButton radioButtonMale = (RadioButton) dialog.findViewById(R.id.radio_btn_male);
        RadioButton radioButtonFemale = (RadioButton) dialog.findViewById(R.id.radio_btn_female);

        tvEmail.setText(myInfor.getEmail().toString());
        int selectedRadioButtonId = radioGroupGender.getCheckedRadioButtonId();

        cvCancelEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cvSaveEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etFullname.getText().toString()) || !isValidNaturalNumber(etAge.getText().toString())) {
                    if (TextUtils.isEmpty(etFullname.getText())) {
                        showInvalidInputDialog();
                    } else {
                        showInvalidAgeDialog();
                    }
                } else {
                    String fullname = etFullname.getText().toString();
                    String gender = "";
                    int age = Integer.parseInt(etAge.getText().toString());
                    if (selectedRadioButtonId != -1) {
                        if (selectedRadioButtonId == radioButtonMale.getId()) {
                            gender = "Nam";
                        } else if (selectedRadioButtonId == radioButtonFemale.getId()) {
                            gender = "Nữ";
                        }
                    }
                    tvFullname.setText(fullname);
                    tvAge.setText(age + " tuổi");
                    updateUserInfor(fullname, gender, age);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    private void updateUserInfor(String fullname, String gender, int age){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("User").document(userID);

        documentReference
                .update("fullname", fullname, "gender", gender, "age", age)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("UpdateUserInfor", "Successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UpdateUserInfor", "Failed");
                    }
                });
    }
    // Hàm kiểm tra xem một chuỗi có phải là số tự nhiên không
    private boolean isValidNaturalNumber(String input) {
        try {
            int number = Integer.parseInt(input);
            return number >= 0; // Nếu là số tự nhiên thì trả về true
        } catch (NumberFormatException e) {
            return false; // Nếu không phải số tự nhiên, trả về false
        }
    }

    // Hàm hiển thị Dialog thông báo khi nhập tuổi không hợp lệ
    private void showInvalidAgeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thông báo");
        builder.setMessage("Vui lòng nhập một số tự nhiên cho tuổi");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng nhấn nút OK trong Dialog
            }
        });
        builder.show();
    }
    private void showInvalidInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thông báo");
        builder.setMessage("Tên đầy đủ không được bỏ trống");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void displayInfor() {
        String fullname = myInfor.getFullname();
        String email = myInfor.getEmail();
        String gender = myInfor.getGender();
        int age = myInfor.getAge();
        int resouce_id = myInfor.getResource_id();

        tvFullname.setText(fullname);
        tvEmail.setText(email);
        if (!gender.equals("")) {
            tvGender.setText(gender);
        } else {
            tvGender.setText("");
            tvGender.setHint("Chưa có thông tin");
        }
        if (age != -1) {
            tvAge.setText(age + " tuổi");
        } else {
            tvAge.setText("");
            tvAge.setHint("Chưa có thông tin");
        }
        if (resouce_id != 0) {
            ivAvatar.setImageResource(resouce_id);
        }
    }
    private void getMyInfor(UserDataCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userReference = firestore.collection("User").document(userID);

        userReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);

                            callback.onDataReceived(user);
                        } else {
                            Log.e("GetUserInfor", "Not found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GetUserInfor", e.getMessage());
                    }
                });
    }
    private String getUserID() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userID = null;
        if (firebaseUser == null) {
            Log.e("GetUserInfor", "Not found");
        } else {
            userID = firebaseUser.getUid();
        }
        return userID;
    }
    private void findViewByIds(View vỉew) {
        ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tvFullname = (TextView) view.findViewById(R.id.tv_fullname);
        tvEmail = (TextView) view.findViewById(R.id.tv_email);
        tvGender = (TextView) view.findViewById(R.id.tv_gender);
        tvAge = (TextView) view.findViewById(R.id.tv_age);

        cvEditProfile = (CardView) view.findViewById(R.id.cv_edit_profile);
        cvChangePassword = (CardView) view.findViewById(R.id.cv_change_password);
        cvLogout = (CardView) view.findViewById(R.id.cv_logout);
    }

    @Override
    public void onDataReceived(User user) {
        myInfor = user;
        // update view
        displayInfor();
        password = myInfor.getPassword();
        email = myInfor.getEmail();
    }
}