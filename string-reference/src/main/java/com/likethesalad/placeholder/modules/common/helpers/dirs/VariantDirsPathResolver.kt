package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.google.auto.factory.AutoFactory
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathFinder.Companion.BASE_DIR_PATH
import com.likethesalad.placeholder.modules.common.helpers.android.AppVariantHelper
import javax.inject.Inject

@AutoFactory
class VariantDirsPathResolver @Inject constructor(
    private val appVariantHelper: AppVariantHelper
) {

    private val flavors by lazy {
        appVariantHelper.getVariantFlavors()
    }

    private val variantName by lazy {
        appVariantHelper.getVariantName()
    }

    private val variantType by lazy {
        appVariantHelper.getVariantType()
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
