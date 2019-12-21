package com.likethesalad.placeholder.data.storage

import java.io.File

class IncrementalDataCleaner(private val incrementalDirsProvider: IncrementalDirsProvider) {

    fun clearRawStrings() {
        clearFilesFromDir(incrementalDirsProvider.getRawStringsDir())
    }

    fun clearTemplateStrings() {
        clearFilesFromDir(incrementalDirsProvider.getTemplateStringsDir())
    }

    private fun clearFilesFromDir(dir: File) {
        dir.listFiles()?.forEach {
            it.delete()
        }
    }
}
