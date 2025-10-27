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
        // 파일명이 activity_register.xml로 되어 있지만, 실제로는 activity_admin_dashboard.xml이어야 합니다.
        // 만약 파일명이 다르다면 여기서 수정해주세요.
        setContentView(R.layout.activity_admin_dashboard);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        // --- 1. UI 요소 연결 (수정) ---
        loadingOverlay = findViewById(R.id.loadingOverlay);
        Button btnAddNewRoom = findViewById(R.id.btnAddNewRoom);
        Button btnManageRooms = findViewById(R.id.btnManageRooms);

        // ▼▼▼ '무게 영점 조절' 버튼을 '건의사항' 버튼으로 수정합니다. ▼▼▼
        Button btnSuggestions = findViewById(R.id.btnSuggestions); // ID 변경
        // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

        // --- 2. 버튼 클릭 리스너 설정 (수정) ---
        // '신규 세탁실 등록' 버튼
        btnAddNewRoom.setOnClickListener(v -> showAddRoomDialog());

        // '세탁실 관리' 버튼
        btnManageRooms.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, RoomListActivity.class);
            startActivity(intent);
        });

        // ▼▼▼ '건의사항' 버튼에 대한 리스너를 추가합니다. ▼▼▼
        btnSuggestions.setOnClickListener(v -> {
            // 아직 만들지 않은 건의사항 화면(SuggestionActivity.class)으로 이동합니다.
            // Intent intent = new Intent(this, SuggestionActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "건의사항 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
        });
        // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

        // --- 3. ViewModel 관찰 시작 ---
        observeViewModel();
    }

    /**
     * 🔹 ViewModel의 LiveData를 관찰하여 UI 업데이트
     */
    private void observeViewModel() {
        // '세탁실 추가' 결과 관찰
        viewModel.getAddRoomResult().observe(this, result -> {
            handleResult(result, "세탁실 추가 실패");
        });
    }

    // handleResult(...) 함수는 수정 없이 그대로 둡니다.
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

    // showAddRoomDialog() 함수는 수정 없이 그대로 둡니다.
    private void showAddRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("신규 세탁실 등록");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("세탁실 이름을 입력하세요");
        builder.setView(input);

        builder.setPositiveButton("등록", (dialog, which) -> {
            String roomName = input.getText().toString().trim();
            if (!roomName.isEmpty()) {
                viewModel.addNewLaundryRoom(roomName);
            } else {
                Toast.makeText(this, "세탁실 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
