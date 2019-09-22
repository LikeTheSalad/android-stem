package com.likethesalad.placeholder.data.resources

import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.StringsTemplatesModel
import java.io.File

interface ResourcesHandler {

    fun getGatheredStringsFromFile(stringFile: File): StringsGatheredModel

    fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel

    fun saveResolvedStringListForValuesFolder(resolvedStrings: List<StringResourceModel>, valuesFolderName: String)

    fun saveTemplatesToFile(templates: StringsTemplatesModel, templateFile: File)

    fun saveGatheredStrings(stringsGathered: StringsGatheredModel)
}