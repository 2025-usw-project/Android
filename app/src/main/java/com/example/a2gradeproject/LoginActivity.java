package com.example.a2gradeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin, btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp); // ← XML에 추가한 버튼과 일치

        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            // 관리자: 12345 / 12345
            if (email.equals("12345") && password.equals("12345")) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
            }
            // 일반 사용자: 1234 / 1234
            else if (email.equals("1234") && password.equals("1234")) {
                startActivity(new Intent(this, OnboardingSimpleActivity.class));
                finish();
            } else {
                editEmail.setError("이메일 또는 비밀번호가 올바르지 않습니다");
                editPassword.setError("이메일 또는 비밀번호가 올바르지 않습니다");
            }
        });

        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
