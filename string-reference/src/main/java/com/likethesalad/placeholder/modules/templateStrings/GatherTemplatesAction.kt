package com.likethesalad.placeholder.modules.templateStrings

import com.likethesalad.placeholder.base.TaskAction
import com.likethesalad.placeholder.modules.common.Constants
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.likethesalad.placeholder.modules.rawStrings.models.StringsGatheredModel
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class GatherTemplatesAction @AssistedInject constructor(
    @Assisted androidVariantContext: AndroidVariantContext
) : TaskAction {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantContext: AndroidVariantContext): TaskAction
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

    fun gatherTemplateStrings() {
        incrementalDataCleaner.clearTemplateStrings()

        for (stringFile in filesProvider.getAllGatheredStringsFiles()) {
            val gatheredString = resourcesHandler.getGatheredStringsFromFile(stringFile)
            resourcesHandler.saveTemplates(gatheredStringsToTemplateStrings(gatheredString))
        }
    }

    private fun gatheredStringsToTemplateStrings(
        gatheredStrings: StringsGatheredModel
    ): StringsTemplatesModel {
        val mergedStrings = gatheredStrings.mergedStrings
        val stringTemplates = mergedStrings.filter { Constants.TEMPLATE_STRING_REGEX.containsMatchIn(it.name) }
        val placeholdersResolved = getPlaceholdersResolved(mergedStrings, stringTemplates)

        return StringsTemplatesModel(
            gatheredStrings.pathIdentity,
            stringTemplates,
            placeholdersResolved
        )
    }

    private fun getPlaceholdersResolved(
        strings: Collection<StringResourceModel>,
        templates: Collection<StringResourceModel>
    ): Map<String, String> {
        val stringsMap = stringResourcesToMap(strings)
        val placeholders = templates.map { Constants.PLACEHOLDER_REGEX.findAll(it.content) }
            .flatMap { it.toList().map { m -> m.groupValues[1] } }.toSet()

        val placeholdersResolved = mutableMapOf<String, String>()

        for (it in placeholders) {
            placeholdersResolved[it] = stringsMap.getValue(it)
        }

        return placeholdersResolved
    }

    private fun stringResourcesToMap(list: Collection<StringResourceModel>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (it in list) {
            map[it.name] = it.content
        }
        return map
    }

    override fun execute() {
        TODO("Not yet implemented")
    }
}