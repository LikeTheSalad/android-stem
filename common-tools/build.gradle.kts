plugins {
    alias(libs.plugins.java.library)
}

dependencies {
    api(gradleApi())
    api(libs.resourceLocator)
    implementation(project(":resource-locator"))
    implementation(libs.androidCompatApi)
    testImplementation(libs.unitTesting)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjvm-default=all"))
    }
}

libConventions {
    setJavaVersion("11")
}