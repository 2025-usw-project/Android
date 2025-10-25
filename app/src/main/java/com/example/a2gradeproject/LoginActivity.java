package com.example.a2gradeproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Room/DB 및 Executor 관련 import
import com.example.a2gradeproject.database.AppDatabase;
import com.example.a2gradeproject.database.User;
import com.example.a2gradeproject.database.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // 백그라운드 작업을 위해 필요

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity_DB";

    private EditText editEmail, editPassword;
    private Button btnLogin, btnSignUp;
    // DB 작업을 위한 스레드 풀. 메모리 누수 방지를 위해 onDestroy에서 종료해야 합니다.
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. UI 요소 연결
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        // 2. DB 인스턴스 및 DAO 가져오기
        final AppDatabase db = AppDatabase.Companion.getInstance(getApplicationContext());
        final UserDao userDao = db.userDao();

        // 3. 로그인 버튼 클릭 이벤트 (하드코딩 로직을 DB 로직으로 대체)
        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. 로그인 인증 로직 (백그라운드 스레드에서 실행)
            executorService.execute(() -> {
                try {
                    // DB에서 이메일과 비밀번호가 일치하는 사용자 조회
                    final User user = userDao.loginUser(email, password);

                    // DB 작업 후 UI 업데이트는 반드시 메인 스레드에서
                    runOnUiThread(() -> {
                        if (user != null) {
                            // 로그인 성공!
                            Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                            // 5. 역할(Role)에 따른 화면 분기
                            Intent intent;
                            if (user.isAdmin()) {
                                // 관리자 대시보드로 이동
                                Log.d(TAG, "관리자 로그인: " + user.getEmail());
                                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                            } else {
                                // 일반 사용자 메인 화면으로 이동
                                Log.d(TAG, "일반 사용자 로그인: " + user.getEmail());
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            }
                            startActivity(intent);
                            finish();

                        } else {
                            // 인증 실패 (사용자 정보가 없거나 비밀번호 불일치)
                            Toast.makeText(LoginActivity.this, "인증 실패: 이메일 또는 비밀번호 불일치.", Toast.LENGTH_LONG).show();
                            editEmail.setError("확인 필요");
                            editPassword.setError("확인 필요");
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "로그인 DB 조회 중 오류 발생", e);
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "❌ DB 조회 오류. 잠시 후 다시 시도하세요.", Toast.LENGTH_LONG).show()
                    );
                }
            });
        });

        // 6. 회원가입 버튼 클릭 이벤트 (기존과 동일)
        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    // 7. 메모리 누수 방지: Activity 종료 시 ExecutorService 종료
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}