package com.example.teamv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamv.R;
import com.example.teamv.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFullname, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // get view
        findViewByIds();

        // Che và hiển thị mật khẩu
        ImageView imShowHidePassword = findViewById(R.id.im_show_hide_password);
        imShowHidePassword.setImageResource(R.drawable.ic_hide_password);
        imShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // Nếu mật khẩu đang hiển thị -> Che mật khẩu
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Đổi icon
                    imShowHidePassword.setImageResource(R.drawable.ic_hide_password);
                } else {
                    // Nếu mật khẩu đang bị che -> Hiện thị
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    // Đổi icon
                    imShowHidePassword.setImageResource(R.drawable.ic_show_password);
                }
            }
        });

        // Che và hiển thị xác nhận mật khẩu
        ImageView imShowHideConfirmPassword = findViewById(R.id.im_show_hide_confirm_password);
        imShowHideConfirmPassword.setImageResource(R.drawable.ic_hide_password);
        imShowHideConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etConfirmPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // Nếu mật khẩu đang hiển thị -> Che mật khẩu
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Đổi icon
                    imShowHideConfirmPassword.setImageResource(R.drawable.ic_hide_password);
                } else {
                    // Nếu mật khẩu đang bị che -> Hiện thị
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    // Đổi icon
                    imShowHideConfirmPassword.setImageResource(R.drawable.ic_show_password);
                }
            }
        });

        // Sự kiện chuyển sang màn hình đăng nhập
        tvLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        // Bắt sự kiện nhấn nút Đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Khai báo biến
                String fullname, email, password, confirm_passwod;
                fullname = etFullname.getText().toString();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                confirm_passwod = etConfirmPassword.getText().toString();
                // Kiểm tra input
                if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm_passwod)){
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_LONG).show();
                    if (TextUtils.isEmpty(fullname)) {
                        //Kiểm tra họ tên
                        etFullname.setError("Hãy nhập họ và tên");
                        etFullname.requestFocus();
                    }
                    else if (TextUtils.isEmpty(email)) {
                        //Kiểm tra email
                        etEmail.setError("Email không được bỏ trống");
                        etEmail.requestFocus();
                    }
                    else if (TextUtils.isEmpty(password)) {
                        //Kiểm tra mật khẩu
                        etPassword.setError("Mật khẩu không hợp lệ");
                        etPassword.requestFocus();
                    }
                    else if (TextUtils.isEmpty(confirm_passwod)) {
                        //Kiểm tra xác nhận mk
                        etConfirmPassword.setError("Mật khẩu không hợp lệ");
                        etConfirmPassword.requestFocus();
                    }
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập lại email", Toast.LENGTH_LONG).show();
                    etEmail.setError("Email không hợp lệ");
                    etEmail.requestFocus();
                } else if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_LONG).show();
                    etPassword.setError("Mật khẩu yếu");
                    etPassword.requestFocus();
                } else if (!password.equals(confirm_passwod)){
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập lại mật khẩu", Toast.LENGTH_LONG).show();
                    etConfirmPassword.setError("Mật khẩu không trùng khớp");
                    etConfirmPassword.requestFocus();
                    // reset status
                    etPassword.clearComposingText();
                    etConfirmPassword.clearComposingText();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    //Sau khi đp ứng đầy đủ điều kiện, tiến hành thực hiện dki
                    registerUser(fullname, email, password);
                }
            }
            private void registerUser(String fullname, String email, String password) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                //Tạo user trong Authentication
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

//                                    // Trích xuất thông tin người dùng từ CSDL cho "Users"
//                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//                                    databaseReference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull ToDoListTask<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                // Gửi mã xác nhận
//                                                firebaseUser.sendEmailVerification();
//                                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng kiểm tra email để xác nhận đăng ký",
//                                                        Toast.LENGTH_LONG).show();
//                                                // Gửi email xác nhận
//                                                firebaseUser.sendEmailVerification();
//
//                                                // Chuyển sang màn hình đăng nhập
//                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                                                // Ngăn không cho chuyên về giao diện đăng ký khi bấm phím back sau khi đăng ký
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                startActivity(intent);
//                                                finish();
//                                            } else {
//                                                Toast.makeText(RegisterActivity.this, "Đăng ký không thành công. Vui lòng thử lại",
//                                                        Toast.LENGTH_LONG).show();
//                                            }
//                                            progressBar.setVisibility(View.GONE);
//                                        }
//                                    });
                                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//                                    Map<String, Object> user = new HashMap<>();
//                                    user.put(KEY_FULLNAME, fullname);
//                                    user.put(KEY_EMAIL, email);
//                                    user.put(KEY_PASSWORD, password);

                                    // Khởi tạo thông tin user vừa đăng ký lưu vào database
                                    String user_id = firebaseUser.getUid();
                                    User user = new User(user_id, fullname, email, password, "", -1, null);

                                    firestore.collection("User").document(user_id).set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Gửi mã xác nhận
                                                        firebaseUser.sendEmailVerification();
                                                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng kiểm tra email để xác nhận đăng ký",
                                                                Toast.LENGTH_LONG).show();
                                                        // Gửi email xác nhận
                                                        firebaseUser.sendEmailVerification();

                                                        // Chuyển sang màn hình đăng nhập
                                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                        // Ngăn không cho chuyên về giao diện đăng ký khi bấm phím back sau khi đăng ký
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Đăng ký không thành công. Vui lòng thử lại",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                      etPassword.setError("Mật khẩu yếu. Sử dụng ít nhất 8 ký tự bao gồm chữ cái, số và ký tự đặc biệt");
                                      etPassword.requestFocus();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        etEmail.setError("Email không hợp lệ hoặc đã được sử dụng. Vui lòng nhập lại");
                                        etEmail.requestFocus();
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        etEmail.setError("Email này đã đuợc sử dụng. Vui lòng nhập email khác");
                                        etEmail.requestFocus();
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
    //Hàm lấy thông tin ID đối tượng cụ thể
    private void findViewByIds() {
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        tvLogin = (TextView) findViewById(R.id.tv_login);
        etFullname = (EditText) findViewById(R.id.et_fullname);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        progressBar = (ProgressBar) findViewById(R.id.process_bar_register);
    }
}