// 경로: app/src/main/java/com/su/washcall/network/RetrofitClient.kt
package com.su.washcall.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 🔹 Retrofit 인스턴스를 관리하는 싱글톤 객체.
 * by lazy를 사용하여, 필요한 시점에 안전하게 객체들을 생성합니다.
 */
object RetrofitClient {

    // 🚨 에뮬레이터에서 로컬 PC의 서버에 접속하려면 이 주소를 사용해야 합니다.
    private const val BASE_URL = "https://unconical-kyong-frolicsome.ngrok-free.dev/"

    // OkHttpClient를 lazy 초기화
    // AuthInterceptor가 App.prefs를 사용하므로, App 클래스가 초기화된 후 생성되어야 합니다.
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor()) // 모든 요청을 가로채 토큰을 주입합니다.
            .addInterceptor(loggingInterceptor) // 그 다음에 로그를 찍도록 순서를 조정하는 것이 좋습니다.
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit 인스턴스를 lazy 초기화
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // 위에서 생성한 OkHttpClient 사용
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ApiService 인터페이스 구현체도 lazy 초기화
    // 외부에서는 RetrofitClient.instance 로 이 객체를 사용하게 됩니다.
    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
