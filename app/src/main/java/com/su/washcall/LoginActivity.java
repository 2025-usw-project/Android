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

// ▼▼▼ [수정 1] Callback<LoginResponse>를 구현(implements)하도록 명시합니다. ▼▼▼
public class LoginActivity extends AppCompatActivity implements Callback<LoginResponse> {

    private final String TAG = "LoginActivity_LOG"; // 로그 태그를 더 명확하게 변경
    private EditText editUserId, editPassword;
    private Button btnLogin, btnSignUp;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Kotlin의 RetrofitClient 싱글톤 인스턴스를 올바르게 가져옵니다.
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
                getFcmTokenAndLogin(userIdInt, password); // 로그인 절차 시작
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
                        fcmToken = "token_fetch_failed"; // 실패 시 대체 토큰
                    } else {
                        fcmToken = task.getResult();
                        Log.d(TAG, "FCM Token: " + fcmToken);
                    }
                    // ▼▼▼ [수정 2] 가져온 fcmToken으로 performLogin 함수를 호출합니다. ▼▼▼
                    performLogin(userId, password, fcmToken);
                });
    }

    // ▼▼▼ [수정 3] 서버에 실제 로그인 요청을 보내는 함수를 정의합니다. ▼▼▼
    private void performLogin(int userId, String password, String fcmToken) {
        LoginRequest loginRequest = new LoginRequest(userId, password, fcmToken);
        // 비동기 방식으로 API를 호출하고, 응답 처리는 onResponse/onFailure 콜백에 위임합니다.
        apiService.login(loginRequest).enqueue(this);
    }

    // ▼▼▼ [수정 4] Callback 인터페이스의 onResponse 메소드를 구현합니다. 여기가 유일한 응답 처리 지점입니다. ▼▼▼
    @Override
    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            try {
                // 1. 서버로부터 받은 토큰
                String receivedToken = response.body().getAccessToken();

                // 2. 암호화된 SharedPreferences 인스턴스 생성
                MasterKey masterKey = new MasterKey.Builder(getApplicationContext())
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                        getApplicationContext(),
                        "auth_prefs", // 모든 ViewModel에서 사용할 파일 이름과 통일
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );

                // 3. 토큰을 "access_token" 키로 저장 (가장 중요한 부분)
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("access_token", receivedToken);
                editor.apply();

                Log.d(TAG, "토큰 저장 성공. Key: access_token");

                // 4. 메인 화면으로 전환
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 로그인 화면 종료

            } catch (GeneralSecurityException | IOException e) {
                Log.e(TAG, "암호화된 SharedPreferences 처리 중 오류 발생", e);
                Toast.makeText(getApplicationContext(), "로그인 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 로그인 실패 처리 (서버가 4xx, 5xx 에러 응답)
            Log.e(TAG, "로그인 실패: " + response.code());
            Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // ▼▼▼ [수정 5] 네트워크 통신 자체에 실패했을 때 호출되는 콜백입니다. ▼▼▼
    @Override
    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
        Log.e(TAG, "로그인 API 통신 실패", t);
        Toast.makeText(getApplicationContext(), "서버와 통신할 수 없습니다.", Toast.LENGTH_SHORT).show();
    }

    // ▼▼▼ [수정 6] 불필요하고 중복되는 onResponse, saveToken 함수를 모두 삭제했습니다. ▼▼▼
}
