package com.likethesalad.placeholder.data.resources

import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.StringsTemplatesModel
import java.io.File

interface ResourcesHandler {

    fun getGatheredStringsFromFile(stringFile: File): StringsGatheredModel

    fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel

    fun saveResolvedStringList(resolvedStrings: List<StringResourceModel>, suffix: String)

    fun removeResolvedStringFileIfExists(suffix: String)

    fun saveTemplates(templates: StringsTemplatesModel)

    fun saveGatheredStrings(stringsGathered: StringsGatheredModel)
}