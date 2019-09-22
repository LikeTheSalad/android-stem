package com.likethesalad.placeholder.models.raw

import java.io.File

abstract class RawFiles(val valuesFolderName: String, val mainValuesRawFiles: List<File>) {
    abstract fun getRawFilesMetaList(): List<List<File>>
}