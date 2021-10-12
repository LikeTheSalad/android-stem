package com.likethesalad.placeholder.modules.templateStrings.models

import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource

data class StringsTemplatesModel(
    val templates: List<StringAndroidResource>,
    val values: Map<String, String>
)