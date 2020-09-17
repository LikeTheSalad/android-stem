package com.likethesalad.placeholder.models

data class StringsGatheredModel(
    val pathIdentity: PathIdentity,
    val mergedStrings: List<StringResourceModel>
)