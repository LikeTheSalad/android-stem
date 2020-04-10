package com.likethesalad.placeholder.data.storage.libraries

import org.junit.Before
import org.junit.Test

class LibrariesFilesProviderTest {

    private lateinit var librariesFilesProvider: LibrariesFilesProvider

    @Before
    fun setUp() {
        librariesFilesProvider = LibrariesFilesProvider()
    }

    @Test
    fun getXmlFilesForFolder() {
        val folderName = "values"

        val files = librariesFilesProvider.getXmlFilesForFolder(folderName)


    }
}