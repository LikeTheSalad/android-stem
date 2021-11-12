package com.likethesalad.placeholder.modules.common.helpers.dirs

import java.io.File

class IncrementalDirsProvider(private val incrementalDir: File) {

    fun getTemplateStringsDir(): File {
        return getIncrementalDirAndCreateIfNotExists("templates")
    }

    private fun getIncrementalDirAndCreateIfNotExists(dirName: String): File {
        val dir = File(incrementalDir, dirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }
}
