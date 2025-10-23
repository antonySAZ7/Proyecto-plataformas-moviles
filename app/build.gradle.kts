plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("kotlin-kapt")
}

android {
    namespace = "com.example.proyectoplatsadj"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proyectoplatsadj"
        minSdk = 27
        targetSdk = 36
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ===================================
    // DEPENDENCIAS BASE (Ya las tenías)
    // ===================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ===================================
    // NAVEGACIÓN TYPE-SAFE (OBLIGATORIO)
    // ===================================
    implementation("androidx.navigation:navigation-compose:2.8.3") // ⬆️ Versión actualizada
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3") // ⬆️ Actualizado

    // ===================================
    // COROUTINES (Ya lo tenías)
    // ===================================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1") // ⬆️ Actualizado

    // ===================================
    // ROOM DATABASE (Base de datos local - OBLIGATORIO según requisitos)
    // ===================================
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // ===================================
    // RETROFIT (Para APIs - OPCIONAL pero recomendado)
    // ===================================
    val retrofitVersion = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ===================================
    // KOTLINX DATETIME (Manejo de fechas)
    // ===================================
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // ===================================
    // COIL (Carga de imágenes - OPCIONAL)
    // ===================================
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ===================================
    // ACCOMPANIST (Utilidades UI - OPCIONAL)
    // ===================================
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
}