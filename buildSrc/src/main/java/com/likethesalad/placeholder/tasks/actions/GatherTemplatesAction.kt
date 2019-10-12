package com.likethesalad.placeholder.tasks.actions

import com.likethesalad.placeholder.data.Constants
import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.AndroidFilesProvider
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsTemplatesModel
import java.io.File

class GatherTemplatesAction(
    private val filesProvider: FilesProvider,
    private val resourcesHandler: ResourcesHandler
) {
    fun getStringFiles(): List<File> {
        return filesProvider.getAllGatheredStringsFiles()
    }

    fun getTemplatesFiles(): List<File> {
        return filesProvider.getAllExpectedTemplatesFiles()
    }

    fun gatherTemplateStrings() {
        val baseStringsFile =
            filesProvider.getGatheredStringsFileForFolder(AndroidFilesProvider.BASE_VALUES_FOLDER_NAME)
        val baseStrings = resourcesHandler.getGatheredStringsFromFile(baseStringsFile).getMergedStrings()
        for (stringFile in filesProvider.getAllGatheredStringsFiles()) {
            val templates = generateTemplatesForStringFile(stringFile, baseStrings)
            val templatesFile = filesProvider.getTemplateFileForStringFile(stringFile)
            val oldTemplates = resourcesHandler.getTemplatesFromFile(templatesFile)
            if (templates != oldTemplates) {
                // Update the templates file only if needed.
                resourcesHandler.saveTemplatesToFile(templates, templatesFile)
            }
        }
    }

    private fun generateTemplatesForStringFile(
        stringFile: File,
        baseStrings: Map<String, StringResourceModel>
    ): StringsTemplatesModel {
        val stringsMap = baseStrings.toMutableMap()
        val specificStrings = resourcesHandler.getGatheredStringsFromFile(stringFile)
        for (specificString in specificStrings.getMergedStrings().values) {
            stringsMap[specificString.name] = specificString
        }
        val mergedStrings = stringsMap.values
        val stringTemplates = mergedStrings.filter { Constants.TEMPLATE_STRING_REGEX.containsMatchIn(it.name) }
        val placeholdersResolved = getPlaceholdersResolved(mergedStrings, stringTemplates)

        return StringsTemplatesModel(specificStrings.valueFolderName, stringTemplates, placeholdersResolved)
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
}