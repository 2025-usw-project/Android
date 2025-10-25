package com.su.washcall.qr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.su.washcall.MainActivity;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StartScanActivity extends AppCompatActivity {

    // ✅ 서버 주소 (👉 본인 FastAPI 서버 주소로 바꾸세요)
    private static final String BASE_URL = "http://192.168.0.5:8000/"; // 예시 IP

    private final ActivityResultLauncher<ScanOptions> qrLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String scannedCode = result.getContents();

                    // 🔹 QR 코드에서 마지막 '/' 뒤 텍스트 추출 (세탁실 ID)
                    String roomId = scannedCode.substring(scannedCode.lastIndexOf("/") + 1);
                    Toast.makeText(this, "QR 코드 인식됨: " + roomId, Toast.LENGTH_SHORT).show();

                    // 🔹 서버에 전송
                    sendRoomIdToServer(roomId);

                } else {
                    Toast.makeText(this, "QR 스캔이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startQRCodeScanner();
    }

    private void startQRCodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("세탁실 QR코드를 스캔하세요");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(QRScanActivity.class);
        qrLauncher.launch(options);
    }

    /**
     * 🔹 Retrofit으로 세탁실 ID 서버 전송
     */
    private void sendRoomIdToServer(String roomId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // API 인터페이스 생성
        QRApi api = retrofit.create(QRApi.class);

        // 요청 객체 생성
        QRRequest request = new QRRequest(roomId);

        // 서버 요청 실행
        api.sendRoomInfo(request).enqueue(new Callback<QRResponse>() {
            @Override
            public void onResponse(@NotNull Call<QRResponse> call, @NotNull Response<QRResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QRResponse data = response.body();

                    // ✅ 서버 응답 처리
                    Toast.makeText(StartScanActivity.this,
                            "서버 응답: " + data.getMessage(),
                            Toast.LENGTH_LONG).show();

                    // ✅ MainActivity로 데이터 전달
                    Intent intent = new Intent(StartScanActivity.this, MainActivity.class);
                    intent.putExtra("room_id", roomId);
                    intent.putExtra("server_message", data.getMessage());
                    startActivity(intent);
                } else {
                    Toast.makeText(StartScanActivity.this, "서버 응답 실패", Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onFailure(@NotNull Call<QRResponse> call, @NotNull Throwable t) {
                Toast.makeText(StartScanActivity.this, "서버 통신 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("QR_API", "오류: ", t);
                finish();
            }
        });
    }
}

