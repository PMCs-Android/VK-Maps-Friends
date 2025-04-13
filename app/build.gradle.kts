plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.gms)
    alias(libs.plugins.plugin.serialization)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.example.mapsfriends"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mapsfriends"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        addManifestPlaceholders(
            mapOf(
                "VKIDClientID" to "53221768", // ID вашего приложения (app_id).
                "VKIDClientSecret" to "MdXayYI8ryeHVJ6uaAKJ", // Ваш защищенный ключ (client_secret).
                "VKIDRedirectHost" to "vk.com", // Обычно используется vk.com.
                "VKIDRedirectScheme" to "vk53221768", // Обычно используется vk{ID приложения}.
            )
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
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
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.vkid)
    implementation(libs.vkid.support)
    implementation(libs.vkid.onetap)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.compose)
}
