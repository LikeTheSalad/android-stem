package com.likethesalad.placeholder.tasks.actions

import com.likethesalad.placeholder.data.VariantRawStrings
import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.models.PathIdentity
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.ValuesStrings

class GatherRawStringsAction(
    private val variantRawStrings: VariantRawStrings,
    private val resourcesHandler: ResourcesHandler
) {

    fun gatherStrings() {
        for (valuesStrings in variantRawStrings.valuesStrings) {
            if (valuesStrings.primaryVariantName.isNotEmpty()) {
                resourcesHandler.saveGatheredStrings(valuesStringsToStringsGathered(valuesStrings))
            }
        }
    }

    private fun valuesStringsToStringsGathered(valuesStrings: ValuesStrings): StringsGatheredModel {
        return StringsGatheredModel(
            PathIdentity(
                valuesStrings.variantName,
                valuesStrings.valuesFolderName,
                valuesStrings.valuesSuffix
            ),
            valuesStrings.mergedStrings
        )
    }
}