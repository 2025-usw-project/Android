package com.su.washcall;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalibrationActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvMessage;
    private Button btnCancel;
    private Handler handler; // 메인 스레드 핸들러

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        progressBar = findViewById(R.id.progressBar);
        tvMessage = findViewById(R.id.tvMessage);
        btnCancel = findViewById(R.id.btnCancel);

        // ✅ 반드시 메인 루퍼로 핸들러 초기화 (안드로이드 12 이상 대응)
        handler = new Handler(Looper.getMainLooper());

        // 🔹 1️⃣ 화면 진입 시 "진행 중" 표시
        tvMessage.setText("⚙️ 캘리브레이션 진행 중...");
        progressBar.setProgress(50);

        // 🔹 2️⃣ 서버 완료 신호 시뮬레이션 (5초 후 완료 처리)
        handler.postDelayed(() -> {
            tvMessage.setText("✅ 캘리브레이션 종료");
            progressBar.setProgress(100);

            Toast.makeText(CalibrationActivity.this,
                    "캘리브레이션이 완료되었습니다.", Toast.LENGTH_LONG).show();

            // 1.5초 후 자동 종료
            handler.postDelayed(this::finish, 1500);
        }, 5000);

        // 🔹 3️⃣ 취소 버튼 클릭 시
        btnCancel.setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null); // 모든 예약된 작업 제거
            tvMessage.setText("❌ 캘리브레이션 취소됨");
            progressBar.setProgress(0);
            Toast.makeText(this, "캘리브레이션이 취소되었습니다.", Toast.LENGTH_SHORT).show();

            handler.postDelayed(this::finish, 1000);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ✅ Activity 종료 시 핸들러 작업 제거 (메모리 누수 방지)
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}

