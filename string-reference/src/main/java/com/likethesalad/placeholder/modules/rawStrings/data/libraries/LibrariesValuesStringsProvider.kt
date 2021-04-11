package com.likethesalad.placeholder.modules.rawStrings.data.libraries

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidConfigHelper
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesFolderXmlFiles
import com.likethesalad.placeholder.modules.rawStrings.models.ValuesFolderStrings

@AutoFactory
class LibrariesValuesStringsProvider(
    private val androidConfigHelper: AndroidConfigHelper,
    @Provided private val librariesFilesProviderFactory: LibrariesFilesProviderFactory
) {
    private val librariesFilesProvider by lazy { librariesFilesProviderFactory.create(androidConfigHelper) }

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