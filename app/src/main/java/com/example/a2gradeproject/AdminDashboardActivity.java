package com.example.a2gradeproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvSummary;
    private TextView tvMachineCount;
    private int washingMachineCount = 0; // 현재 등록된 세탁기 수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button btnCalibration = findViewById(R.id.btnCalibration);
        Button btnLogs = findViewById(R.id.btnLogs);
        tvSummary = findViewById(R.id.tvSummary);
        tvMachineCount = findViewById(R.id.tvMachineCount);

        // 🔹 세탁기 수 선택 클릭 이벤트
        tvMachineCount.setOnClickListener(v -> showMachineCountDialog());

        // 🔹 캘리브레이션 화면으로 이동
        btnCalibration.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalibrationActivity.class);
            startActivity(intent);
        });

        // 🔹 상세 로그 화면으로 이동
        btnLogs.setOnClickListener(v ->
                startActivity(new Intent(this, WashingDetailActivity.class)));
    }

    /**
     * 세탁기 개수 선택 다이얼로그 표시
     */
    private void showMachineCountDialog() {
        final String[] counts = new String[]{"1대", "2대", "3대", "4대", "5대", "6대", "7대", "8대", "9대", "10대"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세탁기 개수를 선택하세요")
                .setItems(counts, (dialog, which) -> {
                    washingMachineCount = which + 1; // index + 1
                    updateMachineCountDisplay();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    /**
     * 선택된 세탁기 개수를 화면에 표시
     */
    private void updateMachineCountDisplay() {
        tvMachineCount.setText("현재 세탁기: " + washingMachineCount + "대");
        tvSummary.setText("현재 세탁기 수: " + washingMachineCount + "대 관리 중입니다.");
    }
}
