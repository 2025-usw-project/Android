// 파일 경로: app/src/main/java/com/su/washcall/WashingMachineActivity.java
package com.su.washcall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.su.washcall.R;
import com.su.washcall.adapter.WashingMachineAdapter;
import com.su.washcall.viewmodel.LaundryViewModel;
import com.su.washcall.viewmodel.LaundryViewModelFactory;


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

        // 1. RecyclerView를 찾습니다.
        RecyclerView recyclerView = findViewById(R.id.rv_washing_machines);

        // 2. WashingMachineAdapter를 생성하고 클릭 리스너를 설정합니다.
        final WashingMachineAdapter adapter = new WashingMachineAdapter(machine -> {
            // --- ▼▼▼ [수정된 최종 로직] ▼▼▼ ---
            // 모니터링 앱의 목적에 맞게 다이얼로그 로직을 완전히 변경합니다.
            String machineStatus = machine.getStatus().toLowerCase();

            // 분기 1: 세탁기가 '작동중'일 때 (가장 중요한 기능)
            if (machineStatus.equals("running") || machineStatus.equals("작동중") || machineStatus.equals("세탁중")) {
                new AlertDialog.Builder(this)
                        .setTitle(machine.getMachineName())
                        .setMessage("세탁이 끝나면 알림을 받으시겠습니까?")
                        .setPositiveButton("알림 받기", (dialog, which) -> {
                            // TODO: 추후 '알림 받기' 로직 구현 (FCM 토큰과 기기ID를 서버에 전송)
                            Toast.makeText(this, machine.getMachineName() + " 완료 시 알림을 설정했습니다.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
            // 분기 2: 그 외 모든 상태('사용 가능', '종료', '고장' 등)일 때
            else {
                new AlertDialog.Builder(this)
                        .setTitle(machine.getMachineName())
                        .setMessage("현재 상태: " + getStatusString(machine.getStatus())) // 현재 상태만 명확히 알려줌
                        .setPositiveButton("확인", null) // 확인 버튼만 제공
                        .show();
            }
            // --- ▲▲▲ [수정된 최종 로직] ▲▲▲ ---

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

    // 상태 문자열을 변환하는 도우미 함수
    private String getStatusString(String status) {
        if (status == null) return "알 수 없음";
        switch (status.toLowerCase()) {
            case "available":
            case "사용 가능":
                return "사용 가능";
            case "running":
            case "세탁중":
            case "작동중":
                return "작동중";
            case "finished":
            case "종료":
                return "종료";
            case "error":
            case "고장":
                return "고장";
            default:
                return "알 수 없음";
        }
    }
}
