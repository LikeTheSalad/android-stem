package com.likethesalad.stem.modules.common.helpers.resources

import com.google.gson.GsonBuilder
import com.likethesalad.stem.modules.common.helpers.files.AndroidXmlResDocument
import com.likethesalad.stem.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.stem.modules.common.helpers.resources.utils.LanguageTypeAdapter
import com.likethesalad.stem.modules.common.helpers.resources.utils.StringResourceTypeAdapter
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.serializer.ResourceSerializer
import java.io.File


class AndroidResourcesHandler(
    private val outputStringFileResolver: OutputStringFileResolver,
    resourceSerializer: ResourceSerializer
) : ResourcesHandler {

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(Language::class.java, LanguageTypeAdapter())
            .registerTypeAdapter(StringAndroidResource::class.java, StringResourceTypeAdapter(resourceSerializer))
            .create()
    }


    override fun saveResolvedStringList(
        outputDir: File,
        resolvedStrings: List<StringAndroidResource>,
        language: Language
    ) {
        val resDocument = AndroidXmlResDocument()
        resDocument.appendAllStringResources(resolvedStrings)
        resDocument.saveToFile(
            outputStringFileResolver.getResolvedStringsFile(
                outputDir,
                getResolvedValuesFolderName(language)
            )
        )
    }

    private fun getResolvedValuesFolderName(language: Language): String {
        if (language == Language.Default) {
            return "values"
        }

        return "values-${language.id}"
    }

    override fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel {
        return gson.fromJson(templateFile.readText(), StringsTemplatesModel::class.java)
    }

    override fun saveTemplates(outputDir: File, templates: StringsTemplatesModel) {
        val jsonTemplates = gson.toJson(templates)
        outputStringFileResolver.getTemplateStringsFile(outputDir, templates.language.id).writeText(jsonTemplates)
    }
}