package com.likethesalad.placeholder.tasks.actions

import com.likethesalad.placeholder.data.VariantRawStrings
import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.IncrementalDataCleaner
import com.likethesalad.placeholder.models.PathIdentity
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.ValuesStrings
import java.io.File

class GatherRawStringsAction(
    private val variantRawStrings: VariantRawStrings,
    private val resourcesHandler: ResourcesHandler,
    private val incrementalDataCleaner: IncrementalDataCleaner
) {

    fun gatherStrings(gradleGeneratedResDirs: Set<File>) {
        incrementalDataCleaner.clearRawStrings()
        for (valuesStrings in variantRawStrings.getValuesStrings(gradleGeneratedResDirs)) {
            if (valuesStrings.hasTemplatesOrValues) {
                resourcesHandler.saveGatheredStrings(valuesStringsToStringsGathered(valuesStrings))
            }
        }
    }

    private fun valuesStringsToStringsGathered(valuesStrings: ValuesStrings): StringsGatheredModel {
        return StringsGatheredModel(
            PathIdentity(
                valuesStrings.valuesFolderName,
                valuesStrings.valuesSuffix
            ),
            valuesStrings.mergedStrings
        )
    }
}