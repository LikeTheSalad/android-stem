package com.likethesalad.stem.modules.common.helpers.resources

import com.likethesalad.android.resources.data.StringResource
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.environment.Language
import java.io.File

interface ResourcesHandler {

    fun getTemplatesFromFile(templateFile: File): StringsTemplatesModel

    fun saveResolvedStringList(outputDir: File, resolvedStrings: List<StringResource>, language: Language)

    fun saveTemplates(outputDir: File, templates: StringsTemplatesModel)
}