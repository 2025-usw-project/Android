package com.example.a2gradeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText; // 이메일/비밀번호 입력을 위해 추가
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

// Room/DB 및 Executor 관련 import
import com.example.a2gradeproject.database.AppDatabase;
import com.example.a2gradeproject.database.User;
import com.example.a2gradeproject.database.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // 백그라운드 작업을 위해 필요

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity_DB";

    private EditText editEmail, editPassword; // 이메일/비밀번호 입력 필드
    private Button btnSignUp;

    // DB 작업을 위한 스레드 풀 (ExecutorService 사용)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. UI 요소 연결 (XML ID에 맞춰주세요!)
        editEmail = findViewById(R.id.editEmail);     // ◀ XML에 이 ID가 있어야 함
        editPassword = findViewById(R.id.editPassword); // ◀ XML에 이 ID가 있어야 함
        btnSignUp = findViewById(R.id.btnSignUp);

        // 2. DB 인스턴스 및 DAO 가져오기
        final AppDatabase db = AppDatabase.Companion.getInstance(getApplicationContext());
        final UserDao userDao = db.userDao();

        btnSignUp.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. 회원가입 로직 (백그라운드 스레드에서 실행)
            executorService.execute(() -> {
                // 새로운 사용자 객체 생성 (기본값: 일반 사용자 false)
                // NOTE: 자바에서 Kotlin Data Class 생성자를 호출합니다.
                final User newUser = new User(email, password, false);

                try {
                    userDao.registerUser(newUser); // DB에 사용자 정보 삽입

                    // DB 작업 후 UI 업데이트는 메인 스레드에서 (runOnUiThread 사용)
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

    // 💡 Activity 종료 시 Executor를 종료하여 메모리 누수를 방지합니다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}