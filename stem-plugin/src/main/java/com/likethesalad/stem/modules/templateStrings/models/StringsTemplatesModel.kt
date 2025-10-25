package com.likethesalad.stem.modules.templateStrings.models

import com.likethesalad.android.resources.data.StringResource
import com.likethesalad.tools.resource.api.android.environment.Language

data class StringsTemplatesModel(
    val language: Language,
    val templates: List<StringResource>,
    val values: Map<String, String>
)