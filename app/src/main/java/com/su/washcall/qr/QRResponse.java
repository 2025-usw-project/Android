package com.su.washcall.qr;

public class QRResponse {
    private String message;       // 서버 메시지 (예: "WASHROOM_1F 세탁실 정보 확인 완료")
    private int machine_count;    // 세탁기 개수
    private String status;        // 상태 (예: "active")

    public String getMessage() {
        return message;
    }

    public int getMachine_count() {
        return machine_count;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "QRResponse{" +
                "message='" + message + '\'' +
                ", machine_count=" + machine_count +
                ", status='" + status + '\'' +
                '}';
    }
}

