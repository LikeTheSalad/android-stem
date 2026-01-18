plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin)
}

android {
    compileSdk = 34
    namespace = "com.likethesalad.stem.sample.lib"

    defaultConfig {
        minSdk = 26
    }
}

kotlin {
    compilerOptions {
        jvmToolchain(17)
    }
}