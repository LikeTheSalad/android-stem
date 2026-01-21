plugins {
    alias(libs.plugins.android.application)
    id("com.likethesalad.stem")
}

android {
    compileSdk = 34
    namespace = "com.likethesalad.stem.demo"

    defaultConfig {
        applicationId = "com.likethesalad.stem.demo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildFeatures {
            resValues = true
        }
        resValue("string", "generated_string", "My generated string")
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

kotlin {
    compilerOptions {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(project(":my-library"))
    stemProvider(project(":my-library"))
    testImplementation(libs.unitTesting)
    testImplementation(libs.robolectric)
}
