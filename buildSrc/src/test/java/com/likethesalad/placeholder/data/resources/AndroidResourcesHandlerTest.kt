package com.likethesalad.placeholder.data.resources

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.PathIdentityResolver
import com.likethesalad.placeholder.data.storage.AndroidFilesProvider
import com.likethesalad.placeholder.models.PathIdentity
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.StringsTemplatesModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AndroidResourcesHandlerTest {

    private lateinit var androidFilesProvider: AndroidFilesProvider
    private lateinit var pathIdentityResolver: PathIdentityResolver
    private lateinit var androidResourcesHandler: AndroidResourcesHandler

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        androidFilesProvider = mockk()
        pathIdentityResolver = mockk()
        androidResourcesHandler = AndroidResourcesHandler(androidFilesProvider, pathIdentityResolver)
    }

    @Test
    fun check_getGatheredStringsFromFile() {
        // Given:
        val file = File(javaClass.getResource("gathered_strings.json").file)

        // When:
        val result = androidResourcesHandler.getGatheredStringsFromFile(file)

        // Then:
        Truth.assertThat(result.pathIdentity).isEqualTo(
            PathIdentity("client", "values", "")
        )
        Truth.assertThat(result.mergedStrings).isEqualTo(
            listOf(
                StringResourceModel("the_name", "the content"),
                StringResourceModel("the_name2", "the content 2"),
                StringResourceModel(
                    mapOf(
                        "name" to "the_name3",
                        "translatable" to "false"
                    ), "The content 3"
                )
            )
        )
    }

    @Test
    fun check_saveGatheredStrings() {
        // Given:
        val pathIdentity = PathIdentity("client", "values", "")
        val file = temporaryFolder.newFile()
        every { pathIdentityResolver.getRawStringsFile(pathIdentity) }.returns(file)
        val mergedStrings = listOf(
            StringResourceModel("the_name", "the content"),
            StringResourceModel("the_name2", "the content 2"),
            StringResourceModel(
                mapOf(
                    "name" to "the_name3",
                    "translatable" to "false"
                ), "The content 3"
            )
        )
        val stringsGathered = StringsGatheredModel(pathIdentity, mergedStrings)

        // When:
        androidResourcesHandler.saveGatheredStrings(stringsGathered)

        // Then:
        val fileResult = File(javaClass.getResource("save_gathered_strings.json").file)
        Truth.assertThat(file.readText()).isEqualTo(fileResult.readText())
    }

    @Test
    fun check_saveResolvedStringListForValuesFolder() {
        // Given:
        val valuesFolder = "values"
        val resolvedFile = temporaryFolder.newFile()
        val stringResourceModel1 = StringResourceModel("welcome", "The welcome message for TesT")
        val stringResourceModel2 = StringResourceModel(
            mapOf(
                "name" to "message_non_translatable",
                "translatable" to "false"
            ),
            "Non translatable TesT"
        )
        val list = listOf(stringResourceModel1, stringResourceModel2)
        every { androidFilesProvider.getResolvedFile(valuesFolder) }.returns(resolvedFile)

        // When:
        androidResourcesHandler.saveResolvedStringList(list, valuesFolder)

        // Then:
        val expectedResult = File(javaClass.getResource("save_resolved_strings.xml").file).readText()
        Truth.assertThat(resolvedFile.readText()).isEqualTo(expectedResult)
    }

    @Test
    fun check_removeResolvedStringFileIfExistsForValuesFolder_exists() {
        // Given:
        val valueFolderName = "values"
        val fileMock = mockk<File>(relaxed = true)
        every { fileMock.exists() }.returns(true)
        every { androidFilesProvider.getResolvedFile(valueFolderName) }.returns(fileMock)

        // When:
        androidResourcesHandler.removeResolvedStringFileIfExists(valueFolderName)

        // Then:
        verify { fileMock.delete() }
    }

    @Test
    fun check_removeResolvedStringFileIfExistsForValuesFolder_not_exists() {
        // Given:
        val valueFolderName = "values"
        val fileMock = mockk<File>(relaxed = true)
        every { fileMock.exists() }.returns(false)
        every { androidFilesProvider.getResolvedFile(valueFolderName) }.returns(fileMock)

        // When:
        androidResourcesHandler.removeResolvedStringFileIfExists(valueFolderName)

        // Then:
        verify(exactly = 0) { fileMock.delete() }
    }

    @Test
    fun check_getTemplatesFromFile_file_exists() {
        // Given:
        val templatesFile = File(javaClass.getResource("string_templates.json").file)

        // When:
        val result = androidResourcesHandler.getTemplatesFromFile(templatesFile)

        // Then:
        Truth.assertThat(result.pathIdentity).isEqualTo(PathIdentity("client", "values-es", "-es"))
        Truth.assertThat(result.templates.size).isEqualTo(2)
        Truth.assertThat(result.templates.map { it.name }).containsExactly(
            "template_welcome",
            "template_message_non_translatable"
        )
        val nameToContent = result.templates.map { it.name to it.content }.toMap()
        Truth.assertThat(nameToContent).containsExactly(
            "template_welcome", "The welcome message for \${app_name}",
            "template_message_non_translatable", "Non translatable \${app_name}"
        )
        val nameToAttrs = result.templates.map { it.name to it.attributes }.toMap()
        Truth.assertThat(nameToAttrs).containsExactly(
            "template_welcome", mapOf("name" to "template_welcome"),
            "template_message_non_translatable", mapOf(
                "name" to "template_message_non_translatable",
                "translatable" to "false"
            )
        )
        Truth.assertThat(result.values).containsExactly(
            "app_name", "TesT"
        )
    }

    @Test
    fun check_saveTemplatesToFile() {
        // Given:
        val template1 = StringResourceModel("template_welcome", "The welcome message for \${app_name}")
        val template2 = StringResourceModel(
            mapOf(
                "name" to "template_message_non_translatable",
                "translatable" to "false"
            ), "Non translatable \${app_name}"
        )
        val pathIdentity = PathIdentity("client", "values-es", "-es")
        val templates = StringsTemplatesModel(
            pathIdentity,
            listOf(template1, template2),
            mapOf("app_name" to "TesT")
        )
        val file = temporaryFolder.newFile()
        every { pathIdentityResolver.getTemplateStringsFile(pathIdentity) }.returns(file)

        // When:
        androidResourcesHandler.saveTemplates(templates)

        // Then:
        val expectedResult = File(javaClass.getResource("string_templates.json").file).readText()
        Truth.assertThat(file.readText()).isEqualTo(expectedResult)
    }
}