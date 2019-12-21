package com.likethesalad.placeholder.data.resources

import com.google.gson.Gson
import com.likethesalad.placeholder.data.PathIdentityResolver
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.StringsTemplatesModel
import com.likethesalad.placeholder.utils.AndroidXmlResDocument
import java.io.File


class AndroidResourcesHandler(
    private val filesProvider: FilesProvider,
    private val pathIdentityResolver: PathIdentityResolver
) : ResourcesHandler {

    private val gson = Gson()

    override fun getGatheredStringsFromFile(stringFile: File): StringsGatheredModel {
        return gson.fromJson(stringFile.readText(), StringsGatheredModel::class.java)
    }

    override fun saveGatheredStrings(stringsGathered: StringsGatheredModel) {
        val jsonStrings = gson.toJson(stringsGathered)
        pathIdentityResolver.getRawStringsFile(stringsGathered.pathIdentity).writeText(jsonStrings)
    }

    override fun saveResolvedStringList(
        resolvedStrings: List<StringResourceModel>,
        suffix: String
    ) {
        val resDocument = AndroidXmlResDocument()
        resDocument.appendAllStringResources(resolvedStrings)
        resDocument.saveToFile(filesProvider.getResolvedFile(suffix))
    }

    override fun removeResolvedStringFileIfExists(suffix: String) {
        val file = filesProvider.getResolvedFile(suffix)
        if (file.exists()) {
            file.delete()
        }
    }

    override fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel {
        return gson.fromJson(templateFile.readText(), StringsTemplatesModel::class.java)
    }

    override fun saveTemplates(templates: StringsTemplatesModel) {
        val jsonTemplates = gson.toJson(templates)
        pathIdentityResolver.getTemplateStringsFile(templates.pathIdentity).writeText(jsonTemplates)
    }
}