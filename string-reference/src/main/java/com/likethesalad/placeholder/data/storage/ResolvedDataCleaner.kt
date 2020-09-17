package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.data.Constants
import com.likethesalad.placeholder.data.VariantDirsPathFinder
import com.likethesalad.placeholder.models.VariantResPaths
import java.io.File

class ResolvedDataCleaner(
    private val variantName: String,
    private val variantDirsPathFinder: VariantDirsPathFinder
) {
    fun removeResolvedFiles() {
        getVariantResPaths()?.let { variantResPaths ->
            ValuesFoldersExtractor(variantResPaths.paths).getValuesFolders().forEach {
                File(it, Constants.RESOLVED_FILE_NAME).delete()
            }
        }
    }

    private fun getVariantResPaths(): VariantResPaths? {
        for (pathResDirs in variantDirsPathFinder.getExistingPathsResDirs()) {
            if (pathResDirs.variantName == variantName) {
                return pathResDirs
            }
        }
        return null
    }
}
