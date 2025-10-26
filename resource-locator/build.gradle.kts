plugins {
    alias(libs.plugins.java.library)
    alias(libs.plugins.wire)
}

libConventions {
    setJavaVersion("11")
}

dependencies {
    compileOnly(libs.android.plugin)
    testImplementation(libs.android.plugin)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

wire {
    kotlin {}
}