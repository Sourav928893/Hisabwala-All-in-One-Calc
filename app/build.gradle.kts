plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.hisabwalaallinonecalc"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.hisabwalaallinonecalc"
        minSdk = 25
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
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference)
    implementation(libs.window)
    implementation(libs.browser)
    implementation("androidx.fragment:fragment-ktx:1.8.3")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // --- Google Material ---
    implementation(libs.material) // maps to 1.12.0 in toml

    // --- Firebase (BOM controls versions) ---
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-inappmessaging-display")
    implementation(libs.firebase.database)

    // --- Google Play Services ---
    implementation(libs.play.services.location)
    implementation(libs.play.services.oss.licenses)
    implementation(libs.app.update)

    // --- Charts & Indicators ---
    implementation("com.tbuonomo:dotsindicator:5.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.gms:play-services-ads:24.5.0")
}