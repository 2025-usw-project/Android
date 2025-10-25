// 경로: app/src/main/java/com/su/washcall/LoginActivity.java
package com.su.washcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.su.washcall.network.ApiService;
import com.su.washcall.network.RetrofitClient;
import com.su.washcall.network.model.LoginRequest;
import com.su.washcall.network.model.LoginResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity_API";

    private EditText editUserId, editPassword;
    private Button btnLogin, btnSignUp;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Retrofit 서비스 초기화
        apiService = RetrofitClient.INSTANCE.getInstance();

        // 2. UI 요소 연결
        editUserId = findViewById(R.id.editUserId);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        // 3. 로그인 버튼
        btnLogin.setOnClickListener(v -> {
            String userIdStr = editUserId.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (userIdStr.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "학번과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int userIdInt = Integer.parseInt(userIdStr);
                performLogin(userIdInt, password);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "학번은 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. 회원가입 버튼
        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    /**
     * 🔹 서버 로그인 요청 (Retrofit)
     * 서버 꺼짐/실패 시 로컬 로그인으로 전환
     */
    private void performLogin(int userId, String password) {
        LoginRequest loginData = new LoginRequest(userId, password);

        apiService.login(loginData).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    String token = loginResponse.getAccessToken();
                    Log.d(TAG, "로그인 성공! 수신된 토큰: " + token);
                    saveToken(token);

                    Toast.makeText(LoginActivity.this, "✅ 로그인 성공!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, OnboardingSimpleActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.w(TAG, "서버 로그인 실패, HTTP " + response.code() + " → 로컬 로그인 시도");
                    Toast.makeText(LoginActivity.this, "서버 응답 실패 — 로컬 로그인으로 전환합니다.", Toast.LENGTH_SHORT).show();
                    attemptLocalLogin(String.valueOf(userId), password);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ 네트워크 통신 오류 → 로컬 로그인 시도", t);
                Toast.makeText(LoginActivity.this, "네트워크 오류 — 로컬 로그인 시도 중...", Toast.LENGTH_SHORT).show();
                attemptLocalLogin(String.valueOf(userId), password);
            }
        });
    }

    /**
     * 🔹 서버 없이 로컬 계정 로그인 허용
     */
    private void attemptLocalLogin(String userIdStr, String password) {
        // ⚙️ 개발 모드 감지
        if (isDebugMode()) {
            Toast.makeText(this, "🔧 디버그 모드: 로그인 통과", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OnboardingSimpleActivity.class));
            finish();
            return;
        }

        // ✅ 테스트용 하드코딩 계정
        if (userIdStr.equals("1234") && password.equals("1234")) {
            Toast.makeText(this, "✅ 로컬 로그인 성공 (일반 사용자)", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OnboardingSimpleActivity.class));
            finish();
            return;
        }

        if (userIdStr.equals("12345") && password.equals("12345")) {
            Toast.makeText(this, "✅ 로컬 로그인 성공 (관리자)", Toast.LENGTH_SHORT).show();
            Intent admin = new Intent(this, AdminDashboardActivity.class);
            startActivity(admin);
            finish();
            return;
        }

        Toast.makeText(this, "❌ 로컬 로그인 실패 — 서버 연결 불가 또는 잘못된 계정", Toast.LENGTH_LONG).show();
    }

    /**
     * 🔹 디버그 모드 여부 확인 (BuildConfig 대체)
     */
    private boolean isDebugMode() {
        try {
            return (getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 🔹 암호화 SharedPreferences에 JWT 토큰 저장
     */
    private void saveToken(String token) {
        try {
            MasterKey masterKey = new MasterKey.Builder(getApplicationContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    getApplicationContext(),
                    "auth_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            sharedPreferences.edit().putString("jwt_token", token).apply();
            Log.d(TAG, "토큰이 암호화되어 안전하게 저장되었습니다.");
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "토큰 저장 실패", e);
            Toast.makeText(this, "보안 저장소에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
