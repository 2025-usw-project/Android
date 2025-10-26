plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)
}

android {
    // --- [추가 시작] ---
    namespace = "com.su.washcall" // "com.example.washcall"을 실제 패키지 이름으로 변경하세요.
    compileSdk = 34 // 최신 안정 버전 SDK를 지정하는 것이 좋습니다.

    defaultConfig {
        applicationId = "com.su.washcall" // "com.example.washcall"을 실제 패키지 이름으로 변경하세요.
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    // --- [추가 끝] ---

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
            sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // ✅ 기본 AndroidX 라이브러리
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ✅ Firebase BoM - 버전 카탈로그(toml)를 통해 한번만 선언
    implementation(platform(libs.firebase.bom))
    // BoM을 선언했으므로, 아래 라이브러리들은 버전 없이 선언합니다.
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)

    // ✅ Android Security (최신 안정 버전으로 변경)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")


    // ✅ Room (로컬 DB) - toml 파일에서 roomVersion을 참조하도록 변경 (아래 후속 조치 필요)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // ✅ 코루틴 (비동기)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ✅ Lifecycle (lifecycleScope 사용)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    // lifecycle-runtime-ktx가 runtime을 포함하므로 아래 라인은 삭제해도 무방합니다.

    // ✅ Retrofit (서버 통신)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // ✅ ZXing (QR 코드 스캔)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.activity:activity-ktx:1.9.0")

    // ✅ LiveData & JWT
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("com.auth0.android:jwtdecode:2.0.2")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
