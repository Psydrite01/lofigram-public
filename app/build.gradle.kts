plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.psydrite.lofigram"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.psydrite.lofigram"
        minSdk = 24
        targetSdk = 35
        versionCode = 10
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("int", "VERSION_CODE", versionCode.toString())
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
        buildConfig = true
    }
}

dependencies {
    //navigation
    implementation(libs.androidx.navigation.compose)

    //lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    //coroutines
    implementation(libs.kotlinx.coroutines.android)

    //coil
    implementation(libs.coil.compose)

    //lottie
    implementation(libs.lottie.compose)

    //google ads
    implementation(libs.play.services.ads)

    //qonversion
    implementation(libs.sdk)

    //purchasing
    implementation(libs.billing.ktx)

    //okhttp
    implementation(libs.okhttp)

    //exoplayer for screensaver
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    //for media player notification
    implementation(libs.androidx.media)
    implementation(libs.androidx.core)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.ads.api)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation(libs.firebase.functions)
    implementation(libs.firebase.ai)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Credential Manager
    implementation ("androidx.credentials:credentials:1.2.2")
    implementation ("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    implementation ("com.google.android.gms:play-services-auth:21.3.0")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Kotlin Parcelize
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:1.9.20")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0-alpha01")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0-alpha01")
    kapt("androidx.lifecycle:lifecycle-compiler:2.8.0-alpha01")

    //datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //for google play review
    implementation("com.google.android.play:review:2.0.2") 
    implementation("com.google.android.play:review-ktx:2.0.2")
}