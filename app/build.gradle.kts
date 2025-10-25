plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
}

android {
    namespace = "com.example.a2gradeproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.a2gradeproject"
        minSdk = 21
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
}