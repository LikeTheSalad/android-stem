import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin)
    id("java-gradle-plugin")
    alias(libs.plugins.androidTestTools)
    alias(libs.plugins.wire)
}

dependencies {
    implementation(libs.gson)
    compileOnly(libs.android.plugin)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.xmlUnit)
    testImplementation(libs.android.plugin)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
    testPluginDependency("com.android.tools.build:gradle:8.13.2")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}

tasks.withType(Test::class).configureEach {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("stemPlugin") {
            id = "com.likethesalad.stem"
            implementationClass = "com.likethesalad.stem.StemPlugin"
        }
    }
}

wire {
    kotlin {}
}
