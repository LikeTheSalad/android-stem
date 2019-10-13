package com.likethesalad.placeholder.models.raw

import java.io.File

abstract class RawFiles(val suffix: String, val mainValuesRawFiles: List<File>) {
    abstract fun getRawFilesMetaList(): List<List<File>>
}