plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Required for Firebase
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.taskmanager1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.taskmanager1"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true

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
        compose = true
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // ✅ Use latest version
    }
}

//tasks.withType<com.google.firebase.crashlytics.tasks.UploadMappingFileTask>().configureEach {
//    enabled = true
//}

dependencies {
    // Core & Lifecycle
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    implementation("androidx.compose.compiler:compiler:1.5.8")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui:1.6.1") // ✅ Use latest Compose version
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")
    implementation("androidx.compose.material3:material3:1.2.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
//    implementation("com.google.firebase:firebase-analytics-ktx")
//    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics:21.6.1") {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
        exclude(group = "com.google.android.gms", module = "play-services-ads-identifier")
    }
    // Firebase Crashlytics without AdMob
    implementation("com.google.firebase:firebase-crashlytics:18.6.1") {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
        exclude(group = "com.google.android.gms", module = "play-services-ads-identifier")
    }

    // Exclude AdMob from any other dependencies
    configurations.all {
        exclude(group = "com.google.android.gms", module = "play-services-ads")
        exclude(group = "com.google.android.gms", module = "play-services-ads-identifier")
    }

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.appcompat)
    implementation(libs.mediation.test.suite)
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Retrofit for API Calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.jakewharton.timber:timber:5.0.1")
}
