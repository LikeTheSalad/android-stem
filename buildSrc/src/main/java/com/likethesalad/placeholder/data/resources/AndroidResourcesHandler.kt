package com.likethesalad.placeholder.data.resources

import com.google.gson.Gson
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.StringsTemplatesModel
import com.likethesalad.placeholder.utils.AndroidXmlResDocument
import java.io.File


class AndroidResourcesHandler(private val filesProvider: FilesProvider) : ResourcesHandler {

    private val gson = Gson()

    override fun getGatheredStringsFromFile(stringFile: File): StringsGatheredModel {
        return gson.fromJson(stringFile.readText(), StringsGatheredModel::class.java)
    }

    override fun saveGatheredStrings(stringsGathered: StringsGatheredModel) {
        val jsonStrings = gson.toJson(stringsGathered)
        filesProvider.getGatheredStringsFileForFolder(stringsGathered.valueFolderName).writeText(jsonStrings)
    }

    override fun saveResolvedStringListForValuesFolder(
        resolvedStrings: List<StringResourceModel>,
        valuesFolderName: String
    ) {
        val resDocument = AndroidXmlResDocument()
        resDocument.appendAllStringResources(resolvedStrings)
        resDocument.saveToFile(filesProvider.getResolvedFileForValuesFolder(valuesFolderName))
    }

    override fun removeResolvedStringFileIfExistsForValuesFolder(valuesFolderName: String) {
        val file = filesProvider.getResolvedFileForValuesFolder(valuesFolderName)
        if (file.exists()) {
            file.delete()
        }
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
        templateFile.writeText(jsonTemplates)
    }
}