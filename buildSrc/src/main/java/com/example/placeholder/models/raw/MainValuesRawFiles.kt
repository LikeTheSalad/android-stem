package com.example.placeholder.models.raw

import java.io.File

class MainValuesRawFiles(
    valuesFolderName: String,
    valuesRawFiles: List<File>
) : RawFiles(valuesFolderName, valuesRawFiles) {
    override fun getRawFilesMetaList(): List<List<File>> {
        return listOf(mainValuesRawFiles)
    }
}