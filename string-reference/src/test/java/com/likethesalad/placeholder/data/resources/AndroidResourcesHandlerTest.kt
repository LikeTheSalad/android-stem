package com.likethesalad.placeholder.data.resources

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.placeholder.modules.common.helpers.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.resource.serializer.android.AndroidResourceSerializer
import com.likethesalad.tools.resource.api.android.AndroidResourceScope
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.serializer.ResourceSerializer
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AndroidResourcesHandlerTest {

    private lateinit var outputStringFileResolver: OutputStringFileResolver
    private lateinit var resourceSerializer: ResourceSerializer
    private lateinit var androidResourcesHandler: AndroidResourcesHandler

    private val androidScope = AndroidResourceScope(Variant.Default, Language.Default)

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        outputStringFileResolver = mockk()
        resourceSerializer = AndroidResourceSerializer()
        androidResourcesHandler =
            AndroidResourcesHandler(
                outputStringFileResolver,
                resourceSerializer
            )
    }

    @Test
    fun check_saveResolvedStringList() {
        // Given:
        val valuesFolderName = "values"
        val language = Language.Default
        val resolvedDir = temporaryFolder.newFolder("resolved")
        val resolvedFile = temporaryFolder.newFile()
        val stringResourceModel1 =
            StringAndroidResource(
                "welcome",
                "The welcome message for TesT",
                androidScope
            )
        val stringResourceModel2 =
            StringAndroidResource(
                mapOf(
                    "name" to "message_non_translatable",
                    "translatable" to "false"
                ),
                "Non translatable TesT",
                androidScope
            )
        val list = listOf(stringResourceModel1, stringResourceModel2)
        every { outputStringFileResolver.getResolvedStringsFile(resolvedDir, valuesFolderName) }.returns(resolvedFile)

        // When:
        androidResourcesHandler.saveResolvedStringList(resolvedDir, list, language)

        // Then:
        val expectedResult = File(javaClass.getResource("save_resolved_strings.xml").file).readText()
        Truth.assertThat(resolvedFile.readText()).isEqualTo(expectedResult)
    }

    @Test
    fun check_getTemplatesFromFile_file_exists() {
        // Given:
        val templatesFile = File(javaClass.getResource("string_templates.json").file)

        // When:
        val result = androidResourcesHandler.getTemplatesFromFile(templatesFile)

        // Then:
        Truth.assertThat(result.language).isEqualTo(
            Language.Custom("es")
        )
        Truth.assertThat(result.templates.size).isEqualTo(2)
        Truth.assertThat(result.templates.map { it.name() }).containsExactly(
            "template_welcome",
            "template_message_non_translatable"
        )
        val nameToContent = result.templates.associate { it.name() to it.stringValue() }
        Truth.assertThat(nameToContent).containsExactly(
            "template_welcome", "The welcome message for \${app_name}",
            "template_message_non_translatable", "Non translatable \${app_name}"
        )
        val nameToAttrs = result.templates.associate { it.name() to it.attributes().asMap() }
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
        val androidScope = AndroidResourceScope(Variant.Default, Language.Custom("es"))
        val template1 = StringAndroidResource(
            "template_welcome",
            "The welcome message for \${app_name}",
            androidScope
        )
        val template2 = StringAndroidResource(
            mapOf(
                "name" to "template_message_non_translatable",
                "translatable" to "false"
            ), "Non translatable \${app_name}",
            androidScope
        )
        val language = Language.Custom("es")
        val templates =
            StringsTemplatesModel(
                language,
                listOf(template1, template2),
                mapOf("app_name" to "TesT")
            )
        val templatesDir = temporaryFolder.newFolder("templates")
        val file = temporaryFolder.newFile()
        every { outputStringFileResolver.getTemplateStringsFile(templatesDir, "es") }.returns(file)

        // When:
        androidResourcesHandler.saveTemplates(templatesDir, templates)

        // Then:
        val expectedResult = File(javaClass.getResource("string_templates.json").file).readText()
        Truth.assertThat(file.readText()).isEqualTo(expectedResult)
    }
}