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

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.su.washcall.network.ApiService;
import com.su.washcall.network.RetrofitClient;
import com.su.washcall.network.user.LoginRequest;
import com.su.washcall.network.user.LoginResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity_FCM";
    private EditText editUserId, editPassword;
    private Button btnLogin, btnSignUp;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiService = RetrofitClient.INSTANCE.getInstance();
        editUserId = findViewById(R.id.editUserId);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v -> {
            String userIdStr = editUserId.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (userIdStr.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "학번과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int userIdInt = Integer.parseInt(userIdStr);
                // ▼▼▼ [수정] 로그인 함수 호출 지점 ▼▼▼
                getFcmTokenAndLogin(userIdInt, password);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "학번은 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        btnSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    /**
     * 🔹 1. FCM 토큰을 비동기적으로 가져옵니다.
     * 🔹 2. 토큰을 성공적으로 가져오면 performLogin 함수를 호출합니다.
     */
    private void getFcmTokenAndLogin(int userId, String password) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        String fcmToken;
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "FCM 토큰 가져오기 실패", task.getException());
                            // ▼▼▼ [수정] 실패하더라도 null이 아닌 임의의 문자열을 보내야 서버에서 422 에러가 나지 않습니다. ▼▼▼
                            fcmToken = "token_fetch_failed";
                            Toast.makeText(LoginActivity.this, "푸시 알림 토큰 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 새 FCM 등록 토큰 가져오기
                            fcmToken = task.getResult();
                        }

                        Log.d(TAG, "FCM Token to be sent: " + fcmToken);
                        // 토큰을 가져온 후(성공하든 실패하든) 로그인 요청 실행
                        performLogin(userId, password, fcmToken);
                    }
                });
    }

    /**
     * 🔹 서버에 로그인 요청 (Retrofit)
     */
    private void performLogin(int userId, String password, String fcmToken) {
        // ▼▼▼ [핵심] LoginRequest 생성 시 fcmToken을 함께 전달합니다. ▼▼▼
        LoginRequest loginData = new LoginRequest(userId, password, fcmToken);

        apiService.login(loginData).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                // ... (기존 JWT 해독 및 분기 로직은 그대로 유지) ...
                if (response.isSuccessful() && response.body() != null) {
                    String accessToken = response.body().getAccessToken();
                    saveToken(accessToken);
                    try {
                        JWT jwt = new JWT(accessToken);
                        Claim roleClaim = jwt.getClaim("role");
                        String role = roleClaim.asString();

                        Log.d(TAG, "JWT 해독 성공! 역할(Role): " + role);

                        if ("admin".equals(role)) {
                            Toast.makeText(LoginActivity.this, "✅ 관리자 로그인 성공!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, OnboardingSimpleActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "✅ 로그인 성공!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, OnboardingSimpleActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "JWT 토큰 해독 중 오류 발생", e);
                    }
                } else {
                    Log.w(TAG, "서버 로그인 실패, HTTP " + response.code());
                    Toast.makeText(LoginActivity.this, "로그인에 실패했습니다. 학번과 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ 네트워크 통신 오류", t);
                Toast.makeText(LoginActivity.this, "네트워크에 연결할 수 없습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToken(String token) {
        // ... (토큰 저장 로직은 그대로 유지) ...
        try {
            MasterKey masterKey = new MasterKey.Builder(getApplicationContext()).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    getApplicationContext(), "auth_prefs", masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            sharedPreferences.edit().putString("jwt_token", token).apply();
            Log.d(TAG, "토큰이 암호화되어 안전하게 저장되었습니다.");
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "토큰 저장 실패", e);
        }
    }
}
