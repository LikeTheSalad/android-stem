package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.VariantDirsPathHandler.Companion.BASE_DIR_PATH

class VariantDirsPathResolver(
    private val variantName: String,
    private val flavors: List<String>,
    val variantType: String
) {

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
