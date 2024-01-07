package com.likethesalad.stem.functionaltest.provider

import com.google.common.truth.Truth
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItem
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import com.likethesalad.android.templates.provider.api.TemplatesProvider
import com.likethesalad.stem.functionaltest.testtools.BasePluginTest
import com.likethesalad.stem.functionaltest.testtools.StemConfigBlock
import com.likethesalad.stem.utils.TemplatesProviderLoader
import com.likethesalad.tools.functional.testing.android.descriptor.AndroidLibProjectDescriptor
import com.likethesalad.tools.functional.testing.blocks.impl.plugins.GradlePluginDeclaration
import com.likethesalad.tools.functional.testing.descriptor.ProjectDescriptor
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
import com.likethesalad.tools.plugin.metadata.consumer.PluginMetadataProvider
import net.lingala.zip4j.ZipFile
import org.junit.Test
import java.io.File

class TemplatesProviderPluginTest : BasePluginTest() {

    private val inputAssetsRoot = TestAssetsProvider("provider")

    @Test
    fun `Create service for basic project`() {
        val projectName = "basic"
        val projectDescriptor = setUpProjectDescriptor(projectName)
        val project = createProject(projectDescriptor)

        project.runGradle(projectName, "assembleDebug")

        val provider = getTemplatesProvider(projectDescriptor, "debug")
        commonVerification(provider, projectName)
        assertTemplatesContainExactly(
            provider,
            TemplateItem("someTemplate", "string")
        )
    }

    @Test
    fun `Keep only one service per project after re-running it`() {
        val projectName = "basic_modified_before"
        val basicProjectDescriptor = setUpProjectDescriptor(projectName)
        val project = createProject(basicProjectDescriptor)

        project.runGradle(projectName, "assembleDebug")

        val provider = getTemplatesProvider(basicProjectDescriptor, "debug")
        commonVerification(provider, projectName)
        assertTemplatesContainExactly(
            provider,
            TemplateItem("someTemplate", "string")
        )

        // Second run
        val secondBasicProjectDescriptor =
            setUpProjectDescriptor(projectName, getInputTestAsset("basic_modified_after"))
        basicProjectDescriptor.projectDirBuilder.clearFilesCreated()
        secondBasicProjectDescriptor.projectDirBuilder.buildDirectory(getTempFile(projectName))

        project.runGradle(projectName, "assembleDebug")

        val provider2 = getTemplatesProvider(secondBasicProjectDescriptor, "debug")
        commonVerification(provider2, projectName)
        assertTemplatesContainExactly(
            provider2,
            TemplateItem("someTemplate", "string"),
            TemplateItem("someTemplate2", "string")
        )
    }

    @Test
    fun `Take only templates from main strings`() {
        val projectName = "multi_language"
        val projectDescriptor = setUpProjectDescriptor(projectName)
        val project = createProject(projectDescriptor)

        project.runGradle(projectName, "assembleDebug")

        val provider = getTemplatesProvider(projectDescriptor, "debug")
        commonVerification(provider, projectName)
        assertTemplatesContainExactly(
            provider,
            TemplateItem("someTemplate", "string")
        )
    }

    @Test
    fun `Take templates from all languages and main strings if feature enabled`() {
        val projectName = "multi_language_all"
        val projectDescriptor = setUpProjectDescriptor(projectName, config = StemConfigBlock(true))
        val project = createProject(projectDescriptor)

        project.runGradle(projectName, "assembleDebug")

        val provider = getTemplatesProvider(projectDescriptor, "debug")
        commonVerification(provider, projectName)
        assertTemplatesContainExactly(
            provider,
            TemplateItem("someTemplate", "string"),
            TemplateItem("someLanguageOnlyTemplate", "string")
        )
    }

    private fun getTemplatesProvider(project: ProjectDescriptor, variantName: String): TemplatesProvider {
        val aarFile = getAarFile(project.projectName, variantName)
        val jarFile = extractJar(aarFile)
        val templateProviders = extractProviders(jarFile)
        Truth.assertThat(templateProviders.size).isEqualTo(1)
        val provider = templateProviders.first()
        jarFile.delete()
        return provider
    }

    private fun commonVerification(provider: TemplatesProvider, projectId: String) {
        Truth.assertThat(provider.getId()).isEqualTo(projectId)
        Truth.assertThat(provider.getPluginVersion()).isEqualTo(getProviderVersion())
    }

    private fun assertTemplatesContainExactly(
        provider: TemplatesProvider,
        vararg templateItems: TemplateItem
    ) {
        val templates = TemplateItemsSerializer.deserialize(provider.getTemplates())
        Truth.assertThat(templates).containsExactly(*templateItems)
    }

    private fun extractProviders(jarFile: File): List<TemplatesProvider> {
        return TemplatesProviderLoader.load(listOf(jarFile))
    }

    private fun extractJar(aarFile: File): File {
        val destinationDir = aarFile.parentFile
        val jarFileName = "classes.jar"
        ZipFile(aarFile).extractFile(jarFileName, destinationDir.absolutePath)

        return File(destinationDir, jarFileName)
    }

    private fun getAarFile(projectName: String, variantName: String): File {
        val projectDir = getTempFile(projectName)
        return File(projectDir, "build/outputs/aar/$projectName-${variantName}.aar")
    }

    private fun getProviderVersion(): String {
        return PluginMetadataProvider.getInstance("com.likethesalad.android_templates-provider-plugin")
            .provide().version
    }

    private fun setUpProjectDescriptor(
        projectName: String,
        inputDir: File = inputAssetsRoot.getFile(projectName),
        config: StemConfigBlock? = null
    ): ProjectDescriptor {
        val blockItems = if (config != null) listOf(config) else emptyList()
        val libProjectDescriptor = AndroidLibProjectDescriptor(
            projectName, inputDir, blockItems
        )
        libProjectDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("com.likethesalad.stem-library"))
        return libProjectDescriptor
    }

    private fun getInputTestAsset(inputDirName: String): File {
        return inputAssetsRoot.getFile(inputDirName)
    }
}