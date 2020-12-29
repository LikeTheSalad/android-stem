package com.likethesalad.placeholder.modules.rawStrings.models

import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesXmlFiles

data class VariantXmlFiles(
    val variantName: String,
    val valuesXmlFiles: Map<String, ValuesXmlFiles>
)