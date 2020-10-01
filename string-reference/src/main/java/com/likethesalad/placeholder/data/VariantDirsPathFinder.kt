package com.likethesalad.placeholder.data

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.data.VariantDirsPathHandler.Companion.BASE_DIR_PATH
import com.likethesalad.placeholder.models.VariantResPaths
import com.likethesalad.placeholder.utils.AndroidExtensionHelper
import java.io.File

@AutoFactory
class VariantDirsPathFinder(
    private val variantDirsPathResolver: VariantDirsPathResolver,
    @Provided private val androidExtensionHelper: AndroidExtensionHelper
) {

    fun getExistingPathsResDirs(extraMainResDirs: List<File>? = null): List<VariantResPaths> {
        val existing = mutableListOf<VariantResPaths>()
        val pathResolved = variantDirsPathResolver.pathList

        for (resolvedName in pathResolved) {
            getResPaths(
                resolvedName, getExtraDirs(resolvedName, extraMainResDirs)
            )?.let {
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
        val dirs = androidExtensionHelper.getVariantSrcDirs(variantName)
        return dirs.filter { it.exists() }.toSet()
    }
}
