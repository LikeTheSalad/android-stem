package com.likethesalad.placeholder.modules.rawStrings.data.libraries

import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesXmlFiles
import com.likethesalad.placeholder.modules.rawStrings.models.ValuesStrings

class LibrariesValuesStringsProvider(private val librariesFilesProvider: LibrariesFilesProvider) {

    fun getValuesStringsFor(folderName: String, parent: ValuesStrings?): ValuesStrings {
        return ValuesStrings(
            folderName,
            ValuesXmlFiles(
                librariesFilesProvider.getXmlFilesForFolder(folderName)
            ),
            parent
        )
    }
}