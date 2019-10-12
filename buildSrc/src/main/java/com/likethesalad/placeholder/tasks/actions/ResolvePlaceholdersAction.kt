package com.likethesalad.placeholder.tasks.actions

import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.AndroidFilesProvider
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.resolver.TemplateResolver
import java.io.File

class ResolvePlaceholdersAction(
    private val filesProvider: FilesProvider,
    private val resourcesHandler: ResourcesHandler,
    private val templateResolver: TemplateResolver
) {

    fun getTemplatesFiles(): List<File> {
        return filesProvider.getAllTemplatesFiles()
    }

    fun getResolvedFiles(): List<File> {
        return filesProvider.getAllExpectedResolvedFiles()
    }

    fun resolve() {
        for (templateFile in filesProvider.getAllTemplatesFiles()) {
            val templatesModel = resourcesHandler.getTemplatesFromFile(templateFile)
            val resolvedTemplates = templateResolver.resolveTemplates(templatesModel)
            val curatedTemplates =
                filterNonTranslatableStringsForLanguageFolder(templatesModel.valuesFolderName, resolvedTemplates)
            if (curatedTemplates.isNotEmpty()) {
                resourcesHandler.saveResolvedStringListForValuesFolder(
                    curatedTemplates,
                    templatesModel.valuesFolderName
                )
            } else {
                // Clean up
                resourcesHandler.removeResolvedStringFileIfExistsForValuesFolder(templatesModel.valuesFolderName)
            }
        }
    }

    private fun filterNonTranslatableStringsForLanguageFolder(
        valuesFolderName: String,
        resolvedStrings: List<StringResourceModel>
    ): List<StringResourceModel> {
        if (valuesFolderName != AndroidFilesProvider.BASE_VALUES_FOLDER_NAME) {
            return resolvedStrings.filter { it.translatable }
        }
        return resolvedStrings
    }
}