plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    id("com.likethesalad.stem")
}

android {
    compileSdk = 34
    namespace = "com.likethesalad.stem.sample"

    defaultConfig {
        applicationId = "com.likethesalad.stem.test"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    testImplementation(libs.unitTesting)
    testImplementation(libs.robolectric)
}
