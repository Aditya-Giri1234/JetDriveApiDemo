@file:Suppress("UnstableApiUsage")


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization.plugin)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.jetdrivedemoapi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.jetdrivedemoapi"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "com.google.common.util.concurrent.ListenableFuture"
            excludes += "META-INF/DEPENDENCIES"
        }
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
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.1"
//    }
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Jetpack Compose recompose debugger by log
    // Solution of this dependency available to all module :- https://stackoverflow.com/a/48443958/17464278
    api("io.github.theapache64:rebugger:1.0.0-rc03")
    api("androidx.compose.material:material-icons-extended:1.7.1")

    //Google gson
    implementation(libs.gson)

    //Navigation
    implementation(libs.androidx.navigation.compose)


    //For Compose Runtime Lifecycle support (collectAsStateWithLifecyle())
    implementation(libs.androidx.lifecycle.runtime.compose)


    //Hilt
    implementation(libs.hilt)
    implementation(libs.hilt.navigation.compse)
    ksp(libs.hilt.ksp)

    //Kotlin - Serialization
    implementation(libs.kotlin.serialization)


    //gauva
    implementation(libs.gauva)

    //For Drive Api Use
    implementation(libs.google.gms.play.services)
    implementation(libs.google.api.client) {
        exclude(group = "org.apache.httpcomponents", module = "guava-jdk5")
    }
    implementation(libs.google.api.drive) {
        exclude(group = "org.apache.httpcomponents", module = "guava-jdk5")
    }

}