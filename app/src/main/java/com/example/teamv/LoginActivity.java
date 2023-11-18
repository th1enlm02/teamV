package com.example.teamv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private TextView tvRegister;
    private Button btnLogin;
    private ProgressBar progressBar;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        btnLogin = (Button) findViewById(R.id.btn_login);
        progressBar = (ProgressBar) findViewById(R.id.process_bar_login);

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

        // Bắt sự kiện chuyển sang màn hình đăng ký
        tvRegister.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        // Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                // Kiểm tra input
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_LONG).show();
                    if (TextUtils.isEmpty(email)) {
                        etEmail.setError("Email không được bỏ trống");
                        etEmail.requestFocus();
                    }
                    else if (TextUtils.isEmpty(password)) {
                        etPassword.setError("Mật khẩu không hợp lệ");
                        etPassword.requestFocus();
                    }
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập lại email", Toast.LENGTH_LONG).show();
                    etEmail.setError("Email không hợp lệ");
                    etEmail.requestFocus();
                } else if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_LONG).show();
                    etPassword.setError("Mật khẩu yếu");
                    etPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(email, password);
                }
            }

            private void loginUser(String email, String password) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Lấy thông tin của User đã nhập
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            // Kiểm tra email đã được xác nhận chưa trước khi cho phép đăng nhập
                            if (firebaseUser.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!",
                                        Toast.LENGTH_LONG).show();
                                // Chuyển sang màn hình Home
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                // Ngăn không cho chuyên về giao diện đăng nhập khi bấm phím back sau khi đăng nhập
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                firebaseAuth.signOut();
                                showAlertDialog();
                            }
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e){
                                etEmail.setError("Người dùng không tồn tại hoặc không còn hiệu lực. Vui lòng nhập lại");
                                etEmail.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                etEmail.setError("Email và mật khẩu không trùng khớp. Vui lòng kiểm tra và nhập lại");
                                etEmail.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(LoginActivity.this, "Đăng nhập không thành công. Vui lòng thử lại",
                                    Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    private void showAlertDialog() {
                        // Thiết lập Alert Builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Email chưa được xác nhận");
                        builder.setMessage("Vui lòng xác nhận email. Bạn không thể đăng nhập nếu email chưa được xác nhận");

                        // Mở email nếu người dùng bấm vào nút Tiếp tục
                        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Mở ứng dụng email ở cửa sổ khác
                                startActivity(intent);
                            }
                        });

                        // Tạo AlertDialog
                        AlertDialog alertDialog = builder.create();

                        // Hiển thị AlertDialog
                        alertDialog.show();
                    }
                });
            }
        });
    }
}