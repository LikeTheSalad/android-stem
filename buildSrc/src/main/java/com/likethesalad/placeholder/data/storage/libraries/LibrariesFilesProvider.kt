package com.likethesalad.placeholder.data.storage.libraries

import com.likethesalad.placeholder.data.storage.libraries.helpers.AndroidLibrariesProvider
import java.io.File

class LibrariesFilesProvider(private val androidLibrariesProvider: AndroidLibrariesProvider) {

    fun getXmlFilesForFolder(folderName: String): Set<File> {
        return androidLibrariesProvider.getAndroidLibraries().map {
            File(it.resDir, "$folderName/$folderName.xml")
        }.filter { it.exists() }.toSet()
    }
}