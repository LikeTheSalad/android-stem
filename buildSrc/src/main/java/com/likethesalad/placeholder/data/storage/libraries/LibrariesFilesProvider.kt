package com.likethesalad.placeholder.data.storage.libraries

import com.likethesalad.placeholder.data.helpers.AndroidConfigHelper
import java.io.File

class LibrariesFilesProvider(
    private val androidConfigHelper: AndroidConfigHelper,
    private val canUseDependenciesRes: Boolean
) {

    fun getXmlFilesForFolder(folderName: String): Set<File> {
        if (!canUseDependenciesRes) {
            return emptySet()
        }

        return androidConfigHelper.librariesResDirs.files.map {
            File(it, "$folderName/$folderName.xml")
        }.filter { it.exists() }.toSet()
    }
}