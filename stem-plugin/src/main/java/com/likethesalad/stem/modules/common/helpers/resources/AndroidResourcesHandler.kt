package com.likethesalad.stem.modules.common.helpers.resources

import com.google.gson.GsonBuilder
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.modules.common.helpers.files.AndroidXmlResDocument
import com.likethesalad.stem.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.stem.modules.common.helpers.resources.utils.LanguageTypeAdapter
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.environment.Language
import java.io.File


class AndroidResourcesHandler(
    private val outputStringFileResolver: OutputStringFileResolver
) : ResourcesHandler {

    private val gson by lazy {
        GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(Language::class.java, LanguageTypeAdapter())
            .create()
    }


    override fun saveResolvedStringList(
        outputDir: File,
        resolvedStrings: List<StringResource>,
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