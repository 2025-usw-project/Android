package com.example.a2gradeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingSimpleActivity extends AppCompatActivity {

    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_simple); // 위 XML과 연결

        btnNext = findViewById(R.id.btnNext);

        // “다음” 버튼 클릭 → 4번째 온보딩 화면으로 이동
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingSimpleActivity.this, OnboardingSimple2Activity.class);
            startActivity(intent);
            finish(); // 현재 화면 종료
        });
    }
}
