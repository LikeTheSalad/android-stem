package com.likethesalad.stem.modules.common.helpers.resources

import com.google.gson.GsonBuilder
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.modules.common.helpers.files.AndroidXmlResDocument
import com.likethesalad.stem.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import java.io.File

class AndroidResourcesHandler(
    private val outputStringFileResolver: OutputStringFileResolver
) : ResourcesHandler {

    private val gson by lazy {
        GsonBuilder()
            .disableHtmlEscaping()
            .create()
    }

    override fun saveResolvedStringList(
        outputDir: File,
        resolvedStrings: List<StringResource>,
        suffix: String
    ) {
        val resDocument = AndroidXmlResDocument()
        resDocument.appendAllStringResources(resolvedStrings)
        resDocument.saveToFile(
            outputStringFileResolver.getResolvedStringsFile(
                outputDir,
                getResolvedValuesFolderName(suffix)
            )
        )
    }

    private fun getResolvedValuesFolderName(suffix: String): String {
        if (suffix.isEmpty()) {
            return "values"
        }

        return "values-$suffix"
    }

    override fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel {
        return gson.fromJson(templateFile.readText(), StringsTemplatesModel::class.java)
    }

    override fun saveTemplates(outputDir: File, templates: StringsTemplatesModel) {
        val jsonTemplates = gson.toJson(templates)
        outputStringFileResolver.getTemplateStringsFile(outputDir, templates.suffix).writeText(jsonTemplates)
    }
}