package com.likethesalad.stem.provider

import com.google.common.truth.Truth
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItem
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import com.likethesalad.android.templates.provider.api.TemplatesProvider
import com.likethesalad.android_templates.provider.plugin.generated.BuildConfig
import com.likethesalad.stem.testtools.StemConfigBlock
import com.likethesalad.stem.testtools.TestConstants.GRADLE_VERSION
import com.likethesalad.stem.utils.TemplatesProviderLoader
import com.likethesalad.tools.functional.testing.AndroidProjectTest
import com.likethesalad.tools.functional.testing.layout.AndroidLibProjectDescriptor
import com.likethesalad.tools.functional.testing.layout.ProjectDescriptor
import com.likethesalad.tools.functional.testing.layout.items.impl.plugins.GradlePluginDeclaration
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
import com.likethesalad.tools.plugin.metadata.consumer.PluginMetadataProvider
import net.lingala.zip4j.ZipFile
import org.junit.Test
import java.io.File

class TemplatesProviderPluginTest : AndroidProjectTest() {

    private val inputAssetsRoot = TestAssetsProvider("functionalTest", "provider")

    @Test
    fun `Create service for basic project`() {
        val project = setUpProject("basic")

        runCommand(project, "assembleDebug")

        val provider = getTemplatesProvider(project, "debug")
        commonVerification(provider, "basic")
        assertTemplatesContainExactly(
            provider,
            TemplateItem("someTemplate", "string")
        )
    }

    @Test
    fun `Keep only one service per project after re-running it`() {
        val projectName = "basic_modified_before"
        val basicProject = setUpProject(projectName)

        runCommand(basicProject, "assembleDebug")

        val provider = getTemplatesProvider(basicProject, "debug")
        commonVerification(provider, projectName)
        assertTemplatesContainExactly(
            provider,
            TemplateItem("someTemplate", "string")
        )

        // Second run
        val secondBasicProject = setUpProject(projectName, "basic_modified_after")
        basicProject.projectDirBuilder.clearFilesCreated()
        secondBasicProject.projectDirBuilder.buildDirectory(getProjectDir(projectName))

        buildProject(getCommandArgs("assembleDebug"), projectName)

        val provider2 = getTemplatesProvider(secondBasicProject, "debug")
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
        val project = setUpProject(projectName)

        runCommand(project, "assembleDebug")

        val provider = getTemplatesProvider(project, "debug")
        commonVerification(provider, projectName)
        assertTemplatesContainExactly(
            provider,
            TemplateItem("someTemplate", "string")
        )
    }

    @Test
    fun `Take templates from all languages and main strings if feature enabled`() {
        val projectName = "multi_language_all"
        val project = setUpProject(projectName, config = StemConfigBlock(true))

        runCommand(project, "assembleDebug")

        val provider = getTemplatesProvider(project, "debug")
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
        val templates = TemplateItemsSerializer().deserialize(provider.getTemplates())
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
        val projectDir = getProjectDir(projectName)
        return File(projectDir, "build/outputs/aar/$projectName-${variantName}.aar")
    }

    private fun getProviderVersion(): String {
        return PluginMetadataProvider.getInstance(BuildConfig.METADATA_PROPERTIES_ID).provide().version
    }

    private fun runCommand(projectDescriptor: ProjectDescriptor, command: String) {
        val buildResult = createProjectAndBuild(projectDescriptor, getCommandArgs(command))
        println("BUILD: ${buildResult.output}")//todo delete
    }

    private fun getCommandArgs(commandStr: String): List<String> {
        return commandStr.split(Regex("[\\s\\t]+"))
    }

    private fun setUpProject(
        projectName: String,
        sourceDirName: String = projectName,
        config: StemConfigBlock? = null
    ): ProjectDescriptor {
        val inputDir = inputAssetsRoot.getAssetFile(sourceDirName)

        val blockItems = if (config != null) listOf(config) else emptyList()
        val libProjectDescriptor = AndroidLibProjectDescriptor(
            projectName, inputDir, "0.0.0",
            blockItems
        )
        libProjectDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("com.likethesalad.stem-library"))
        return libProjectDescriptor
    }

    override fun getGradleVersion(): String {
        return GRADLE_VERSION
    }
}