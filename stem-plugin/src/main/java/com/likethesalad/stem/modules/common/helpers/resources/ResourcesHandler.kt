package com.likethesalad.stem.modules.common.helpers.resources

import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import java.io.File

interface ResourcesHandler {

    fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel

    fun saveResolvedStringList(outputDir: File, resolvedStrings: List<StringResource>, suffix: String)

    fun saveTemplates(outputDir: File, templates: StringsTemplatesModel)
}