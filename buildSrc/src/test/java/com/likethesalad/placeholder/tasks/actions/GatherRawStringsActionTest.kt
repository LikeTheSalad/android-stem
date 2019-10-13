package com.likethesalad.placeholder.tasks.actions

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.data.storage.AndroidFilesProvider
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.raw.FlavorValuesRawFiles
import com.likethesalad.placeholder.models.raw.MainValuesRawFiles
import io.mockk.*
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
    fun check_getInputFiles_single_values() {
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

    @Test
    fun check_getInputFiles_many_values() {
        // Given:
        val folder1 = "values"
        val folder1XmlFiles = mutableListOf<File>()
        folder1XmlFiles.add(getRawMainFile(folder1, "strings_1.xml"))
        folder1XmlFiles.add(getRawMainFile(folder1, "strings_2.xml"))
        val folder2 = "values-es"
        val folder2XmlFiles = mutableListOf<File>()
        folder2XmlFiles.add(getRawMainFile(folder2, "strings_1.xml"))
        folder2XmlFiles.add(getRawMainFile(folder2, "strings_2.xml"))

        val mainRawFile1 = MainValuesRawFiles(folder1, folder1XmlFiles)
        val mainRawFile2 = MainValuesRawFiles(folder2, folder2XmlFiles)

        val allXmlFiles = mutableListOf<File>()
        allXmlFiles.addAll(folder1XmlFiles)
        allXmlFiles.addAll(folder2XmlFiles)

        every { filesProvider.getAllFoldersRawResourcesFiles() }.returns(listOf(mainRawFile1, mainRawFile2))

        // When:
        val result = gatherRawStringsAction.getInputFiles()

        // Then:
        Truth.assertThat(result).containsExactlyElementsIn(allXmlFiles)
    }

    @Test
    fun check_getInputFiles_with_flavor() {
        // Given:
        val mainValuesFolder = "values"
        val mainValuesFiles = mutableListOf<File>()
        mainValuesFiles.add(getRawMainFile(mainValuesFolder, "strings_1.xml"))
        mainValuesFiles.add(getRawMainFile(mainValuesFolder, "strings_2.xml"))
        val flavorValuesFolder = "values"
        val flavorValuesFiles = mutableListOf<File>()
        flavorValuesFiles.add(getRawFlavorFile(flavorValuesFolder, "flavor_strings_1.xml"))
        flavorValuesFiles.add(getRawFlavorFile(flavorValuesFolder, "flavor_strings_2.xml"))

        val flavorRawFile = FlavorValuesRawFiles("demo", flavorValuesFolder, mainValuesFiles, flavorValuesFiles)

        val allXmlFiles = mutableListOf<File>()
        allXmlFiles.addAll(mainValuesFiles)
        allXmlFiles.addAll(flavorValuesFiles)

        every { filesProvider.getAllFoldersRawResourcesFiles() }.returns(listOf(flavorRawFile))

        // When:
        val result = gatherRawStringsAction.getInputFiles()

        // Then:
        Truth.assertThat(result).containsExactlyElementsIn(allXmlFiles)
    }

    @Test
    fun check_getOutputFile() {
        // Given:
        val outputFile = mockk<File>()
        every { filesProvider.getGatheredStringsFileForFolder(AndroidFilesProvider.BASE_VALUES_FOLDER_NAME) }.returns(
            outputFile
        )

        // When:
        val result = gatherRawStringsAction.getOutputFile()

        // Then:
        Truth.assertThat(result).isEqualTo(outputFile)
    }

    @Test
    fun check_gatherStrings() {
        // Given:
        val folderName = "values"
        val xmlFiles = mutableListOf<File>()
        xmlFiles.add(getRawMainFile(folderName, "strings_1.xml"))
        val mainRawFile = MainValuesRawFiles("values", xmlFiles)
        val savedStringsSlot = slot<StringsGatheredModel>()
        every { filesProvider.getAllFoldersRawResourcesFiles() }.returns(listOf(mainRawFile))
        every { resourcesHandler.saveGatheredStrings(capture(savedStringsSlot)) } just Runs

        // When:
        gatherRawStringsAction.gatherStrings()

        // Then:
        Truth.assertThat(savedStringsSlot.isCaptured).isTrue()
        val capturedStringsGathered = savedStringsSlot.captured
        Truth.assertThat(capturedStringsGathered.valueFolderName).isEqualTo(folderName)
        Truth.assertThat(capturedStringsGathered.mainStrings).containsExactly(
            StringResourceModel("welcome_1", "The welcome message for TesT"),
            StringResourceModel(
                mapOf("name" to "message_non_translatable_1", "translatable" to "false"),
                "Non translatable TesT"
            )
        )
        Truth.assertThat(capturedStringsGathered.complementaryStrings).isEmpty()
    }

    @Test
    fun check_gatherStrings_empty() {
        // Given:
        val folderName = "values"
        val xmlFiles = mutableListOf<File>()
        xmlFiles.add(getRawMainFile(folderName, "empty_strings.xml"))
        val mainRawFile = MainValuesRawFiles("values", xmlFiles)
        every { filesProvider.getAllFoldersRawResourcesFiles() }.returns(listOf(mainRawFile))

        // When:
        gatherRawStringsAction.gatherStrings()

        // Then:
        verify(exactly = 0) { resourcesHandler.saveGatheredStrings(any()) }
    }

    @Test
    fun check_gatherStrings_many_files() {
        // Given:
        val folderName = "values"
        val xmlFiles = mutableListOf<File>()
        xmlFiles.add(getRawMainFile(folderName, "strings_1.xml"))
        xmlFiles.add(getRawMainFile(folderName, "strings_2.xml"))
        val mainRawFile = MainValuesRawFiles("values", xmlFiles)
        val savedStringsSlot = slot<StringsGatheredModel>()
        every { filesProvider.getAllFoldersRawResourcesFiles() }.returns(listOf(mainRawFile))
        every { resourcesHandler.saveGatheredStrings(capture(savedStringsSlot)) } just Runs

        // When:
        gatherRawStringsAction.gatherStrings()

        // Then:
        Truth.assertThat(savedStringsSlot.isCaptured).isTrue()
        val captured = savedStringsSlot.captured
        Truth.assertThat(captured.valueFolderName).isEqualTo(folderName)
        Truth.assertThat(captured.mainStrings).containsExactly(
            StringResourceModel("welcome_1", "The welcome message for TesT"),
            StringResourceModel(
                mapOf("name" to "message_non_translatable_1", "translatable" to "false"),
                "Non translatable TesT"
            ),
            StringResourceModel("welcome_2", "The welcome2 message for TesT"),
            StringResourceModel(
                mapOf("name" to "message_non_translatable_2", "translatable" to "false"),
                "Non translatable2 TesT"
            )
        )
    }

    @Test
    fun check_gatherStrings_with_flavor() {
        // Given:
        val mainValuesFolder = "values"
        val mainValuesFiles = mutableListOf<File>()
        mainValuesFiles.add(getRawMainFile(mainValuesFolder, "strings_1.xml"))
        val flavorValuesFolder = "values"
        val flavorValuesFiles = mutableListOf<File>()
        flavorValuesFiles.add(getRawFlavorFile(flavorValuesFolder, "flavor_strings_1.xml"))

        val flavorRawFile = FlavorValuesRawFiles("demo", flavorValuesFolder, mainValuesFiles, flavorValuesFiles)
        val savedStringsSlot = slot<StringsGatheredModel>()
        every { filesProvider.getAllFoldersRawResourcesFiles() }.returns(listOf(flavorRawFile))
        every { resourcesHandler.saveGatheredStrings(capture(savedStringsSlot)) } just Runs

        // When:
        gatherRawStringsAction.gatherStrings()

        // Then:
        Truth.assertThat(savedStringsSlot.isCaptured).isTrue()
        val capturedStringsGathered = savedStringsSlot.captured
        Truth.assertThat(capturedStringsGathered.valueFolderName).isEqualTo(flavorValuesFolder)
        Truth.assertThat(capturedStringsGathered.mainStrings).containsExactly(
            StringResourceModel("welcome_1", "The welcome flavor message for TesT"),
            StringResourceModel(
                mapOf("name" to "message_non_translatable_1", "translatable" to "false"),
                "Non translatable flavor TesT"
            )
        )
        Truth.assertThat(capturedStringsGathered.complementaryStrings).containsExactly(
            listOf(
                StringResourceModel("welcome_1", "The welcome message for TesT"),
                StringResourceModel(
                    mapOf("name" to "message_non_translatable_1", "translatable" to "false"),
                    "Non translatable TesT"
                )
            )
        )
    }

    private fun getRawMainFile(folderName: String, fileName: String): File {
        return File(javaClass.getResource("gatherRawStrings/raw_files/main_files/$folderName/$fileName").file)
    }

    private fun getRawFlavorFile(folderName: String, fileName: String): File {
        return File(javaClass.getResource("gatherRawStrings/raw_files/flavor_files/$folderName/$fileName").file)
    }
}