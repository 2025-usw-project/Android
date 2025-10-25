package com.su.washcall; // ◀ 패키지 이름 확인

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Retrofit/Network 관련 import
import com.su.washcall.network.ApiService;
import com.su.washcall.network.RetrofitClient;
import com.su.washcall.network.model.LoginRequest;
import com.su.washcall.network.model.LoginResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit; // 시간 제한을 위해 추가 (선택 사항)

import retrofit2.Response; // Retrofit Response import

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity_API_Test";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Retrofit 서비스 객체 가져오기
        final ApiService apiService = RetrofitClient.INSTANCE.getInstance();

        // 2. [API TEST] 백그라운드 스레드에서 로그인 API 호출 시도
        executorService.execute(() -> {
            Log.d(TAG, "FastAPI 로그인 테스트 시작...");

            try {
                // 3. 테스트 로그인 데이터 생성 (서버에 미리 등록된 ID/PW가 필요합니다!)
                LoginRequest loginData = new LoginRequest("test_user_id", "test_password");

                // 4. API 호출 및 응답 받기
                // .execute()는 동기 호출이며, ExecutorService 스레드에서 실행되므로 안전합니다.
                Response<LoginResponse> response = apiService.login(loginData).execute();

                // 5. UI 스레드에서 결과 처리 (Logcat 출력)
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        // HTTP 200-299 성공
                        LoginResponse body = response.body();
                        Log.i(TAG, "✅ API 통신 성공!");
                        Log.i(TAG, "토큰: " + body.getAccess_token().substring(0, 10) + "...");
                        Log.i(TAG, "역할: " + body.getUser_role());
                        Toast.makeText(this, "API 연결 성공!", Toast.LENGTH_LONG).show();

                    } else {
                        // HTTP 4xx, 5xx 실패 (서버 오류, 인증 실패 등)
                        Log.e(TAG, "❌ API 통신 실패: HTTP " + response.code());
                        Log.e(TAG, "오류 본문: " + response.errorBody().toString());
                        Toast.makeText(this, "API 통신 실패! 서버 상태 확인 필요.", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                // 네트워크 연결 오류, JSON 파싱 오류 등 예외 발생
                Log.e(TAG, "❌ 네트워크 호출 중 예외 발생!", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "❌ 네트워크 오류: 서버 주소 확인", Toast.LENGTH_LONG).show()
                );
            }
        });

        // ... (다른 onCreate 코드는 그대로 유지) ...
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Executor 종료 (메모리 누수 방지)
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow(); // 강제 종료
        }
    }
}