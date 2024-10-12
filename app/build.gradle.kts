plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.qrcode"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.qrcode"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.google.mlkit:barcode-scanning:16.1.1")
    // CameraX dependencies
    implementation ("androidx.camera:camera-core:1.1.0-alpha05")
    implementation ("androidx.camera:camera-camera2:1.1.0-alpha05")
    implementation ("androidx.camera:camera-lifecycle:1.1.0-alpha05")
    implementation ("androidx.camera:camera-view:1.0.0-alpha27")
}