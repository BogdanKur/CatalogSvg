plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.catalogsvg"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.catalogsvg"
        minSdk = 27
        targetSdk = 35
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
    packagingOptions {
        resources {
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}
dependencies {
    implementation("org.maps:organicmaps:2024.11.27-12-android")
    implementation("com.yandex.android:maps.mobile:4.9.0-full")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.volley)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.junit.jupiter)
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("com.caverock:androidsvg:1.4")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("com.google.android.material:material:1.12.0")

    implementation ("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.database)
    implementation(platform(libs.firebase.bom))
    implementation (libs.firebase.storage.ktx)
    implementation (libs.firebase.firestore.ktx)
    implementation(libs.firebase.inappmessaging.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation (libs.glide)
    kapt ("com.github.bumptech.glide:compiler:4.13.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}