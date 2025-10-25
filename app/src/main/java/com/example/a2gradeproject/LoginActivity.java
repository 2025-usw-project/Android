package com.example.a2gradeproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Room/DB 및 Executor 관련 import (UserDao 추가 확인)
import com.example.a2gradeproject.database.AppDatabase;
import com.example.a2gradeproject.database.User;
import com.example.a2gradeproject.database.UserDao; // UserDao 임포트 확인!

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // 백그라운드 작업을 위해 필요

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity_DB";

    private EditText editEmail, editPassword;
    private Button btnLogin, btnSignUp;
    // DB 작업을 위한 스레드 풀
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

        // 3. 로그인 버튼 클릭 이벤트
        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. 로그인 인증 로직 (백그라운드 스레드에서 실행)
            executorService.execute(() -> {
                User user = null; // user 변수를 try 블록 밖에서 선언
                try {
                    // DB에서 이메일과 비밀번호가 일치하는 사용자 조회
                    Log.d(TAG, "DB 조회 시도: email=" + email);
                    user = userDao.loginUser(email, password); // final 제거, try 블록 밖에서 선언된 변수에 할당
                    Log.d(TAG, "DB 조회 결과: " + (user == null ? "NULL" : user.getEmail())); // Getter 사용

                } catch (Exception e) {
                    // DB 조회 자체에서 오류 발생 시
                    Log.e(TAG, "로그인 DB 조회 중 오류 발생", e);
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "❌ DB 조회 오류. 잠시 후 다시 시도하세요.", Toast.LENGTH_LONG).show()
                    );
                    return; // 오류 발생 시 더 이상 진행하지 않음
                }

                // final 변수를 runOnUiThread 밖에서 선언하여 람다에서 접근 가능하게 함
                final User loggedInUser = user;

                // DB 작업 후 UI 업데이트는 반드시 메인 스레드에서
                runOnUiThread(() -> {
                    if (loggedInUser != null) {
                        // 로그인 성공!
                        Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                        // 5. 역할(Role)에 따른 화면 분기 (Getter 사용)
                        Intent intent;
                        if (loggedInUser.isAdmin()) { // <-- Getter 사용 (isAdmin() 또는 isIsAdmin())
                            // 관리자 대시보드로 이동
                            Log.d(TAG, "관리자 로그인: " + loggedInUser.getEmail()); // <-- Getter 사용
                            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        } else {
                            // 일반 사용자 메인 화면으로 이동
                            Log.d(TAG, "일반 사용자 로그인: " + loggedInUser.getEmail()); // <-- Getter 사용
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish(); // 로그인 성공 후 현재 화면 종료

                    } else {
                        // 인증 실패 (사용자 정보가 없거나 비밀번호 불일치)
                        Log.d(TAG, "로그인 실패: 사용자 정보 불일치");
                        Toast.makeText(LoginActivity.this, "인증 실패: 이메일 또는 비밀번호 불일치.", Toast.LENGTH_LONG).show();
                        editEmail.setError("확인 필요");
                        editPassword.setError("확인 필요");
                    }
                }); // runOnUiThread 끝
            }); // executorService.execute 끝
        }); // btnLogin.setOnClickListener 끝

        // 6. 회원가입 버튼 클릭 이벤트
        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    // 7. 메모리 누수 방지: Activity 종료 시 ExecutorService 종료
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}