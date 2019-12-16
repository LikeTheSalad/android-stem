package com.likethesalad.placeholder.models

import com.likethesalad.placeholder.data.ValuesStringFiles

data class VariantStringFiles(
    val variantName: String,
    val valuesStringFiles: Map<String, ValuesStringFiles>
)