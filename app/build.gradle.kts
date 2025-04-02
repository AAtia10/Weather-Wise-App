plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("plugin.serialization") version "2.1.10"
    id("kotlin-kapt")


}

android {
    namespace = "com.example.weatherwise"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.weatherwise"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packagingOptions {
        exclude("META-INF/LICENSE-notice.md")
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/LICENSE.txt")
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // nav
    val nav_version = "2.8.8"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json: 1.8.0")

    // lottie
    implementation("com.airbnb.android:lottie-compose:5.2.0")

    //constraint
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    //Scoped API
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")
    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")

    //Maps
    implementation("com.google.maps.android:maps-compose:2.7.2")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    //room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    //workmanger
    implementation ("androidx.work:work-runtime-ktx:2.8.1")


    androidTestImplementation ("io.mockk:mockk-android:1.13.17")
    androidTestImplementation ("io.mockk:mockk-agent:1.13.17")

    val androidXTestExtKotlinRunnerVersion by extra("1.1.3")
    val androidXTestCoreVersion by extra("1.6.1")
    val coroutinesVersion by extra("1.5.2")
    testImplementation ("io.mockk:mockk-android:1.13.17")
    testImplementation ("io.mockk:mockk-agent:1.13.17")

    //kotlinx-coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation(kotlin("test"))

    // AndroidX and Robolectric
    testImplementation ("androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion")
    testImplementation ("androidx.test:core-ktx:$androidXTestCoreVersion")
    testImplementation ("org.robolectric:robolectric:4.11")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")







}