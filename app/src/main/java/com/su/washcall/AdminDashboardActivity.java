// 경로: app/src/main/java/com/su/washcall/AdminDashboardActivity.java
package com.su.washcall;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.su.washcall.viewmodel.AdminViewModel;
import com.su.washcall.viewmodel.RegisterResult;

public class AdminDashboardActivity extends AppCompatActivity {

    private static final String TAG = "AdminDashboardActivity";

    private TextView tvSummary;
    private TextView tvMachineCount;
    private LinearLayout layoutMachines;
    private int washingMachineCount = 0;
    private View loadingOverlay; // 로딩 오버레이 뷰

    private AdminViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        // UI 요소 연결
        Button btnCalibration = findViewById(R.id.btnCalibration);
        Button btnLogs = findViewById(R.id.btnLogs);
        tvSummary = findViewById(R.id.tvSummary);
        tvMachineCount = findViewById(R.id.tvMachineCount);
        layoutMachines = findViewById(R.id.layoutMachines);
        loadingOverlay = findViewById(R.id.loadingOverlay); // 로딩 오버레이 ID 연결

        // SharedPreferences에서 기존 값 불러오기
        SharedPreferences prefs = getSharedPreferences("MachinePrefs", MODE_PRIVATE);
        washingMachineCount = prefs.getInt("machine_count", 0);
        if (washingMachineCount > 0) {
            updateMachineCountDisplay();
            generateMachineBoxes(washingMachineCount);
        }

        tvMachineCount.setOnClickListener(v -> showMachineCountDialog());
        btnCalibration.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, CalibrationActivity.class)));
        // btnLogs.setOnClickListener(v -> ...);

        observeViewModel();
    }

    /**
     * 🔹 ViewModel의 LiveData를 관찰하여 UI 업데이트
     */
    private void observeViewModel() {
        viewModel.getRegisterResult().observe(this, result -> {
            // 로딩 상태 처리
            if (result instanceof RegisterResult.Loading) {
                loadingOverlay.setVisibility(View.VISIBLE);
                return;
            }

            loadingOverlay.setVisibility(View.GONE); // 로딩 완료 후 숨김

            // 성공 또는 실패 처리
            if (result instanceof RegisterResult.Success) {
                String message = ((RegisterResult.Success) result).getMessage();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else if (result instanceof RegisterResult.Failure) {
                String message = ((RegisterResult.Failure) result).getMessage();
                Toast.makeText(this, "오류: " + message, Toast.LENGTH_LONG).show();
                Log.e(TAG, "등록 실패: " + message);
            }
        });
    }

    /**
     * 🔹 세탁기 등록을 위한 다이얼로그 표시 (roomId, machineId, machineName 입력)
     */
    private void showMachineRegistrationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("신규 세탁기 등록");

        // 다이얼로그에 표시될 레이아웃 생성
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(40, 20, 40, 20);

        // 입력 필드 생성
        final EditText inputRoomId = new EditText(this);
        inputRoomId.setHint("세탁실 번호 (예: 301)");
        inputRoomId.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputMachineId = new EditText(this);
        inputMachineId.setHint("세탁기 번호 (예: 1)");
        inputMachineId.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputMachineName = new EditText(this);
        inputMachineName.setHint("세탁기 이름 (예: 1번 세탁기)");

        // 레이아웃에 필드 추가
        dialogLayout.addView(inputRoomId);
        dialogLayout.addView(inputMachineId);
        dialogLayout.addView(inputMachineName);

        builder.setView(dialogLayout);

        // 확인 버튼 설정
        builder.setPositiveButton("등록", (dialog, which) -> {
            String roomIdStr = inputRoomId.getText().toString();
            String machineIdStr = inputMachineId.getText().toString();
            String machineName = inputMachineName.getText().toString();

            if (roomIdStr.isEmpty() || machineIdStr.isEmpty() || machineName.isEmpty()) {
                Toast.makeText(this, "모든 값을 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int roomId = Integer.parseInt(roomIdStr);
                int machineId = Integer.parseInt(machineIdStr);

                // ViewModel에 새 함수 호출
                viewModel.registerNewMachine(roomId, machineId, machineName);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "번호는 숫자로 입력해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼 설정
        builder.setNegativeButton("취소", null);
        builder.show();
    }


    // --- 아래는 UI 관련 메서드들 (기존 코드와 유사) ---

    private void showMachineCountDialog() {
        // 이 부분은 SharedPreferences에 저장하는 로컬 기능이므로 유지하거나,
        // 서버에서 세탁기 목록을 직접 받아오는 방식으로 변경할 수 있습니다.
        // 지금은 그대로 두겠습니다.
        final String[] counts = {"1대", "2대", "3대", "4대", "5대", "6대", "7대", "8대", "9대", "10대"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세탁기 개수를 선택하세요")
                .setItems(counts, (dialog, which) -> {
                    washingMachineCount = which + 1;
                    SharedPreferences prefs = getSharedPreferences("MachinePrefs", MODE_PRIVATE);
                    prefs.edit().putInt("machine_count", washingMachineCount).apply();
                    updateMachineCountDisplay();
                    generateMachineBoxes(washingMachineCount);
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void updateMachineCountDisplay() {
        tvMachineCount.setText("현재 세탁기: " + washingMachineCount + "대");
        tvSummary.setText("현재 세탁기 수: " + washingMachineCount + "대 관리 중입니다.");
    }

    /**
     * ❗️ 중요: 이 메서드는 이제 단순히 UI 박스를 생성하는 역할만 합니다.
     * 실제 등록은 상단의 showMachineRegistrationDialog()가 담당합니다.
     * 더 나은 구현은 서버에서 실제 등록된 머신 목록을 가져와 그리는 것입니다.
     */
    // 이 메서드만 아래 내용으로 교체해주세요.
    private void generateMachineBoxes(int count) {
        layoutMachines.removeAllViews(); // 기존 뷰 모두 삭제

        // ❗️ 중요: 이 버튼은 이제 '신규 세탁기 등록' 기능만 담당합니다.
        Button btnAddNewMachine = new Button(this);
        btnAddNewMachine.setText("➕ 신규 세탁기 등록하기");
        btnAddNewMachine.setOnClickListener(v -> showMachineRegistrationDialog()); // 팝업창 띄우기 연결

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 8);
        btnAddNewMachine.setLayoutParams(params);
        // 스타일은 필요에 따라 추가할 수 있습니다.
        // btnAddNewMachine.setBackgroundTintList(...);
        // btnAddNewMachine.setTextColor(...);

        layoutMachines.addView(btnAddNewMachine);

        // 참고: 서버에서 실제 등록된 세탁기 목록을 불러와서
        // 여기에 리스트로 보여주는 기능을 추후에 추가할 수 있습니다.
    }
}
