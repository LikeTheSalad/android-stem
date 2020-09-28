package com.likethesalad.placeholder.data.storage.libraries

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.data.helpers.AndroidConfigHelper
import com.likethesalad.placeholder.utils.ConfigurationProvider
import java.io.File

@AutoFactory
class LibrariesFilesProvider(
    private val androidConfigHelper: AndroidConfigHelper,
    @Provided private val configurationProvider: ConfigurationProvider
) {

    fun getXmlFilesForFolder(folderName: String): Set<File> {
        if (!configurationProvider.useDependenciesRes()) {
            return emptySet()
        }

        return androidConfigHelper.librariesResDirs.files.map {
            File(it, "$folderName/$folderName.xml")
        }.filter { it.exists() }.toSet()
    }
}