package com.likethesalad.placeholder.tasks

import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.AndroidFilesProvider
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.data.Constants.Companion.PLACEHOLDER_REGEX
import com.likethesalad.placeholder.data.Constants.Companion.TEMPLATE_STRING_REGEX
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsTemplatesModel
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GatherTemplatesTask : DefaultTask() {

    lateinit var resourcesHandler: ResourcesHandler
    lateinit var filesProvider: FilesProvider

    @InputFiles
    fun getStringFiles(): List<File> {
        return filesProvider.getAllGatheredStringsFiles()
    }

    @OutputFiles
    fun getTemplatesFiles(): List<File> {
        return filesProvider.getAllExpectedTemplatesFiles()
    }

    @TaskAction
    fun gatherTemplateStrings() {
        val baseStringsFile =
            filesProvider.getStringsResourcesFileForFolder(AndroidFilesProvider.BASE_VALUES_FOLDER_NAME)
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
        val stringTemplates = mergedStrings.filter { TEMPLATE_STRING_REGEX.containsMatchIn(it.name) }
        val placeholdersResolved = getPlaceholdersResolved(mergedStrings, stringTemplates)

        return StringsTemplatesModel(specificStrings.valueFolderName, stringTemplates, placeholdersResolved)
    }

    private fun getPlaceholdersResolved(
        strings: Collection<StringResourceModel>,
        templates: Collection<StringResourceModel>
    ): Map<String, String> {
        val stringsMap = stringResourcesToMap(strings)
        val placeholders = templates.map { PLACEHOLDER_REGEX.findAll(it.content) }
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