package com.likethesalad.placeholder.models.raw

import java.io.File

class MainValuesRawFiles(
    suffix: String,
    valuesRawFiles: List<File>
) : RawFiles(suffix, valuesRawFiles) {
    override fun getRawFilesMetaList(): List<List<File>> {
        return listOf(mainValuesRawFiles)
    }
}