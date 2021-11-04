package com.likethesalad.placeholder.modules.templateStrings

import com.likethesalad.placeholder.base.TaskAction
import com.likethesalad.placeholder.modules.common.Constants
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.data.AndroidResourceType
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.api.collection.ResourceCollection
import com.likethesalad.tools.resource.locator.android.extension.LanguageResourceFinder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class GatherTemplatesAction @AssistedInject constructor(
    @Assisted androidVariantContext: AndroidVariantContext
) : TaskAction {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantContext: AndroidVariantContext): GatherTemplatesAction
    }

    private val filesProvider = androidVariantContext.filesProvider
    private val incrementalDataCleaner = androidVariantContext.incrementalDataCleaner
    private val resourcesHandler = androidVariantContext.androidResourcesHandler

    fun getStringFiles(): List<File> {
        return filesProvider.getAllGatheredStringsFiles()
    }

    fun getTemplatesFiles(): List<File> {
        return filesProvider.getAllExpectedTemplatesFiles()
    }

    fun gatherTemplateStrings(languageResourceFinder: LanguageResourceFinder) {
        incrementalDataCleaner.clearTemplateStrings()

        for (language in languageResourceFinder.listLanguages()) {
            val resources = languageResourceFinder.getMergedResourcesForLanguage(language)
            resourcesHandler.saveTemplates(gatheredStringsToTemplateStrings(language, resources))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun gatheredStringsToTemplateStrings(
        language: Language,
        resources: ResourceCollection
    ): StringsTemplatesModel {
        val stringResources =
            resources.getResourcesByType(AndroidResourceType.StringType) as List<StringAndroidResource>
        val stringTemplates = stringResources.filter { Constants.TEMPLATE_STRING_REGEX.containsMatchIn(it.name()) }
        val placeholdersResolved = getPlaceholdersResolved(stringResources, stringTemplates)

        return StringsTemplatesModel(
            language,
            stringTemplates,
            placeholdersResolved
        )
    }

    private fun getPlaceholdersResolved(
        strings: List<StringAndroidResource>,
        templates: List<StringAndroidResource>
    ): Map<String, String> {
        val stringsMap = stringResourcesToMap(strings)
        val placeholders = templates.map { Constants.PLACEHOLDER_REGEX.findAll(it.stringValue()) }
            .flatMap { it.toList().map { m -> m.groupValues[1] } }.toSet()

        val placeholdersResolved = mutableMapOf<String, String>()

        for (it in placeholders) {
            placeholdersResolved[it] = stringsMap.getValue(it)
        }

        return placeholdersResolved
    }

    private fun stringResourcesToMap(list: Collection<StringAndroidResource>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (it in list) {
            map[it.name()] = it.stringValue()
        }
        return map
    }

    override fun execute() {
        TODO("Not yet implemented")
    }
}