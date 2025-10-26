//package com.su.washcall;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class WashingDetailActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_washing_detail);
//
//        TextView tvCardTitle = findViewById(R.id.tvCardTitle);
//        TextView tvCardBody = findViewById(R.id.tvCardBody);
//        Button btnExit = findViewById(R.id.btnExit);
//
//        // 예시 로그 데이터 표시
//        tvCardTitle.setText("세탁기 #1 상세 로그");
//        tvCardBody.setText(
//                "- 완료 시간: 14:32\n" +
//                        "- 소요 시간: 42분\n" +
//                        "- 최대 진동값: 5.2g\n" +
//                        "- 평균 진동값: 2.8g\n\n" +
//                        "진동 지문(요약):\n" +
//                        "  • 예열 2분 → 고속세탁 30분 → 탈수 10분\n" +
//                        "  • 이상치 감지 없음\n"
//        );
//
//        // 🔹 나가기 버튼 클릭 → 관리자 대시보드로 이동
//        btnExit.setOnClickListener(v -> {
//            Intent intent = new Intent(this, AdminDashboardActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//            finish();
//        });
//    }
//}
