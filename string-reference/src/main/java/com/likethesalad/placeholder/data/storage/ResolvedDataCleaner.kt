package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.data.Constants
import com.likethesalad.placeholder.data.VariantDirsPathFinder
import com.likethesalad.placeholder.models.VariantResPaths
import com.likethesalad.placeholder.utils.AutoFactory
import com.likethesalad.placeholder.utils.VariantDataExtractor
import java.io.File

@AutoFactory
class ResolvedDataCleaner(
    private val variantDataExtractor: VariantDataExtractor,
    private val variantDirsPathFinder: VariantDirsPathFinder
) {

    private val variantName by lazy {
        variantDataExtractor.getVariantName()
    }

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
