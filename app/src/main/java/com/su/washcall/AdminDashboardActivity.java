//package com.su.washcall;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.InputType;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class AdminDashboardActivity extends AppCompatActivity {
//
//    private TextView tvSummary;
//    private TextView tvMachineCount;
//    private LinearLayout layoutMachines;
//    private int washingMachineCount = 0;
//    private static final String BASE_URL = "http://192.168.0.5:8000/"; // 서버 IP로 수정
//
//    private final List<Integer> machineValues = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_dashboard);
//
//        Button btnCalibration = findViewById(R.id.btnCalibration);
//        Button btnLogs = findViewById(R.id.btnLogs);
//        tvSummary = findViewById(R.id.tvSummary);
//        tvMachineCount = findViewById(R.id.tvMachineCount);
//        layoutMachines = findViewById(R.id.layoutMachines);
//
//        // SharedPreferences에서 기존 값 불러오기
//        SharedPreferences prefs = getSharedPreferences("MachinePrefs", MODE_PRIVATE);
//        washingMachineCount = prefs.getInt("machine_count", 0);
//        if (washingMachineCount > 0) {
//            updateMachineCountDisplay();
//            generateMachineBoxes(washingMachineCount);
//        }
//
//        tvMachineCount.setOnClickListener(v -> showMachineCountDialog());
//        btnCalibration.setOnClickListener(v ->
//                startActivity(new android.content.Intent(this, CalibrationActivity.class)));
//        btnLogs.setOnClickListener(v ->
//                startActivity(new android.content.Intent(this, WashingDetailActivity.class)));
//    }
//
//    private void showMachineCountDialog() {
//        final String[] counts = {"1대", "2대", "3대", "4대", "5대", "6대", "7대", "8대", "9대", "10대"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("세탁기 개수를 선택하세요")
//                .setItems(counts, (dialog, which) -> {
//                    washingMachineCount = which + 1;
//
//                    SharedPreferences prefs = getSharedPreferences("MachinePrefs", MODE_PRIVATE);
//                    prefs.edit().putInt("machine_count", washingMachineCount).apply();
//
//                    updateMachineCountDisplay();
//                    generateMachineBoxes(washingMachineCount);
//                })
//                .setNegativeButton("취소", null)
//                .show();
//    }
//
//    private void updateMachineCountDisplay() {
//        tvMachineCount.setText("현재 세탁기: " + washingMachineCount + "대");
//        tvSummary.setText("현재 세탁기 수: " + washingMachineCount + "대 관리 중입니다.");
//    }
//
//    private void generateMachineBoxes(int count) {
//        layoutMachines.removeAllViews();
//        machineValues.clear();
//
//        for (int i = 1; i <= count; i++) {
//            LinearLayout box = new LinearLayout(this);
//            box.setOrientation(LinearLayout.HORIZONTAL);
//            box.setPadding(16, 16, 16, 16);
//            box.setBackgroundResource(R.drawable.rounded_white_box);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            params.setMargins(0, 8, 0, 8);
//            box.setLayoutParams(params);
//
//            TextView tvName = new TextView(this);
//            tvName.setText("세탁기 " + i + "번: 값 없음");
//            tvName.setTextSize(16);
//            tvName.setTextColor(0xFF000000);
//            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
//
//            Button btnInput = new Button(this);
//            btnInput.setText("입력");
//            btnInput.setBackgroundTintList(getColorStateList(R.color.purple_500));
//            btnInput.setTextColor(0xFFFFFFFF);
//
//            int machineIndex = i;
//            btnInput.setOnClickListener(v -> showInputDialog(machineIndex, tvName));
//
//            box.addView(tvName);
//            box.addView(btnInput);
//            layoutMachines.addView(box);
//        }
//    }
//
//    private void showInputDialog(int machineIndex, TextView tvName) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("세탁기 " + machineIndex + "번 값 입력");
//
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_NUMBER);
//        input.setHint("숫자를 입력하세요");
//        builder.setView(input);
//
//        builder.setPositiveButton("확인", (dialog, which) -> {
//            String value = input.getText().toString();
//            if (!value.isEmpty()) {
//                int num = Integer.parseInt(value);
//                tvName.setText("세탁기 " + machineIndex + "번: " + num);
//                sendMachineToServer(machineIndex, num);
//            }
//        });
//
//        builder.setNegativeButton("취소", null);
//        builder.show();
//    }
//
//    /**
//     * 서버로 세탁기 데이터 전송
//     */
//    private void sendMachineToServer(int id, int value) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        MachineApi api = retrofit.create(MachineApi.class);
//        MachineRequest request = new MachineRequest(id, value);
//
//        api.registerMachine(request).enqueue(new Callback<MachineResponse>() {
//            @Override
//            public void onResponse(Call<MachineResponse> call, Response<MachineResponse> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(AdminDashboardActivity.this, "세탁기 " + id + "번 등록 완료", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(AdminDashboardActivity.this, "등록 실패", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MachineResponse> call, Throwable t) {
//                Toast.makeText(AdminDashboardActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
