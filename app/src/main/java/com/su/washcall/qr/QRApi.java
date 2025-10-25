package com.su.washcall.qr;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface QRApi {

    // 🔹 FastAPI 엔드포인트 예시: POST /scan
    @POST("scan")
    Call<QRResponse> sendRoomInfo(@Body QRRequest request);
}