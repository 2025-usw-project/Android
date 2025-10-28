//package com.su.washcall.test;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//// [가라 코드] 실제 서버 통신 클래스는 import 하지만 사용하지 않음
//// import com.su.washcall.network.ApiService;
//// import com.su.washcall.network.RetrofitClient;
//
//import com.su.washcall.R;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random; // [가라 코드] 랜덤 생성을 위해 import
//
//// [가라 코드] 실제 서버 통신 클래스는 import 하지만 사용하지 않음
//// import retrofit2.Call;
//// import retrofit2.Callback;
//// import retrofit2.Response;
//
//public class UserMachineListActivity extends AppCompatActivity {
//
//    private final String TAG = "MachineListActivity_GARA"; // [가라 코드] 로그 태그 변경
//    private Toolbar toolbar;
//    private RecyclerView recyclerViewMachines;
//    private ProgressBar progressBar;
//    private TextView textViewEmpty;
//
//    private int roomId = -1;
//
//    // [수정] 가라(임시) 데이터와 주기적 업데이트를 위한 변수
//    // private ApiService apiService; // 실제 서버 통신 안 하므로 주석 처리
//    private MachineAdapter adapter;
//    private List<Machine> machineList = new ArrayList<>();
//    private Handler handler = new Handler(Looper.getMainLooper());
//    private Runnable fetcher; // 가라 데이터를 주기적으로 업데이트하는 객체
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_machine_list);
//
//        roomId = getIntent().getIntExtra("ROOM_ID", -1);
//        if (roomId == -1) {
//            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // apiService = RetrofitClient.INSTANCE.getInstance(); // 실제 서버 통신 안 하므로 주석 처리
//
//        initViews();
//        setupToolbar();
//        setupRecyclerView();
//        startFetchingGaraData(); // [가라 코드] 실제 서버 대신 가라 데이터 가져오기 시작
//    }
//
//    // UI 요소 초기화 (수정 없음)
//    private void initViews() {
//        toolbar = findViewById(R.id.toolbar);
//        recyclerViewMachines = findViewById(R.id.recyclerViewMachines);
//        progressBar = findViewById(R.id.progressBar);
//        textViewEmpty = findViewById(R.id.textViewEmpty);
//    }
//
//    // 툴바 설정 (수정 없음)
//    private void setupToolbar() {
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(roomId + "번 세탁실 (테스트 모드)"); // [가라 코드] 제목에 테스트 모드 표시
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//    }
//
//    // RecyclerView 설정 (수정 없음)
//    private void setupRecyclerView() {
//        adapter = new MachineAdapter(machineList);
//        recyclerViewMachines.setLayoutManager(new LinearLayoutManager(this));
//        recyclerViewMachines.setAdapter(adapter);
//    }
//
//    // ▼▼▼▼▼ [핵심] 실제 서버 통신 대신 가라 데이터를 만드는 로직 ▼▼▼▼▼
//    private void startFetchingGaraData() {
//        progressBar.setVisibility(View.VISIBLE);
//
//        // --- 1. 최초 가라 데이터 4개 생성 ---
//        machineList.clear();
//        machineList.add(new Machine(1, "가라-세탁기 1", "AVAILABLE"));
//        machineList.add(new Machine(2, "가라-세탁기 2", "RUNNING"));
//        machineList.add(new Machine(3, "가라-세탁기 3", "FINISHED"));
//        machineList.add(new Machine(4, "가라-세탁기 4", "AVAILABLE"));
//        adapter.updateData(machineList); // 어댑터에 데이터 반영 및 새로고침
//
//        progressBar.setVisibility(View.GONE);
//        Log.d(TAG, "가라 데이터 생성 완료. 세탁기 4개 표시됨.");
//
//        // --- 2. 5초마다 상태를 랜덤으로 바꾸는 로직 ---
//        fetcher = new Runnable() {
//            @Override
//            public void run() {
//                final String[] statuses = {"AVAILABLE", "RUNNING", "FINISHED"};
//                final Random random = new Random();
//
//                if (!machineList.isEmpty()) {
//                    // 랜덤으로 세탁기 하나와 상태 하나를 고름
//                    int machineIndex = random.nextInt(machineList.size()); // 0~3
//                    int statusIndex = random.nextInt(statuses.length);    // 0~2
//
//                    // 해당 세탁기의 상태를 직접 변경 (실제 서버에서는 이 값을 받아옴)
//                    Machine machineToUpdate = machineList.get(machineIndex);
//                    // DTO가 setter가 없으므로 새 객체를 만들어 교체
//                    machineList.set(machineIndex, new Machine(
//                            machineToUpdate.getId(),
//                            machineToUpdate.getName(),
//                            statuses[statusIndex]
//                    ));
//
//                    // 어댑터에 해당 아이템 하나만 변경되었다고 알려서 효율적으로 새로고침
//                    adapter.notifyItemChanged(machineIndex);
//
//                    Log.d(TAG, machineToUpdate.getName() + " 상태 변경 -> " + statuses[statusIndex]);
//                }
//                // 5초 후에 이 작업을 다시 반복
//                handler.postDelayed(this, 5000);
//            }
//        };
//
//        // 첫 상태 변경 시작 (5초 후)
//        handler.post(fetcher);
//    }
//    // ▲▲▲▲▲ [핵심] 가라 코드 로직 끝 ▲▲▲▲▲
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // [중요] 화면이 종료될 때, 반복되던 작업을 반드시 중지시켜 메모리 누수 방지
//        if (fetcher != null) {
//            handler.removeCallbacks(fetcher);
//        }
//    }
//}
