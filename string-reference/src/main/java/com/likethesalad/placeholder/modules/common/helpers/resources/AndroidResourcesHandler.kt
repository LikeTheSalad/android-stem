package com.likethesalad.placeholder.modules.common.helpers.resources

import com.google.gson.GsonBuilder
import com.likethesalad.placeholder.modules.common.helpers.files.AndroidXmlResDocument
import com.likethesalad.placeholder.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.placeholder.modules.common.helpers.resources.utils.LanguageTypeAdapter
import com.likethesalad.placeholder.modules.common.helpers.resources.utils.VariantTypeAdapter
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import java.io.File


class AndroidResourcesHandler(
    private val outputStringFileResolver: OutputStringFileResolver
) : ResourcesHandler {

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(Language::class.java, LanguageTypeAdapter())
            .registerTypeAdapter(Variant::class.java, VariantTypeAdapter())
            .create()
    }

    override fun saveResolvedStringList(
        resolvedStrings: List<StringAndroidResource>,
        language: Language
    ) {
        val resDocument = AndroidXmlResDocument()
        resDocument.appendAllStringResources(resolvedStrings)
        resDocument.saveToFile(outputStringFileResolver.getResolvedStringsFile(getResolvedValuesFolderName(language)))
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

    override fun saveTemplates(templates: StringsTemplatesModel) {
        val jsonTemplates = gson.toJson(templates)
        outputStringFileResolver.getTemplateStringsFile(templates.language.id).writeText(jsonTemplates)
    }
}