package com.likethesalad.placeholder.provider

import com.google.common.truth.Truth
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItem
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import com.likethesalad.android.templates.provider.api.TemplatesProvider
import com.likethesalad.android_templates.provider.plugin.generated.BuildConfig
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
import com.likethesalad.tools.plugin.metadata.consumer.PluginMetadataProvider
import net.lingala.zip4j.ZipFile
import org.junit.Test
import java.io.File
import java.net.URLClassLoader
import java.util.ServiceLoader

class TemplatesProviderPluginTest : BaseGradleTest() {

    private val inputAssetsRoot = TestAssetsProvider("functionalTest", "provider")

    @Test
    fun `Create service for basic project`() {
        setUpProject("basic")

        runCommand("assembleDebug")

        val provider = getTemplatesProvider("debug")
        commonVerification(provider, "basic")
        assertTemplatesContainExactly(
            provider,
            TemplateItem("someTemplate", "string")
        )
    }

    @Test
    fun `Keep only one service per project after re-running it`() {
        val projectName = "basic"
        setUpProject(projectName)

        runCommand("assembleDebug")

        val provider = getTemplatesProvider("debug")
        commonVerification(provider, projectName)
        assertTemplatesContainExactly(
            provider,
            TemplateItem("someTemplate", "string")
        )

        // Second run
        removeTestAssetsFromProject()
        setUpProject(projectName, "basic-modified")

        runCommand("assembleDebug")

        val provider2 = getTemplatesProvider("debug")
        commonVerification(provider2, projectName)
        assertTemplatesContainExactly(
            provider2,
            TemplateItem("someTemplate", "string"),
            TemplateItem("someTemplate2", "string")
        )
    }

    private fun getTemplatesProvider(variantName: String): TemplatesProvider {
        val aarFile = getAarFile(variantName)
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
        val classLoader = getClassLoader(jarFile)
        val serviceLoader = ServiceLoader.load(TemplatesProvider::class.java, classLoader)
        return serviceLoader.toList()
    }

    private fun getClassLoader(jarFile: File): ClassLoader {
        val urls = arrayOf(jarFile.toURI().toURL())
        return URLClassLoader(urls, javaClass.classLoader)
    }

    private fun extractJar(aarFile: File): File {
        val destinationDir = aarFile.parentFile
        val jarFileName = "classes.jar"
        ZipFile(aarFile).extractFile(jarFileName, destinationDir.absolutePath)

        return File(destinationDir, jarFileName)
    }

    private fun getAarFile(variantName: String): File {
        val projectName = projectDir.name
        return File(projectDir, "build/outputs/aar/$projectName-${variantName}.aar")
    }

    override fun getSourceDir(name: String): File {
        return inputAssetsRoot.getAssetFile(name)
    }

    private fun getProviderVersion(): String {
        return PluginMetadataProvider.getInstance(BuildConfig.METADATA_PROPERTIES_ID).provide().version
    }
}