package com.likethesalad.placeholder.modules.resolveStrings.data.helpers.files

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.modules.common.Constants
import com.likethesalad.placeholder.modules.common.helpers.android.AppVariantHelper
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathFinder
import com.likethesalad.placeholder.modules.common.models.VariantResPaths
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs.VariantValuesFoldersFactory
import java.io.File

@AutoFactory
class ResolvedDataCleaner(
    private val appVariantHelper: AppVariantHelper,
    private val variantDirsPathFinder: VariantDirsPathFinder,
    @Provided private val variantValuesFoldersFactory: VariantValuesFoldersFactory
) {

    private val variantName by lazy {
        appVariantHelper.getVariantName()
    }

    fun removeResolvedFiles() {
        getVariantResPaths()?.let { variantResPaths ->
            val variantValuesFolders = variantValuesFoldersFactory.create(variantResPaths)
            variantValuesFolders.valuesFolders.forEach {
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
