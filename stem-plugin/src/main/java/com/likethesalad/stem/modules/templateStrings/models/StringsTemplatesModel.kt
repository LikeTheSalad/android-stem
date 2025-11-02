package com.likethesalad.stem.modules.templateStrings.models

import com.likethesalad.android.protos.StringResource

data class StringsTemplatesModel(
    val suffix: String,
    val templates: List<StringResource>,
    val values: Map<String, String>
)