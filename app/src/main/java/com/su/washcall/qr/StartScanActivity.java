package com.su.washcall.qr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.su.washcall.MainActivity;

public class StartScanActivity extends AppCompatActivity {

    // ✅ QR 스캔 런처 등록
    private final ActivityResultLauncher<ScanOptions> qrLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    // 스캔 성공 시
                    String scannedCode = result.getContents();
                    Toast.makeText(this, "QR 코드 인식됨: " + scannedCode, Toast.LENGTH_SHORT).show();

                    // MainActivity로 값 전달
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("qrCode", scannedCode);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "QR 스캔이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
                finish(); // 항상 Activity 종료
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startQRCodeScanner(); // 실행 시 바로 카메라 실행
    }

    /**
     * QR 코드 스캐너 실행
     */
    private void startQRCodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("세탁실 QR코드를 스캔하세요");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(QRScanActivity.class);
        qrLauncher.launch(options);
    }
}
