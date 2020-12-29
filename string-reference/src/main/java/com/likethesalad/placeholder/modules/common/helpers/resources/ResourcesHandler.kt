package com.likethesalad.placeholder.modules.common.helpers.resources

import com.likethesalad.placeholder.modules.common.models.PathIdentity
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.likethesalad.placeholder.modules.rawStrings.models.StringsGatheredModel
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import java.io.File

interface ResourcesHandler {

    fun getGatheredStringsFromFile(stringFile: File): StringsGatheredModel

    fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel

    fun saveResolvedStringList(resolvedStrings: List<StringResourceModel>, pathIdentity: PathIdentity)

    fun saveTemplates(templates: StringsTemplatesModel)

    fun saveGatheredStrings(stringsGathered: StringsGatheredModel)
}