package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import java.io.File

class VariantDirsPathFinder(
    private val variantDirsPathResolver: VariantDirsPathResolver,
    private val androidProjectHelper: AndroidProjectHelper
) {

    fun getExistingPaths(): List<Set<File>> {
        val existing = mutableListOf<Set<File>>()
        val pathResolved = variantDirsPathResolver.getPath()
        val sourceSets = androidProjectHelper.androidExtension.getSourceSets()

        for (resolved in pathResolved) {
            val resolvedRes = sourceSets.getValue(resolved).getRes()
            if (resolvedRes.getSrcDirs().any { it.exists() }) {
                existing.add(sourceSets.getValue(resolved).getRes().getSrcDirs())
            }
        }

        return existing
    }
}
