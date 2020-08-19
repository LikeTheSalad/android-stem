package com.likethesalad.placeholder.testutils.base

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Paths

abstract class BaseAndroidProjectTest {

    private var rootGradleFile: File? = null
    private var settingsFile: File? = null

    companion object {
        @JvmStatic
        @get:ClassRule
        val testProjectDir = TemporaryFolder()
    }

    @Before
    fun setUpRoot() {
        setupRootProject()
    }

    protected fun createAppAndRun(projectDefinition: ProjectDefinition, commands: List<String>): BuildResult {
        val name = projectDefinition.projectName()
        val projectDir = testProjectDir.newFolder(name)

        createAppBuildGradle(projectDefinition, projectDir)
        settingsFile!!.appendText(
            """
            
            include '$name'
        """.trimIndent()
        )

        return GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(commands.map { ":$name:$it" })
            .build()
    }

    private fun createAppBuildGradle(projectDefinition: ProjectDefinition, parentDir: File) {
        val buildGradle = File(parentDir, "build.gradle")
        buildGradle.writeText(projectDefinition.getBuildGradleContents())
    }

    private fun setupRootProject() {
        rootGradleFile = File(testProjectDir.root, "build.gradle")
        settingsFile = File(testProjectDir.root, "settings.gradle")
        if (rootGradleFile?.exists() == true) {
            return
        }

        rootGradleFile = testProjectDir.newFile("build.gradle")

        val libsDir = Paths.get("build", "libs").toFile().absolutePath
        val pluginJarPath = "$libsDir/buildSrc-1.2.0.jar"

        rootGradleFile!!.writeText(
            """
            buildscript {
                repositories {
                    google()
                    jcenter()
                }
                dependencies {
                    classpath 'com.android.tools.build:gradle:${getAndroidBuildPluginVersion()}'
                    classpath files("$pluginJarPath")
                }
            }
        """.trimIndent()
        )

        settingsFile = testProjectDir.newFile("settings.gradle")

        settingsFile!!.writeText(
            """
                rootProject.name = "sample-project"
                """.trimIndent()
        )
    }

    abstract fun getAndroidBuildPluginVersion(): String
}