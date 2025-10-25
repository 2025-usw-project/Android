plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
    alias(libs.plugins.google.gms.google.services) // Firebase 연결 플러그인 적용
}

android {
    namespace = "com.su.washcall"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.su.washcall"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase BoM (Bill of Materials) - 버전 관리를 위해 권장
    implementation(platform("com.google.firebase:firebase-bom:33.0.0")) // 최신 BOM 버전 확인
    // 필요한 Firebase 라이브러리 추가 (예: 인증, 실시간 DB)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // Android Security 라이브러리 추가
    implementation("androidx.security:security-crypto:1.1.0-alpha06") // 최신 버전 확인 권장

    val roomVersion = "2.6.1"

    // 1. Room 런타임
    implementation("androidx.room:room-runtime:$roomVersion")
    // 2. Room 컴파일러 (KSP 플러그인이 반드시 설정되어 있어야 함)
    ksp("androidx.room:room-compiler:$roomVersion")
    // 3. Room KTX (코루틴 지원)
    implementation("androidx.room:room-ktx:$roomVersion")
    // Kotlin Coroutine (코루틴을 사용하기 위해 필수)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Dispatchers.IO 사용 가능
    // Android KTX: lifecycleScope 사용을 위해 필수
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0") // [확인] 버전은 최신으로 조정 가능
    // Android KTX: lifecycleScope 사용을 위해 필수
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    // ★★★ 자바 파일에서 Lifecycle 기능을 사용하기 위해 이 줄을 추가하세요! ★★★
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")

    // Retrofit (네트워크 통신)
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // 버전은 최신 안정 버전 확인
    // Gson Converter (JSON <-> 객체 변환)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp Logging Interceptor (통신 로그 확인용 - 선택 사항이지만 추천)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // OkHttp BOM 사용 시 버전 생략 가능
}