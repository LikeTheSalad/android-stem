package com.likethesalad.placeholder.modules.rawStrings

import com.likethesalad.placeholder.modules.rawStrings.data.VariantRawStrings
import com.likethesalad.placeholder.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.placeholder.modules.common.helpers.files.IncrementalDataCleaner
import com.likethesalad.placeholder.modules.common.models.PathIdentity
import com.likethesalad.placeholder.modules.rawStrings.models.StringsGatheredModel
import com.likethesalad.placeholder.modules.rawStrings.models.ValuesFolderStrings
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

    private fun valuesStringsToStringsGathered(valuesFolderStrings: ValuesFolderStrings): StringsGatheredModel {
        return StringsGatheredModel(
            PathIdentity(
                valuesFolderStrings.valuesFolderName,
                valuesFolderStrings.valuesSuffix
            ),
            valuesFolderStrings.mergedStrings
        )
    }
}