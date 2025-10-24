package com.example.a2gradeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingSimple2Activity extends AppCompatActivity {

    Button btnNext; // ← 이름 변경

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_simple2); // XML 연결

        btnNext = findViewById(R.id.btnNext); // XML과 같은 id로 변경

        // "시작하기" 클릭 시 → MainActivity로 이동
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingSimple2Activity.this, MainActivity.class);
            // 이전 화면들 다 닫고 메인으로 이동
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}