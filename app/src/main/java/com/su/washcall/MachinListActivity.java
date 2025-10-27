// 파일 경로: app/src/main/java/com/su/washcall/MachinListActivity.java
package com.su.washcall;

// 이제 SharedPreferences나 토큰 관련 로직이 없으므로 import가 깔끔해집니다.
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.su.washcall.adapter.MachineListAdapter;
import com.su.washcall.viewmodel.AdminViewModel;
import com.su.washcall.viewmodel.AdminViewModelFactory;
import com.su.washcall.viewmodel.LaundryViewModel;
import com.su.washcall.viewmodel.LaundryViewModelFactory; // ViewModel 파일 내에 Factory가 있으므로 이 import는 유효합니다.
import com.su.washcall.viewmodel.MachineListResult;

import java.util.List;

public class MachinListActivity extends AppCompatActivity {
    private static final String TAG = "MachinListActivity_LOG";

    private AdminViewModel adminViewModel;
    private LaundryViewModel laundryViewModel;

    private MachineListAdapter adapter;
    private int roomId;
    private String roomName;

    private ProgressBar progressBar;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machin_list);
        Log.d(TAG, "onCreate: Activity 생성됨");

        Intent intent = getIntent();
        roomId = intent.getIntExtra("ROOM_ID", -1);
        roomName = intent.getStringExtra("ROOM_NAME");

        if (roomId == -1) {
            Toast.makeText(this, "세탁실 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // --- (UI 초기화 코드는 이전과 동일) ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(roomName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        progressBar = findViewById(R.id.progressBar);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMachines);
        FloatingActionButton fabAddMachine = findViewById(R.id.fabAddMachine);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new MachineListAdapter();
        recyclerView.setAdapter(adapter);

        // --- (ViewModel 초기화 코드는 이전과 동일) ---
        // 각 ViewModel은 자신의 Factory를 통해 생성됩니다.
        AdminViewModelFactory adminFactory = new AdminViewModelFactory(getApplication());
        adminViewModel = new ViewModelProvider(this, adminFactory).get(AdminViewModel.class);

        LaundryViewModelFactory laundryFactory = new LaundryViewModelFactory(getApplication());
        laundryViewModel = new ViewModelProvider(this, laundryFactory).get(LaundryViewModel.class);
        // --- (ViewModel 초기화 코드 끝) ---

        observeViewModel();
        adminViewModel.loadMachineList(roomId); // 초기 세탁기 목록 로드
        fabAddMachine.setOnClickListener(v -> showAddMachineDialog());
    }

    private void observeViewModel() {
        // (이 함수의 내용은 이전과 동일하게 유지됩니다)
        adminViewModel.getMachineListResult().observe(this, result -> {
            progressBar.setVisibility(View.GONE);
            if (result instanceof MachineListResult.Success) {
                List<com.su.washcall.network.washmachinResponse.MachineInfo> machines = ((MachineListResult.Success) result).getMachines();
                if (machines != null && !machines.isEmpty()) {
                    adapter.setMachines(machines);
                    textViewEmpty.setVisibility(View.GONE);
                } else {
                    adapter.setMachines(null);
                    textViewEmpty.setVisibility(View.VISIBLE);
                }
            } else if (result instanceof MachineListResult.Failure) {
                String errorMessage = ((MachineListResult.Failure) result).getMessage();
                Toast.makeText(this, "오류 발생: " + errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        laundryViewModel.getOperationStatus().observe(this, message -> {
            progressBar.setVisibility(message.equals("요청 중...") ? View.VISIBLE : View.GONE);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            // 세탁기 추가 성공 시, 목록을 다시 불러옵니다.
            if (message.contains("✅")) {
                adminViewModel.loadMachineList(roomId);
            }
        });
    }

    private void showAddMachineDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_machine, null);
        final EditText editTextMachineId = dialogView.findViewById(R.id.editTextMachineId);
        final EditText editTextMachineName = dialogView.findViewById(R.id.editTextMachineName);

        new AlertDialog.Builder(this)
                .setTitle("신규 세탁기 추가")
                .setView(dialogView)
                .setPositiveButton("추가", (dialog, which) -> {
                    String machineIdStr = editTextMachineId.getText().toString();
                    String machineName = editTextMachineName.getText().toString();

                    if (machineIdStr.isEmpty() || machineName.isEmpty()) {
                        Toast.makeText(this, "세탁기 번호와 이름을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int machineId = Integer.parseInt(machineIdStr);

                        // --- ▼▼▼ [최종 수정] Activity는 더 이상 토큰을 신경쓰지 않습니다. ▼▼▼ ---
                        // ViewModel을 호출하기만 하면, ViewModel이 알아서 토큰을 가져와 처리합니다.
                        Log.d(TAG, "laundryViewModel.addDevice 호출 -> roomId: " + roomId + ", machineId: " + machineId);
                        laundryViewModel.addDevice(roomId, machineId, machineName);
                        // --- ▲▲▲ [최종 수정] ▲▲▲ ---

                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "세탁기 번호는 숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // 불필요한 getToken() 함수는 완전히 삭제되었습니다.

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
