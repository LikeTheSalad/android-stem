package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.VariantDirsPathHandler.Companion.BASE_DIR_PATH
import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import com.likethesalad.placeholder.models.VariantResPaths
import java.io.File

class VariantDirsPathFinder(
    private val variantDirsPathResolver: VariantDirsPathResolver,
    private val androidProjectHelper: AndroidProjectHelper
) {

    fun getExistingPathsResDirs(extraMainResDirs: List<File>? = null): List<VariantResPaths> {
        val existing = mutableListOf<VariantResPaths>()
        val pathResolved = variantDirsPathResolver.pathList
        val sourceSets = androidProjectHelper.androidExtension.getSourceSets()

        for (resolvedName in pathResolved) {
            getResPaths(
                resolvedName, sourceSets, getExtraDirs(resolvedName, extraMainResDirs)
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
        sourceSets: Map<String, AndroidSourceSetWrapper>,
        extraDirs: List<File>
    ): VariantResPaths? {
        val variantSourceSet = sourceSets.getValue(variantName)
        val resolvedResDirs = getExistingResDirs(variantSourceSet)
        if (resolvedResDirs.isNotEmpty() || extraDirs.isNotEmpty()) {
            return VariantResPaths(
                variantName,
                resolvedResDirs + extraDirs
            )
        }

        return null
    }

    private fun getExistingResDirs(sourceSet: AndroidSourceSetWrapper): Set<File> {
        return sourceSet.getRes().getSrcDirs().filter { it.exists() }.toSet()
    }
}
