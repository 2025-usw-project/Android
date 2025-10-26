// 경로: app/src/main/java/com/su/washcall/AdminDashboardActivity.java
package com.su.washcall;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.su.washcall.viewmodel.AdminViewModel;
import com.su.washcall.viewmodel.RegisterResult;

public class AdminDashboardActivity extends AppCompatActivity {

    private static final String TAG = "AdminDashboardActivity";

    private View loadingOverlay;
    private AdminViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        // --- 1. UI 요소 연결 ---
        loadingOverlay = findViewById(R.id.loadingOverlay);
        Button btnAddNewRoom = findViewById(R.id.btnAddNewRoom); // activity_admin_dashboard.xml에 이 ID의 버튼이 있어야 함
        Button btnAddNewMachine = findViewById(R.id.btnAddNewMachine); // activity_admin_dashboard.xml에 이 ID의 버튼이 있어야 함
        Button btnCalibration = findViewById(R.id.btnCalibration);
        Button btnLogs = findViewById(R.id.btnLogs);

        // --- 2. 버튼 클릭 리스너 설정 ---
        // '신규 세탁실 등록' 버튼 클릭 시 다이얼로그 표시
        btnAddNewRoom.setOnClickListener(v -> showAddRoomDialog());

        // '신규 세탁기 등록' 버튼 클릭 시 다이얼로그 표시
        btnAddNewMachine.setOnClickListener(v -> showMachineRegistrationDialog());

        btnCalibration.setOnClickListener(v ->
                startActivity(new Intent(this, CalibrationActivity.class)));

        // 로그 버튼 리스너 (필요 시 구현)
        // btnLogs.setOnClickListener(v -> ...);

        // --- 3. ViewModel 관찰 시작 ---
        observeViewModel();
    }

    /**
     * 🔹 ViewModel의 LiveData를 관찰하여 UI 업데이트
     */
    private void observeViewModel() {
        // 1. '세탁기 등록' 결과 관찰
        viewModel.getRegisterResult().observe(this, result -> {
            handleResult(result, "세탁기 등록 실패");
        });

        // 2. '세탁실 추가' 결과 관찰
        viewModel.getAddRoomResult().observe(this, result -> {
            handleResult(result, "세탁실 추가 실패");
        });
    }

    /**
     * 🔹 공통 결과 처리 함수
     * @param result ViewModel로부터 받은 결과 (Loading, Success, Failure)
     * @param failurePrefix 실패 시 Toast 메시지에 표시할 접두사
     */
    private void handleResult(RegisterResult result, String failurePrefix) {
        if (result instanceof RegisterResult.Loading) {
            loadingOverlay.setVisibility(View.VISIBLE);
            return;
        }

        loadingOverlay.setVisibility(View.GONE);

        if (result instanceof RegisterResult.Success) {
            String message = ((RegisterResult.Success) result).getMessage();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else if (result instanceof RegisterResult.Failure) {
            String message = ((RegisterResult.Failure) result).getMessage();
            Toast.makeText(this, failurePrefix + ": " + message, Toast.LENGTH_LONG).show();
            Log.e(TAG, failurePrefix + ": " + message);
        }
    }

    /**
     * 🔹 신규 세탁실 등록을 위한 다이얼로그 표시
     */
    private void showAddRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("신규 세탁실 등록");

        final EditText inputRoomName = new EditText(this);
        inputRoomName.setHint("세탁실 이름 (예: 본관 3층 세탁실)");
        inputRoomName.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(50, 30, 50, 30);
        layout.addView(inputRoomName);
        builder.setView(layout);

        builder.setPositiveButton("등록", (dialog, which) -> {
            String roomName = inputRoomName.getText().toString().trim();
            if (roomName.isEmpty()) {
                Toast.makeText(this, "세탁실 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.addNewLaundryRoom(roomName);
            }
        });

        builder.setNegativeButton("취소", null);
        builder.show();
    }

    /**
     * 🔹 세탁기 등록을 위한 다이얼로그 표시 (roomId, machineId, machineName 입력)
     */
    private void showMachineRegistrationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("신규 세탁기 등록");

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(40, 20, 40, 20);

        final EditText inputRoomId = new EditText(this);
        inputRoomId.setHint("세탁실 번호 (예: 1)");
        inputRoomId.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputMachineId = new EditText(this);
        inputMachineId.setHint("세탁기 고유 번호 (예: 101)");
        inputMachineId.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputMachineName = new EditText(this);
        inputMachineName.setHint("세탁기 이름 (예: 1번 세탁기)");

        dialogLayout.addView(inputRoomId);
        dialogLayout.addView(inputMachineId);
        dialogLayout.addView(inputMachineName);

        builder.setView(dialogLayout);

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
                viewModel.registerNewMachine(roomId, machineId, machineName);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "번호는 숫자로 입력해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", null);
        builder.show();
    }
}
