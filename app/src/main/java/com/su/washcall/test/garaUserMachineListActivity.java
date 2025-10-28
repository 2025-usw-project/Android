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
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.su.washcall.R;
//import com.su.washcall.network.ApiService;
//import com.su.washcall.network.RetrofitClient;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class UserMachineListActivity extends AppCompatActivity {
//
//    private final String TAG = "MachineListActivity";
//    private Toolbar toolbar;
//    private RecyclerView recyclerViewMachines;
//    private ProgressBar progressBar;
//    private TextView textViewEmpty;
//
//    private int roomId = -1;
//
//    // [수정] 서버 통신 및 주기적 업데이트를 위한 변수
//    private ApiService apiService;
//    private MachineAdapter adapter;
//    private List<Machine> machineList = new ArrayList<>();
//    private Handler handler = new Handler(Looper.getMainLooper());
//    private Runnable fetcher; // 서버 데이터를 주기적으로 가져오는 객체
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
//        // [수정] Retrofit 서비스 초기화
//        apiService = RetrofitClient.INSTANCE.getInstance();
//
//        initViews();
//        setupToolbar();
//        setupRecyclerView();
//        startFetchingMachineData(); // [핵심] 서버 데이터 가져오기 시작
//    }
//
//    // UI 요소 초기화
//    private void initViews() {
//        toolbar = findViewById(R.id.toolbar);
//        recyclerViewMachines = findViewById(R.id.recyclerViewMachines);
//        progressBar = findViewById(R.id.progressBar);
//        textViewEmpty = findViewById(R.id.textViewEmpty);
//        // 사용자 화면에서는 FloatingActionButton이 필요 없으므로 관련 코드 제거
//    }
//
//    // 툴바 설정
//    private void setupToolbar() {
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(roomId + "번 세탁실");
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//    }
//
//    // RecyclerView 설정
//    private void setupRecyclerView() {
//        adapter = new MachineAdapter(machineList);
//        recyclerViewMachines.setLayoutManager(new LinearLayoutManager(this));
//        recyclerViewMachines.setAdapter(adapter);
//    }
//
//    // [핵심] 주기적으로 서버에서 세탁기 데이터를 가져오는 로직
//    private void startFetchingMachineData() {
//        fetcher = new Runnable() {
//            @Override
//            public void run() {
//                // 서버에 데이터 요청
//                apiService.getMachinesByRoom(roomId).enqueue(new Callback<List<Machine>>() {
//                    @Override
//                    public void onResponse(@NonNull Call<List<Machine>> call, @NonNull Response<List<Machine>> response) {
//                        progressBar.setVisibility(View.GONE); // 첫 로딩 후에는 프로그레스바 숨김
//
//                        if (response.isSuccessful() && response.body() != null) {
//                            List<Machine> newMachines = response.body();
//                            if (newMachines.isEmpty()) {
//                                recyclerViewMachines.setVisibility(View.GONE);
//                                textViewEmpty.setVisibility(View.VISIBLE);
//                            } else {
//                                recyclerViewMachines.setVisibility(View.VISIBLE);
//                                textViewEmpty.setVisibility(View.GONE);
//                                adapter.updateData(newMachines); // 어댑터의 데이터 교체 및 새로고침
//                                Log.d(TAG, "서버 데이터 수신 및 화면 업데이트 성공. 세탁기 개수: " + newMachines.size());
//                            }
//                        } else {
//                            Log.e(TAG, "서버 응답 실패: " + response.code());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<List<Machine>> call, @NonNull Throwable t) {
//                        progressBar.setVisibility(View.GONE);
//                        Log.e(TAG, "서버 통신 실패", t);
//                        // 네트워크 오류가 지속될 경우 사용자에게 알림
//                    }
//                });
//
//                // 5초 후에 이 작업을 다시 반복
//                handler.postDelayed(this, 5000);
//            }
//        };
//
//        // 첫 데이터 로딩 시작
//        progressBar.setVisibility(View.VISIBLE);
//        handler.post(fetcher);
//    }
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
