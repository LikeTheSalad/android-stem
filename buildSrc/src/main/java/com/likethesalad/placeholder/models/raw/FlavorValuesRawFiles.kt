package com.likethesalad.placeholder.models.raw

import java.io.File

class FlavorValuesRawFiles(
    val flavorName: String,
    suffix: String,
    val complimentaryRawFiles: List<File>,
    flavorValuesRawFiles: List<File>
) : RawFiles(suffix, flavorValuesRawFiles) {

    override fun getRawFilesMetaList(): List<List<File>> {
        return listOf(complimentaryRawFiles, mainValuesRawFiles)
    }
}