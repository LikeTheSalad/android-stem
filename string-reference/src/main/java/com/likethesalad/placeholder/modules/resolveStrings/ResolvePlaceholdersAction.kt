package com.likethesalad.placeholder.modules.resolveStrings

import com.likethesalad.placeholder.base.TaskAction
import com.likethesalad.placeholder.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.placeholder.modules.common.helpers.files.storage.FilesProvider
import com.likethesalad.placeholder.modules.resolveStrings.data.helpers.files.ResolvedDataCleaner
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.likethesalad.placeholder.modules.resolveStrings.resolver.TemplateResolver
import java.io.File

class ResolvePlaceholdersAction(
    private val filesProvider: FilesProvider,
    private val resourcesHandler: ResourcesHandler,
    private val templateResolver: TemplateResolver,
    private val resolvedDataCleaner: ResolvedDataCleaner
) : TaskAction {

    fun getTemplatesFiles(): List<File> {
        return filesProvider.getAllTemplatesFiles()
    }

    fun getResolvedFiles(): List<File> {
        return filesProvider.getAllExpectedResolvedFiles()
    }

    fun resolve() {
        resolvedDataCleaner.removeResolvedFiles()
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

    override fun execute() {
        TODO("Not yet implemented")
    }
}