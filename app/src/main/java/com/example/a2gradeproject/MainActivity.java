package com.example.a2gradeproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

// Room/DB 관련 import
import com.example.a2gradeproject.database.AppDatabase;
import com.example.a2gradeproject.database.LaundryRoom;
import com.example.a2gradeproject.database.LaundryRoomDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // DB 작업을 위한 스레드 풀 사용

public class MainActivity extends AppCompatActivity {
    // 로그 태그
    private final String TAG = "MainActivity_DB_Test";
    // DB 작업을 위한 싱글 스레드 Executor (코루틴 대신 자바 표준 사용)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. DB 인스턴스 및 DAO 가져오기
        try {
            Log.d(TAG, "DB 인스턴스 가져오기 시도...");
            // Kotlin의 Companion object를 자바에서 가져오는 표준 방식
            final AppDatabase db = AppDatabase.Companion.getInstance(getApplicationContext());
            final LaundryRoomDao dao = db.laundryRoomDao();
            Log.d(TAG, "DB 인스턴스 및 DAO 가져오기 성공.");
        } catch (Exception e) {
            // DB 인스턴스 생성 실패 시 (매우 희귀)
            Log.e(TAG, "Room DB 초기화 중 치명적인 오류 발생!", e);
        }

        // 기존 버튼 클릭 리스너 코드 (R.id.btn_alert가 XML에 있다고 가정)
        Button alertButton = findViewById(R.id.btn_alert);
        if (alertButton != null) {
            alertButton.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "알림을 받았습니다", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Activity가 종료될 때 Executor를 종료하여 메모리 누수를 방지합니다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}