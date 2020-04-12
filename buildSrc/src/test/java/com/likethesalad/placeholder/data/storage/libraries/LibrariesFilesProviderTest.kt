package com.likethesalad.placeholder.data.storage.libraries

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.storage.libraries.helpers.AndroidLibrariesProvider
import com.likethesalad.placeholder.models.AndroidLibrary
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LibrariesFilesProviderTest {

    private lateinit var androidLibrariesProvider: AndroidLibrariesProvider
    private lateinit var librariesFilesProvider: LibrariesFilesProvider
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        androidLibrariesProvider = mockk()
        librariesFilesProvider = LibrariesFilesProvider(androidLibrariesProvider)
    }

    @Test
    fun getXmlFilesForFolder() {
        verifyXmlFilesForFolder("values")
        verifyXmlFilesForFolder("values-es")
        verifyXmlFilesForFolder("values-it")
    }

    private fun verifyXmlFilesForFolder(folderName: String) {
        val libName1 = "library1"
        val libName2 = "library2"
        val libName3 = "library3"
        val libName4 = "library4"
        val library1 = AndroidLibrary(libName1, createResDirWithExistingXmlValue(libName1, folderName))
        val library2 = AndroidLibrary(libName2, createResDirWithExistingXmlValue(libName2, folderName))
        val library3 = AndroidLibrary(libName3, createEmptyResDir(libName3))
        val library4 = AndroidLibrary(libName4, createEmptyLibraryDir(libName4))
        every { androidLibrariesProvider.getAndroidLibraries() }.returns(
            setOf(
                library1, library2, library3, library4
            )
        )

        val files = librariesFilesProvider.getXmlFilesForFolder(folderName)

        Truth.assertThat(files).containsExactly(
            File(temporaryFolder.root, "$libName1/res/$folderName/$folderName.xml"),
            File(temporaryFolder.root, "$libName2/res/$folderName/$folderName.xml")
        )
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