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
    private val filesMovedFromTestAssets = mutableListOf<File>()

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

    protected fun setUpProject(projectName: String, sourceDirName: String = projectName) {
        val sourceProjectDir = getSourceDir(sourceDirName)
        projectDir = getFolder(projectName)
        moveFilesToDir(sourceProjectDir.listFiles(), projectDir)
    }

    private fun getFolder(projectName: String): File {
        val folder = File(testFolder.root, projectName)
        if (folder.exists()) {
            return folder
        }
        return testFolder.newFolder(projectName)
    }

    private fun moveFilesToDir(files: Array<File>?, destinationDir: File) {
        files?.forEach {
            if (it.isDirectory) {
                moveFilesToDir(it.listFiles(), File(destinationDir, it.name))
            } else {
                val target = File(destinationDir, it.name)
                filesMovedFromTestAssets.add(target)
                it.copyTo(target)
            }
        }
    }

    protected fun removeTestAssetsFromProject() {
        filesMovedFromTestAssets.forEach {
            it.delete()
        }
    }

    abstract fun getSourceDir(name: String): File
}