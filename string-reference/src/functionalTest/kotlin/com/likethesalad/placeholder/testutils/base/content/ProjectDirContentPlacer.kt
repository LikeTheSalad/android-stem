package com.likethesalad.placeholder.testutils.base.content

import java.io.File

abstract class ProjectDirContentPlacer {

    fun setProjectDir(projectDir: File) {
        val dir = File(projectDir, getRelativePath())
        if (!dir.exists()) {
            dir.mkdirs()
        }

        onDirReady(dir)
    }

    protected abstract fun getRelativePath(): String

    protected abstract fun onDirReady(dir: File)
}