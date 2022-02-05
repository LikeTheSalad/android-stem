package com.likethesalad.placeholder.provider

import com.google.common.truth.Truth
import com.likethesalad.android.templates.provider.api.TemplatesProvider
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
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

        val aarFile = getAarFile("debug")
        val jarFile = extractJar(aarFile)
        val templateProviders = extractProviders(jarFile)
        Truth.assertThat(templateProviders.size).isEqualTo(1)
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
}