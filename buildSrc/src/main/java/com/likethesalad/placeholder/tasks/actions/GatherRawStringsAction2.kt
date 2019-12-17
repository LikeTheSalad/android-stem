package com.likethesalad.placeholder.tasks.actions

import com.likethesalad.placeholder.data.VariantDirsPathHandler
import com.likethesalad.placeholder.data.VariantRawStrings

class GatherRawStringsAction2(
    private val variantRawStrings: VariantRawStrings,
    private val variantDirsPathHandler: VariantDirsPathHandler
) {

    fun gatherStrings() {
        variantRawStrings.valuesStrings
    }
}