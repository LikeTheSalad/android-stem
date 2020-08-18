package com.likethesalad.placeholder

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Paths

class CheckOutputsTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var settingsFile: File
    private lateinit var buildFile: File
    private lateinit var pluginJarPath: String

    @Before
    fun setup() {
        settingsFile = testProjectDir.newFile("settings.gradle")
        buildFile = testProjectDir.newFile("build.gradle")

        settingsFile.writeText(
            """
            rootProject.name = "hello-world"
        """.trimIndent()
        )

        val libsDir = Paths.get("build", "libs").toFile().absolutePath
        pluginJarPath = "$libsDir/buildSrc-1.2.0.jar"
    }

    @Test
    fun checkIntegration() {
        buildFile.writeText(
            """
            buildscript {
                repositories {
                    google()
                    jcenter()
                }
                dependencies {
                    classpath 'com.android.tools.build:gradle:3.3.3'
                    classpath files("$pluginJarPath")
                }
            }
            
            apply plugin: 'com.android.application'
            apply plugin: 'placeholder-resolver'
            
            android {
                compileSdkVersion = 28
            }
        """.trimIndent()
        )

        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .build()
    }
}