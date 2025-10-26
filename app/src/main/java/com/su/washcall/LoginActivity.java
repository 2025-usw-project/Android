// 경로: app/src/main/java/com/su/washcall/LoginActivity.java
package com.su.washcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // View import 추가
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.firebase.messaging.FirebaseMessaging;
import com.su.washcall.network.ApiService;
import com.su.washcall.network.RetrofitClient;
import com.su.washcall.network.user.LoginRequest;
import com.su.washcall.network.user.LoginResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private EditText editUserId, editPassword;
    // 1. ✨ btnAdminSignUp 변수 선언 추가
    private Button btnLogin, btnSignUp, btnAdminSignUp;
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
        // 2. ✨ XML의 btnAdminSignUp 버튼과 변수를 연결
        btnAdminSignUp = findViewById(R.id.btnAdminSignUp);

        // 로그인 버튼 클릭 이벤트
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

        // 일반 회원가입 버튼 클릭 이벤트
        btnSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        // 3. ✨ [문제 해결] 관리자 회원가입 버튼 클릭 이벤트 추가
        btnAdminSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AdminRegisterActivity로 이동하는 Intent를 생성하고 실행
                Intent intent = new Intent(LoginActivity.this, AdminRegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // 아래의 나머지 코드는 모두 그대로 유지됩니다. (수정할 필요 없음)

    private void getFcmTokenAndLogin(int userId, String password) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    String fcmToken;
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "FCM 토큰 가져오기 실패", task.getException());
                        fcmToken = "token_fetch_failed";
                        Toast.makeText(getApplicationContext(), "푸시 알림 토큰 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        fcmToken = task.getResult();
                        Log.d(TAG, "FCM Token: " + fcmToken);
                    }
                    performLogin(new WeakReference<>(this), userId, password, fcmToken);
                });
    }

    private void performLogin(WeakReference<LoginActivity> activityRef, int userId, String password, String fcmToken) {
        LoginRequest loginData = new LoginRequest(userId, password, fcmToken);

        apiService.login(loginData).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                LoginActivity activity = activityRef.get();
                if (activity == null || activity.isFinishing()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    String accessToken = response.body().getAccessToken();
                    Log.d(TAG, "서버로부터 받은 Access Token: " + accessToken);
                    activity.saveToken(accessToken);

                    try {
                        JWT jwt = new JWT(accessToken);
                        Claim roleClaim = jwt.getClaim("role");
                        String role = roleClaim.asString();
                        Log.d(TAG, "JWT 해독 성공! 역할(Role): " + role);

                        if ("ADMIN".equals(role)) {
                            Toast.makeText(activity, "✅ 관리자 로그인 성공!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity, AdminDashboardActivity.class);
                            activity.startActivity(intent);
                        } else {
                            Toast.makeText(activity, "✅ 로그인 성공!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity, OnboardingSimpleActivity.class);
                            activity.startActivity(intent);
                        }
                        activity.finish();
                    } catch (Exception e) {
                        Log.e(TAG, "JWT 토큰 해독 중 오류 발생", e);
                        Toast.makeText(activity, "사용자 정보를 확인하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String errorBodyString = "응답 없음";
                    if (response.errorBody() != null) {
                        try {
                            errorBodyString = response.errorBody().string();
                        } catch (IOException e) {
                            errorBodyString = "에러 메시지를 읽는 데 실패했습니다.";
                        }
                    }
                    Log.e(TAG, "로그인 실패: HTTP Code=" + response.code() + ", 서버 응답=" + errorBodyString);
                    Toast.makeText(activity, "로그인에 실패했습니다. 학번과 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                LoginActivity activity = activityRef.get();
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                Log.e(TAG, "네트워크 통신 오류", t);
                Toast.makeText(activity, "네트워크에 연결할 수 없습니다. 인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            }
        });
    }


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

            sharedPreferences.edit().putString("access_token", token).apply();

            Log.d(TAG, "토큰이 암호화되어 안전하게 저장되었습니다: " + token);
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "토큰을 안전하게 저장하는 데 실패했습니다.", e);
            Toast.makeText(getApplicationContext(), "오류: 사용자 정보를 기기에 저장할 수 없습니다.", Toast.LENGTH_LONG).show();
        }
    }
}
