plugins {
    alias(libs.plugins.java.library)
}

libConventions {
    setJavaVersion("11")
}

dependencies {
    api(gradleApi())
    compileOnly(libs.android.plugin)
    testImplementation(libs.android.plugin)
    testImplementation(libs.unitTesting)
}
