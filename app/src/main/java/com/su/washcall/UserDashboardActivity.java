package com.su.washcall;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// ▼▼▼ [핵심 수정] 클래스 이름을 파일 이름과 동일하게 UserDashboardActivity로 정의합니다. ▼▼▼
public class UserDashboardActivity extends AppCompatActivity {

    private static final String TAG = "UserDashboard_LOG";

    // 대시보드 화면의 버튼 변수들을 선언합니다.
    private Button btnRoomCode;
    private Button btnEnterRoom;
    private Button btnSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 이 액티비티가 보여줄 레이아웃으로 activity_user_dashboard.xml 을 설정합니다.
        setContentView(R.layout.activity_user_dashboard);

        // XML의 UI 요소들을 ID를 통해 모두 연결합니다.
        btnRoomCode = findViewById(R.id.btnRoomCode);
        btnEnterRoom = findViewById(R.id.btnEnterRoom);
        btnSuggestions = findViewById(R.id.btnSuggestions);

        // 1. '세탁실 등록 코드 입력' 버튼 기능을 설정합니다.
        btnRoomCode.setOnClickListener(v -> {
            Log.d(TAG, "'세탁실 등록 코드 입력' 버튼 클릭. 다이얼로그 표시.");
            showNumberInputDialog(); // 숫자 입력 팝업창을 띄우는 함수 호출
        });

        // 2. '세탁실 및 세탁기 관리' 버튼 기능을 설정합니다.
        btnEnterRoom.setOnClickListener(v -> {
            Log.d(TAG, "'세탁실 및 세탁기 관리' 버튼 클릭. UserRoomListActivity로 이동 시작.");
            // 목적지를 UserRoomListActivity로 지정하여 화면 이동
            Intent intent = new Intent(UserDashboardActivity.this, UserRoomListActivity.class);
            startActivity(intent);
        });

        // 3. '건의사항' 버튼 기능을 설정합니다.
        btnSuggestions.setOnClickListener(v -> {
            Log.d(TAG, "'건의사항' 버튼 클릭.");
            Toast.makeText(this, "건의사항 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show();
        });
    }

    // 숫자 입력을 위한 다이얼로그(팝업창)를 보여주는 함수
    private void showNumberInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세탁실 등록 코드 입력");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setGravity(Gravity.CENTER);
        input.setHint("4자리 숫자 코드");

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 50;
        params.rightMargin = 50;
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("완료", (dialog, which) -> {
            String enteredCode = input.getText().toString();
            if ("1234".equals(enteredCode)) {
                Toast.makeText(getApplicationContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "코드가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
