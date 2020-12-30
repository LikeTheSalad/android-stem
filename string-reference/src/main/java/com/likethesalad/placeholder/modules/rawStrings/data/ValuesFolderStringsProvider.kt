package com.likethesalad.placeholder.modules.rawStrings.data

import com.likethesalad.placeholder.modules.rawStrings.models.ValuesFolderStrings
import com.likethesalad.placeholder.modules.rawStrings.models.VariantXmlFiles
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValuesFolderStringsProvider @Inject constructor() {

    fun getValuesStringsForFolderFromVariants(
        valuesFolderName: String,
        variantXmlFilesList: List<VariantXmlFiles>,
        parentFolderStrings: ValuesFolderStrings?
    ): ValuesFolderStrings? {
        var lastValuesFolderStrings: ValuesFolderStrings? = parentFolderStrings
        for (variantXmlFiles in variantXmlFilesList) {

            val valuesXmlFiles = variantXmlFiles.findValuesFolderXmlFilesByName(valuesFolderName) ?: continue

            val valuesStrings = ValuesFolderStrings(
                valuesFolderName,
                valuesXmlFiles,
                lastValuesFolderStrings
            )
            lastValuesFolderStrings = valuesStrings
        }
        if (lastValuesFolderStrings != parentFolderStrings) {
            return lastValuesFolderStrings
        }
        return null
    }
}