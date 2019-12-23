package com.likethesalad.placeholder.tasks.actions

import com.likethesalad.placeholder.data.resources.ResourcesHandler
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
                filterNonTranslatableStringsForLanguage(templatesModel.pathIdentity.suffix, resolvedTemplates)
            if (curatedTemplates.isNotEmpty()) {
                resourcesHandler.saveResolvedStringList(
                    curatedTemplates,
                    templatesModel.pathIdentity
                )
            }
        }
    }

    private fun filterNonTranslatableStringsForLanguage(
        suffix: String,
        resolvedStrings: List<StringResourceModel>
    ): List<StringResourceModel> {
        if (suffix.isNotEmpty()) {
            // It is a language specific file
            return resolvedStrings.filter { it.translatable }
        }
        return resolvedStrings
    }
}