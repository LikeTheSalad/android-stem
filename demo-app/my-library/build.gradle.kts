plugins {
    alias(libs.plugins.android.library)
}

android {
    compileSdk = 34
    namespace = "com.likethesalad.stem.demo.lib"

    defaultConfig {
        minSdk = 26
    }
}

kotlin {
    compilerOptions {
        jvmToolchain(17)
    }
}