package com.su.washcall;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvSummary;
    private TextView tvMachineCount;
    private LinearLayout layoutMachines; // 세탁기 칸을 표시할 영역
    private int washingMachineCount = 0; // 세탁기 개수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button btnCalibration = findViewById(R.id.btnCalibration);
        Button btnLogs = findViewById(R.id.btnLogs);
        tvSummary = findViewById(R.id.tvSummary);
        tvMachineCount = findViewById(R.id.tvMachineCount);
        layoutMachines = findViewById(R.id.layoutMachines);

        // 세탁기 수 선택
        tvMachineCount.setOnClickListener(v -> showMachineCountDialog());

        // 캘리브레이션 화면으로 이동
        btnCalibration.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, CalibrationActivity.class)));
        // 상세 로그 화면으로 이동
        btnLogs.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, WashingDetailActivity.class)));
    }

    /**
     * 세탁기 개수 선택 다이얼로그 표시
     */
    private void showMachineCountDialog() {
        final String[] counts = {"1대", "2대", "3대", "4대", "5대", "6대", "7대", "8대", "9대", "10대"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세탁기 개수를 선택하세요")
                .setItems(counts, (dialog, which) -> {
                    washingMachineCount = which + 1; // index + 1
                    updateMachineCountDisplay();
                    generateMachineBoxes(washingMachineCount); // 세탁기 칸 생성
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

    /**
     * 세탁기 개수만큼 칸 생성
     */
    private void generateMachineBoxes(int count) {
        layoutMachines.removeAllViews(); // 기존 박스 초기화

        for (int i = 1; i <= count; i++) {
            // 수평 레이아웃(박스)
            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.HORIZONTAL);
            box.setPadding(16, 16, 16, 16);
            box.setBackgroundResource(R.drawable.rounded_white_box);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 8);
            box.setLayoutParams(params);

            // 세탁기 이름 표시
            TextView tvName = new TextView(this);
            tvName.setText("세탁기 " + i + "번");
            tvName.setTextSize(16);
            tvName.setTextColor(0xFF000000);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            // 입력 버튼
            Button btnInput = new Button(this);
            btnInput.setText("입력");
            btnInput.setBackgroundTintList(getColorStateList(R.color.purple_500));
            btnInput.setTextColor(0xFFFFFFFF);

            int machineIndex = i;
            btnInput.setOnClickListener(v -> showInputDialog(machineIndex, tvName));

            box.addView(tvName);
            box.addView(btnInput);
            layoutMachines.addView(box);
        }
    }

    /**
     * 입력 다이얼로그
     */
    private void showInputDialog(int machineIndex, TextView tvName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세탁기 " + machineIndex + "번 값 입력");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("숫자를 입력하세요");
        builder.setView(input);

        builder.setPositiveButton("확인", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                tvName.setText("세탁기 " + machineIndex + "번: " + value);
            }
        });

        builder.setNegativeButton("취소", null);
        builder.show();
    }
}