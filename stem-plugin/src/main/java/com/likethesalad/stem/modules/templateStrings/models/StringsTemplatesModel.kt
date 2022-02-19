package com.likethesalad.stem.modules.templateStrings.models

import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource

data class StringsTemplatesModel(
    val language: Language,
    val templates: List<StringAndroidResource>,
    val values: Map<String, String>
)