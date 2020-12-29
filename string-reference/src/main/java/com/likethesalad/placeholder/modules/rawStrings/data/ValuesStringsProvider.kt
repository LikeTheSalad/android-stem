package com.likethesalad.placeholder.modules.rawStrings.data

import com.likethesalad.placeholder.modules.rawStrings.models.ValuesStrings
import com.likethesalad.placeholder.modules.rawStrings.models.VariantXmlFiles
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValuesStringsProvider @Inject constructor() {

    fun getValuesStringsForFolderFromVariants(
        valuesFolderName: String,
        variantXmlFilesList: List<VariantXmlFiles>,
        parentStrings: ValuesStrings?
    ): ValuesStrings? {
        var lastValuesStrings: ValuesStrings? = parentStrings
        for (valuesStringFiles in variantXmlFilesList) {

            val valuesXmlFiles = valuesStringFiles.valuesXmlFiles[valuesFolderName] ?: continue

            val valuesStrings = ValuesStrings(
                valuesFolderName,
                valuesXmlFiles,
                lastValuesStrings
            )
            lastValuesStrings = valuesStrings
        }
        if (lastValuesStrings != parentStrings) {
            return lastValuesStrings
        }
        return null
    }
}