package com.likethesalad.stem.modules.templateStrings

import com.likethesalad.android.resources.StringResourceCollector
import com.likethesalad.android.resources.data.StringResource
import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItem
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import com.likethesalad.stem.locator.entrypoints.common.utils.TemplatesProviderJarsFinder
import com.likethesalad.stem.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.stem.utils.TemplatesProviderLoader
import com.likethesalad.tools.resource.api.android.environment.Language
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class GatherTemplatesAction @AssistedInject constructor(
    @Assisted private val androidVariantContext: AndroidVariantContext,
    private val stemConfiguration: StemConfiguration
) {

    private val VALUES_REGEX = Regex("values(?:-(.+))*")

    @AssistedFactory
    interface Factory {
        fun create(androidVariantContext: AndroidVariantContext): GatherTemplatesAction
    }

    private val resourcesHandler = androidVariantContext.androidResourcesHandler
    private val templatesProviderJarsFinder =
        TemplatesProviderJarsFinder(androidVariantContext.androidVariantData.getLibrariesJars())

    fun gatherTemplateStrings(
        outputDir: File,
        variantResDirs: List<Collection<File>>,
        libraryResourcesDirs: Set<File>,
        templateIdsContainer: File
    ) {
        val allDirs = mutableListOf<Collection<File>>()
        allDirs.add(libraryResourcesDirs)
        allDirs.addAll(variantResDirs)
        val stringCollection = StringResourceCollector.collectStringResourcesPerValueDir(allDirs)
        val templateIds = getTemplateIds(templateIdsContainer)

        if (templateIds.isEmpty()) {
            return
        }

        stringCollection.forEach { (valueDirName, strings) ->
            val language = getLanguage(valueDirName)
            val templates = getTemplatesFromResources(templateIds, strings)
            val resources = strings.minus(templates)
            resourcesHandler.saveTemplates(outputDir, gatheredStringsToTemplateStrings(language, resources, templates))
        }
    }

    private fun getLanguage(valuesDirName: String): Language {
        val match = VALUES_REGEX.matchEntire(valuesDirName)
            ?: throw IllegalArgumentException("Invalid values dir name: $valuesDirName")

        val languagePart = match.groupValues[1]
        if (languagePart.isEmpty()) {
            return Language.Default
        }
        return Language.Custom(languagePart)
    }

    private fun getTemplatesFromResources(
        templateIds: List<TemplateItem>,
        resources: Collection<StringResource>
    ): List<StringResource> {
        val templateNames = templateIds.map { it.name }
        return resources.filter {
            it.getName() in templateNames
        }
    }

    private fun getTemplateIds(localTemplateIdsContainer: File): List<TemplateItem> {
        val templateIdsFromDependencies = getTemplateIdsFromDependencies()
        return TemplateItemsSerializer.deserialize(localTemplateIdsContainer.readText()) + templateIdsFromDependencies
    }

    private fun getTemplateIdsFromDependencies(): List<TemplateItem> {
        val templatesProviders = TemplatesProviderLoader.load(templatesProviderJarsFinder.templateProviderJars)
        return templatesProviders.map { TemplateItemsSerializer.deserialize(it.getTemplates()) }.flatten()
    }

    private fun gatheredStringsToTemplateStrings(
        language: Language,
        stringResources: List<StringResource>,
        stringTemplates: List<StringResource>
    ): StringsTemplatesModel {
        val placeholdersResolved = getPlaceholdersResolved(stringResources, stringTemplates)

        return StringsTemplatesModel(
            language,
            stringTemplates,
            placeholdersResolved
        )
    }

    private fun getPlaceholdersResolved(
        strings: List<StringResource>,
        templates: List<StringResource>
    ): Map<String, String> {
        val stringsMap = stringResourcesToMap(strings)
        val placeholders = templates.map { stemConfiguration.placeholderRegex.findAll(it.value) }
            .flatMap { it.toList().map { m -> m.groupValues[1] } }.toSet()

        val placeholdersResolved = mutableMapOf<String, String>()

        for (it in placeholders) {
            placeholdersResolved[it] = stringsMap.getValue(it)
        }

        return placeholdersResolved
    }

    private fun stringResourcesToMap(list: List<StringResource>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (it in list) {
            map[it.getName()] = it.value
        }
        return map
    }
}