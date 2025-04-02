plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.gms)
    alias(libs.plugins.plugin.serialization)
<<<<<<< HEAD
    alias(libs.plugins.ktlint)
=======
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
}

android {
    namespace = "com.example.mapsfriends"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mapsfriends"
        minSdk = 24
<<<<<<< HEAD
        //noinspection OldTargetApi
        targetSdk = 33
=======
        targetSdk = 35
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        addManifestPlaceholders(
            mapOf(
<<<<<<< HEAD
                // ID вашего приложения (app_id).
                "VKIDClientID" to "53221768",
                // Ваш защищенный ключ (client_secret).
                "VKIDClientSecret" to "MdXayYI8ryeHVJ6uaAKJ",
                // Обычно используется vk.com.
                "VKIDRedirectHost" to "vk.com",
                // Обычно используется vk{ID приложения}.
                "VKIDRedirectScheme" to "vk53221768",
            ),
=======
                "VKIDClientID" to "53221768", // ID вашего приложения (app_id).
                "VKIDClientSecret" to "MdXayYI8ryeHVJ6uaAKJ", // Ваш защищенный ключ (client_secret).
                "VKIDRedirectHost" to "vk.com", // Обычно используется vk.com.
                "VKIDRedirectScheme" to "vk53221768", // Обычно используется vk{ID приложения}.
            )
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
<<<<<<< HEAD
                "proguard-rules.pro",
=======
                "proguard-rules.pro"
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
            )
        }
    }
    compileOptions {
<<<<<<< HEAD
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
=======
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
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
<<<<<<< HEAD
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.ktx)
=======
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
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
<<<<<<< HEAD
}
=======
}
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
