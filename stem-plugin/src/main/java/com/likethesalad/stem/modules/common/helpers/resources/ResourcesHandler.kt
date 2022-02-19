package com.likethesalad.stem.modules.common.helpers.resources

import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import java.io.File

interface ResourcesHandler {

    fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel

    fun saveResolvedStringList(outputDir: File, resolvedStrings: List<StringAndroidResource>, language: Language)

    fun saveTemplates(outputDir: File, templates: StringsTemplatesModel)
}