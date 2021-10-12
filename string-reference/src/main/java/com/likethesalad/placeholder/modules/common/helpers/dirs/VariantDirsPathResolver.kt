package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathFinder.Companion.BASE_DIR_PATH
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class VariantDirsPathResolver @AssistedInject constructor(
    @Assisted androidVariantData: AndroidVariantData
) {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantData: AndroidVariantData): VariantDirsPathResolver
    }

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
