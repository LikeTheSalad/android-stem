package com.likethesalad.placeholder.modules.resolveStrings

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.base.TaskAction
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.likethesalad.placeholder.modules.resolveStrings.data.helpers.files.ResolvedDataCleanerFactory
import com.likethesalad.placeholder.modules.resolveStrings.resolver.TemplateResolver
import java.io.File

@AutoFactory
class ResolvePlaceholdersAction(
    androidVariantContext: AndroidVariantContext,
    @Provided resolvedDataCleanerFactory: ResolvedDataCleanerFactory,
    @Provided private val templateResolver: TemplateResolver
) : TaskAction {

    private val filesProvider = androidVariantContext.filesProvider
    private val resourcesHandler = androidVariantContext.androidResourcesHandler
    private val resolvedDataCleaner = resolvedDataCleanerFactory.create(androidVariantContext)

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