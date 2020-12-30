package com.likethesalad.placeholder.modules.rawStrings.data.libraries

import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesXmlFiles
import com.likethesalad.placeholder.modules.rawStrings.models.ValuesFolderStrings

class LibrariesValuesStringsProvider(private val librariesFilesProvider: LibrariesFilesProvider) {

    fun getValuesStringsFor(folderName: String, parent: ValuesFolderStrings?): ValuesFolderStrings {
        return ValuesFolderStrings(
            folderName,
            ValuesXmlFiles(
                librariesFilesProvider.getXmlFilesForFolder(folderName)
            ),
            parent
        )
    }
}