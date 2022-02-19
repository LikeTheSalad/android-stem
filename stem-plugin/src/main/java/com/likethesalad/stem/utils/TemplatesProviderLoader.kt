package com.likethesalad.stem.utils

import com.likethesalad.android.templates.common.utils.CommonConstants
import com.likethesalad.android.templates.provider.api.TemplatesProvider
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import java.io.File
import java.net.URLClassLoader

object TemplatesProviderLoader {

    @Suppress("UNCHECKED_CAST")
    fun load(jars: List<File>): List<TemplatesProvider> {
        if (jars.isEmpty()) {
            return emptyList()
        }

        val scanResult = scanJars(jars.toSet())
        return scanResult.use { result ->
            result.getClassesImplementing(TemplatesProvider::class.java).loadClasses().map {
                it.newInstance()
            }
        } as List<TemplatesProvider>
    }

    private fun scanJars(jarFiles: Set<File>): ScanResult {
        return ClassGraph()
            .acceptPackages(CommonConstants.PROVIDER_PACKAGE_NAME)
            .overrideClassLoaders(createLocalClassloader(jarFiles))
            .scan()
    }

    private fun createLocalClassloader(jarFiles: Set<File>): ClassLoader {
        val urls = jarFiles.map { it.toURI().toURL() }
        return URLClassLoader(urls.toTypedArray(), javaClass.classLoader)
    }
}