package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.google.auto.factory.AutoFactory
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathFinder.Companion.BASE_DIR_PATH
import com.likethesalad.tools.android.plugin.AndroidVariantData
import javax.inject.Inject

@AutoFactory
class VariantDirsPathResolver @Inject constructor(
    private val androidVariantData: AndroidVariantData
) {

    private val flavors by lazy {
        androidVariantData.getVariantFlavors()
    }

    private val variantName by lazy {
        androidVariantData.getVariantName()
    }

    private val variantType by lazy {
        androidVariantData.getVariantType()
    }

    val pathList: List<String> by lazy {
        val pathList = mutableListOf<String>()
        pathList.add(BASE_DIR_PATH)
        addFlavorPaths(pathList)
        addVariantNameWithoutType(pathList)
        addVariantType(pathList)
        addFullVariantName(pathList)
        pathList
    }

    private fun addFullVariantName(pathList: MutableList<String>) {
        if (flavors.isNotEmpty()) {
            pathList.add(variantName)
        }
    }

    private fun addVariantType(pathList: MutableList<String>) {
        pathList.add(variantType)
    }

    private fun addVariantNameWithoutType(pathList: MutableList<String>) {
        getMergedFlavorsLowerCamelCase()?.let {
            pathList.add(it)
        }
    }

    private fun addFlavorPaths(pathList: MutableList<String>) {
        for (it in flavors.reversed()) {
            pathList.add(it)
        }
    }

    private fun getMergedFlavorsLowerCamelCase(): String? {
        if (flavors.size < 2) {
            return null
        }
        var result = flavors.first()
        for (it in flavors.drop(1)) {
            result += it.capitalize()
        }
        return result
    }
}
