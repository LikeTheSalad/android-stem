package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidExtensionHelper
import com.likethesalad.placeholder.modules.common.helpers.android.AppVariantHelper
import com.likethesalad.placeholder.modules.common.models.VariantResPaths
import java.io.File

@AutoFactory
class VariantDirsPathFinder(
    private val appVariantHelper: AppVariantHelper,
    @Provided private val variantDirsPathResolverFactory: VariantDirsPathResolverFactory,
    @Provided private val androidExtensionHelper: AndroidExtensionHelper
) {
    private val variantDirsPathResolver by lazy { variantDirsPathResolverFactory.create(appVariantHelper) }

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
        val dirs = androidExtensionHelper.getVariantSrcDirs(variantName)
        return dirs.filter { it.exists() }.toSet()
    }
}
