package com.likethesalad.stem.modules.resolveStrings

import com.likethesalad.android.resources.data.StringResource
import com.likethesalad.stem.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.stem.modules.resolveStrings.resolver.TemplateResolver
import com.likethesalad.tools.resource.api.android.environment.Language
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class ResolvePlaceholdersAction @AssistedInject constructor(
    @Assisted androidVariantContext: AndroidVariantContext,
    private val templateResolver: TemplateResolver
) {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantContext: AndroidVariantContext): ResolvePlaceholdersAction
    }

    private val resourcesHandler = androidVariantContext.androidResourcesHandler

    fun resolve(templatesDir: File, outputDir: File) {
        val templateFiles = templatesDir.listFiles()?.toList() ?: emptyList<File>()
        for (templateFile in templateFiles) {
            val templatesModel = resourcesHandler.getTemplatesFromFile(templateFile)
            val resolvedTemplates = templateResolver.resolveTemplates(templatesModel)
            val curatedTemplates =
                filterNonTranslatableStringsForLanguage(templatesModel.language, resolvedTemplates)
            if (curatedTemplates.isNotEmpty()) {
                resourcesHandler.saveResolvedStringList(
                    outputDir,
                    curatedTemplates,
                    templatesModel.language
                )
            }
        }
    }

    private fun filterNonTranslatableStringsForLanguage(
        language: Language,
        resolvedStrings: List<StringResource>
    ): List<StringResource> {
        if (language != Language.Default) {
            // It is a language specific file
            return resolvedStrings.filter { isTranslatable(it) }
//            return resolvedStrings.filter { isTranslatable(it) && belongsToLanguage(it, language) }
        }
        return resolvedStrings
    }

//    private fun belongsToLanguage(stringAndroidResource: StringResource, language: Language): Boolean {
//        return stringAndroidResource.getAndroidScope().language == language
//    }

    private fun isTranslatable(stringResource: StringResource): Boolean {
        val translatable = stringResource.attributes.firstOrNull { it.name == "translatable" }?.value ?: return true
        return translatable.toBoolean()
    }
}