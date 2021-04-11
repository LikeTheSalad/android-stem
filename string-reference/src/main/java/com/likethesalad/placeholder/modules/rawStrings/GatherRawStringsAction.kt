package com.likethesalad.placeholder.modules.rawStrings

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.base.TaskAction
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.models.PathIdentity
import com.likethesalad.placeholder.modules.rawStrings.data.VariantRawStringsFactory
import com.likethesalad.placeholder.modules.rawStrings.models.StringsGatheredModel
import com.likethesalad.placeholder.modules.rawStrings.models.ValuesFolderStrings
import java.io.File

@AutoFactory
class GatherRawStringsAction(
    androidVariantContext: AndroidVariantContext,
    @Provided private val variantRawStringsFactory: VariantRawStringsFactory
) : TaskAction {

    private val incrementalDataCleaner = androidVariantContext.incrementalDataCleaner
    private val resourcesHandler = androidVariantContext.androidResourcesHandler

    private val variantRawStrings by lazy {
        variantRawStringsFactory.create(androidVariantContext)
    }

    fun gatherStrings(gradleGeneratedResDirs: Set<File>) {
        incrementalDataCleaner.clearRawStrings()
        for (valuesStrings in variantRawStrings.getValuesFolderStrings(gradleGeneratedResDirs)) {
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

    override fun execute() {
        TODO("Not yet implemented")
    }
}