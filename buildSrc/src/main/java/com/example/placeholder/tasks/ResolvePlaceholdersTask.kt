package com.example.placeholder.tasks

import com.example.placeholder.data.resources.ResourcesHandler
import com.example.placeholder.data.storage.FilesProvider
import com.example.placeholder.resolver.TemplateResolver
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
            resourcesHandler.saveResolvedStringListForValuesFolder(resolvedTemplates, templatesModel.valuesFolderName)
        }
    }
}