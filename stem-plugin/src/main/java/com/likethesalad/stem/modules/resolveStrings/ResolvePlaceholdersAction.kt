package com.likethesalad.stem.modules.resolveStrings

import com.likethesalad.stem.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.stem.modules.resolveStrings.resolver.TemplateResolver
import java.io.File

class ResolvePlaceholdersAction(
    private val templateResolver: TemplateResolver,
    private val resourcesHandler: ResourcesHandler
) {

    fun resolve(templatesDir: File, outputDir: File) {
        val templateFiles = templatesDir.listFiles()?.toList() ?: emptyList<File>()
        for (templateFile in templateFiles) {
            val templatesModel = resourcesHandler.getTemplatesFromFile(templateFile)
            val resolvedTemplates = templateResolver.resolveTemplates(templatesModel)
            if (resolvedTemplates.isNotEmpty()) {
                resourcesHandler.saveResolvedStringList(
                    outputDir,
                    resolvedTemplates,
                    templatesModel.suffix
                )
            }
        }
    }
}