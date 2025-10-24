package com.example.a2gradeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingSimple2Activity extends AppCompatActivity {

    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_simple2);

        btnStart = findViewById(R.id.btnStart);

        // “시작하기” 버튼 클릭 → MainActivity(파란 화면)으로 이동
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingSimple2Activity.this, MainActivity.class);
            // 🔹 이전 액티비티(Login, Register 등 다 닫고 메인만 남기기)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}