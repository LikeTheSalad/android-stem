package com.likethesalad.placeholder.modules.rawStrings.models

import com.likethesalad.placeholder.modules.common.models.PathIdentity
import com.likethesalad.placeholder.modules.common.models.StringResourceModel

data class StringsGatheredModel(
    val pathIdentity: PathIdentity,
    val mergedStrings: List<StringResourceModel>
)