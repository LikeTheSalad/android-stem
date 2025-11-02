plugins {
    alias(libs.plugins.java.library)
    alias(libs.plugins.androidTestTools)
    alias(libs.plugins.wire)
    id("java-gradle-plugin")
}

dependencies {
    implementation(libs.resourceLocator)
    implementation(libs.gson)
    compileOnly(libs.android.plugin)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.xmlUnit)
    testImplementation(libs.android.plugin)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
    testPluginDependency("com.android.tools.build:gradle:8.12.0")
}

libConventions {
    setJavaVersion("11")
}

tasks.withType(Test::class).configureEach {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("placeholderResolverPlugin") {
            id = "com.likethesalad.stem"
            implementationClass = "com.likethesalad.stem.StemPlugin"
        }
    }
}

wire {
    kotlin {}
}
