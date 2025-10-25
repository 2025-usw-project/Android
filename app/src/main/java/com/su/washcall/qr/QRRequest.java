package com.su.washcall.qr;

public class QRRequest {
    private String room_id;  // 서버에 보낼 세탁실 ID

    public QRRequest(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }
}
