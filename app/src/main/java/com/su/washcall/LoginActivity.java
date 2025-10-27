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

// ▼▼▼ [수정 1] JWT 디코딩 라이브러리와 관리자 화면을 import 합니다. ▼▼▼
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.su.washcall.AdminDashboardActivity; // 관리자 화면 경로

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

public class LoginActivity extends AppCompatActivity implements Callback<LoginResponse> {

    private final String TAG = "LoginActivity_LOG";
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
                getFcmTokenAndLogin(userIdInt, password);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "학번은 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void getFcmTokenAndLogin(int userId, String password) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    String fcmToken;
                    if (!task.isSuccessful() || task.getResult() == null) {
                        Log.w(TAG, "FCM 토큰 가져오기 실패", task.getException());
                        fcmToken = "token_fetch_failed";
                    } else {
                        fcmToken = task.getResult();
                        Log.d(TAG, "FCM Token: " + fcmToken);
                    }
                    performLogin(userId, password, fcmToken);
                });
    }

    private void performLogin(int userId, String password, String fcmToken) {
        LoginRequest loginRequest = new LoginRequest(userId, password, fcmToken);
        apiService.login(loginRequest).enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            try {
                String receivedToken = response.body().getAccessToken();

                // 1. 토큰을 암호화하여 저장합니다.
                saveToken(receivedToken);

                // 2. ▼▼▼ [핵심 수정] 토큰을 분석하여 권한에 따라 화면을 분기시킵니다. ▼▼▼
                navigateToNextActivityByRole(receivedToken);

            } catch (GeneralSecurityException | IOException e) {
                Log.e(TAG, "암호화된 SharedPreferences 처리 중 오류 발생", e);
                Toast.makeText(getApplicationContext(), "로그인 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "로그인 실패: " + response.code());
            Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
        Log.e(TAG, "로그인 API 통신 실패", t);
        Toast.makeText(getApplicationContext(), "서버와 통신할 수 없습니다.", Toast.LENGTH_SHORT).show();
    }

    /**
     * 🔹 토큰을 암호화된 SharedPreferences에 저장하는 함수.
     */
    private void saveToken(String token) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(getApplicationContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                getApplicationContext(),
                "auth_prefs", // 파일 이름
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.apply();

        Log.d(TAG, "토큰 저장 성공. Key: access_token");
    }

    /**
     * 🔹 [핵심] 토큰을 디코딩하고 권한(role)에 따라 적절한 Activity로 이동하는 함수.
     */
    private void navigateToNextActivityByRole(String token) {
        Intent intent;
        try {
            // JWT 토큰 디코딩
            JWT jwt = new JWT(token);
            // 'role' 클레임(Claim) 가져오기
            Claim roleClaim = jwt.getClaim("role");
            String role = roleClaim.asString();

            Log.d(TAG, "추출된 사용자 권한: " + role);

            // 'ADMIN' 또는 'USER' 문자열과 비교
            if ("ADMIN".equals(role)) {
                Log.d(TAG, "관리자 확인 -> AdminBoardingActivity로 이동");
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            } else { // 'USER' 또는 그 외의 경우 모두 사용자 화면으로
                Log.d(TAG, "사용자 확인 -> MainActivity로 이동");
                intent = new Intent(LoginActivity.this, MainActivity.class);
            }
        } catch (Exception e) {
            // 토큰 디코딩 실패 등 예외 발생 시 기본 화면으로 이동
            Log.e(TAG, "토큰 처리 중 오류 발생. 기본 화면으로 이동합니다.", e);
            Toast.makeText(this, "세션 정보를 처리하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }

        startActivity(intent);
        finish(); // 로그인 화면 종료
    }
}
