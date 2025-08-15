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
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.assertj)
    testImplementation(gradleTestKit())
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}