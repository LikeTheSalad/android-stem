package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import java.io.File

class VariantDirsPathFinder(
    private val variantDirsPathResolver: VariantDirsPathResolver,
    private val androidProjectHelper: AndroidProjectHelper
) {

    fun getExistingPathsResDirs(): Map<String, Set<File>> {
        val existing = mutableMapOf<String, Set<File>>()
        val pathResolved = variantDirsPathResolver.pathList
        val sourceSets = androidProjectHelper.androidExtension.getSourceSets()

        for (resolvedName in pathResolved) {
            val resolvedRes = sourceSets.getValue(resolvedName).getRes()
            if (resolvedRes.getSrcDirs().any { it.exists() }) {
                existing[resolvedName] = sourceSets.getValue(resolvedName).getRes().getSrcDirs()
            }
        }

        return existing
    }
}
