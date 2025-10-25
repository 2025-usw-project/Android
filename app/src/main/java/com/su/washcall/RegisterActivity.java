package com.su.washcall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText; // 입력 필드 사용
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Room/DB 및 Executor 관련 import (UserDao 추가 확인)
import su.database.AppDatabase;
import su.database.User;
import su.database.UserDao; // UserDao 임포트 확인

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // 백그라운드 작업을 위해 필요

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity_DB";

    // [수정] userId 입력 필드 추가
    private EditText editUserId, editEmail, editPassword;
    private Button btnSignUp;

    // DB 작업을 위한 스레드 풀
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. UI 요소 연결 (XML ID에 맞춰주세요!)
        editUserId = findViewById(R.id.editUserId);     // ◀ [추가] XML에 이 ID가 있어야 함
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        // 2. DB 인스턴스 및 DAO 가져오기
        final AppDatabase db = AppDatabase.Companion.getInstance(getApplicationContext());
        final UserDao userDao = db.userDao();

        btnSignUp.setOnClickListener(v -> {
            // [수정] userId 값 가져오기
            String userId = editUserId.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            // [수정] userId도 비어있는지 확인
            if (userId.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "ID, 이메일, 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. 회원가입 로직 (백그라운드 스레드에서 실행)
            executorService.execute(() -> {
                // [수정] userId를 포함하여 User 객체 생성
                final User newUser = new User(userId, email, password, false); // isAdmin은 false로 기본 설정

                try {
                    userDao.registerUser(newUser); // DB에 사용자 정보 삽입

                    // DB 작업 후 UI 업데이트는 메인 스레드에서
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "✅ 회원가입 성공!", Toast.LENGTH_LONG).show();

                        // 4. 회원가입 완료 → 로그인 화면으로 이동
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    Log.e(TAG, "회원가입 DB 오류 발생", e);
                    // DB 오류 발생 시 사용자에게 알림
                    runOnUiThread(() ->
                            Toast.makeText(RegisterActivity.this, "❌ 오류 발생! 다시 시도하세요.", Toast.LENGTH_LONG).show()
                    );
                }
            });
        });
    }

    // Activity 종료 시 Executor를 종료하여 메모리 누수를 방지합니다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}