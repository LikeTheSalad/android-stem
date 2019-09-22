package com.example.placeholder.data.resources

import com.example.placeholder.data.storage.FilesProvider
import com.example.placeholder.models.StringResourceModel
import com.example.placeholder.models.StringsGatheredModel
import com.example.placeholder.models.StringsTemplatesModel
import com.example.placeholder.utils.AndroidXmlResDocument
import com.google.gson.Gson
import java.io.File


class AndroidResourcesHandler(private val filesProvider: FilesProvider) : ResourcesHandler {

    private val gson = Gson()

    override fun getGatheredStringsFromFile(stringFile: File): StringsGatheredModel {
        return gson.fromJson(stringFile.readText(), StringsGatheredModel::class.java)
    }

    override fun saveGatheredStrings(stringsGathered: StringsGatheredModel) {
        val jsonStrings = gson.toJson(stringsGathered)
        filesProvider.getStringsResourcesFileForFolder(stringsGathered.valueFolderName).writeText(jsonStrings)
    }

    override fun saveResolvedStringListForValuesFolder(
        resolvedStrings: List<StringResourceModel>,
        valuesFolderName: String
    ) {
        val resDocument = AndroidXmlResDocument()
        resDocument.appendAllStringResources(resolvedStrings)
        resDocument.saveToFile(filesProvider.getResolvedFileForValuesFolder(valuesFolderName))
    }

    override fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel {
        return if (templateFile.exists()) {
            gson.fromJson(templateFile.readText(), StringsTemplatesModel::class.java)
        } else {
            StringsTemplatesModel("", listOf(), mapOf())
        }
    }

    override fun saveTemplatesToFile(templates: StringsTemplatesModel, templateFile: File) {
        val jsonTemplates = gson.toJson(templates)
        if (!templateFile.exists()) {
            val created = templateFile.createNewFile()
        }
        templateFile.writeText(jsonTemplates)
    }
}