// 파일 경로: app/src/main/java/com/su/washcall/MainActivity.java
package com.su.washcall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.su.washcall.adapter.LaundryRoomAdapter;
import com.su.washcall.viewmodel.LaundryViewModel;
import com.su.washcall.viewmodel.LaundryViewModelFactory;

public class MainActivity extends AppCompatActivity {

    // 1. LaundryViewModel 변수를 선언합니다.
    private LaundryViewModel laundryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2. activity_main.xml 레이아웃을 설정합니다.
        setContentView(R.layout.activity_main);

        // 3. ViewModelFactory를 사용하여 LaundryViewModel 인스턴스를 생성합니다.
        //    (Kotlin의 by viewModels 역할을 자바에서는 ViewModelProvider가 수행합니다)
        LaundryViewModelFactory factory = new LaundryViewModelFactory(getApplication());
        laundryViewModel = new ViewModelProvider(this, factory).get(LaundryViewModel.class);

        // 4. RecyclerView를 레이아웃에서 찾습니다.
        RecyclerView recyclerView = findViewById(R.id.rv_laundry_rooms);

        // 5. LaundryRoomAdapter를 생성합니다. (Kotlin으로 만든 어댑터를 자바에서 그대로 사용)
        //    아이템 클릭 시 동작할 람다(lambda)를 정의합니다.
        final LaundryRoomAdapter adapter = new LaundryRoomAdapter(laundryRoom -> {
            // 아이템 클릭 시 Toast 메시지를 띄웁니다.
            Toast.makeText(this, laundryRoom.getRoomName() + " 선택됨", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, WashingMachineActivity.class);
            intent.putExtra("ROOM_ID", laundryRoom.getRoomId());
            intent.putExtra("ROOM_NAME", laundryRoom.getRoomName());
            startActivity(intent);

            return null;
        });

        // 6. RecyclerView에 어댑터와 레이아웃 매니저를 설정합니다.
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 7. ViewModel의 세탁실 목록 데이터(allLaundryRooms)를 관찰(observe)합니다.
        laundryViewModel.getAllLaundryRooms().observe(this, rooms -> {
            if (rooms != null) {
                // DB 데이터가 변경될 때마다 어댑터에 새로운 목록을 제출하여 UI를 갱신합니다.
                adapter.submitList(rooms);
                Log.d("MainActivity", "세탁실 목록 업데이트: " + rooms.size() + "개");
            }
        });

        // 8. (선택사항) ViewModel의 데이터 로딩 상태나 오류 메시지를 관찰하여 사용자에게 피드백을 줍니다.
        laundryViewModel.getOperationStatus().observe(this, status -> {
            if (status != null) {
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
            }
        });

        // 9. 앱이 처음 시작될 때 서버로부터 최신 데이터를 가져오도록 요청합니다.
        laundryViewModel.refreshData();
    }
}
