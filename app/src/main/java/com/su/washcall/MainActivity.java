// C:/Users/eclipseuser/AndroidStudioProjects/washcall/app/src/main/java/com/su/washcall/MainActivity.java

package com.su.washcall;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.su.washcall.network.ApiService;
import com.su.washcall.network.RetrofitClient;
// ★★★ [수정] 회원가입에 필요한 RegisterRequest를 import 합니다. ★★★
import com.su.washcall.network.user.RegisterRequest;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity_API_Test";
    private ApiService apiService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrofit 인스턴스 초기화
        apiService = RetrofitClient.INSTANCE.getInstance();

        // --- 테스트를 위한 회원가입 자동 호출 코드 ---
        executorService.execute(() -> {
            Log.d(TAG, "FastAPI 회원가입 테스트 시작...");
            try {
                // 중복되지 않는 고유한 학번을 만들기 위한 임시 코드
                // 예: 1 + (현재 시간의 마지막 4자리) = 1xxxx
                String uniqueIdString = "1" + System.currentTimeMillis() % 10000;
                int uniqueUserId = Integer.parseInt(uniqueIdString);

                // ▼▼▼▼▼ [핵심 오류 수정] ▼▼▼▼▼
                // LoginRequest가 아닌, 서버가 요구하는 RegisterRequest 객체를 생성합니다.
                // 4개의 인자: 이름, 비밀번호, 역할(일반 사용자=false), 학번
                RegisterRequest registerData = new RegisterRequest(
                        "테스트유저" + uniqueUserId, // user_username
                        "testpassword123",      // user_password
                        true,                  // user_role
                        uniqueUserId            // user_snum
                );
                // ▲▲▲▲▲ [핵심 오류 수정] ▲▲▲▲▲

                // 이제 register 메서드에 올바른 타입의 객체가 전달됩니다.
                Response<Void> response = apiService.register(registerData).execute();

                // UI 스레드에서 결과 로깅
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "✅ 테스트 회원가입 성공! HTTP " + response.code());
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            // 오류 무시
                        }
                        Log.e(TAG, "❌ 테스트 회원가입 실패: HTTP " + response.code() + ", 오류: " + errorBody);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "테스트 회원가입 중 예외 발생", e);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
