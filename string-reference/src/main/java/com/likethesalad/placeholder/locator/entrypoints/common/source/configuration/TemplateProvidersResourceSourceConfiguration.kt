package com.likethesalad.placeholder.locator.entrypoints.common.source.configuration

import com.likethesalad.placeholder.locator.entrypoints.common.utils.TemplatesProviderJarsFinder
import com.likethesalad.placeholder.providers.ProjectDirsProvider
import com.likethesalad.tools.resource.collector.android.data.resdir.ResDir
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.impl.AndroidLibrariesSourceConfiguration
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class TemplateProvidersResourceSourceConfiguration @AssistedInject constructor(
    @Assisted variantTree: VariantTree,
    @Assisted private val templatesProviderJarsFinder: TemplatesProviderJarsFinder,
    private val projectDirsProvider: ProjectDirsProvider
) : AndroidLibrariesSourceConfiguration(variantTree) {

    @AssistedFactory
    interface Factory {
        fun create(
            variantTree: VariantTree,
            templatesProviderJarsFinder: TemplatesProviderJarsFinder
        ): TemplateProvidersResourceSourceConfiguration
    }

    companion object {
        private val LOCAL_BUILD_DIR_PATTERN = Regex("^.+/build/")
    }

    override fun getResDirs(): List<ResDir> {
        val allResDirs = super.getResDirs()
        return filterTemplateProviderResDirs(allResDirs)
    }

    override fun getSourceFiles(): Iterable<File> {
        return super.getSourceFiles().plus(templatesProviderJarsFinder.allJarFiles)
    }

    private fun filterTemplateProviderResDirs(allResDirs: List<ResDir>): List<ResDir> {
        val templatesProvidersPattern = createTemplatesProviderPathStartPattern()
        return allResDirs.filter {
            templatesProvidersPattern.matches(it.dir.absolutePath)
        }
    }

    private fun createTemplatesProviderPathStartPattern(): Regex {
        val templatesProviderJars = templatesProviderJarsFinder.templateProviderJars
        val localJars = filterLocalJars(templatesProviderJars)
        val externalJars = templatesProviderJars.minus(localJars)

        val resDirPatterns = getLocalResDirPatterns(localJars) + getExternalResDirPatterns(externalJars)
        return Regex(resDirPatterns.joinToString("|"))
    }

    private fun getExternalResDirPatterns(externalJars: List<File>): List<String> {
        val fileNames = externalJars.map { it.nameWithoutExtension.substringBeforeLast("-runtime") }
        return fileNames.map { ".+/${it}/res\$" }
    }

    private fun getLocalResDirPatterns(localJars: List<File>): List<String> {
        val localBuildPaths = localJars.map { getBuildDir(it) }.distinct()
        return localBuildPaths.map { "^${it}.+" }
    }

    private fun getBuildDir(localJarFile: File): String {
        return LOCAL_BUILD_DIR_PATTERN.find(localJarFile.absolutePath)!!.value
    }

    private fun filterLocalJars(templatesProviderJars: List<File>): List<File> {
        val rootProjectDirPath = projectDirsProvider.getRootProjectDir().absolutePath
        val localJarPattern = Regex("^$rootProjectDirPath.+")
        return templatesProviderJars.filter { localJarPattern.matches(it.absolutePath) }
    }
}