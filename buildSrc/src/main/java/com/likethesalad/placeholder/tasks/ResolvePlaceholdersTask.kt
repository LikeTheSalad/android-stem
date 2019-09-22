package com.likethesalad.placeholder.tasks

import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.AndroidFilesProvider
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.resolver.TemplateResolver
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ResolvePlaceholdersTask : DefaultTask() {

    lateinit var resourcesHandler: ResourcesHandler
    lateinit var filesProvider: FilesProvider
    lateinit var templateResolver: TemplateResolver

    @InputFiles
    fun getTemplatesFiles(): List<File> {
        return filesProvider.getAllTemplatesFiles()
    }

    @OutputFiles
    fun getResolvedFiles(): List<File> {
        return filesProvider.getAllExpectedResolvedFiles()
    }

    @TaskAction
    fun resolve() {
        for (templateFile in filesProvider.getAllTemplatesFiles()) {
            val templatesModel = resourcesHandler.getTemplatesFromFile(templateFile)
            val resolvedTemplates = templateResolver.resolveTemplates(templatesModel)
            val curatedTemplates =
                filterNonTranslatableStringsForLanguageFolder(templatesModel.valuesFolderName, resolvedTemplates)
            if (curatedTemplates.isNotEmpty()) {
                resourcesHandler.saveResolvedStringListForValuesFolder(
                    resolvedTemplates,
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