package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.likethesalad.placeholder.modules.common.models.VariantResPaths
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class VariantDirsPathFinder @AssistedInject constructor(
    @Assisted androidVariantData: AndroidVariantData,
    private val variantDirsPathResolverFactory: VariantDirsPathResolver.Factory,
    androidExtensionProvider: AndroidExtensionProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantData: AndroidVariantData): VariantDirsPathFinder
    }

    private val androidExtension by lazy { androidExtensionProvider.getExtension() }
    private val variantDirsPathResolver by lazy { variantDirsPathResolverFactory.create(androidVariantData) }

    companion object {
        const val BASE_DIR_PATH = "main"
    }

    fun getExistingPathsResDirs(extraMainResDirs: List<File>? = null): List<VariantResPaths> {
        val existing = mutableListOf<VariantResPaths>()
        val pathResolved = variantDirsPathResolver.pathList

        for (variantName in pathResolved) {
            getResPaths(variantName, getExtraDirs(variantName, extraMainResDirs))?.let {
                existing.add(it)
            }
        }

        return existing
    }

    private fun getExtraDirs(variantName: String, extraMainResDirs: List<File>?): List<File> {
        if (extraMainResDirs == null) {
            return emptyList()
        }

        if (variantName != BASE_DIR_PATH) {
            return emptyList()
        }

        return extraMainResDirs
    }

    private fun getResPaths(
        variantName: String,
        extraDirs: List<File>
    ): VariantResPaths? {
        val resolvedResDirs = getExistingResDirs(variantName)
        if (resolvedResDirs.isNotEmpty() || extraDirs.isNotEmpty()) {
            return VariantResPaths(
                variantName,
                resolvedResDirs + extraDirs
            )
        }

        return null
    }

    private fun getExistingResDirs(variantName: String): Set<File> {
        val dirs = androidExtension.getVariantSrcDirs(variantName)
        return dirs.filter { it.exists() }.toSet()
    }
}
