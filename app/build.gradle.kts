// 파일 경로: app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.su.washcall"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.su.washcall"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // --- ▼▼▼▼▼ [핵심 수정] 모든 라이브러리를 깔끔하게 정리하고 버전을 고정합니다 ▼▼▼▼▼ ---

    // ✅ 기본 AndroidX 라이브러리 (중복 완전 제거)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // ✅ Activity 라이브러리 (호환되는 버전 1.9.0으로 직접 명시)
    implementation("androidx.activity:activity-ktx:1.9.0")
    // 'activity-ktx'가 'activity'를 포함하므로 위의 한 줄만 선언해도 충분합니다.

    // ✅ 테스트 라이브러리
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ✅ Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)

    // ✅ Android Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // ✅ Room (로컬 DB)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // ✅ 코루틴 (비동기)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ✅ Lifecycle (ViewModel, LiveData 등)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // ✅ Retrofit (서버 통신)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // ✅ ZXing (QR 코드 스캔)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // ✅ JWT
    implementation("com.auth0.android:jwtdecode:2.0.2")

    // --- ▲▲▲▲▲ [핵심 수정] 파일 맨 아래에 있던 모든 중복 선언을 제거했습니다 ▲▲▲▲▲ ---
}
