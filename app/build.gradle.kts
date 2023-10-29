
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("androidx.navigation.safeargs")

    // ROOM database
    id ("kotlin-kapt")
}

android {
    namespace = "com.bangkit.storyappdicoding"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bangkit.storyappdicoding"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.github.bumptech.glide:glide:4.15.1")

    // moshi berfungsi untuk mengubah json menjadi data class
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.moshi:moshi-adapters:1.12.0")
    implementation("com.squareup.moshi:moshi-kotlin-codegen:1.12.0")

    // retrofit
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // view pager 2 untuk membuat swipe view
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Splash Screen untuk membuat splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // data store untuk menyimpan data secara lokal
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Room components
    implementation ("androidx.room:room-runtime:2.6.0")
    implementation ("androidx.room:room-paging:2.6.0") // For paging 3 remote mediator
    implementation("androidx.paging:paging-runtime-ktx:3.2.1") // For paging 3
    implementation("com.google.android.gms:play-services-location:21.0.1") // For FusedLocation
    kapt ("androidx.room:room-compiler:2.6.0")

    // couroutine support
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.room:room-ktx:2.6.0")

    // gmaps
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}