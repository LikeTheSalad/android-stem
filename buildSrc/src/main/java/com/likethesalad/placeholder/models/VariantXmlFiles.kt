package com.likethesalad.placeholder.models

import com.likethesalad.placeholder.data.ValuesXmlFiles

data class VariantXmlFiles(
    val variantName: String,
    val valuesXmlFiles: Map<String, ValuesXmlFiles>
)