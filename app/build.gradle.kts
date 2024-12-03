plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")  // For Kotlin Android support
    id("kotlin-kapt")  // For Kotlin annotation processing (KAPT)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.plantezeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.plantezeapp"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

buildscript {
    repositories {
        google()
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    testImplementation ("org.mockito:mockito-core:5.5.0")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("androidx.test.ext:junit:1.1.5")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.filament.android)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-database-ktx")

    //room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    //Mockito
    testImplementation("org.mockito:mockito-core:5.6.2") // Mockito core library
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0") // Kotlin extensions
    testImplementation("org.mockito:mockito-android:4.4.0")

}