package com.likethesalad.android.resources

import com.android.build.api.variant.Variant
import org.gradle.internal.extensions.stdlib.capitalized

class VariantRes(val layers: List<Layer>) {

    class Layer(val name: String, private val position: Int) : Comparable<Layer> {
        override fun compareTo(other: Layer): Int {
            return position.compareTo(other.position)
        }
    }

    companion object {
        fun forVariant(variant: Variant): VariantRes {
            val layers = getNames(variant).mapIndexed { index: Int, name: String ->
                Layer(name, index)
            }
            return VariantRes(layers)
        }

        private fun getNames(variant: Variant): List<String> {
            val layersNames = mutableSetOf<String>()
            val flavorNames = variant.productFlavors.map { it.second }
            layersNames.add("main")
            if (flavorNames.isNotEmpty()) {
                layersNames.addAll(flavorNames.reversed())
                layersNames.add(flavorNames.foldIndexed("") { index, accumulated, item ->
                    if (index == 0) {
                        item
                    } else {
                        accumulated + item.capitalized()
                    }
                })
            }
            variant.buildType?.let { layersNames.add(it) }
            layersNames.add(variant.name)
            return layersNames.toList()
        }
    }
}