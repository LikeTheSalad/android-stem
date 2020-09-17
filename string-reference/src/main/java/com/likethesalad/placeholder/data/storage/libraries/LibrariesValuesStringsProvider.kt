package com.likethesalad.placeholder.data.storage.libraries

import com.likethesalad.placeholder.data.ValuesXmlFiles
import com.likethesalad.placeholder.models.ValuesStrings

class LibrariesValuesStringsProvider(private val librariesFilesProvider: LibrariesFilesProvider) {

    fun getValuesStringsFor(folderName: String, parent: ValuesStrings?): ValuesStrings {
        return ValuesStrings(
            folderName,
            ValuesXmlFiles(librariesFilesProvider.getXmlFilesForFolder(folderName)),
            parent
        )
    }
}