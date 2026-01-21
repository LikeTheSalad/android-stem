package com.likethesalad.tools.content

import java.io.File

class ProjectDirBuilder(private val inputDir: File) {

    private val createdFiles = mutableListOf<File>()

    fun buildDirectory(projectDir: File) {
        val dir = File(projectDir, "src")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        onDirReady(dir)
    }

    fun clearFilesCreated() {
        return createdFiles.forEach { it.deleteRecursively() }
    }

    private fun onDirReady(dir: File) {
        moveFilesToDir(inputDir.listFiles(), dir)
    }

    private fun moveFilesToDir(files: Array<File>?, destinationDir: File) {
        files?.forEach {
            if (it.isDirectory) {
                moveFilesToDir(it.listFiles(), File(destinationDir, it.name))
            } else {
                val target = File(destinationDir, it.name)
                createdFiles.add(target)
                it.copyTo(target)
            }
        }
    }
}