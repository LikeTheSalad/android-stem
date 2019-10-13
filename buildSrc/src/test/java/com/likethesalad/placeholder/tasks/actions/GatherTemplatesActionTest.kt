package com.likethesalad.placeholder.tasks.actions

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.AndroidFilesProvider
import com.likethesalad.placeholder.data.storage.FilesProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.io.File

class GatherTemplatesActionTest {

    private lateinit var gatherTemplatesAction: GatherTemplatesAction
    private lateinit var filesProvider: FilesProvider
    private lateinit var resourcesHandler: ResourcesHandler

    @Before
    fun setUp() {
        filesProvider = mockk()
        resourcesHandler = spyk(AndroidResourcesHandler(filesProvider))
        gatherTemplatesAction = GatherTemplatesAction(filesProvider, resourcesHandler)
    }

    @Test
    fun check_getStringFiles() {
        // Given:
        val files = listOf<File>(mockk())
        every { filesProvider.getAllGatheredStringsFiles() } returns files

        // When:
        val result = gatherTemplatesAction.getStringFiles()

        // Then:
        verify { filesProvider.getAllGatheredStringsFiles() }
        Truth.assertThat(result).isEqualTo(files)
    }

    @Test
    fun check_getTemplatesFiles() {
        // Given:
        val files = listOf<File>(mockk())
        every { filesProvider.getAllExpectedTemplatesFiles() } returns files

        // When:
        val result = gatherTemplatesAction.getTemplatesFiles()

        // Then:
        verify { filesProvider.getAllExpectedTemplatesFiles() }
        Truth.assertThat(result).isEqualTo(files)
    }

    @Test
    fun check_gatherTemplateStrings() {
        // Given:
        val baseGatheredStringsFile = mockk<File>()
        every {
            filesProvider.getGatheredStringsFileForFolder(AndroidFilesProvider.BASE_VALUES_FOLDER_NAME)
        } returns baseGatheredStringsFile
    }
}