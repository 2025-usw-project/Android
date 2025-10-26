package com.su.washcall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.su.washcall.network.ApiService;
import com.su.washcall.network.RetrofitClient;
import com.su.washcall.network.user.AdminRegistrationRequest;
import com.su.washcall.network.user.AdminRegistrationResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRegisterActivity extends AppCompatActivity {
    private static final String TAG = "AdminRegisterActivity";
    private EditText editAdminName, editAdminId, editAdminPassword, editAdminAuthCode;
    private Button btnAdminSignUp;
    private ProgressBar progressBar;
    private ApiService apiService;

    private static final String ADMIN_AUTH_CODE = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_register);

        apiService = RetrofitClient.INSTANCE.getInstance();

        editAdminName = findViewById(R.id.editAdminName);
        editAdminId = findViewById(R.id.editAdminId);
        editAdminPassword = findViewById(R.id.editAdminPassword);
        editAdminAuthCode = findViewById(R.id.editAdminAuthCode);
        btnAdminSignUp = findViewById(R.id.btnAdminSignUp);
        progressBar = findViewById(R.id.progressBar);

        btnAdminSignUp.setOnClickListener(v -> {
            // UI에서 입력받은 값들
            String name = editAdminName.getText().toString().trim(); // 서버의 "user_username"에 해당
            String adminIdStr = editAdminId.getText().toString().trim(); // 서버의 "user_snum"에 해당
            String password = editAdminPassword.getText().toString().trim(); // 서버의 "user_password"에 해당
            String authCode = editAdminAuthCode.getText().toString().trim();

            if (name.isEmpty() || adminIdStr.isEmpty() || password.isEmpty() || authCode.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!authCode.equals(ADMIN_AUTH_CODE)) {
                Toast.makeText(this, "관리자 인증 코드가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int adminIdInt = Integer.parseInt(adminIdStr);
                // 서버 명세에 맞는 파라미터 순서로 함수를 호출합니다.
                performAdminRegistration(name, password, adminIdInt);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "관리자 ID(학번)는 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 서버 명세에 맞게 파라미터 이름을 변경하고, requestData 객체를 생성합니다.
     * @param userName 서버의 "user_username" 필드에 들어갈 값
     * @param userPassword 서버의 "user_password" 필드에 들어갈 값
     * @param userSnum 서버의 "user_snum" 필드에 들어갈 값
     */
    private void performAdminRegistration(String userName, String userPassword, int userSnum) {
        progressBar.setVisibility(View.VISIBLE);
        btnAdminSignUp.setEnabled(false);

        // 서버 명세와 일치하는 데이터 객체 생성 (이제 이 부분에서 오류가 발생하지 않습니다)
        AdminRegistrationRequest requestData = new AdminRegistrationRequest(userName, userPassword, userSnum);

        // Retrofit 호출 부분은 그대로 유지됩니다.
        apiService.registerAdmin(requestData).enqueue(new Callback<AdminRegistrationResponse>() {
            @Override
            public void onResponse(@NonNull Call<AdminRegistrationResponse> call, @NonNull Response<AdminRegistrationResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnAdminSignUp.setEnabled(true);

                // 서버가 "register ok" 메시지와 함께 성공 응답(2xx)을 보냈을 경우
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "관리자 등록 성공: " + response.body().getMessage());
                    Toast.makeText(AdminRegisterActivity.this, "✅ 관리자 등록 성공!", Toast.LENGTH_SHORT).show();

                    // 성공 시 로그인 화면으로 이동
                    Intent intent = new Intent(AdminRegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // 서버가 오류(4xx, 5xx)를 응답했을 경우
                    String errorMsg = "서버 응답 오류";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (IOException e) {
                            errorMsg = "에러 메시지 파싱 실패";
                        }
                    }
                    Log.e(TAG, "관리자 등록 실패: Code=" + response.code() + ", Message=" + errorMsg);
                    Toast.makeText(AdminRegisterActivity.this, "등록 실패: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminRegistrationResponse> call, @NonNull Throwable t) {
                // 네트워크 연결 자체에 실패했을 경우 (인터넷 문제 등)
                progressBar.setVisibility(View.GONE);
                btnAdminSignUp.setEnabled(true);
                Log.e(TAG, "네트워크 통신 오류", t);
                Toast.makeText(AdminRegisterActivity.this, "네트워크에 연결할 수 없습니다. 인터넷을 확인해주세요.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
