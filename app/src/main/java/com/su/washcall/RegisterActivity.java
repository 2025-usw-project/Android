// C:/Users/eclipseuser/AndroidStudioProjects/washcall/app/src/main/java/com/su/washcall/RegisterActivity.java

package com.su.washcall;

import android.os.Bundle;import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Retrofit/Network 관련 import
import com.su.washcall.network.ApiService;
import com.su.washcall.network.RetrofitClient;
// ★★★ 로그인 요청이 아닌, 회원가입 요청 모델을 import 합니다. ★★★
import com.su.washcall.network.user.RegisterRequest;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity_API";

    // UI 요소
    private EditText editUserName, editUserId, editPassword;
    private Button btnSignUp;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Retrofit 서비스 초기화
        apiService = RetrofitClient.INSTANCE.getInstance();

        // 2. UI 요소 연결 (XML 레이아웃에 해당 ID가 있어야 합니다)
        editUserName = findViewById(R.id.editUserName); // 이름 입력 필드
        editUserId = findViewById(R.id.editUserId);     // 학번 입력 필드
        editPassword = findViewById(R.id.editPassword); // 비밀번호 입력 필드
        btnSignUp = findViewById(R.id.btnSignUp);

        // 3. 회원가입 버튼 클릭 리스너 설정
        btnSignUp.setOnClickListener(v -> {
            String userName = editUserName.getText().toString().trim();
            String userIdStr = editUserId.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            // 입력 값 검증
            if (userName.isEmpty() || userIdStr.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이름, 학번, 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. 백그라운드 스레드에서 회원가입 요청 실행
            executorService.execute(() -> {
                try {
                    int userIdInt = Integer.parseInt(userIdStr);

                    // ▼▼▼▼▼ [핵심 수정] 서버 요구사항에 맞는 RegisterRequest 객체 생성 ▼▼▼▼▼
                    // 4개의 인자: 이름, 비밀번호, 역할(일반 사용자=false), 학번
                    RegisterRequest registerData = new RegisterRequest(
                            userName,       // user_username
                            password,       // user_password
                            true,          // user_role
                            userIdInt       // user_snum
                    );
                    // ▲▲▲▲▲ [핵심 수정] ▲▲▲▲▲

                    // API 동기 호출
                    Response<Void> response = apiService.register(registerData).execute();

                    // 5. UI 스레드에서 결과 처리
                    runOnUiThread(() -> handleRegisterResponse(response));

                } catch (NumberFormatException e) {
                    // 학번이 숫자가 아닐 경우
                    Log.w(TAG, "학번 형식이 올바르지 않습니다: " + userIdStr);
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "학번은 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    // 네트워크 오류 또는 기타 예외
                    Log.e(TAG, "회원가입 요청 중 예외 발생!", e);
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "❌ 네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    /**
     * API 응답을 처리하는 메서드 (UI 스레드에서 호출됨)
     */
    private void handleRegisterResponse(Response<Void> response) {
        if (response.isSuccessful()) {
            // ✅ 회원가입 성공 (HTTP 2xx)
            Log.d(TAG, "회원가입 성공! HTTP " + response.code());
            Toast.makeText(this, "✅ 회원가입 성공! 로그인 화면으로 이동합니다.", Toast.LENGTH_LONG).show();

            // 성공 시 로그인 화면으로 이동
            // Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            // startActivity(intent);
            finish(); // 현재 액티비티를 종료하여 이전 화면(로그인)으로 돌아가게 함
        } else {
            // ❌ 회원가입 실패 (HTTP 4xx, 5xx)
            String errorMsg;
            if (response.code() == 409) { // 409 Conflict: 이미 존재하는 사용자
                errorMsg = "이미 존재하는 학번입니다.";
            } else {
                errorMsg = "서버 응답 오류 (코드: " + response.code() + ")";
                try {
                    if (response.errorBody() != null) {
                        Log.w(TAG, "회원가입 실패, 오류 본문: " + response.errorBody().string());
                    }
                } catch (IOException e) { /* 오류 본문 파싱 실패는 무시 */ }
            }

            Log.e(TAG, "회원가입 실패: " + errorMsg);
            Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 스레드 풀을 안전하게 종료
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
