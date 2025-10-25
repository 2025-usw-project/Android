package com.su.washcall;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// ⚙️ 서버 관련 임포트 (현재 사용 안 함)
// import okhttp3.*;
// import org.json.JSONObject;
// import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layoutMachineContainer;
    private static final String TAG = "MainActivity_LocalMode";

    // ⚙️ 서버 관련 변수 (현재 비활성화)
    // private static final String WS_URL = "ws://192.168.0.5:8000/ws/machines";
    // private WebSocket webSocket;
    // private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutMachineContainer = findViewById(R.id.layoutMachineContainer);

        // ✅ 로컬 테스트용: 세탁기 10대 표시
        generateMachineViews(10);

        // ⚙️ 서버 연결 주석 처리
        // connectWebSocket();

        // ✅ 로컬 모드 시뮬레이션 시작
        simulateMachineStatus();
    }

    /**
     * 세탁기 UI 생성 (10대)
     */
    private void generateMachineViews(int count) {
        layoutMachineContainer.removeAllViews();

        for (int i = 1; i <= count; i++) {
            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.VERTICAL);
            box.setGravity(Gravity.CENTER);
            box.setPadding(16, 16, 16, 16);

            ImageView img = new ImageView(this);
            img.setId(1000 + i);
            img.setImageResource(R.drawable.ic_launcher_foreground);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(150, 150);
            img.setLayoutParams(imgParams);

            TextView tv = new TextView(this);
            tv.setId(2000 + i);
            tv.setText("세탁기 " + i + "번\n상태: 대기중");
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);

            box.addView(img);
            box.addView(tv);
            layoutMachineContainer.addView(box);
        }
    }

    /**
     * ⚙️ 서버 없이 상태를 순환 시뮬레이션
     */
    private void simulateMachineStatus() {
        new Thread(() -> {
            try {
                int cycle = 0;
                while (cycle < 10) { // 10번 반복
                    for (int i = 1; i <= 10; i++) {
                        String status;
                        if (i % 3 == 0) status = "washing";
                        else if (i % 2 == 0) status = "done";
                        else status = "waiting";

                        int finalI = i;
                        String finalStatus = status;
                        runOnUiThread(() -> updateMachineStatus(finalI, finalStatus));
                    }
                    cycle++;
                    Thread.sleep(3000); // 3초마다 상태 갱신
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "시뮬레이션 중단", e);
            }
        }).start();
    }

    /**
     * 상태 변경 UI 적용
     */
    private void updateMachineStatus(int id, String status) {
        ImageView img = findViewById(1000 + id);
        TextView tv = findViewById(2000 + id);

        if (img == null || tv == null) return;

        switch (status) {
            case "washing":
                img.setColorFilter(Color.parseColor("#FF9F00")); // 주황
                tv.setText("세탁기 " + id + "번\n상태: 세탁 중");
                break;
            case "done":
                img.setColorFilter(Color.parseColor("#008CFF")); // 파랑
                tv.setText("세탁기 " + id + "번\n상태: 세탁 완료");
                break;
            default:
                img.setColorFilter(Color.GRAY);
                tv.setText("세탁기 " + id + "번\n상태: 대기 중");
                break;
        }
    }

    // ⚙️ 서버 연결 함수 주석화
    /*
    private void connectWebSocket() {
        client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(WS_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "✅ WebSocket 연결 성공");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "📩 서버 메시지 수신: " + text);
                runOnUiThread(() -> handleMachineUpdate(text));
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "❌ WebSocket 오류", t);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "🔌 WebSocket 종료됨: " + reason);
            }
        });
    }

    private void handleMachineUpdate(String jsonText) {
        try {
            JSONObject obj = new JSONObject(jsonText);
            int id = obj.getInt("machine_id");
            String status = obj.getString("status");
            updateMachineStatus(id, status);
        } catch (Exception e) {
            Log.e(TAG, "JSON 파싱 오류", e);
        }
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // ⚙️ 서버 관련 종료 처리 주석
        /*
        if (webSocket != null) webSocket.close(1000, "앱 종료");
        if (client != null) client.dispatcher().executorService().shutdown();
        */
    }
}