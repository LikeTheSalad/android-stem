package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import com.likethesalad.placeholder.models.VariantResPaths

class VariantDirsPathFinder(
    private val variantDirsPathResolver: VariantDirsPathResolver,
    private val androidProjectHelper: AndroidProjectHelper
) {

    val existingPathsResDirs: List<VariantResPaths> by lazy {
        val existing = mutableListOf<VariantResPaths>()
        val pathResolved = variantDirsPathResolver.pathList
        val sourceSets = androidProjectHelper.androidExtension.getSourceSets()

        for (resolvedName in pathResolved) {
            val resolvedRes = sourceSets.getValue(resolvedName).getRes()
            if (resolvedRes.getSrcDirs().any { it.exists() }) {
                existing.add(
                    VariantResPaths(
                        resolvedName,
                        sourceSets.getValue(resolvedName).getRes().getSrcDirs()
                    )
                )
            }
        }

        existing
    }
}
