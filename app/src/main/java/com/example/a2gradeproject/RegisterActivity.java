package com.example.a2gradeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    Button btnSignUp;  // "Sign Up" 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnSignUp = findViewById(R.id.btnSignUp);  // XML에 있는 Sign Up 버튼 id 맞춰주세요

        btnSignUp.setOnClickListener(v -> {
            // 회원가입 완료 → 로그인 화면으로 이동
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            // 🔹 스택 정리: 뒤로가기 눌러도 Register로 안 돌아가게
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // 현재 RegisterActivity 종료
        });
    }
}