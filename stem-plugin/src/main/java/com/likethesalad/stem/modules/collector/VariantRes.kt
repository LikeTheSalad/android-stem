package com.likethesalad.stem.modules.collector

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.likethesalad.tools.agpcompat.api.bridges.AndroidVariantData
import java.io.File
import java.util.Locale

class VariantRes(private val androidExtension: ApplicationExtension) {

    fun getResDirs(variant: Variant): List<Collection<File>> {
        val collections = mutableListOf<Collection<File>>()
        val layers = getLayerNames(variant)

        layers.forEach { layer ->
            val resDirs =
                (androidExtension.sourceSets.getByName(layer).res as DefaultAndroidSourceDirectorySet).srcDirs
            collections.add(resDirs)
        }

        return collections
    }

    //TODO remove function
    fun getResDirs(variantData: AndroidVariantData): List<Collection<File>> {
        val collections = mutableListOf<Collection<File>>()
        val layers = getLayerNames(variantData)

        layers.forEach { layer ->
            val resDirs =
                (androidExtension.sourceSets.getByName(layer).res as DefaultAndroidSourceDirectorySet).srcDirs
            collections.add(resDirs)
        }

        return collections
    }

    //TODO remove function
    internal fun getLayerNames(variant: AndroidVariantData): List<String> {
        val layersNames = mutableSetOf<String>()
        val flavorNames = variant.getVariantFlavors()
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
        variant.getVariantType().let { layersNames.add(it) }
        layersNames.add(variant.getVariantName())
        return layersNames.toList()
    }

    internal fun getLayerNames(variant: Variant): List<String> {
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