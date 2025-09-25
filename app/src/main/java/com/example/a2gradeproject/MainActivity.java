package com.example.a2gradeproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_main.xml 화면을 이 액티비티와 연결
        setContentView(R.layout.activity_main);
    }
}