// 경로: C:/Users/eclipseuser/AndroidStudioProjects/washcall/app/src/main/java/com/su/washcall/LoginActivity.java

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

        // 3. 로그인 버튼 클릭 리스너 설정
        btnLogin.setOnClickListener(v -> {
            String userIdStr = editUserId.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (userIdStr.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "학번과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int userIdInt = Integer.parseInt(userIdStr);
                // 로그인 요청 실행
                performLogin(userIdInt, password);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "학번은 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. 회원가입 버튼 클릭 리스너 설정
        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    /**
     * Retrofit을 사용하여 서버에 로그인 요청을 보냅니다. (비동기 방식)
     *
     * @param userId   사용자 학번 (int)
     * @param password 사용자 비밀번호 (String)
     */
    private void performLogin(int userId, String password) {
        // ① 요청 데이터 생성
        LoginRequest loginData = new LoginRequest(userId, password);

        // ② API 호출 (비동기 방식 enqueue 사용)
        apiService.login(loginData).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                // ③ 응답 처리
                if (response.isSuccessful() && response.body() != null) {
                    // ✅ 로그인 성공
                    LoginResponse loginResponse = response.body();

                    // ▼▼▼ [핵심 수정] 서버 응답이 변경됨에 따라 토큰만 가져옵니다. ▼▼▼
                    String token = loginResponse.getAccessToken(); // 'role' 관련 코드는 모두 제거됨
                    Log.d(TAG, "로그인 성공! 수신된 토큰: " + token);

                    // 암호화된 SharedPreferences에 토큰 저장
                    saveToken(token);
                    // ▲▲▲ [핵심 수정] ▲▲▲

                    Toast.makeText(LoginActivity.this, "✅ 로그인 성공!", Toast.LENGTH_SHORT).show();

                    // MainActivity로 이동
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // 로그인 화면으로 돌아오지 않도록 종료

                } else {
                    // ❌ 로그인 실패 (서버 응답 오류, 예: 401 Unauthorized)
                    Log.e(TAG, "로그인 실패: HTTP " + response.code());
                    Toast.makeText(LoginActivity.this, "학번 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // ❌ 네트워크 통신 자체 실패
                Log.e(TAG, "네트워크 통신 오류", t);
                Toast.makeText(LoginActivity.this, "❌ 네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 암호화된 SharedPreferences에 JWT 토큰을 저장하는 메서드
     *
     * @param token 저장할 JWT 토큰
     */
    private void saveToken(String token) {
        try {
            MasterKey masterKey = new MasterKey.Builder(getApplicationContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    getApplicationContext(),
                    "auth_prefs", // 암호화된 SharedPreferences 파일 이름
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("jwt_token", token);
            editor.apply();

            Log.d(TAG, "토큰이 암호화되어 안전하게 저장되었습니다.");

        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "토큰 저장 실패", e);
            // 사용자에게 오류를 알리거나, 일반 SharedPreferences를 사용하는 등의 예비 방안을 고려할 수 있습니다.
            Toast.makeText(this, "보안 저장소에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
