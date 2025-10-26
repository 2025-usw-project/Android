package com.su.washcall;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    // 🔹 새 토큰이 발급될 때 자동 호출됨
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        // 필요하다면 서버로 토큰 전송
        sendRegistrationToServer(token);
    }

    // 🔹 FCM 푸시 메시지를 받을 때 호출됨
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // ✅ (1) 데이터 페이로드가 있을 경우
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // 예시: 데이터 처리
            handleDataPayload(remoteMessage.getData());
        }

        // ✅ (2) 노티피케이션 페이로드가 있을 경우
        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + body);

            // 예시: 알림 표시 (직접 커스텀 가능)
            showNotification(body);
        }
    }

    // 🔸 토큰을 서버로 전송 (선택사항)
    private void sendRegistrationToServer(String token) {
        // TODO: 서버로 전송하는 코드 작성 (필요시)
    }

    // 🔸 데이터 페이로드 처리 로직
    private void handleDataPayload(Map<String, String> data) {
        // TODO: 데이터 기반 처리 (예: 특정 화면 이동, DB 업데이트 등)
        Log.d(TAG, "Handling data payload...");
    }

    // 🔸 알림 표시 예시 (커스텀 NotificationManager 사용 가능)
    private void showNotification(String messageBody) {
        // TODO: 실제 Notification 띄우기 로직 구현
        Log.d(TAG, "Showing notification: " + messageBody);
    }
}
