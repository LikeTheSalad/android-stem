package com.example.placeholder.models.raw

import java.io.File

class FlavorValuesRawFiles(
    val flavorName: String,
    valuesFolderName: String,
    val complimentaryRawFiles: List<File>,
    flavorValuesRawFiles: List<File>
) : RawFiles(valuesFolderName, flavorValuesRawFiles) {

    override fun getRawFilesMetaList(): List<List<File>> {
        return listOf(complimentaryRawFiles, mainValuesRawFiles)
    }
}