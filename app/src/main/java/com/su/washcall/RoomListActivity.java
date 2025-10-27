// 파일 경로: app/src/main/java/com/su/washcall/RoomListActivity.java
package com.su.washcall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.su.washcall.adapter.RoomListAdapter;
import com.su.washcall.database.LaundryRoom;
import com.su.washcall.viewmodel.LaundryViewModel;
import com.su.washcall.viewmodel.LaundryViewModelFactory;

// [삭제] 이제 Activity가 Repository를 직접 알 필요가 없으므로 import를 제거해도 됩니다.
// import com.su.washcall.repository.LaundryRepository;

import java.util.List;

public class RoomListActivity extends AppCompatActivity {
    private static final String TAG = "RoomListActivity_LOG";

    private LaundryViewModel laundryViewModel;
    private RoomListAdapter adapter;

    private ProgressBar progressBar;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        Log.d(TAG, "onCreate: Activity가 생성되었습니다.");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("세탁실 관리");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Log.d(TAG, "onCreate: Toolbar가 설정되었습니다.");

        progressBar = findViewById(R.id.progressBar);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRooms);
        FloatingActionButton fabAddRoom = findViewById(R.id.fabAddRoom);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new RoomListAdapter();
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "onCreate: UI 요소 및 RecyclerView 어댑터가 초기화되었습니다.");

        // --- ▼▼▼▼▼ [핵심 수정] Kotlin 파일들과 동일한 방식으로 ViewModel을 생성합니다. ▼▼▼▼▼ ---
        // 1. ViewModelFactory를 생성할 때 Application만 전달합니다.
        //    이제 Factory가 내부에서 Repository 등 모든 것을 알아서 만듭니다.
        LaundryViewModelFactory factory = new LaundryViewModelFactory(getApplication());

        // 2. 생성된 Factory를 사용하여 ViewModel 인스턴스를 가져옵니다.
        laundryViewModel = new ViewModelProvider(this, factory).get(LaundryViewModel.class);
        // --- ▲▲▲▲▲ [핵심 수정] ▲▲▲▲▲ ---

        Log.d(TAG, "onCreate: LaundryViewModel이 준비되었습니다. 데이터 구독을 시작합니다.");

        // 로컬 DB의 모든 세탁실 목록을 관찰(observe)하고, 변경이 생기면 UI를 업데이트합니다.
        // Kotlin의 'allLaundryRooms' 속성은 Java에서 'getAllLaundryRooms()' 메서드로 접근합니다.
        laundryViewModel.getAllLaundryRooms().observe(this, new Observer<List<LaundryRoom>>() {
            @Override
            public void onChanged(@Nullable final List<LaundryRoom> rooms) {
                Log.d(TAG, "onChanged: LiveData 변경 감지됨.");
                progressBar.setVisibility(View.GONE);

                if (rooms != null && !rooms.isEmpty()) {
                    Log.d(TAG, "onChanged: DB로부터 " + rooms.size() + "개의 세탁실 목록을 받아 어댑터를 업데이트합니다.");
                    adapter.setRooms(rooms);
                    textViewEmpty.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "onChanged: DB 데이터가 비어있어 '등록된 세탁실 없음' 메시지를 표시합니다.");
                    adapter.setRooms(null);
                    textViewEmpty.setVisibility(View.VISIBLE);
                }
            }
        });

        adapter.setOnItemClickListener(room -> {
            Log.d(TAG, "onItemClick: [" + room.getRoomName() + "] (ID: " + room.getRoomId() + ") 아이템이 클릭되었습니다.");
            Intent intent = new Intent(RoomListActivity.this, MachinListActivity.class);
            intent.putExtra("ROOM_ID", room.getRoomId());
            intent.putExtra("ROOM_NAME", room.getRoomName());
            Log.d(TAG, "onItemClick: MachinListActivity로 이동을 시작합니다. (전달 데이터: ID=" + room.getRoomId() + ", 이름=" + room.getRoomName() + ")");
            startActivity(intent);
        });

        fabAddRoom.setOnClickListener(v -> {
            Log.d(TAG, "onClick: FAB(세탁실 추가) 버튼이 클릭되었습니다.");
            Toast.makeText(this, "신규 세탁실 등록 기능 구현 예정", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
