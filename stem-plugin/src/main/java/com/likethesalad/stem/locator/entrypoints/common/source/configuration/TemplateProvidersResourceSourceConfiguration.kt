package com.likethesalad.stem.locator.entrypoints.common.source.configuration

import com.likethesalad.android.templates.common.utils.CommonConstants.FILE_SEPARATOR_MATCHER
import com.likethesalad.android.templates.common.utils.Logger
import com.likethesalad.stem.locator.entrypoints.common.utils.TemplatesProviderJarsFinder
import com.likethesalad.stem.providers.ProjectDirsProvider
import com.likethesalad.tools.resource.collector.android.data.resdir.ResDir
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.impl.AndroidLibrariesSourceConfiguration
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.gradle.api.file.FileCollection
import java.io.File
import java.util.regex.Pattern

class TemplateProvidersResourceSourceConfiguration @AssistedInject constructor(
    @Assisted variantTree: VariantTree,
    @Assisted private val templatesProviderJarsFinder: TemplatesProviderJarsFinder,
    private val projectDirsProvider: ProjectDirsProvider,
    private val loggerFactory: Logger.Factory
) : AndroidLibrariesSourceConfiguration(variantTree) {

    private val logger by lazy {
        loggerFactory.create(javaClass)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            variantTree: VariantTree,
            templatesProviderJarsFinder: TemplatesProviderJarsFinder
        ): TemplateProvidersResourceSourceConfiguration
    }

    companion object {
        private val LOCAL_BUILD_DIR_PATTERN = Regex("^.+" + FILE_SEPARATOR_MATCHER + "build" + FILE_SEPARATOR_MATCHER)
    }

    override fun getResDirs(): List<ResDir> {
        val allResDirs = super.getResDirs()
        return filterTemplateProviderResDirs(allResDirs)
    }

    override fun getLibrariesResourcesFileCollection(): FileCollection {
        return super.getLibrariesResourcesFileCollection().plus(templatesProviderJarsFinder.allJarFiles)
    }

    private fun filterTemplateProviderResDirs(allResDirs: List<ResDir>): List<ResDir> {
        val templatesProvidersPattern = createTemplatesProviderPathStartPattern() ?: return emptyList()

        return allResDirs.filter {
            templatesProvidersPattern.matches(it.dir.absolutePath)
        }
    }

    private fun createTemplatesProviderPathStartPattern(): Regex? {
        val templatesProviderJars = templatesProviderJarsFinder.templateProviderJars

        if (templatesProviderJars.isEmpty()) {
            logger.debug("No templates provider jars found")
            return null
        }

        val localJars = filterLocalJars(templatesProviderJars)
        logger.debug("Local templates provider jars: {}", localJars)
        val externalJars = templatesProviderJars.minus(localJars)
        logger.debug("External templates provider jars: {}", externalJars)

        val resDirPatterns = getLocalResDirPatterns(localJars) + getExternalResDirPatterns(externalJars)

        val pattern = resDirPatterns.joinToString("|")
        logger.debug("Template providers pattern: {}", pattern)
        return Regex(pattern)
    }

    private fun getExternalResDirPatterns(externalJars: List<File>): List<String> {
        val fileNames = externalJars.map { it.name.substringBeforeLast("-runtime") }.distinct()
        return fileNames.map { ".+${FILE_SEPARATOR_MATCHER}${Pattern.quote(it)}${FILE_SEPARATOR_MATCHER}res\$" }
    }

    private fun getLocalResDirPatterns(localJars: List<File>): List<String> {
        val localBuildPaths = localJars.map { getBuildDir(it) }.distinct()
        return localBuildPaths.map { "^${Pattern.quote(it)}.+" }
    }

    private fun getBuildDir(localJarFile: File): String {
        return LOCAL_BUILD_DIR_PATTERN.find(localJarFile.absolutePath)!!.value
    }

    private fun filterLocalJars(templatesProviderJars: List<File>): List<File> {
        val rootProjectDirPath = projectDirsProvider.getRootProjectDir().absolutePath
        val localJarPattern = Regex("^${Pattern.quote(rootProjectDirPath)}.+")
        return templatesProviderJars.filter { localJarPattern.matches(it.absolutePath) }
    }
}