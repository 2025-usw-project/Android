package com.su.washcall;

import android.os.Bundle;
import android.view.MenuItem; // MenuItem을 사용하기 위해 추가
import androidx.annotation.NonNull; // @NonNull 어노테이션을 사용하기 위해 추가
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserMachineListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_machine_list);

        // 1. XML에 있는 툴바를 찾아서 이 화면의 액션바로 지정합니다.
        // (이제 NoActionBar 테마이므로 충돌 없이 정상 동작합니다.)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. 액션바에 제목과 뒤로가기 버튼을 설정합니다.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("세탁기 선택");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    // 3. 툴바의 뒤로가기 버튼(home)을 눌렀을 때의 동작을 처리합니다.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 현재 화면을 종료하여 이전 화면(UserRoomListActivity)으로 돌아갑니다.
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
