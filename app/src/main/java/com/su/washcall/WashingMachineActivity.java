// 파일 경로: app/src/main/java/com/su/washcall/activity/WashingMachineActivity.java
package com.su.washcall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.su.washcall.R;
import com.su.washcall.adapter.WashingMachineAdapter;
import com.su.washcall.viewmodel.LaundryViewModel;
import com.su.washcall.viewmodel.LaundryViewModelFactory;

import androidx.appcompat.app.AlertDialog;

public class WashingMachineActivity extends AppCompatActivity {
    // ViewModel, 세탁실 ID와 이름을 담을 변수 선언
    private LaundryViewModel laundryViewModel;
    private int roomId;
    private String roomName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washing_machine);

        // MainActivity에서 보낸 Intent로부터 세탁실 ID와 이름 가져오기
        Intent intent = getIntent();
        roomId = intent.getIntExtra("ROOM_ID", -1);
        roomName = intent.getStringExtra("ROOM_NAME");

        // 만약 ID를 제대로 받지 못했다면 액티비티 종료
        if (roomId == -1) {
            Toast.makeText(this, "세탁실 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 화면 상단의 세탁실 이름 설정
        TextView roomNameHeader = findViewById(R.id.tv_room_name_header);
        roomNameHeader.setText(roomName);

        // ViewModel 초기화
        LaundryViewModelFactory factory = new LaundryViewModelFactory(getApplication());
        laundryViewModel = new ViewModelProvider(this, factory).get(LaundryViewModel.class);

        // RecyclerView 설정 (어댑터는 다음 단계에서 만듭니다)
        // 1. RecyclerView를 찾습니다.
        RecyclerView recyclerView = findViewById(R.id.rv_washing_machines);

        // 2. 수정된 WashingMachineAdapter를 생성하고 클릭 리스너를 설정합니다.
        final WashingMachineAdapter adapter = new WashingMachineAdapter(machine -> {
            Toast.makeText(this, machine.getMachineName() + " 선택됨", Toast.LENGTH_SHORT).show();
            // TODO: 추후 세탁기 제어 화면으로 이동하는 로직 추가
            return null; // Java 람다에서 Unit을 반환하기 위함
        });

        // 3. RecyclerView에 어댑터와 레이아웃 매니저를 설정합니다.
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 4. ViewModel로부터 '특정 세탁실(roomId)의 세탁기 목록'을 관찰합니다.
        laundryViewModel.getWashingMachinesByRoom(roomId).observe(this, machines -> {
            if (machines != null) {
                // 데이터가 변경될 때마다 어댑터에 목록을 제출하여 UI를 갱신합니다.
                adapter.submitList(machines);
                Log.d("WashingMachineActivity", roomName + "의 세탁기 " + machines.size() + "개 로드됨");
            }
        });
    }
}
