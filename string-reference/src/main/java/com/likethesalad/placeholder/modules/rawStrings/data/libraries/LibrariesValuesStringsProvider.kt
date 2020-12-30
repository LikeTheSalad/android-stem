package com.likethesalad.placeholder.modules.rawStrings.data.libraries

import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesFolderXmlFiles
import com.likethesalad.placeholder.modules.rawStrings.models.ValuesFolderStrings

class LibrariesValuesStringsProvider(private val librariesFilesProvider: LibrariesFilesProvider) {

    fun getValuesStringsFor(folderName: String, parent: ValuesFolderStrings?): ValuesFolderStrings {
        return ValuesFolderStrings(
            folderName,
            ValuesFolderXmlFiles(
                folderName,
                librariesFilesProvider.getXmlFilesForFolder(folderName)
            ),
            parent
        )
    }
}