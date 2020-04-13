package com.likethesalad.placeholder.data.storage.utils

import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantXmlFiles

class ValuesStringsBaseProvider(private val valuesStringsProvider: ValuesStringsProvider) {

    fun getBaseValuesStringsFor(
        valuesFolderName: String,
        baseValuesXmlFiles: VariantXmlFiles?
    ): ValuesStrings? {
        return null
    }
}