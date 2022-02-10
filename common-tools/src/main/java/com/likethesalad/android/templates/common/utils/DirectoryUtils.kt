package com.likethesalad.android.templates.common.utils

import java.io.File

object DirectoryUtils {

    fun clearIfNeeded(directory: File) {
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                file.deleteRecursively()
            }
        }
    }
}