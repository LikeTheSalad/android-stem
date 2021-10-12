package com.likethesalad.placeholder.modules.resolveStrings.data.helpers.files

import com.likethesalad.placeholder.modules.common.Constants
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.models.VariantResPaths
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs.VariantValuesFolders
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class ResolvedDataCleaner @AssistedInject constructor(
    androidVariantContext: AndroidVariantContext,
    private val variantValuesFoldersFactory: VariantValuesFolders.Factory
) {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantContext: AndroidVariantContext): ResolvedDataCleaner
    }

    private val variantDirsPathFinder = androidVariantContext.variantDirsPathFinder
    private val variantName by lazy {
        androidVariantContext.androidVariantData.getVariantName()
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
