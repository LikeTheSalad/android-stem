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
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import java.util.ServiceLoader
import java.util.jar.JarFile

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
        val url = jarFile.toURI().toURL()
        val templateProviders = extractProviders(url)
        Truth.assertThat(templateProviders.size).isEqualTo(1)
        val provider = templateProviders.first()
        closeJar(url)
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

    private fun extractProviders(url: URL): List<TemplatesProvider> {
        val classLoader = getClassLoader(url)
        val serviceLoader = ServiceLoader.load(TemplatesProvider::class.java, classLoader)
        return serviceLoader.toList()
    }

    private fun getClassLoader(url: URL): ClassLoader {
        val urls = arrayOf(url)
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

    private fun closeJar(url: URL?) {
        // JarFileFactory jarFactory = JarFileFactory.getInstance();
        val jarFactoryClazz = Class.forName("sun.net.www.protocol.jar.JarFileFactory")
        val getInstance: Method = jarFactoryClazz.getMethod("getInstance")
        getInstance.isAccessible = true
        val jarFactory: Any = getInstance.invoke(jarFactoryClazz)

        // JarFile jarFile = jarFactory.get(url);
        val get: Method = jarFactoryClazz.getMethod("get", URL::class.java)
        get.isAccessible = true
        val jarFile: Any = get.invoke(jarFactory, url)

        // jarFactory.close(jarFile);
        val close: Method = jarFactoryClazz.getMethod("close", JarFile::class.java)
        close.isAccessible = true
        close.invoke(jarFactory, jarFile)

        // jarFile.close();
        (jarFile as JarFile).close()
    }
}