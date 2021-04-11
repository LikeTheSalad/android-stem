package com.likethesalad.placeholder.modules.resolveStrings.data.helpers.files

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.modules.common.Constants
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.models.VariantResPaths
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs.VariantValuesFoldersFactory
import java.io.File

@AutoFactory
class ResolvedDataCleaner(
    androidVariantContext: AndroidVariantContext,
    @Provided private val variantValuesFoldersFactory: VariantValuesFoldersFactory
) {
    private val variantDirsPathFinder = androidVariantContext.variantDirsPathFinder
    private val variantName by lazy {
        androidVariantContext.appVariantHelper.getVariantName()
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
