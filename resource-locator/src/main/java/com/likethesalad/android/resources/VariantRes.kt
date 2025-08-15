package com.likethesalad.android.resources

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.Variant
import java.util.Locale

class VariantRes(private val androidExtension: ApplicationExtension, val layers: List<String>) {

    //    fun getResDirs(): List<Collection<File>> {
//        val collections = mutableListOf<Collection<File>>()
//
//        layers.forEach { layer ->
//            val resDirs =
//                (androidExtension.sourceSets.getByName(layer).res as DefaultAndroidSourceDirectorySet).srcDirs
//            collections.add(resDirs)
//        }
//
//        return collections
//    }
//
    companion object {
        fun forVariant(androidExtension: ApplicationExtension, variant: Variant): VariantRes {
            return VariantRes(androidExtension, getLayerNames(variant))
        }

        private fun getLayerNames(variant: Variant): List<String> {
            val layersNames = mutableSetOf<String>()
            val flavorNames = variant.productFlavors.map { it.second }
            layersNames.add("main")
            if (flavorNames.isNotEmpty()) {
                layersNames.addAll(flavorNames.reversed())
                layersNames.add(flavorNames.foldIndexed("") { index, accumulated, item ->
                    if (index == 0) {
                        item
                    } else {
                        accumulated + item.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    }
                })
            }
            variant.buildType?.let { layersNames.add(it) }
            layersNames.add(variant.name)
            return layersNames.toList()
        }
    }
}