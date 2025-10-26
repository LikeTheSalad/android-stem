package com.likethesalad.stem.modules.templateStrings.data

import com.likethesalad.stem.modules.templateStrings.GatherTemplatesAction2
import java.io.File

data class GatherTemplatesArgs2(
    val gatherTemplatesAction: GatherTemplatesAction2,
    val variantResDirs: List<Collection<File>>,
)