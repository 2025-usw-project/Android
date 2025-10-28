package com.su.washcall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem; // MenuItem을 사용하기 위해 추가
import androidx.annotation.NonNull; // @NonNull 어노테이션을 사용하기 위해 추가
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class UserRoomListActivity extends AppCompatActivity {

    private static final String TAG = "UserRoomList_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_room_list);

        // 1. 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("세탁실 선택");
            // 뒤로가기 버튼 (좌측 화살표) 활성화
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 2. '수원(a)' 카드(cardSuwonARoom)를 찾아서 클릭 리스너를 설정
        CardView cardSuwonARoom = findViewById(R.id.cardSuwonARoom);
        cardSuwonARoom.setOnClickListener(v -> {
            Log.d(TAG, "'수원(a)' 카드 클릭됨. UserMachineListActivity 로 이동을 시도합니다.");

            // 다음 화면으로 이동하기 위한 Intent(의도) 생성
            // 목적지: UserMachineListActivity.class
            Intent intent = new Intent(UserRoomListActivity.this, UserMachineListActivity.class);

            // Intent 실행하여 화면 전환
            startActivity(intent);
        });
    }

    // 3. ▼▼▼ [핵심 수정] 툴바의 뒤로가기 버튼 동작을 표준 방식으로 재정의합니다. ▼▼▼
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 눌린 메뉴 아이템의 ID를 확인합니다.
        if (item.getItemId() == android.R.id.home) {
            // ID가 'home'이면 (즉, 툴바의 뒤로가기 버튼이면)
            // 현재 액티비티를 종료하여 이전 화면으로 돌아갑니다.
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
}
