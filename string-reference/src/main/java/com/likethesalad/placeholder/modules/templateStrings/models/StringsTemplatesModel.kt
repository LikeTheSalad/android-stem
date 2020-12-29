package com.likethesalad.placeholder.modules.templateStrings.models

import com.likethesalad.placeholder.modules.common.models.PathIdentity
import com.likethesalad.placeholder.modules.common.models.StringResourceModel

data class StringsTemplatesModel(
    val pathIdentity: PathIdentity,
    val templates: List<StringResourceModel>,
    val values: Map<String, String>
)