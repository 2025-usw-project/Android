package com.example.a2gradeproject; // 수정: 올바른 패키지명으로 수정

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // R을 참조하는 코드

        // Set up the button click listener
        Button alertButton = findViewById(R.id.btn_alert);
        alertButton.setOnClickListener(view -> {
            // Handle button click (for now, show a toast)
            Toast.makeText(MainActivity.this, "알림을 받았습니다", Toast.LENGTH_SHORT).show();
        });
    }
}