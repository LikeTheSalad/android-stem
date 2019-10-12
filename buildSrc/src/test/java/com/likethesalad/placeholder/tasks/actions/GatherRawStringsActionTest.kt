package com.likethesalad.placeholder.tasks.actions

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.data.storage.AndroidFilesProvider
import com.likethesalad.placeholder.models.raw.MainValuesRawFiles
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class GatherRawStringsActionTest {
    private lateinit var gatherRawStringsAction: GatherRawStringsAction
    private lateinit var filesProvider: AndroidFilesProvider
    private lateinit var resourcesHandler: AndroidResourcesHandler

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        filesProvider = mockk()
        resourcesHandler = mockk()
        gatherRawStringsAction = GatherRawStringsAction(filesProvider, resourcesHandler)
    }

    @Test
    fun check_getInputFiles() {
        // Given:
        val folderName = "values"
        val xmlFiles = mutableListOf<File>()
        xmlFiles.add(getRawMainFile(folderName, "strings_1.xml"))
        xmlFiles.add(getRawMainFile(folderName, "strings_2.xml"))
        val mainRawFile = MainValuesRawFiles("values", xmlFiles)
        every { filesProvider.getAllFoldersRawResourcesFiles() }.returns(listOf(mainRawFile))

        // When:
        val result = gatherRawStringsAction.getInputFiles()

        // Then:
        Truth.assertThat(result).containsExactlyElementsIn(xmlFiles)
    }

    private fun getRawMainFile(folderName: String, fileName: String): File {
        return File(javaClass.getResource("raw_files/main_files/$folderName/$fileName").file)
    }
}