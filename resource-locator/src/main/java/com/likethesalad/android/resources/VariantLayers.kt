package com.likethesalad.android.resources

import com.android.build.api.variant.Variant
import org.gradle.internal.extensions.stdlib.capitalized

data class VariantLayers(val names: List<String>) {

    companion object {
        fun fromAndroidVariant(variant: Variant): VariantLayers {
            val names = mutableSetOf<String>()
            val flavorNames = variant.productFlavors.map { it.second }
            names.add("main")
            if (flavorNames.isNotEmpty()) {
                names.addAll(flavorNames.reversed())
                names.add(flavorNames.foldIndexed("") { index, accumulated, item ->
                    if (index == 0) {
                        item
                    } else {
                        accumulated + item.capitalized()
                    }
                })
            }
            variant.buildType?.let { names.add(it) }
            names.add(variant.name)
            return VariantLayers(names.toList())
        }
    }
}