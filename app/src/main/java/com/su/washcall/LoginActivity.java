package com.su.washcall; // ◀ 요청하신 패키지 이름

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey; // MasterKey 클래스
import android.content.SharedPreferences;

// Retrofit/Network 관련 import (패키지 경로 확인!)
import com.su.washcall.network.ApiService;
import com.su.washcall.network.RetrofitClient;
import com.su.washcall.network.model.LoginRequest;
import com.su.washcall.network.model.LoginResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response; // Retrofit Response는 필수

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity_API";

    // UI 요소
    private EditText editUserId;
    private EditText editPassword;
    private Button btnLogin, btnSignUp;

    // 백그라운드 작업을 위한 ExecutorService (자바 표준 비동기 처리)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ApiService apiService; // Retrofit 서비스 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Retrofit 인스턴스 가져오기
        apiService = RetrofitClient.INSTANCE.getInstance();

        // 2. UI 요소 연결 (XML ID가 일치해야 합니다.)
        editUserId = findViewById(R.id.editUserId); // ID(학번) 입력 필드
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        // 3. 로그인 버튼 클릭 이벤트 핸들링
        btnLogin.setOnClickListener(v -> {
            // 사용자 입력 값 가져오기
            String userId = editUserId.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "학번과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. 네트워크 통신: 백그라운드 스레드에서 API 호출
            executorService.execute(() -> {
                try {
                    // API 호출에 사용할 데이터 객체 생성
                    LoginRequest loginData = new LoginRequest(userId, password);

                    // Retrofit 동기 호출 (백그라운드 스레드에서만 가능)
                    Response<LoginResponse> response = apiService.login(loginData).execute();

                    // 5. UI 스레드에서 결과 처리 (Toast, 화면 이동 등)
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            // ✅ API 호출 성공 (HTTP 200-299)
                            LoginResponse loginResponse = response.body();
                            String token = loginResponse.getAccess_token();
                            String role = loginResponse.getUser_role();

                            Log.d(TAG, "로그인 성공! 토큰: " + token.substring(0, 10) + "..., 역할: " + role);

                            // [중요] 토큰을 EncryptedSharedPreferences에 저장
                            saveToken(token);

                            // 역할에 따른 화면 분기
                            Intent intent;
                            // 관리자 역할(ADMIN)이면 AdminDashboardActivity로 이동
                            if ("ADMIN".equalsIgnoreCase(role)) {
                                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                            }
                            // 그 외(일반 사용자)는 MainActivity 또는 온보딩 화면으로 이동
                            else {
                                // 일반 사용자 온보딩 과정을 거치도록 OnboardingSimpleActivity로 이동
                                intent = new Intent(LoginActivity.this, OnboardingSimpleActivity.class);
                            }
                            startActivity(intent);
                            finish(); // 로그인 화면 닫기

                        } else {
                            // ❌ API 호출 실패 (HTTP 4xx, 5xx 등)
                            String errorMsg = "로그인 실패: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    // 오류 본문이 있다면 로그에 상세 기록
                                    Log.w(TAG, errorMsg + ", 오류 본문: " + response.errorBody().string());
                                }
                            } catch (Exception e) {
                                // 오류 본문 파싱 실패는 무시
                            }
                            Toast.makeText(LoginActivity.this, "학번 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    // ❌ 네트워크 연결 오류 등 예외 발생
                    Log.e(TAG, "로그인 API 호출 오류", e);
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "❌ 네트워크 오류: 서버 주소 또는 상태 확인", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });

        // 6. 회원가입 버튼 클릭 이벤트
        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    // --- 토큰 저장 로직 ---
    /**
     * JWT 토큰을 암호화된 Shared Preferences에 안전하게 저장합니다.
     * @param token 서버에서 받은 JWT 토큰 문자열
     */
    private void saveToken(String token) {
        try {
            // [수정] MasterKey.Builder를 사용하여 MasterKey 객체를 생성합니다.
            MasterKey masterKey = new MasterKey.Builder(getApplicationContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // 2. create 함수의 세 번째 인자로 MasterKey 객체를 전달합니다.
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    getApplicationContext(),
                    "auth_prefs", // 저장소 파일 이름
                    masterKey, // ◀ String 대신 MasterKey 객체를 전달
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // 3. 토큰을 저장합니다. (나머지 코드는 유지)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("jwt_token", token);
            editor.apply();

            Log.d(TAG, "토큰 저장 성공");

        } catch (Exception e) {
            Log.e(TAG, "EncryptedSharedPreferences 생성 또는 토큰 저장 실패", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Activity 종료 시 ExecutorService도 종료하여 스레드 누수 방지
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}