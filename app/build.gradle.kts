plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.quickcart"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.quickcart"
        minSdk = 28
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

dependencies {

    implementation(libs.androidx.activity)
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.appcompat.v161)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    // for sp and dp
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    //Navigation component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    //lifecycle
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.com.google.firebase.firebase.analytics)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.play.services.auth)
    implementation(libs.google.firebase.storage.ktx)
    implementation(libs.google.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging.ktx)
    //lottie
    implementation(libs.lottie)
    //image slider
    implementation(libs.imageslideshow)
    //shimmer effect
    implementation(libs.shimmer)

    //roomlibrary
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)

    //noinspection KaptUsageInsteadOfKsp
    kapt (libs.room.compiler)
    implementation (libs.androidx.room.testing)

    implementation(libs.glide)

    //phonePay dependency
    implementation(libs.intentsdk.v243)

    //phonepay
    implementation("phonepe.intentsdk.android.release:IntentSDK:2.4.3")

    //retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)






}