package com.likethesalad.placeholder.testutils.base

import com.likethesalad.placeholder.testutils.base.layout.ProjectDescriptor
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

        private const val BUILD_GRADLE_FILE_NAME = "build.gradle"
        private const val ANDROID_MANIFEST_FILE_NAME = "AndroidManifest.xml"
        private const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"
    }

    @Before
    fun setUpRoot() {
        setupRootProject()
    }

    protected fun createProjectAndRun(projectDescriptor: ProjectDescriptor, commands: List<String>): BuildResult {
        val name = projectDescriptor.getProjectName()

        createProject(projectDescriptor)

        return GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(commands.map { ":$name:$it" })
            .build()
    }

    protected fun createProject(projectDescriptor: ProjectDescriptor) {
        val name = projectDescriptor.getProjectName()
        val projectDir = testProjectDir.newFolder(name)

        createProjectBuildGradleFile(projectDescriptor.getBuildGradleContents(), projectDir)
        createProjectManifestFile("some.package.name.for.$name", projectDir)
        projectDescriptor.projectDirectoryBuilder.buildDirectory(projectDir)

        settingsFile!!.appendText(
            """
            
            include '$name'
        """.trimIndent()
        )
    }

    protected fun getProjectDir(projectName: String): File {
        val dir = File(testProjectDir.root, projectName)
        if (!dir.exists()) {
            throw IllegalStateException("Directory for $projectName doesn't exist")
        }

        return dir
    }

    private fun createProjectBuildGradleFile(buildGradleContent: String, parentDir: File) {
        val buildGradle = File(parentDir, BUILD_GRADLE_FILE_NAME)
        buildGradle.writeText(buildGradleContent)
    }

    private fun createProjectManifestFile(packageName: String, parentDir: File) {
        val mainDir = File(parentDir, "src/main")
        if (!mainDir.exists()) {
            mainDir.mkdirs()
        }
        val androidManifest = File(mainDir, ANDROID_MANIFEST_FILE_NAME)
        androidManifest.writeText(
            """
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="$packageName" />
        """.trimIndent()
        )
    }

    private fun setupRootProject() {
        rootGradleFile = File(testProjectDir.root, BUILD_GRADLE_FILE_NAME)
        settingsFile = File(testProjectDir.root, SETTINGS_GRADLE_FILE_NAME)
        if (rootGradleFile?.exists() == true) {
            return
        }

        rootGradleFile = testProjectDir.newFile(BUILD_GRADLE_FILE_NAME)

        val libsDir = Paths.get("build", "libs").toFile().absolutePath
        val pluginJarPath = "$libsDir/buildSrc-1.2.1.jar"

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
            
            subprojects {
                repositories {
                    google()
                    jcenter()
                }
            }
        """.trimIndent()
        )

        settingsFile = testProjectDir.newFile(SETTINGS_GRADLE_FILE_NAME)

        settingsFile!!.writeText(
            """
                rootProject.name = "sample-project"
                """.trimIndent()
        )
    }

    abstract fun getAndroidBuildPluginVersion(): String
}