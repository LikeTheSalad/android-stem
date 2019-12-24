package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import java.io.File

class IncrementalDirsProvider(androidVariantHelper: AndroidVariantHelper) {
    private val incrementalDir: File by lazy {
        val dir = File(androidVariantHelper.incrementalDir)
        if (!dir.exists()) {
            dir.createNewFile()
        }
        dir
    }

    fun getRawStringsDir(): File {
        return getIncrementalDirAndCreateIfNotExists("strings")
    }

    fun getTemplateStringsDir(): File {
        return getIncrementalDirAndCreateIfNotExists("templates")
    }

    private fun getIncrementalDirAndCreateIfNotExists(dirName: String): File {
        val dir = File(incrementalDir, dirName)
        if (!dir.exists()) {
            dir.createNewFile()
        }

        return dir
    }
}
