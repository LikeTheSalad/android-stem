package com.likethesalad.placeholder.provider

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

abstract class BaseGradleTest {

    @get:Rule
    val testFolder = TemporaryFolder()

    protected lateinit var projectDir: File

    protected fun runCommand(commandStr: String): BuildResult {
        val arguments = getCommandArgs(commandStr).plus(listOf("--info", "--stacktrace"))

        return GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments(arguments)
            .withGradleVersion("6.5")
            .withPluginClasspath()
            .build()
    }

    private fun getCommandArgs(commandStr: String): List<String> {
        return commandStr.split(Regex("[\\s\\t]+"))
    }

    protected fun setUpProject(projectName: String) {
        val sourceProjectDir = getSourceDir(projectName)
        projectDir = testFolder.newFolder(projectName)
        moveFilesToDir(sourceProjectDir.listFiles(), projectDir)
    }

    private fun moveFilesToDir(files: Array<File>?, destinationDir: File) {
        files?.forEach {
            if (it.isDirectory) {
                moveFilesToDir(it.listFiles(), File(destinationDir, it.name))
            } else {
                val target = File(destinationDir, it.name)
                it.copyTo(target)
            }
        }
    }

    abstract fun getSourceDir(name: String): File
}