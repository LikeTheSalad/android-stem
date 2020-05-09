package com.likethesalad.placeholder.data.storage.libraries

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.AndroidConfigHelper
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.file.FileCollection
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LibrariesFilesProviderTest {

    private lateinit var androidConfigHelper: AndroidConfigHelper
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        androidConfigHelper = mockk()
    }

    @Test
    fun getXmlFilesForFolder_canUseDependencies_true() {
        val librariesFilesProvider = LibrariesFilesProvider(androidConfigHelper, true)
        verifyXmlFilesForFolder("values", librariesFilesProvider, false)
        verifyXmlFilesForFolder("values-es", librariesFilesProvider, false)
        verifyXmlFilesForFolder("values-it", librariesFilesProvider, false)
    }

    @Test
    fun getXmlFilesForFolder_canUseDependencies_false() {
        val librariesFilesProvider = LibrariesFilesProvider(androidConfigHelper, false)
        verifyXmlFilesForFolder("values", librariesFilesProvider, true)
        verifyXmlFilesForFolder("values-es", librariesFilesProvider, true)
        verifyXmlFilesForFolder("values-it", librariesFilesProvider, true)
    }

    private fun verifyXmlFilesForFolder(
        folderName: String,
        librariesFilesProvider: LibrariesFilesProvider,
        emptyResponse: Boolean
    ) {
        val libName1 = "library1"
        val libName2 = "library2"
        val libName3 = "library3"
        val libName4 = "library4"
        val libraryResDir1 = createResDirWithExistingXmlValue(libName1, folderName)
        val libraryResDir2 = createResDirWithExistingXmlValue(libName2, folderName)
        val libraryResDir3 = createEmptyResDir(libName3)
        val libraryResDir4 = createEmptyLibraryDir(libName4)
        val fileCollection = mockk<FileCollection>()
        every { androidConfigHelper.librariesResDirs }.returns(
            fileCollection
        )
        every { fileCollection.files }.returns(
            setOf(
                libraryResDir1, libraryResDir2, libraryResDir3, libraryResDir4
            )
        )

        val files = librariesFilesProvider.getXmlFilesForFolder(folderName)

        if (emptyResponse) {
            Truth.assertThat(files).isEmpty()
        } else {
            Truth.assertThat(files).containsExactly(
                File(temporaryFolder.root, "$libName1/res/$folderName/$folderName.xml"),
                File(temporaryFolder.root, "$libName2/res/$folderName/$folderName.xml")
            )
        }
    }

    private fun createResDirWithExistingXmlValue(libraryDir: String, valuesFolderName: String): File {
        val resDir = createEmptyResDir(libraryDir)
        temporaryFolder.newFolder(libraryDir, "res", valuesFolderName)
        temporaryFolder.newFile("$libraryDir/res/$valuesFolderName/$valuesFolderName.xml")
        return resDir
    }

    private fun createEmptyResDir(libraryDir: String): File {
        return getResDirFor(libraryDir).takeIf { it.exists() }
            ?: temporaryFolder.newFolder(libraryDir, "res")
    }

    private fun getResDirFor(libraryDir: String): File {
        return File(createEmptyLibraryDir(libraryDir), "res")
    }

    private fun createEmptyLibraryDir(libraryDirName: String): File {
        return File(temporaryFolder.root, libraryDirName).takeIf {
            it.exists()
        } ?: temporaryFolder.newFolder(libraryDirName)
    }
}