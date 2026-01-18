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
