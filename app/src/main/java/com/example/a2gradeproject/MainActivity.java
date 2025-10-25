package com.example.a2gradeproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

// Room/DB 관련 import
import com.example.a2gradeproject.database.AppDatabase;
import com.example.a2gradeproject.database.LaundryRoom;
import com.example.a2gradeproject.database.LaundryRoomDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // DB 작업을 위한 스레드 풀 사용

public class MainActivity extends AppCompatActivity {
    // 로그 태그
    private final String TAG = "MainActivity_DB_Test";
    // DB 작업을 위한 싱글 스레드 Executor (코루틴 대신 자바 표준 사용)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. DB 인스턴스 및 DAO 가져오기
        try {
            Log.d(TAG, "DB 인스턴스 가져오기 시도...");
            // Kotlin의 Companion object를 자바에서 가져오는 표준 방식
            final AppDatabase db = AppDatabase.Companion.getInstance(getApplicationContext());
            final LaundryRoomDao dao = db.laundryRoomDao();
            Log.d(TAG, "DB 인스턴스 및 DAO 가져오기 성공.");

            // 2. [임시] 가짜 데이터 삽입 (Executor를 사용해 백그라운드에서 실행)
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Executor 시작됨! 데이터 삽입 시도.");

                    try {
                        // DAO와 DB 인스턴스를 다시 가져오는 대신, 밖에서 가져온 것을 사용합니다.
                        // DB 인스턴스/DAO를 람다 밖에서 final로 선언했으므로, 여기서 사용 가능해야 합니다.

                        // 3. 가짜 세탁실 데이터를 만듭니다. (변수를 여기서 다시 선언)
                        final LaundryRoom mockRoom1 = new LaundryRoom("room_A1", "기숙사 A동 1층", true);
                        final LaundryRoom mockRoom2 = new LaundryRoom("room_B1", "기숙사 B동 1층", false);

                        // DAO는 람다 바깥에서 final로 선언된 것을 사용합니다.
                        // final LaundryRoomDao dao = db.laundryRoomDao(); // 이 줄은 제거하고, 바깥의 'dao' 변수를 사용합니다.

                        // 4. DAO를 통해 DB에 데이터를 삽입합니다.
                        dao.insertOrUpdate(mockRoom1); // 여기서 mockRoom1을 찾을 수 있습니다.
                        dao.insertOrUpdate(mockRoom2);

                        Log.d(TAG, "데이터 삽입 완료! App Inspection 확인.");
                    } catch (Exception dbException) {
                        Log.e(TAG, "데이터 삽입 중 오류 발생!", dbException);
                    }
                }
            });

        } catch (Exception e) {
            // DB 인스턴스 생성 실패 시 (매우 희귀)
            Log.e(TAG, "Room DB 초기화 중 치명적인 오류 발생!", e);
        }

        // 기존 버튼 클릭 리스너 코드 (R.id.btn_alert가 XML에 있다고 가정)
        Button alertButton = findViewById(R.id.btn_alert);
        if (alertButton != null) {
            alertButton.setOnClickListener(view -> {
                Toast.makeText(MainActivity.this, "알림을 받았습니다", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Activity가 종료될 때 Executor를 종료하여 메모리 누수를 방지합니다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}