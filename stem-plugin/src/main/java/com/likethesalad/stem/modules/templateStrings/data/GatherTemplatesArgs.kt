package com.likethesalad.stem.modules.templateStrings.data

import com.likethesalad.stem.modules.templateStrings.GatherTemplatesAction
import java.io.File

data class GatherTemplatesArgs(
    val gatherTemplatesAction: GatherTemplatesAction,
    val variantResDirs: List<Collection<File>>,
)