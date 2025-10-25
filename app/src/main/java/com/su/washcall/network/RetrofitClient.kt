package com.su.washcall.network // 네 패키지 이름 확인!

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // Logcat에 통신 로그를 찍기 위함
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory // JSON 변환기

object RetrofitClient {

    // ⛔️ [매우 중요] ⛔️
    // 여기에 너의 FastAPI 서버 기본 주소를 넣어야 해!
    // (예: "http://192.168.0.10:8000/" 또는 "http://10.0.2.2:8000/" (에뮬레이터용))
    // 반드시 "http://"로 시작하고 "/"로 끝나야 해.
    private const val BASE_URL = "https://charming-ladybird-roughly.ngrok-free.app/"

    // 네트워크 통신 로그를 찍어주는 OkHttp 클라이언트 설정
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY // 요청/응답 내용을 모두 보여줌
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Retrofit 인스턴스 생성 (lazy: 처음 사용할 때 딱 한 번만 만듦)
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 1. 기본 서버 주소 설정
            .client(okHttpClient) // 2. 로그용 OkHttp 클라이언트 연결
            .addConverterFactory(GsonConverterFactory.create()) // 3. JSON 변환기 설정
            .build()
            .create(ApiService::class.java) // 4. ApiService 인터페이스 구현
    }
}