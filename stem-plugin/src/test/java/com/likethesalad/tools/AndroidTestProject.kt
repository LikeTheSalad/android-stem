package com.likethesalad.tools

import com.likethesalad.tools.descriptor.ProjectDescriptor
import java.io.File
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

class AndroidTestProject(val rootDir: File) {
    private lateinit var rootGradleFile: File
    private lateinit var settingsFile: File

    companion object {
        private const val BUILD_GRADLE_FILE_NAME = "build.gradle"
        private const val ANDROID_MANIFEST_FILE_NAME = "AndroidManifest.xml"
        private const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"
    }

    init {
        setUpRootProject()
    }

    fun runGradle(
        forProjectName: String,
        command: String,
        withInfo: Boolean = false
    ): BuildResult {
        return runGradle(forProjectName, listOf(command), withInfo)
    }

    fun runGradleAndFail(
        forProjectName: String,
        command: String,
        withInfo: Boolean = false
    ): BuildResult {
        return runGradleAndFail(forProjectName, listOf(command), withInfo)
    }

    fun runGradle(
        forProjectName: String,
        commands: List<String>,
        withInfo: Boolean = false
    ): BuildResult {
        val extraArgs = mutableListOf("--stacktrace")
        if (withInfo) {
            extraArgs.add("--info")
        }
        return createGradleRunner()
            .withArguments(commands.map { ":$forProjectName:$it" }.plus(extraArgs))
            .build()
    }

    fun runGradleAndFail(
        forProjectName: String,
        commands: List<String>,
        withInfo: Boolean = false
    ): BuildResult {
        val extraArgs = mutableListOf("--stacktrace")
        if (withInfo) {
            extraArgs.add("--info")
        }
        return createGradleRunner()
            .withArguments(commands.map { ":$forProjectName:$it" }.plus(extraArgs))
            .buildAndFail()
    }

    fun addSubproject(descriptor: ProjectDescriptor) {
        val name = descriptor.projectName
        val projectDir = File(rootDir, name)

        projectDir.mkdirs()

        createProjectBuildGradleFile(descriptor.getBuildGradleContents(), projectDir)
        createProjectManifestFile(projectDir)
        descriptor.projectDirBuilder.buildDirectory(projectDir)

        settingsFile.appendText(
            """
            
            include '$name'
        """.trimIndent()
        )
    }

    private fun createGradleRunner(): GradleRunner {
        return GradleRunner.create()
            .withProjectDir(rootDir)
            .withPluginClasspath()
    }

    private fun createProjectBuildGradleFile(buildGradleContent: String, parentDir: File) {
        val buildGradle = File(parentDir, BUILD_GRADLE_FILE_NAME)
        buildGradle.writeText(buildGradleContent)
    }

    private fun createProjectManifestFile(parentDir: File) {
        val mainDir = File(parentDir, "src/main")
        if (!mainDir.exists()) {
            mainDir.mkdirs()
        }
        val androidManifest = File(mainDir, ANDROID_MANIFEST_FILE_NAME)
        androidManifest.writeText(
            """
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"/>
        """.trimIndent()
        )
    }

    private fun setUpRootProject() {
        rootGradleFile = File(rootDir, BUILD_GRADLE_FILE_NAME)
        settingsFile = File(rootDir, SETTINGS_GRADLE_FILE_NAME)

        rootGradleFile.writeText(
            """
        """.trimIndent()
        )

        settingsFile.writeText(
            """
                pluginManagement {
                    repositories {
                        gradlePluginPortal()
                        mavenCentral()
                        google()
                    }
                }
                dependencyResolutionManagement {
                    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                    repositories {
                        mavenCentral()
                        google()
                        gradlePluginPortal()
                    }
                }
                rootProject.name = "sample-project"
                """.trimIndent()
        )
    }
}