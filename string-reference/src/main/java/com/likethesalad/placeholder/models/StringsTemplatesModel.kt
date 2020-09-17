package com.likethesalad.placeholder.models

data class StringsTemplatesModel(
    val pathIdentity: PathIdentity,
    val templates: List<StringResourceModel>,
    val values: Map<String, String>
)