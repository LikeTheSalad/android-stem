package com.likethesalad.stem.locator.entrypoints.common.utils

import com.likethesalad.android.templates.common.utils.CommonConstants.PROVIDER_PACKAGE_NAME
import com.likethesalad.android.templates.provider.api.TemplatesProvider
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import java.io.File
import org.gradle.api.file.FileCollection

class TemplatesProviderJarsFinder(val allJarFiles: FileCollection) {

    val templateProviderJars: List<File> by lazy {
        findTemplateProviderJars(allJarFiles.files)
    }

    private fun findTemplateProviderJars(allJars: Set<File>): List<File> {
        val scanResult = scanJars(allJars) ?: return emptyList()

        return scanResult.use {
            scanResult.getClassesImplementing(TemplatesProvider::class.java.name).map { info ->
                info.classpathElementFile
            }
        }
    }

    private fun scanJars(jarFiles: Set<File>): ScanResult? {
        if (jarFiles.isEmpty()) {
            return null
        }
        return ClassGraph()
            .acceptPackages(PROVIDER_PACKAGE_NAME)
            .overrideClasspath(jarFiles)
            .scan()
    }
}